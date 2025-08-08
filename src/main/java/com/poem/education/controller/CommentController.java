// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "b4a42fdc-2d03-4831-9d30-9278970f029a"
//   Timestamp: "2025-08-07T12:00:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "RESTful API设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.controller;

import com.poem.education.dto.request.CommentRequest;
import com.poem.education.dto.response.CommentDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.Result;
import com.poem.education.service.CommentService;
import com.poem.education.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 评论控制器
 * 处理评论相关的API接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/api/v1/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {
    
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取评论列表（支持层级嵌套）
     * GET /api/v1/comments?targetId={objectId}&targetType=guwen&page=1&size=20
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 评论分页列表
     */
    @GetMapping
    public Result<PageResult<CommentDTO>> getCommentsByTarget(
            @RequestParam String targetId,
            @RequestParam String targetType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        logger.info("获取评论列表: targetId={}, targetType={}, page={}, size={}", 
                   targetId, targetType, page, size);
        
        PageResult<CommentDTO> result = commentService.getCommentsByTarget(targetId, targetType, page, size);
        
        return Result.success(result, "获取评论列表成功");
    }
    
    /**
     * 发表评论
     * POST /api/v1/comments
     * 
     * @param request HTTP请求
     * @param commentRequest 评论请求
     * @return 评论详情
     */
    @PostMapping
    public Result<CommentDTO> createComment(HttpServletRequest request,
                                          @Valid @RequestBody CommentRequest commentRequest) {

        logger.info("=== 开始处理评论创建请求 ===");
        logger.info("请求URL: {}", request.getRequestURL());
        logger.info("请求方法: {}", request.getMethod());
        logger.info("Content-Type: {}", request.getHeader("Content-Type"));
        logger.info("Authorization: {}", request.getHeader("Authorization"));
        logger.info("请求体: {}", commentRequest);

        try {
            Long userId = getCurrentUserId(request);
            logger.info("获取用户ID成功: userId={}", userId);

            logger.info("调用服务层创建评论: userId={}, request={}", userId, commentRequest);
            CommentDTO commentDTO = commentService.createComment(userId, commentRequest);
            logger.info("评论创建成功: {}", commentDTO);

            return Result.success(commentDTO, "发表评论成功");
        } catch (Exception e) {
            logger.error("评论创建失败: ", e);
            logger.error("异常类型: {}", e.getClass().getSimpleName());
            logger.error("异常消息: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 根据ID获取评论详情
     * GET /api/v1/comments/{id}
     * 
     * @param id 评论ID
     * @return 评论详情
     */
    @GetMapping("/{id}")
    public Result<CommentDTO> getCommentById(@PathVariable String id) {
        logger.info("获取评论详情: id={}", id);
        
        CommentDTO commentDTO = commentService.getCommentById(id);
        
        return Result.success(commentDTO, "获取评论详情成功");
    }
    
    /**
     * 删除评论
     * DELETE /api/v1/comments/{id}
     * 
     * @param request HTTP请求
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(HttpServletRequest request, @PathVariable String id) {
        Long userId = getCurrentUserId(request);
        logger.info("删除评论: id={}, userId={}", id, userId);
        
        commentService.deleteComment(id, userId);
        
        return Result.success(null, "删除评论成功");
    }
    
    /**
     * 点赞评论
     * POST /api/v1/comments/{id}/like
     * 
     * @param request HTTP请求
     * @param id 评论ID
     * @return 点赞结果
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likeComment(HttpServletRequest request, @PathVariable String id) {
        Long userId = getCurrentUserId(request);
        logger.info("点赞评论: id={}, userId={}", id, userId);
        
        boolean success = commentService.likeComment(id, userId);
        
        return Result.success(success, "点赞成功");
    }
    
    /**
     * 取消点赞评论
     * DELETE /api/v1/comments/{id}/like
     * 
     * @param request HTTP请求
     * @param id 评论ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/{id}/like")
    public Result<Boolean> unlikeComment(HttpServletRequest request, @PathVariable String id) {
        Long userId = getCurrentUserId(request);
        logger.info("取消点赞评论: id={}, userId={}", id, userId);
        
        boolean success = commentService.unlikeComment(id, userId);
        
        return Result.success(success, "取消点赞成功");
    }
    
    /**
     * 获取用户的评论列表
     * GET /api/v1/comments/user?page=1&size=20
     * 
     * @param request HTTP请求
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 用户评论分页列表
     */
    @GetMapping("/user")
    public Result<PageResult<CommentDTO>> getUserComments(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long userId = getCurrentUserId(request);
        logger.info("获取用户评论: userId={}, page={}, size={}", userId, page, size);
        
        PageResult<CommentDTO> result = commentService.getUserComments(userId, page, size);
        
        return Result.success(result, "获取用户评论成功");
    }
    
    /**
     * 获取热门评论
     * GET /api/v1/comments/hot?targetId={objectId}&targetType=guwen&limit=10
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param limit 限制数量，默认10
     * @return 热门评论列表
     */
    @GetMapping("/hot")
    public Result<List<CommentDTO>> getHotComments(
            @RequestParam String targetId,
            @RequestParam String targetType,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("获取热门评论: targetId={}, targetType={}, limit={}", targetId, targetType, limit);
        
        List<CommentDTO> result = commentService.getHotComments(targetId, targetType, limit);
        
        return Result.success(result, "获取热门评论成功");
    }
    
    /**
     * 获取最新评论
     * GET /api/v1/comments/latest?targetId={objectId}&targetType=guwen&limit=10
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param limit 限制数量，默认10
     * @return 最新评论列表
     */
    @GetMapping("/latest")
    public Result<List<CommentDTO>> getLatestComments(
            @RequestParam String targetId,
            @RequestParam String targetType,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("获取最新评论: targetId={}, targetType={}, limit={}", targetId, targetType, limit);
        
        List<CommentDTO> result = commentService.getLatestComments(targetId, targetType, limit);
        
        return Result.success(result, "获取最新评论成功");
    }
    
    /**
     * 获取评论统计
     * GET /api/v1/comments/stats?targetId={objectId}&targetType=guwen
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 评论统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getCommentStats(
            @RequestParam String targetId,
            @RequestParam String targetType) {
        
        logger.info("获取评论统计: targetId={}, targetType={}", targetId, targetType);
        
        long count = commentService.getCommentCount(targetId, targetType);
        
        // 构建统计信息
        Object stats = new Object() {
            public final long commentCount = count;
        };
        
        return Result.success(stats, "获取评论统计成功");
    }
    
    /**
     * 从请求中获取当前用户ID
     * 
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未找到有效的认证令牌");
    }
}
// {{END_MODIFICATIONS}}
