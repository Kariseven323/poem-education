// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "171c06d1-98e0-423c-808a-8a20f8c3bead"
//   Timestamp: "2025-08-08T14:05:00+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "RESTful API设计最佳实践，遵循现有Controller模式"
//   Quality_Check: "编译通过，API接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.controller;

import com.poem.education.dto.request.CreationRequest;
import com.poem.education.dto.response.CreationDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.RadarDataDTO;
import com.poem.education.dto.response.Result;
import com.poem.education.service.CreationService;
import com.poem.education.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * 创作控制器
 * 处理诗词创作相关的API接口
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@RestController
@RequestMapping("/api/v1/creations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CreationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CreationController.class);
    
    @Autowired
    private CreationService creationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 创建新的诗词创作
     * POST /api/v1/creations
     * 
     * @param request 创作请求
     * @param httpRequest HTTP请求
     * @return 创作详情
     */
    @PostMapping
    public Result<CreationDTO> createCreation(@Valid @RequestBody CreationRequest request,
                                            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        logger.info("用户{}创建新作品，标题：{}", userId, request.getTitle());
        
        CreationDTO creationDTO = creationService.createCreation(userId, request);
        
        logger.info("创作创建成功，ID：{}", creationDTO.getId());
        return Result.success(creationDTO, "创作提交成功");
    }
    
    /**
     * 根据ID获取创作详情
     * GET /api/v1/creations/{id}
     * 
     * @param id 创作ID
     * @return 创作详情
     */
    @GetMapping("/{id}")
    public Result<CreationDTO> getCreationById(@PathVariable String id) {
        logger.info("获取创作详情: id={}", id);
        
        CreationDTO creationDTO = creationService.getCreationById(id);
        
        return Result.success(creationDTO, "获取创作详情成功");
    }
    
    /**
     * 获取当前用户的创作列表
     * GET /api/v1/creations/my?page=1&size=20&style=律诗&status=1
     * 
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param style 风格（可选）
     * @param status 状态（可选）
     * @param httpRequest HTTP请求
     * @return 创作分页列表
     */
    @GetMapping("/my")
    public Result<PageResult<CreationDTO>> getMyCreations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String style,
            @RequestParam(required = false) Integer status,
            HttpServletRequest httpRequest) {
        
        Long userId = getCurrentUserId(httpRequest);
        logger.info("获取用户{}的创作列表: page={}, size={}, style={}, status={}", 
                   userId, page, size, style, status);
        
        PageResult<CreationDTO> result = creationService.getUserCreations(userId, page, size, style, status);
        
        return Result.success(result, "获取创作列表成功");
    }
    
    /**
     * 获取公开创作列表
     * GET /api/v1/creations/public?page=1&size=20&style=律诗
     * 
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param style 风格（可选）
     * @return 创作分页列表
     */
    @GetMapping("/public")
    public Result<PageResult<CreationDTO>> getPublicCreations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String style) {
        
        logger.info("获取公开创作列表: page={}, size={}, style={}", page, size, style);
        
        PageResult<CreationDTO> result = creationService.getPublicCreations(page, size, style);
        
        return Result.success(result, "获取公开创作列表成功");
    }
    
    /**
     * 更新创作信息
     * PUT /api/v1/creations/{id}
     * 
     * @param id 创作ID
     * @param request 更新请求
     * @param httpRequest HTTP请求
     * @return 更新后的创作详情
     */
    @PutMapping("/{id}")
    public Result<CreationDTO> updateCreation(@PathVariable String id,
                                            @Valid @RequestBody CreationRequest request,
                                            HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        logger.info("用户{}更新创作{}，标题：{}", userId, id, request.getTitle());
        
        CreationDTO creationDTO = creationService.updateCreation(userId, id, request);
        
        logger.info("创作更新成功，ID：{}", id);
        return Result.success(creationDTO, "创作更新成功");
    }
    
    /**
     * 删除创作
     * DELETE /api/v1/creations/{id}
     * 
     * @param id 创作ID
     * @param httpRequest HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCreation(@PathVariable String id,
                                     HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        logger.info("用户{}删除创作{}", userId, id);
        
        creationService.deleteCreation(userId, id);
        
        logger.info("创作删除成功，ID：{}", id);
        return Result.success(null, "创作删除成功");
    }
    
    /**
     * 触发AI评分
     * POST /api/v1/creations/{id}/score
     * 
     * @param id 创作ID
     * @param httpRequest HTTP请求
     * @return 评分触发结果
     */
    @PostMapping("/{id}/score")
    public Result<String> requestAIScore(@PathVariable String id,
                                       HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        logger.info("用户{}请求AI评分，创作ID：{}", userId, id);
        
        // 异步触发AI评分
        CompletableFuture<Boolean> future = creationService.requestAIScore(userId, id);
        
        // 立即返回，不等待评分完成
        logger.info("AI评分请求已提交，创作ID：{}", id);
        return Result.success("评分请求已提交，请稍后查看结果", "AI评分请求成功");
    }
    
    /**
     * 获取雷达图数据
     * GET /api/v1/creations/{id}/radar
     * 
     * @param id 创作ID
     * @return 雷达图数据
     */
    @GetMapping("/{id}/radar")
    public Result<RadarDataDTO> getRadarData(@PathVariable String id) {
        logger.info("获取雷达图数据，创作ID：{}", id);
        
        RadarDataDTO radarData = creationService.getRadarData(id);
        
        return Result.success(radarData, "获取雷达图数据成功");
    }
    
    /**
     * 切换创作公开状态
     * PUT /api/v1/creations/{id}/public
     * 
     * @param id 创作ID
     * @param isPublic 是否公开
     * @param httpRequest HTTP请求
     * @return 更新后的创作详情
     */
    @PutMapping("/{id}/public")
    public Result<CreationDTO> togglePublicStatus(@PathVariable String id,
                                                 @RequestParam Boolean isPublic,
                                                 HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        logger.info("用户{}切换创作{}公开状态为：{}", userId, id, isPublic);
        
        CreationDTO creationDTO = creationService.togglePublicStatus(userId, id, isPublic);
        
        logger.info("创作公开状态更新成功，ID：{}，公开：{}", id, isPublic);
        return Result.success(creationDTO, "公开状态更新成功");
    }
    
    /**
     * 点赞/取消点赞创作
     * POST /api/v1/creations/{id}/like
     * 
     * @param id 创作ID
     * @param httpRequest HTTP请求
     * @return 更新后的创作详情
     */
    @PostMapping("/{id}/like")
    public Result<CreationDTO> toggleLike(@PathVariable String id,
                                        HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        logger.info("用户{}切换创作{}点赞状态", userId, id);
        
        CreationDTO creationDTO = creationService.toggleLike(userId, id);
        
        logger.info("创作点赞状态更新成功，ID：{}", id);
        return Result.success(creationDTO, "点赞状态更新成功");
    }
    
    /**
     * 搜索创作
     * GET /api/v1/creations/search?keyword=春天&page=1&size=20&style=律诗
     * 
     * @param keyword 关键词
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @param style 风格（可选）
     * @return 创作分页列表
     */
    @GetMapping("/search")
    public Result<PageResult<CreationDTO>> searchCreations(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String style) {
        
        logger.info("搜索创作，关键词：{}，页码：{}，大小：{}", keyword, page, size);
        
        PageResult<CreationDTO> result = creationService.searchCreations(keyword, page, size, style);
        
        return Result.success(result, "搜索创作成功");
    }
    
    /**
     * 从Spring Security认证信息中获取当前用户ID
     *
     * @param request HTTP请求
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // 优先从Spring Security认证信息中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            return (Long) authentication.getPrincipal();
        }

        // 如果Spring Security中没有认证信息，则手动解析JWT（兼容性处理）
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未提供有效的认证令牌");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("无效的认证令牌");
        }

        return jwtUtil.getUserIdFromToken(token);
    }
}
// {{END_MODIFICATIONS}}
