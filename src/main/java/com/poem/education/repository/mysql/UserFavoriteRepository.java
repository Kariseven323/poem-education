// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "f00f17a5-84b0-45ee-9828-816019892137"
//   Timestamp: "2025-08-07T11:15:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Data JPA Repository最佳实践"
//   Quality_Check: "编译通过，查询方法符合业务需求。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.repository.mysql;

import com.poem.education.entity.mysql.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户收藏Repository接口
 * 提供用户收藏相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    
    /**
     * 查找用户对特定目标的收藏记录
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 收藏记录
     */
    Optional<UserFavorite> findByUserIdAndTargetIdAndTargetType(Long userId, String targetId, String targetType);
    
    /**
     * 检查用户是否收藏了特定目标
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 是否已收藏
     */
    boolean existsByUserIdAndTargetIdAndTargetType(Long userId, String targetId, String targetType);
    
    /**
     * 查找用户的所有收藏
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 收藏分页列表
     */
    Page<UserFavorite> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 查找用户特定类型的收藏
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param pageable 分页参数
     * @return 收藏分页列表
     */
    Page<UserFavorite> findByUserIdAndTargetType(Long userId, String targetType, Pageable pageable);
    
    /**
     * 查找用户特定收藏夹的收藏
     *
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @param pageable 分页参数
     * @return 收藏分页列表
     */
    Page<UserFavorite> findByUserIdAndFolderName(Long userId, String folderName, Pageable pageable);

    /**
     * 查找用户特定收藏夹的收藏（不分页）
     *
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @return 收藏列表
     */
    List<UserFavorite> findByUserIdAndFolderName(Long userId, String folderName);
    
    /**
     * 查找用户特定收藏夹和类型的收藏
     * 
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @param targetType 目标类型
     * @param pageable 分页参数
     * @return 收藏分页列表
     */
    Page<UserFavorite> findByUserIdAndFolderNameAndTargetType(
            Long userId, String folderName, String targetType, Pageable pageable);
    
    /**
     * 查找用户的收藏夹列表
     *
     * @param userId 用户ID
     * @return 收藏夹名称列表
     */
    @Query("SELECT DISTINCT uf.folderName FROM UserFavorite uf WHERE uf.userId = :userId ORDER BY uf.folderName")
    List<String> findFolderNamesByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否有指定收藏夹
     *
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @return 是否存在
     */
    boolean existsByUserIdAndFolderName(Long userId, String folderName);

    /**
     * 根据ID和用户ID查找收藏
     *
     * @param id 收藏ID
     * @param userId 用户ID
     * @return 收藏记录
     */
    Optional<UserFavorite> findByIdAndUserId(Long id, Long userId);
    
    /**
     * 统计用户的收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户特定类型的收藏数量
     * 
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 收藏数量
     */
    long countByUserIdAndTargetType(Long userId, String targetType);
    
    /**
     * 统计用户特定收藏夹的收藏数量
     * 
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @return 收藏数量
     */
    long countByUserIdAndFolderName(Long userId, String folderName);
    
    /**
     * 统计特定目标的收藏数量
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 收藏数量
     */
    long countByTargetIdAndTargetType(String targetId, String targetType);
    
    /**
     * 删除用户对特定目标的收藏
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 删除的记录数
     */
    long deleteByUserIdAndTargetIdAndTargetType(Long userId, String targetId, String targetType);
    
    /**
     * 删除用户的所有收藏
     * 
     * @param userId 用户ID
     * @return 删除的记录数
     */
    long deleteByUserId(Long userId);
    
    /**
     * 删除用户特定收藏夹的所有收藏
     * 
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @return 删除的记录数
     */
    long deleteByUserIdAndFolderName(Long userId, String folderName);
    
    /**
     * 查找用户在指定时间范围内的收藏
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 收藏分页列表
     */
    Page<UserFavorite> findByUserIdAndCreatedAtBetween(
            Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查找最受欢迎的收藏内容
     * 
     * @param targetType 目标类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 热门收藏列表
     */
    @Query("SELECT uf.targetId, COUNT(uf) as favoriteCount " +
           "FROM UserFavorite uf " +
           "WHERE uf.targetType = :targetType " +
           "AND uf.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY uf.targetId " +
           "ORDER BY favoriteCount DESC")
    List<Object[]> findPopularFavorites(
            @Param("targetType") String targetType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 查找用户最近的收藏
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 收藏列表
     */
    List<UserFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 批量更新收藏夹名称
     *
     * @param userId 用户ID
     * @param oldFolderName 旧收藏夹名称
     * @param newFolderName 新收藏夹名称
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE UserFavorite uf SET uf.folderName = :newFolderName " +
           "WHERE uf.userId = :userId AND uf.folderName = :oldFolderName")
    int updateFolderName(@Param("userId") Long userId,
                        @Param("oldFolderName") String oldFolderName,
                        @Param("newFolderName") String newFolderName);

    /**
     * 按收藏夹分组统计用户收藏数量
     *
     * @param userId 用户ID
     * @return 收藏夹统计信息列表，每个元素包含[folderName, count]
     */
    @Query("SELECT uf.folderName, COUNT(uf) as favoriteCount " +
           "FROM UserFavorite uf " +
           "WHERE uf.userId = :userId AND uf.targetType != 'folder' " +
           "GROUP BY uf.folderName " +
           "ORDER BY uf.folderName")
    List<Object[]> findFolderStatsGroupByFolderName(@Param("userId") Long userId);

    /**
     * 统计用户特定收藏夹中非占位符记录的数量
     *
     * @param userId 用户ID
     * @param folderName 收藏夹名称
     * @return 真实收藏数量（排除占位符）
     */
    @Query("SELECT COUNT(uf) FROM UserFavorite uf " +
           "WHERE uf.userId = :userId AND uf.folderName = :folderName AND uf.targetType != 'folder'")
    long countRealFavoritesByUserIdAndFolderName(@Param("userId") Long userId, @Param("folderName") String folderName);
}
// {{END_MODIFICATIONS}}
