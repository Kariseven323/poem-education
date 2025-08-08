// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "cbae72cf-b030-48a2-b381-ce2a1484c281"
//   Timestamp: "2025-08-07T12:15:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service实现最佳实践，严格按照数据库表结构"
//   Quality_Check: "编译通过，业务逻辑完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.poem.education.dto.request.FavoriteRequest;
import com.poem.education.dto.response.FavoriteDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.entity.mysql.UserFavorite;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mysql.UserFavoriteRepository;
import com.poem.education.service.FavoriteService;
import com.poem.education.constant.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Service
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {
    
    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);
    
    @Autowired
    private UserFavoriteRepository userFavoriteRepository;
    
    @Override
    @Transactional
    public FavoriteDTO addFavorite(Long userId, FavoriteRequest request) {
        logger.info("添加收藏: userId={}, request={}", userId, request);
        
        // 检查是否已收藏
        Optional<UserFavorite> existingFavorite = userFavoriteRepository
                .findByUserIdAndTargetIdAndTargetType(userId, request.getTargetId(), request.getTargetType());
        
        if (existingFavorite.isPresent()) {
            // 如果已收藏，返回现有记录
            return convertToDTO(existingFavorite.get());
        }
        
        // 创建新的收藏记录
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserId(userId);
        userFavorite.setTargetId(request.getTargetId());
        userFavorite.setTargetType(request.getTargetType());
        userFavorite.setFolderName(request.getFolderName());
        userFavorite.setNotes(request.getNotes());
        
        UserFavorite savedFavorite = userFavoriteRepository.save(userFavorite);
        
        return convertToDTO(savedFavorite);
    }
    
    @Override
    @Transactional
    public boolean removeFavorite(Long userId, String targetId, String targetType) {
        logger.info("取消收藏: userId={}, targetId={}, targetType={}", userId, targetId, targetType);
        
        Optional<UserFavorite> favoriteOptional = userFavoriteRepository
                .findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
        
        if (favoriteOptional.isPresent()) {
            userFavoriteRepository.delete(favoriteOptional.get());
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean isFavorited(Long userId, String targetId, String targetType) {
        return userFavoriteRepository.existsByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
    }
    
    @Override
    public PageResult<FavoriteDTO> getUserFavorites(Long userId, Integer page, Integer size) {
        logger.info("获取用户收藏列表: userId={}, page={}, size={}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFavorite> favoritePage = userFavoriteRepository.findByUserId(userId, pageable);
        
        List<FavoriteDTO> favoriteDTOList = favoritePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(favoriteDTOList, page, size, favoritePage.getTotalElements());
    }
    
    @Override
    public PageResult<FavoriteDTO> getUserFavoritesByType(Long userId, String targetType, Integer page, Integer size) {
        logger.info("根据类型获取用户收藏: userId={}, targetType={}, page={}, size={}", 
                   userId, targetType, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFavorite> favoritePage = userFavoriteRepository.findByUserIdAndTargetType(userId, targetType, pageable);
        
        List<FavoriteDTO> favoriteDTOList = favoritePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(favoriteDTOList, page, size, favoritePage.getTotalElements());
    }
    
    @Override
    public PageResult<FavoriteDTO> getFavoritesByFolder(Long userId, String folderName, Integer page, Integer size) {
        logger.info("根据收藏夹获取收藏: userId={}, folderName={}, page={}, size={}", 
                   userId, folderName, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFavorite> favoritePage = userFavoriteRepository.findByUserIdAndFolderName(userId, folderName, pageable);
        
        List<FavoriteDTO> favoriteDTOList = favoritePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(favoriteDTOList, page, size, favoritePage.getTotalElements());
    }
    
    @Override
    public List<String> getUserFolders(Long userId) {
        logger.info("获取用户收藏夹列表: userId={}", userId);
        
        return userFavoriteRepository.findFolderNamesByUserId(userId);
    }
    
    @Override
    @Transactional
    public boolean createFolder(Long userId, String folderName) {
        logger.info("创建收藏夹: userId={}, folderName={}", userId, folderName);

        // 验证收藏夹名称
        if (folderName == null || folderName.trim().isEmpty()) {
            logger.warn("收藏夹名称不能为空: userId={}", userId);
            return false;
        }

        // 检查收藏夹是否已存在
        boolean exists = userFavoriteRepository.existsByUserIdAndFolderName(userId, folderName.trim());
        if (exists) {
            logger.info("收藏夹已存在: userId={}, folderName={}", userId, folderName);
            return false; // 收藏夹已存在
        }

        // 创建一个占位符收藏记录来表示空收藏夹
        // 使用特殊的targetId和targetType来标识这是一个收藏夹占位符
        UserFavorite placeholder = new UserFavorite();
        placeholder.setUserId(userId);
        placeholder.setTargetId("FOLDER_PLACEHOLDER");
        placeholder.setTargetType("folder");
        placeholder.setFolderName(folderName.trim());
        placeholder.setNotes("收藏夹占位符，用于标识空收藏夹");

        userFavoriteRepository.save(placeholder);

        logger.info("成功创建收藏夹: userId={}, folderName={}", userId, folderName);
        return true;
    }
    
    @Override
    @Transactional
    public boolean renameFolder(Long userId, String oldFolderName, String newFolderName) {
        logger.info("重命名收藏夹: userId={}, oldFolderName={}, newFolderName={}", 
                   userId, oldFolderName, newFolderName);
        
        List<UserFavorite> favorites = userFavoriteRepository.findByUserIdAndFolderName(userId, oldFolderName);
        
        if (favorites.isEmpty()) {
            return false; // 收藏夹不存在
        }
        
        // 更新所有该收藏夹下的收藏记录
        favorites.forEach(favorite -> favorite.setFolderName(newFolderName));
        userFavoriteRepository.saveAll(favorites);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean deleteFolder(Long userId, String folderName) {
        logger.info("删除收藏夹: userId={}, folderName={}", userId, folderName);

        // 检查是否为默认收藏夹，默认收藏夹不可删除
        if ("默认收藏夹".equals(folderName)) {
            logger.warn("尝试删除默认收藏夹: userId={}", userId);
            return false;
        }

        // 删除该收藏夹下的所有收藏
        long deletedCount = userFavoriteRepository.deleteByUserIdAndFolderName(userId, folderName);

        return deletedCount > 0;
    }
    
    @Override
    @Transactional
    public boolean moveFavoriteToFolder(Long userId, Long favoriteId, String newFolderName) {
        logger.info("移动收藏到收藏夹: userId={}, favoriteId={}, newFolderName={}", 
                   userId, favoriteId, newFolderName);
        
        Optional<UserFavorite> favoriteOptional = userFavoriteRepository.findByIdAndUserId(favoriteId, userId);
        
        if (favoriteOptional.isPresent()) {
            UserFavorite favorite = favoriteOptional.get();
            favorite.setFolderName(newFolderName);
            userFavoriteRepository.save(favorite);
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional
    public boolean updateFavoriteNotes(Long userId, Long favoriteId, String notes) {
        logger.info("更新收藏备注: userId={}, favoriteId={}, notes={}", userId, favoriteId, notes);
        
        Optional<UserFavorite> favoriteOptional = userFavoriteRepository.findByIdAndUserId(favoriteId, userId);
        
        if (favoriteOptional.isPresent()) {
            UserFavorite favorite = favoriteOptional.get();
            favorite.setNotes(notes);
            userFavoriteRepository.save(favorite);
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional
    public List<FavoriteDTO> batchAddFavorites(Long userId, List<FavoriteRequest> requests) {
        logger.info("批量添加收藏: userId={}, count={}", userId, requests.size());
        
        return requests.stream()
                .map(request -> addFavorite(userId, request))
                .collect(Collectors.toList());
    }
    
    @Override
    public long countUserFavorites(Long userId, String targetType) {
        if (targetType != null) {
            return userFavoriteRepository.countByUserIdAndTargetType(userId, targetType);
        } else {
            return userFavoriteRepository.countByUserId(userId);
        }
    }
    
    @Override
    public long countTargetFavorites(String targetId, String targetType) {
        return userFavoriteRepository.countByTargetIdAndTargetType(targetId, targetType);
    }
    
    @Override
    public List<FavoriteDTO> getRecentFavorites(Long userId, Integer limit) {
        logger.info("获取最近收藏: userId={}, limit={}", userId, limit);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserFavorite> favoritePage = userFavoriteRepository.findByUserId(userId, pageable);
        
        return favoritePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public long deleteUserFavorites(Long userId) {
        logger.info("删除用户所有收藏: userId={}", userId);

        return userFavoriteRepository.deleteByUserId(userId);
    }

    @Override
    public java.util.Map<String, Object> getFolderStats(Long userId) {
        logger.info("获取收藏夹统计信息: userId={}", userId);

        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // 获取用户所有收藏夹
        List<String> folders = userFavoriteRepository.findFolderNamesByUserId(userId);

        // 统计每个收藏夹的收藏数量
        java.util.Map<String, Long> folderCounts = new java.util.HashMap<>();
        long totalCount = 0;

        for (String folder : folders) {
            // 使用优化的查询方法，直接统计真实收藏数量（排除占位符）
            long realCount = userFavoriteRepository.countRealFavoritesByUserIdAndFolderName(userId, folder);
            folderCounts.put(folder, realCount);
            totalCount += realCount;
        }

        stats.put("totalFolders", folders.size());
        stats.put("totalItems", totalCount);
        stats.put("folderCounts", folderCounts);
        stats.put("folders", folders);

        return stats;
    }
    
    @Override
    public com.poem.education.dto.response.FolderStatsDTO getFolderStatsDTO(Long userId) {
        logger.info("获取收藏夹统计信息DTO: userId={}", userId);

        // 获取用户所有收藏夹
        List<String> folders = userFavoriteRepository.findFolderNamesByUserId(userId);

        // 创建收藏夹详情列表
        List<com.poem.education.dto.response.FolderDTO> folderDetails = new java.util.ArrayList<>();
        java.util.Map<String, Long> folderCounts = new java.util.HashMap<>();
        long totalCount = 0;

        for (String folder : folders) {
            // 使用优化的查询方法，直接统计真实收藏数量（排除占位符）
            long realCount = userFavoriteRepository.countRealFavoritesByUserIdAndFolderName(userId, folder);
            folderCounts.put(folder, realCount);
            totalCount += realCount;

            // 创建FolderDTO
            com.poem.education.dto.response.FolderDTO folderDTO = new com.poem.education.dto.response.FolderDTO(
                folder,
                (int) realCount
            );
            folderDetails.add(folderDTO);
        }

        // 创建统计DTO
        com.poem.education.dto.response.FolderStatsDTO statsDTO = new com.poem.education.dto.response.FolderStatsDTO(
            folders.size(),
            totalCount,
            folderDetails,
            folderCounts,
            folders
        );

        return statsDTO;
    }

    /**
     * 将UserFavorite实体转换为FavoriteDTO
     *
     * @param userFavorite UserFavorite实体
     * @return FavoriteDTO
     */
    private FavoriteDTO convertToDTO(UserFavorite userFavorite) {
        FavoriteDTO dto = new FavoriteDTO();
        BeanUtils.copyProperties(userFavorite, dto);
        return dto;
    }
}
// {{END_MODIFICATIONS}}
