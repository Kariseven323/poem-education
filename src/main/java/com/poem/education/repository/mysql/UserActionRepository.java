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

import com.poem.education.entity.mysql.UserAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户行为Repository接口
 * 提供用户行为相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    
    /**
     * 查找用户对特定目标的特定行为
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 用户行为记录
     */
    Optional<UserAction> findByUserIdAndTargetIdAndTargetTypeAndActionType(
            Long userId, String targetId, String targetType, String actionType);
    
    /**
     * 检查用户是否对特定目标执行了特定行为
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 是否存在
     */
    boolean existsByUserIdAndTargetIdAndTargetTypeAndActionType(
            Long userId, String targetId, String targetType, String actionType);
    
    /**
     * 查找用户的所有行为记录
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 用户行为分页列表
     */
    Page<UserAction> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 查找用户的特定类型行为记录
     * 
     * @param userId 用户ID
     * @param actionType 行为类型
     * @param pageable 分页参数
     * @return 用户行为分页列表
     */
    Page<UserAction> findByUserIdAndActionType(Long userId, String actionType, Pageable pageable);
    
    /**
     * 查找特定目标的所有行为记录
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param pageable 分页参数
     * @return 用户行为分页列表
     */
    Page<UserAction> findByTargetIdAndTargetType(String targetId, String targetType, Pageable pageable);
    
    /**
     * 查找特定目标的特定类型行为记录
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @param pageable 分页参数
     * @return 用户行为分页列表
     */
    Page<UserAction> findByTargetIdAndTargetTypeAndActionType(
            String targetId, String targetType, String actionType, Pageable pageable);
    
    /**
     * 统计用户的所有行为数量
     *
     * @param userId 用户ID
     * @return 行为数量
     */
    long countByUserId(Long userId);

    /**
     * 统计用户的特定行为数量
     *
     * @param userId 用户ID
     * @param actionType 行为类型
     * @return 行为数量
     */
    long countByUserIdAndActionType(Long userId, String actionType);
    
    /**
     * 统计特定目标的特定行为数量
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 行为数量
     */
    long countByTargetIdAndTargetTypeAndActionType(String targetId, String targetType, String actionType);
    
    /**
     * 查找用户在指定时间范围内的行为记录
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 用户行为分页列表
     */
    Page<UserAction> findByUserIdAndCreatedAtBetween(
            Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查找指定时间范围内的行为记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 用户行为分页列表
     */
    Page<UserAction> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 删除用户对特定目标的特定行为
     * 
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @return 删除的记录数
     */
    long deleteByUserIdAndTargetIdAndTargetTypeAndActionType(
            Long userId, String targetId, String targetType, String actionType);
    
    /**
     * 删除用户的所有行为记录
     * 
     * @param userId 用户ID
     * @return 删除的记录数
     */
    long deleteByUserId(Long userId);
    
    /**
     * 查找热门内容（按行为数量排序）
     * 
     * @param targetType 目标类型
     * @param actionType 行为类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 热门内容列表
     */
    @Query("SELECT ua.targetId, COUNT(ua) as actionCount " +
           "FROM UserAction ua " +
           "WHERE ua.targetType = :targetType " +
           "AND ua.actionType = :actionType " +
           "AND ua.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY ua.targetId " +
           "ORDER BY actionCount DESC")
    List<Object[]> findHotContentsByAction(
            @Param("targetType") String targetType,
            @Param("actionType") String actionType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 查找用户最近的行为记录
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户行为列表
     */
    List<UserAction> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 统计用户在指定时间范围内的行为数量
     *
     * @param userId 用户ID
     * @param actionType 行为类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 行为数量
     */
    long countByUserIdAndActionTypeAndCreatedAtBetween(
            Long userId, String actionType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定时间之后的特定行为类型数量
     *
     * @param actionType 行为类型
     * @param startTime 开始时间
     * @return 行为数量
     */
    long countByActionTypeAndCreatedAtAfter(String actionType, LocalDateTime startTime);
}
// {{END_MODIFICATIONS}}
