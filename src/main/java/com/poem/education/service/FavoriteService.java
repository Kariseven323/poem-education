// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "cbae72cf-b030-48a2-b381-ce2a1484c281"
//   Timestamp: "2025-08-07T12:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service接口设计最佳实践"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.dto.request.FavoriteRequest;
import com.poem.education.dto.response.FavoriteDTO;
import com.poem.education.dto.response.PageResult;

import java.util.List;

/**
 * 收藏服务接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public interface FavoriteService {
    
    /**
     * 添加收藏
     * 
     * @param userId 用户ID
     * @param request 收藏请求
     * @return 收藏记录
     */
    FavoriteDTO addFavorite(Long userId, FavoriteRequest request);
    
    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 是否成功
     */
    boolean removeFavorite(Long userId, String targetId, String targetType);
    
    /**
     * 检查是否已收藏
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 是否已收藏
     */
    boolean isFavorited(Long userId, String targetId, String targetType);
    
    /**
     * 获取用户收藏列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 收藏分页列表
     */
    PageResult<FavoriteDTO> getUserFavorites(Long userId, Integer page, Integer size);
    
    /**
     * 根据收藏类型获取用户收藏列表
     * 
     * @param userId 用户ID
     * @param targetType 收藏类型
     * @param page 页码
     * @param size 每页大小
     * @return 收藏分页列表
     */
    PageResult<FavoriteDTO> getUserFavoritesByType(Long userId, String targetType, Integer page, Integer size);
    
    /**
     * 根据收藏夹获取收藏列表
     * 
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @param page 页码
     * @param size 每页大小
     * @return 收藏分页列表
     */
    PageResult<FavoriteDTO> getFavoritesByFolder(Long userId, String folderName, Integer page, Integer size);
    
    /**
     * 获取用户的收藏夹列表
     * 
     * @param userId 用户ID
     * @return 收藏夹名称列表
     */
    List<String> getUserFolders(Long userId);
    
    /**
     * 创建收藏夹
     * 
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @return 是否成功
     */
    boolean createFolder(Long userId, String folderName);
    
    /**
     * 重命名收藏夹
     * 
     * @param userId 用户ID
     * @param oldFolderName 旧收藏夹名称
     * @param newFolderName 新收藏夹名称
     * @return 是否成功
     */
    boolean renameFolder(Long userId, String oldFolderName, String newFolderName);
    
    /**
     * 删除收藏夹
     * 
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @return 是否成功
     */
    boolean deleteFolder(Long userId, String folderName);
    
    /**
     * 移动收藏到指定收藏夹
     * 
     * @param userId 用户ID
     * @param favoriteId 收藏ID
     * @param newFolderName 新收藏夹名称
     * @return 是否成功
     */
    boolean moveFavoriteToFolder(Long userId, Long favoriteId, String newFolderName);
    
    /**
     * 更新收藏备注
     * 
     * @param userId 用户ID
     * @param favoriteId 收藏ID
     * @param notes 新备注
     * @return 是否成功
     */
    boolean updateFavoriteNotes(Long userId, Long favoriteId, String notes);
    
    /**
     * 批量添加收藏
     * 
     * @param userId 用户ID
     * @param requests 收藏请求列表
     * @return 收藏记录列表
     */
    List<FavoriteDTO> batchAddFavorites(Long userId, List<FavoriteRequest> requests);
    
    /**
     * 统计用户收藏数量
     * 
     * @param userId 用户ID
     * @param targetType 收藏类型（可选）
     * @return 收藏数量
     */
    long countUserFavorites(Long userId, String targetType);
    
    /**
     * 统计目标的收藏数量
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 收藏数量
     */
    long countTargetFavorites(String targetId, String targetType);
    
    /**
     * 获取最近收藏的内容
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近收藏列表
     */
    List<FavoriteDTO> getRecentFavorites(Long userId, Integer limit);
    
    /**
     * 删除用户的所有收藏
     *
     * @param userId 用户ID
     * @return 删除的记录数量
     */
    long deleteUserFavorites(Long userId);

    /**
     * 获取收藏夹统计信息
     *
     * @param userId 用户ID
     * @return 收藏夹统计信息
     */
    java.util.Map<String, Object> getFolderStats(Long userId);

    /**
     * 获取收藏夹统计信息DTO
     *
     * @param userId 用户ID
     * @return 收藏夹统计信息DTO
     */
    com.poem.education.dto.response.FolderStatsDTO getFolderStatsDTO(Long userId);
}
// {{END_MODIFICATIONS}}
