// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "cbae72cf-b030-48a2-b381-ce2a1484c281"
//   Timestamp: "2025-08-07T12:20:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "RESTful API设计最佳实践，严格按照数据库表结构"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.controller;

import com.poem.education.dto.request.UserActionRequest;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.Result;
import com.poem.education.dto.response.UserActionDTO;
import com.poem.education.service.UserActionService;
import com.poem.education.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 用户行为控制器
 * 处理用户行为相关的API接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/api/v1/actions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserActionController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserActionController.class);
    
    @Autowired
    private UserActionService userActionService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 记录用户行为
     * POST /api/v1/actions
     * 
     * @param request HTTP请求
     * @param actionRequest 行为请求
     * @return 行为记录
     */
    @PostMapping
    public Result<UserActionDTO> recordAction(HttpServletRequest request, 
                                            @Valid @RequestBody UserActionRequest actionRequest) {
        
        Long userId = getCurrentUserId(request);
        logger.info("记录用户行为: userId={}, request={}", userId, actionRequest);
        
        UserActionDTO actionDTO = userActionService.recordAction(userId, actionRequest);
        
        return Result.success(actionDTO, "记录行为成功");
    }
    
    /**
     * 取消用户行为
     * DELETE /api/v1/actions
     * 
     * @param request HTTP请求
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 取消结果
     */
    @DeleteMapping
    public Result<Boolean> cancelAction(HttpServletRequest request,
                                      @RequestParam String targetId,
                                      @RequestParam String targetType,
                                      @RequestParam String actionType) {
        
        Long userId = getCurrentUserId(request);
        logger.info("取消用户行为: userId={}, targetId={}, targetType={}, actionType={}", 
                   userId, targetId, targetType, actionType);
        
        boolean success = userActionService.cancelAction(userId, targetId, targetType, actionType);
        
        return Result.success(success, success ? "取消行为成功" : "行为记录不存在");
    }
    
    /**
     * 检查用户是否已执行某行为
     * GET /api/v1/actions/check
     * 
     * @param request HTTP请求
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 检查结果
     */
    @GetMapping("/check")
    public Result<Boolean> hasAction(HttpServletRequest request,
                                   @RequestParam String targetId,
                                   @RequestParam String targetType,
                                   @RequestParam String actionType) {
        
        Long userId = getCurrentUserId(request);
        logger.info("检查用户行为: userId={}, targetId={}, targetType={}, actionType={}", 
                   userId, targetId, targetType, actionType);
        
        boolean hasAction = userActionService.hasAction(userId, targetId, targetType, actionType);
        
        return Result.success(hasAction, "检查完成");
    }
    
    /**
     * 获取用户行为列表
     * GET /api/v1/actions/user?page=1&size=20
     * 
     * @param request HTTP请求
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 用户行为分页列表
     */
    @GetMapping("/user")
    public Result<PageResult<UserActionDTO>> getUserActions(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long userId = getCurrentUserId(request);
        logger.info("获取用户行为列表: userId={}, page={}, size={}", userId, page, size);
        
        PageResult<UserActionDTO> result = userActionService.getUserActions(userId, page, size);
        
        return Result.success(result, "获取用户行为列表成功");
    }
    
    /**
     * 根据行为类型获取用户行为列表
     * GET /api/v1/actions/user/type?actionType=like&page=1&size=20
     * 
     * @param request HTTP请求
     * @param actionType 行为类型
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 用户行为分页列表
     */
    @GetMapping("/user/type")
    public Result<PageResult<UserActionDTO>> getUserActionsByType(
            HttpServletRequest request,
            @RequestParam String actionType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long userId = getCurrentUserId(request);
        logger.info("根据类型获取用户行为: userId={}, actionType={}, page={}, size={}", 
                   userId, actionType, page, size);
        
        PageResult<UserActionDTO> result = userActionService.getUserActionsByType(userId, actionType, page, size);
        
        return Result.success(result, "获取用户行为列表成功");
    }
    
    /**
     * 获取目标的行为统计
     * GET /api/v1/actions/stats?targetId={objectId}&targetType=guwen
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 行为统计信息
     */
    @GetMapping("/stats")
    public Result<Object> getTargetActionStats(
            @RequestParam String targetId,
            @RequestParam String targetType) {
        
        logger.info("获取目标行为统计: targetId={}, targetType={}", targetId, targetType);
        
        Object stats = userActionService.getTargetActionStats(targetId, targetType);
        
        return Result.success(stats, "获取行为统计成功");
    }
    
    /**
     * 获取热门内容
     * GET /api/v1/actions/hot?targetType=guwen&limit=10
     * 
     * @param targetType 目标类型
     * @param limit 限制数量，默认10
     * @return 热门内容列表
     */
    @GetMapping("/hot")
    public Result<List<Object>> getHotContentByLikes(
            @RequestParam String targetType,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        logger.info("获取热门内容: targetType={}, limit={}", targetType, limit);
        
        List<Object> result = userActionService.getHotContentByLikes(targetType, limit);
        
        return Result.success(result, "获取热门内容成功");
    }
    
    /**
     * 获取用户最近的行为
     * GET /api/v1/actions/recent?limit=10
     * 
     * @param request HTTP请求
     * @param limit 限制数量，默认10
     * @return 最近行为列表
     */
    @GetMapping("/recent")
    public Result<List<UserActionDTO>> getRecentActions(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") Integer limit) {
        
        Long userId = getCurrentUserId(request);
        logger.info("获取用户最近行为: userId={}, limit={}", userId, limit);
        
        List<UserActionDTO> result = userActionService.getRecentActions(userId, limit);
        
        return Result.success(result, "获取最近行为成功");
    }
    
    /**
     * 统计用户行为数量
     * GET /api/v1/actions/count?actionType=like
     * 
     * @param request HTTP请求
     * @param actionType 行为类型（可选）
     * @return 行为数量
     */
    @GetMapping("/count")
    public Result<Long> countUserActions(
            HttpServletRequest request,
            @RequestParam(required = false) String actionType) {
        
        Long userId = getCurrentUserId(request);
        logger.info("统计用户行为数量: userId={}, actionType={}", userId, actionType);
        
        long count = userActionService.countUserActions(userId, actionType);
        
        return Result.success(count, "统计完成");
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
