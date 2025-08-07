// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "0e54f46e-6ced-46bf-9b54-3a6819f266b3"
//   Timestamp: "2025-08-07T11:25:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Data MongoDB Repository最佳实践"
//   Quality_Check: "编译通过，查询方法符合业务需求。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.repository.mongodb;

import com.poem.education.entity.mongodb.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论Repository接口
 * 提供评论相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    /**
     * 根据目标ID和类型查找评论列表
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByTargetIdAndTargetType(String targetId, String targetType, Pageable pageable);
    
    /**
     * 根据目标ID、类型和状态查找评论列表
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByTargetIdAndTargetTypeAndStatus(String targetId, String targetType, Integer status, Pageable pageable);
    
    /**
     * 根据用户ID查找评论列表
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和状态查找评论列表
     * 
     * @param userId 用户ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);
    
    /**
     * 根据父评论ID查找子评论列表
     * 
     * @param parentId 父评论ID
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByParentId(String parentId, Pageable pageable);
    
    /**
     * 根据父评论ID和状态查找子评论列表
     * 
     * @param parentId 父评论ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByParentIdAndStatus(String parentId, Integer status, Pageable pageable);
    
    /**
     * 根据路径查找层级评论
     * 使用路径前缀匹配查找所有子评论
     * 
     * @param pathPrefix 路径前缀
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByPathStartingWith(String pathPrefix, Pageable pageable);
    
    /**
     * 根据路径和状态查找层级评论
     * 
     * @param pathPrefix 路径前缀
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByPathStartingWithAndStatus(String pathPrefix, Integer status, Pageable pageable);
    
    /**
     * 根据评论层级查找评论
     * 
     * @param level 评论层级
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByLevel(Integer level, Pageable pageable);
    
    /**
     * 根据状态查找评论列表
     * 
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 统计目标的评论数量
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 评论数量
     */
    long countByTargetIdAndTargetType(String targetId, String targetType);
    
    /**
     * 统计目标的有效评论数量
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param status 状态
     * @return 评论数量
     */
    long countByTargetIdAndTargetTypeAndStatus(String targetId, String targetType, Integer status);
    
    /**
     * 统计用户的评论数量
     * 
     * @param userId 用户ID
     * @return 评论数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户的有效评论数量
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 评论数量
     */
    long countByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 统计父评论的回复数量
     * 
     * @param parentId 父评论ID
     * @return 回复数量
     */
    long countByParentId(String parentId);
    
    /**
     * 统计父评论的有效回复数量
     * 
     * @param parentId 父评论ID
     * @param status 状态
     * @return 回复数量
     */
    long countByParentIdAndStatus(String parentId, Integer status);
    
    /**
     * 查找指定时间范围内的评论
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查找用户在指定时间范围内的评论
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查找热门评论（按点赞数排序）
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByTargetIdAndTargetTypeAndStatusOrderByLikeCountDesc(String targetId, String targetType, Integer status, Pageable pageable);
    
    /**
     * 查找最新评论
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    Page<Comment> findByTargetIdAndTargetTypeAndStatusOrderByCreatedAtDesc(String targetId, String targetType, Integer status, Pageable pageable);
    
    /**
     * 删除用户的所有评论
     * 
     * @param userId 用户ID
     * @return 删除的评论数量
     */
    long deleteByUserId(Long userId);
    
    /**
     * 删除目标的所有评论
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 删除的评论数量
     */
    long deleteByTargetIdAndTargetType(String targetId, String targetType);
    
    /**
     * 查找顶级评论（无父评论）
     * 
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param status 状态
     * @param pageable 分页参数
     * @return 评论分页列表
     */
    @Query("{ 'targetId': ?0, 'targetType': ?1, 'status': ?2, 'parentId': { $exists: false } }")
    Page<Comment> findTopLevelComments(String targetId, String targetType, Integer status, Pageable pageable);
    
    /**
     * 查找用户最近的评论
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 评论列表
     */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
// {{END_MODIFICATIONS}}
