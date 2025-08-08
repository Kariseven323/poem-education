// {{RIPER-5+SMART-6:
//   Action: "Modified"
//   Task_ID: "2306db29-b2dd-4420-b1fa-fc4c261474a4"
//   Timestamp: "2025-08-08T16:42:37+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "RESTful API设计最佳实践"
//   Quality_Check: "编译通过，接口参数验证完整，错误处理完善。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.controller;

import com.poem.education.dto.response.Result;
import com.poem.education.service.GuwenService;
import com.poem.education.service.SentenceService;
import com.poem.education.service.WriterService;
import com.poem.education.service.UserActionService;
import com.poem.education.repository.mysql.UserActionRepository;
import com.poem.education.repository.mysql.ContentStatsRepository;
import com.poem.education.entity.mysql.ContentStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 全局统计控制器
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@RestController
@RequestMapping("/api/v1/stats")
@CrossOrigin(origins = "*")
public class StatsController {
    
    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);
    
    @Autowired
    private GuwenService guwenService;
    
    @Autowired
    private SentenceService sentenceService;
    
    @Autowired
    private WriterService writerService;
    
    @Autowired
    private UserActionService userActionService;

    @Autowired
    private UserActionRepository userActionRepository;

    @Autowired
    private ContentStatsRepository contentStatsRepository;
    
    /**
     * 获取全局统计信息
     * GET /api/v1/stats/global
     * 
     * @return 全局统计信息
     */
    @GetMapping("/global")
    public Result<Map<String, Object>> getGlobalStats() {
        logger.info("获取全局统计信息");
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取各模块的总数
            long poemCount = guwenService.getTotalCount();
            long writerCount = writerService.getTotalCount();
            long sentenceCount = sentenceService.getTotalCount();
            
            // 获取今日访问量（这里先用模拟数据，后续可以从Redis或数据库获取）
            long todayViews = getTodayViews();
            
            stats.put("poemCount", poemCount);
            stats.put("writerCount", writerCount);
            stats.put("sentenceCount", sentenceCount);
            stats.put("todayViews", todayViews);
            
            // 添加格式化的显示文本
            stats.put("poemCountDisplay", formatCount(poemCount));
            stats.put("writerCountDisplay", formatCount(writerCount));
            stats.put("sentenceCountDisplay", formatCount(sentenceCount));
            stats.put("todayViewsDisplay", String.valueOf(todayViews));
            
            return Result.success(stats, "获取全局统计信息成功");
            
        } catch (Exception e) {
            logger.error("获取全局统计信息失败", e);
            return Result.error("获取统计信息失败");
        }
    }
    
    /**
     * 格式化数量显示
     *
     * @param count 数量
     * @return 格式化后的字符串
     */
    private String formatCount(long count) {
        if (count >= 10000) {
            // 对于万级别的数据，显示精确的万数
            if (count % 10000 == 0) {
                return String.format("%dW", count / 10000);
            } else {
                return String.format("%.1fW", count / 10000.0);
            }
        } else if (count >= 1000) {
            // 对于千级别的数据，显示精确的千数
            if (count % 1000 == 0) {
                return String.format("%dK", count / 1000);
            } else {
                return String.format("%.1fK", count / 1000.0);
            }
        } else {
            return String.valueOf(count);
        }
    }
    
    /**
     * 获取内容统计信息
     * GET /api/v1/stats/content?contentId={contentId}&contentType={contentType}
     *
     * @param contentId 内容ID (MongoDB ObjectId)
     * @param contentType 内容类型 (guwen/sentence/writer/creation)
     * @return 内容统计信息
     */
    @GetMapping("/content")
    public Result<Map<String, Object>> getContentStats(
            @RequestParam String contentId,
            @RequestParam String contentType) {

        logger.info("获取内容统计信息: contentId={}, contentType={}", contentId, contentType);

        try {
            // 验证参数
            if (contentId == null || contentId.trim().isEmpty()) {
                return Result.error("内容ID不能为空");
            }
            if (contentType == null || contentType.trim().isEmpty()) {
                return Result.error("内容类型不能为空");
            }

            // 验证contentId格式（MongoDB ObjectId应该是24个字符）
            if (contentId.length() != 24) {
                return Result.error("内容ID格式不正确，应为24个字符的MongoDB ObjectId");
            }

            // 验证contentType
            String[] validTypes = {"guwen", "sentence", "writer", "creation"};
            boolean isValidType = false;
            for (String validType : validTypes) {
                if (validType.equals(contentType)) {
                    isValidType = true;
                    break;
                }
            }
            if (!isValidType) {
                return Result.error("内容类型不正确，应为：guwen、sentence、writer、creation之一");
            }

            // 查询统计信息
            Optional<ContentStats> statsOptional = contentStatsRepository
                    .findByContentIdAndContentType(contentId, contentType);

            ContentStats stats;
            if (statsOptional.isPresent()) {
                stats = statsOptional.get();
                logger.debug("找到现有统计记录: {}", stats);
            } else {
                // 如果不存在统计记录，创建默认记录
                stats = new ContentStats();
                stats.setContentId(contentId);
                stats.setContentType(contentType);
                stats.setViewCount(0L);
                stats.setLikeCount(0L);
                stats.setFavoriteCount(0L);
                stats.setCommentCount(0L);
                stats.setShareCount(0L);
                stats.setLastUpdated(LocalDateTime.now());

                // 保存默认记录
                stats = contentStatsRepository.save(stats);
                logger.info("创建新的统计记录: contentId={}, contentType={}", contentId, contentType);
            }

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("contentId", stats.getContentId());
            result.put("contentType", stats.getContentType());
            result.put("viewCount", stats.getViewCount());
            result.put("likeCount", stats.getLikeCount());
            result.put("favoriteCount", stats.getFavoriteCount());
            result.put("commentCount", stats.getCommentCount());
            result.put("shareCount", stats.getShareCount());
            result.put("lastUpdated", stats.getLastUpdated());

            // 计算热度分数
            result.put("hotScore", stats.calculateHotScore());

            return Result.success(result, "获取内容统计信息成功");

        } catch (Exception e) {
            logger.error("获取内容统计信息失败: contentId={}, contentType={}", contentId, contentType, e);
            return Result.error("获取内容统计信息失败");
        }
    }

    /**
     * 获取今日访问量
     * 从用户行为表中统计今日的VIEW行为数量
     *
     * @return 今日访问量
     */
    private long getTodayViews() {
        try {
            // 获取今日开始时间（00:00:00）
            LocalDate today = LocalDate.now();
            java.time.LocalDateTime startOfDay = today.atStartOfDay();

            // 从user_actions表中统计今日的VIEW行为数量
            long todayViewCount = userActionRepository.countByActionTypeAndCreatedAtAfter(
                "view",
                startOfDay
            );

            logger.debug("今日访问量统计: {}", todayViewCount);
            return todayViewCount;

        } catch (Exception e) {
            logger.warn("获取今日访问量失败，返回默认值", e);
            return 0;
        }
    }
}
// {{END_MODIFICATIONS}}
