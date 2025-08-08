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

import com.poem.education.dto.request.FavoriteRequest;
import com.poem.education.dto.response.FavoriteDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.Result;
import com.poem.education.service.FavoriteService;
import com.poem.education.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 * 处理收藏相关的API接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@RestController
@RequestMapping("/api/v1/favorites")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {
    
    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);
    
    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 添加收藏
     * POST /api/v1/favorites
     * 
     * @param request HTTP请求
     * @param favoriteRequest 收藏请求
     * @return 收藏记录
     */
    @PostMapping
    public Result<FavoriteDTO> addFavorite(HttpServletRequest request, 
                                         @Valid @RequestBody FavoriteRequest favoriteRequest) {
        
        Long userId = getCurrentUserId(request);
        logger.info("添加收藏: userId={}, request={}", userId, favoriteRequest);
        
        FavoriteDTO favoriteDTO = favoriteService.addFavorite(userId, favoriteRequest);
        
        return Result.success(favoriteDTO, "添加收藏成功");
    }
    
    /**
     * 取消收藏
     * DELETE /api/v1/favorites
     * 
     * @param request HTTP请求
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 取消结果
     */
    @DeleteMapping
    public Result<Boolean> removeFavorite(HttpServletRequest request,
                                        @RequestParam String targetId,
                                        @RequestParam String targetType) {
        
        Long userId = getCurrentUserId(request);
        logger.info("取消收藏: userId={}, targetId={}, targetType={}", userId, targetId, targetType);
        
        boolean success = favoriteService.removeFavorite(userId, targetId, targetType);
        
        return Result.success(success, success ? "取消收藏成功" : "收藏记录不存在");
    }
    
    /**
     * 检查是否已收藏
     * GET /api/v1/favorites/check
     * 
     * @param request HTTP请求
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 检查结果
     */
    @GetMapping("/check")
    public Result<Boolean> isFavorited(HttpServletRequest request,
                                     @RequestParam String targetId,
                                     @RequestParam String targetType) {
        
        Long userId = getCurrentUserId(request);
        logger.info("检查收藏状态: userId={}, targetId={}, targetType={}", userId, targetId, targetType);
        
        boolean isFavorited = favoriteService.isFavorited(userId, targetId, targetType);
        
        return Result.success(isFavorited, "检查完成");
    }
    
    /**
     * 获取用户收藏列表
     * GET /api/v1/favorites/user?page=1&size=20
     * 
     * @param request HTTP请求
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 用户收藏分页列表
     */
    @GetMapping("/user")
    public Result<PageResult<FavoriteDTO>> getUserFavorites(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long userId = getCurrentUserId(request);
        logger.info("获取用户收藏列表: userId={}, page={}, size={}", userId, page, size);
        
        PageResult<FavoriteDTO> result = favoriteService.getUserFavorites(userId, page, size);
        
        return Result.success(result, "获取用户收藏列表成功");
    }
    
    /**
     * 根据收藏类型获取用户收藏列表
     * GET /api/v1/favorites/user/type?targetType=guwen&page=1&size=20
     * 
     * @param request HTTP请求
     * @param targetType 收藏类型
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 用户收藏分页列表
     */
    @GetMapping("/user/type")
    public Result<PageResult<FavoriteDTO>> getUserFavoritesByType(
            HttpServletRequest request,
            @RequestParam String targetType,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long userId = getCurrentUserId(request);
        logger.info("根据类型获取用户收藏: userId={}, targetType={}, page={}, size={}", 
                   userId, targetType, page, size);
        
        PageResult<FavoriteDTO> result = favoriteService.getUserFavoritesByType(userId, targetType, page, size);
        
        return Result.success(result, "获取用户收藏列表成功");
    }
    
    /**
     * 根据收藏夹获取收藏列表
     * GET /api/v1/favorites/folder?folderName=默认收藏夹&page=1&size=20
     * 
     * @param request HTTP请求
     * @param folderName 收藏夹名称
     * @param page 页码，默认1
     * @param size 每页大小，默认20
     * @return 收藏分页列表
     */
    @GetMapping("/folder")
    public Result<PageResult<FavoriteDTO>> getFavoritesByFolder(
            HttpServletRequest request,
            @RequestParam String folderName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        
        Long userId = getCurrentUserId(request);
        logger.info("根据收藏夹获取收藏: userId={}, folderName={}, page={}, size={}", 
                   userId, folderName, page, size);
        
        PageResult<FavoriteDTO> result = favoriteService.getFavoritesByFolder(userId, folderName, page, size);
        
        return Result.success(result, "获取收藏列表成功");
    }
    
    /**
     * 获取用户的收藏夹列表
     * GET /api/v1/favorites/folders
     * 
     * @param request HTTP请求
     * @return 收藏夹名称列表
     */
    @GetMapping("/folders")
    public Result<List<String>> getUserFolders(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        logger.info("获取用户收藏夹列表: userId={}", userId);
        
        List<String> folders = favoriteService.getUserFolders(userId);
        
        return Result.success(folders, "获取收藏夹列表成功");
    }
    
    /**
     * 创建收藏夹
     * POST /api/v1/favorites/folders
     * 
     * @param request HTTP请求
     * @param folderName 收藏夹名称
     * @return 创建结果
     */
    @PostMapping("/folders")
    public Result<Boolean> createFolder(HttpServletRequest request, @RequestParam String folderName) {
        Long userId = getCurrentUserId(request);
        logger.info("创建收藏夹: userId={}, folderName={}", userId, folderName);
        
        boolean success = favoriteService.createFolder(userId, folderName);
        
        return Result.success(success, success ? "创建收藏夹成功" : "收藏夹已存在");
    }
    
    /**
     * 重命名收藏夹
     * PUT /api/v1/favorites/folders
     * 
     * @param request HTTP请求
     * @param oldFolderName 旧收藏夹名称
     * @param newFolderName 新收藏夹名称
     * @return 重命名结果
     */
    @PutMapping("/folders")
    public Result<Boolean> renameFolder(HttpServletRequest request,
                                      @RequestParam String oldFolderName,
                                      @RequestParam String newFolderName) {
        
        Long userId = getCurrentUserId(request);
        logger.info("重命名收藏夹: userId={}, oldFolderName={}, newFolderName={}", 
                   userId, oldFolderName, newFolderName);
        
        boolean success = favoriteService.renameFolder(userId, oldFolderName, newFolderName);
        
        return Result.success(success, success ? "重命名收藏夹成功" : "收藏夹不存在");
    }
    
    /**
     * 删除收藏夹
     * DELETE /api/v1/favorites/folders
     * 
     * @param request HTTP请求
     * @param folderName 收藏夹名称
     * @return 删除结果
     */
    @DeleteMapping("/folders")
    public Result<Boolean> deleteFolder(HttpServletRequest request, @RequestParam String folderName) {
        Long userId = getCurrentUserId(request);
        logger.info("删除收藏夹: userId={}, folderName={}", userId, folderName);
        
        boolean success = favoriteService.deleteFolder(userId, folderName);
        
        return Result.success(success, success ? "删除收藏夹成功" : "收藏夹不存在");
    }
    
    /**
     * 移动收藏到指定收藏夹
     * PUT /api/v1/favorites/{favoriteId}/move
     * 
     * @param request HTTP请求
     * @param favoriteId 收藏ID
     * @param newFolderName 新收藏夹名称
     * @return 移动结果
     */
    @PutMapping("/{favoriteId}/move")
    public Result<Boolean> moveFavoriteToFolder(HttpServletRequest request,
                                              @PathVariable Long favoriteId,
                                              @RequestParam String newFolderName) {
        
        Long userId = getCurrentUserId(request);
        logger.info("移动收藏到收藏夹: userId={}, favoriteId={}, newFolderName={}", 
                   userId, favoriteId, newFolderName);
        
        boolean success = favoriteService.moveFavoriteToFolder(userId, favoriteId, newFolderName);
        
        return Result.success(success, success ? "移动收藏成功" : "收藏不存在");
    }
    
    /**
     * 更新收藏备注
     * PUT /api/v1/favorites/{favoriteId}/notes
     * 
     * @param request HTTP请求
     * @param favoriteId 收藏ID
     * @param notes 新备注
     * @return 更新结果
     */
    @PutMapping("/{favoriteId}/notes")
    public Result<Boolean> updateFavoriteNotes(HttpServletRequest request,
                                              @PathVariable Long favoriteId,
                                              @RequestParam String notes) {
        
        Long userId = getCurrentUserId(request);
        logger.info("更新收藏备注: userId={}, favoriteId={}, notes={}", userId, favoriteId, notes);
        
        boolean success = favoriteService.updateFavoriteNotes(userId, favoriteId, notes);
        
        return Result.success(success, success ? "更新备注成功" : "收藏不存在");
    }
    
    /**
     * 统计用户收藏数量
     * GET /api/v1/favorites/count?targetType=guwen
     * 
     * @param request HTTP请求
     * @param targetType 收藏类型（可选）
     * @return 收藏数量
     */
    @GetMapping("/count")
    public Result<Long> countUserFavorites(
            HttpServletRequest request,
            @RequestParam(required = false) String targetType) {
        
        Long userId = getCurrentUserId(request);
        logger.info("统计用户收藏数量: userId={}, targetType={}", userId, targetType);
        
        long count = favoriteService.countUserFavorites(userId, targetType);
        
        return Result.success(count, "统计完成");
    }
    
    /**
     * 获取最近收藏的内容
     * GET /api/v1/favorites/recent?limit=10
     *
     * @param request HTTP请求
     * @param limit 限制数量，默认10
     * @return 最近收藏列表
     */
    @GetMapping("/recent")
    public Result<List<FavoriteDTO>> getRecentFavorites(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") Integer limit) {

        Long userId = getCurrentUserId(request);
        logger.info("获取最近收藏: userId={}, limit={}", userId, limit);

        List<FavoriteDTO> result = favoriteService.getRecentFavorites(userId, limit);

        return Result.success(result, "获取最近收藏成功");
    }

    /**
     * 获取收藏夹统计信息
     * GET /api/v1/favorites/folders/stats
     *
     * @param request HTTP请求
     * @return 收藏夹统计信息
     */
    @GetMapping("/folders/stats")
    public Result<com.poem.education.dto.response.FolderStatsDTO> getFolderStats(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        logger.info("获取收藏夹统计信息: userId={}", userId);

        com.poem.education.dto.response.FolderStatsDTO stats = favoriteService.getFolderStatsDTO(userId);

        return Result.success(stats, "获取收藏夹统计信息成功");
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
