// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "2442c79c-d034-48f6-a1ec-206b15c6976c"
//   Timestamp: "2025-08-08T16:42:37+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service实现类设计最佳实践"
//   Quality_Check: "编译通过，异步更新功能完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.poem.education.service.ContentStatsService;
import com.poem.education.repository.mysql.ContentStatsRepository;
import com.poem.education.entity.mysql.ContentStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 内容统计服务实现类
 * 负责内容统计数据的更新和管理
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@Service
public class ContentStatsServiceImpl implements ContentStatsService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContentStatsServiceImpl.class);
    
    @Autowired
    private ContentStatsRepository contentStatsRepository;
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<Boolean> updateContentStats(String contentId, String contentType, String actionType) {
        logger.info("异步更新内容统计: contentId={}, contentType={}, actionType={}", 
                   contentId, contentType, actionType);
        
        try {
            boolean result = updateContentStatsSync(contentId, contentType, actionType);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            logger.error("异步更新内容统计失败: contentId={}, contentType={}, actionType={}", 
                        contentId, contentType, actionType, e);
            return CompletableFuture.completedFuture(false);
        }
    }
    
    @Override
    @Transactional
    public boolean updateContentStatsSync(String contentId, String contentType, String actionType) {
        logger.debug("同步更新内容统计: contentId={}, contentType={}, actionType={}", 
                    contentId, contentType, actionType);
        
        try {
            // 验证参数
            if (!validateParameters(contentId, contentType, actionType)) {
                return false;
            }
            
            // 确保统计记录存在
            if (!ensureContentStatsExists(contentId, contentType)) {
                logger.warn("无法创建或找到统计记录: contentId={}, contentType={}", contentId, contentType);
                return false;
            }
            
            // 根据行为类型更新对应的统计字段
            return incrementContentStats(contentId, contentType, actionType, 1L);
            
        } catch (Exception e) {
            logger.error("同步更新内容统计失败: contentId={}, contentType={}, actionType={}", 
                        contentId, contentType, actionType, e);
            return false;
        }
    }
    
    @Override
    @Async
    @Transactional
    public CompletableFuture<Boolean> batchUpdateContentStats(String contentId, String contentType, String[] actionTypes) {
        logger.info("批量异步更新内容统计: contentId={}, contentType={}, actionTypes={}", 
                   contentId, contentType, actionTypes);
        
        try {
            boolean allSuccess = true;
            for (String actionType : actionTypes) {
                boolean result = updateContentStatsSync(contentId, contentType, actionType);
                if (!result) {
                    allSuccess = false;
                    logger.warn("批量更新中某项失败: actionType={}", actionType);
                }
            }
            return CompletableFuture.completedFuture(allSuccess);
        } catch (Exception e) {
            logger.error("批量异步更新内容统计失败: contentId={}, contentType={}", 
                        contentId, contentType, e);
            return CompletableFuture.completedFuture(false);
        }
    }
    
    @Override
    @Transactional
    public boolean incrementContentStats(String contentId, String contentType, String actionType, Long increment) {
        logger.debug("增量更新内容统计: contentId={}, contentType={}, actionType={}, increment={}", 
                    contentId, contentType, actionType, increment);
        
        try {
            int affectedRows = 0;
            
            // 根据行为类型调用对应的增量更新方法
            switch (actionType.toLowerCase()) {
                case "view":
                    affectedRows = contentStatsRepository.incrementViewCount(contentId, contentType, increment);
                    break;
                case "like":
                    affectedRows = contentStatsRepository.incrementLikeCount(contentId, contentType, increment);
                    break;
                case "favorite":
                    affectedRows = contentStatsRepository.incrementFavoriteCount(contentId, contentType, increment);
                    break;
                case "comment":
                    affectedRows = contentStatsRepository.incrementCommentCount(contentId, contentType, increment);
                    break;
                case "share":
                    affectedRows = contentStatsRepository.incrementShareCount(contentId, contentType, increment);
                    break;
                default:
                    logger.warn("不支持的行为类型: actionType={}", actionType);
                    return false;
            }
            
            boolean success = affectedRows > 0;
            if (success) {
                logger.debug("统计更新成功: contentId={}, actionType={}, affectedRows={}", 
                           contentId, actionType, affectedRows);
            } else {
                logger.warn("统计更新失败，可能记录不存在: contentId={}, contentType={}, actionType={}", 
                          contentId, contentType, actionType);
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("增量更新内容统计失败: contentId={}, contentType={}, actionType={}, increment={}", 
                        contentId, contentType, actionType, increment, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean ensureContentStatsExists(String contentId, String contentType) {
        try {
            // 检查记录是否已存在
            if (contentStatsRepository.existsByContentIdAndContentType(contentId, contentType)) {
                return true;
            }
            
            // 创建新的统计记录
            ContentStats stats = new ContentStats();
            stats.setContentId(contentId);
            stats.setContentType(contentType);
            stats.setViewCount(0L);
            stats.setLikeCount(0L);
            stats.setFavoriteCount(0L);
            stats.setCommentCount(0L);
            stats.setShareCount(0L);
            stats.setLastUpdated(LocalDateTime.now());
            
            contentStatsRepository.save(stats);
            logger.info("创建新的内容统计记录: contentId={}, contentType={}", contentId, contentType);
            return true;
            
        } catch (Exception e) {
            logger.error("确保内容统计记录存在失败: contentId={}, contentType={}", 
                        contentId, contentType, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean resetContentStats(String contentId, String contentType) {
        logger.info("重置内容统计数据: contentId={}, contentType={}", contentId, contentType);
        
        try {
            Optional<ContentStats> statsOptional = contentStatsRepository
                    .findByContentIdAndContentType(contentId, contentType);
            
            if (statsOptional.isPresent()) {
                ContentStats stats = statsOptional.get();
                stats.setViewCount(0L);
                stats.setLikeCount(0L);
                stats.setFavoriteCount(0L);
                stats.setCommentCount(0L);
                stats.setShareCount(0L);
                stats.setLastUpdated(LocalDateTime.now());
                
                contentStatsRepository.save(stats);
                logger.info("内容统计数据重置成功: contentId={}, contentType={}", contentId, contentType);
                return true;
            } else {
                logger.warn("要重置的统计记录不存在: contentId={}, contentType={}", contentId, contentType);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("重置内容统计数据失败: contentId={}, contentType={}", 
                        contentId, contentType, e);
            return false;
        }
    }
    
    /**
     * 验证参数有效性
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param actionType 行为类型
     * @return 参数是否有效
     */
    private boolean validateParameters(String contentId, String contentType, String actionType) {
        // 验证contentId
        if (contentId == null || contentId.trim().isEmpty()) {
            logger.warn("contentId不能为空");
            return false;
        }
        if (contentId.length() != 24) {
            logger.warn("contentId格式不正确，应为24个字符的MongoDB ObjectId: {}", contentId);
            return false;
        }
        
        // 验证contentType
        if (contentType == null || contentType.trim().isEmpty()) {
            logger.warn("contentType不能为空");
            return false;
        }
        String[] validTypes = {"guwen", "sentence", "writer", "creation"};
        boolean isValidType = false;
        for (String validType : validTypes) {
            if (validType.equals(contentType)) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            logger.warn("contentType不正确: {}", contentType);
            return false;
        }
        
        // 验证actionType
        if (actionType == null || actionType.trim().isEmpty()) {
            logger.warn("actionType不能为空");
            return false;
        }
        String[] validActions = {"view", "like", "favorite", "comment", "share"};
        boolean isValidAction = false;
        for (String validAction : validActions) {
            if (validAction.equalsIgnoreCase(actionType)) {
                isValidAction = true;
                break;
            }
        }
        if (!isValidAction) {
            logger.warn("actionType不正确: {}", actionType);
            return false;
        }
        
        return true;
    }
}
// {{END_MODIFICATIONS}}
