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

import com.poem.education.entity.mongodb.Creation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创作Repository接口
 * 提供创作相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface CreationRepository extends MongoRepository<Creation, String> {
    
    /**
     * 根据用户ID查找创作列表
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和状态查找创作列表
     * 
     * @param userId 用户ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);
    
    /**
     * 根据状态查找创作列表
     * 
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据风格查找创作列表
     * 
     * @param style 风格
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByStyle(String style, Pageable pageable);
    
    /**
     * 根据风格和状态查找创作列表
     * 
     * @param style 风格
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByStyleAndStatus(String style, Integer status, Pageable pageable);
    
    /**
     * 根据标题模糊查询创作
     * 
     * @param title 标题关键字
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    /**
     * 根据内容模糊查询创作
     *
     * @param content 内容关键字
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByContentContainingIgnoreCase(String content, Pageable pageable);

    /**
     * 根据关键词搜索创作（标题或内容包含关键词）
     *
     * @param keyword 关键词
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    @Query("{ $and: [ " +
           "{ $or: [ " +
           "  { 'title': { $regex: ?0, $options: 'i' } }, " +
           "  { 'content': { $regex: ?0, $options: 'i' } } " +
           "] }, " +
           "{ 'status': ?1 } " +
           "] }")
    Page<Creation> searchByKeyword(String keyword, Integer status, Pageable pageable);

    /**
     * 根据关键词和风格搜索创作
     *
     * @param keyword 关键词
     * @param style 风格
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    @Query("{ $and: [ " +
           "{ $or: [ " +
           "  { 'title': { $regex: ?0, $options: 'i' } }, " +
           "  { 'content': { $regex: ?0, $options: 'i' } } " +
           "] }, " +
           "{ 'style': ?1 }, " +
           "{ 'status': ?2 } " +
           "] }")
    Page<Creation> searchByKeywordAndStyle(String keyword, String style, Integer status, Pageable pageable);
    
    /**
     * 统计用户的创作数量
     * 
     * @param userId 用户ID
     * @return 创作数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户特定状态的创作数量
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 创作数量
     */
    long countByUserIdAndStatus(Long userId, Integer status);
    
    /**
     * 统计特定状态的创作数量
     * 
     * @param status 状态
     * @return 创作数量
     */
    long countByStatus(Integer status);
    
    /**
     * 统计特定风格的创作数量
     * 
     * @param style 风格
     * @return 创作数量
     */
    long countByStyle(String style);
    
    /**
     * 查找指定时间范围内的创作
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查找用户在指定时间范围内的创作
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * 查找最新创作
     * 
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByStatusOrderByCreatedAtDesc(Integer status, Pageable pageable);
    
    /**
     * 查找用户最新创作
     * 
     * @param userId 用户ID
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Integer status, Pageable pageable);
    
    /**
     * 查找高分创作（按AI评分排序）
     * 
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    @Query("{ 'status': ?0, 'aiScore.totalScore': { $exists: true } }")
    Page<Creation> findByStatusOrderByAiScoreTotalScoreDesc(Integer status, Pageable pageable);
    
    /**
     * 查找AI评分大于指定分数的创作
     * 
     * @param minScore 最低分数
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    @Query("{ 'status': ?1, 'aiScore.totalScore': { $gte: ?0 } }")
    Page<Creation> findByAiScoreGreaterThanAndStatus(Integer minScore, Integer status, Pageable pageable);
    
    /**
     * 全文搜索创作
     * 使用MongoDB的文本索引进行搜索
     * 
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    @Query("{ $text: { $search: ?0 } }")
    Page<Creation> findByTextSearch(String keyword, Pageable pageable);
    
    /**
     * 高级搜索创作
     * 支持多条件组合搜索
     * 
     * @param title 标题关键字
     * @param content 内容关键字
     * @param style 风格
     * @param status 状态
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'title': { $exists: false } } ] }, " +
           "{ $or: [ { 'content': { $regex: ?1, $options: 'i' } }, { 'content': { $exists: false } } ] }, " +
           "{ $or: [ { 'style': ?2 }, { 'style': { $exists: false } } ] }, " +
           "{ $or: [ { 'status': ?3 }, { 'status': { $exists: false } } ] } " +
           "] }")
    Page<Creation> findByAdvancedSearch(String title, String content, String style, Integer status, Pageable pageable);
    
    /**
     * 查找随机创作
     * 
     * @param status 状态
     * @param size 数量
     * @return 创作列表
     */
    @Query("{ 'status': ?0 }")
    List<Creation> findRandomCreations(Integer status, int size);
    
    /**
     * 删除用户的所有创作
     * 
     * @param userId 用户ID
     * @return 删除的创作数量
     */
    long deleteByUserId(Long userId);
    
    /**
     * 查找用户最近的创作
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 创作列表
     */
    List<Creation> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 查找待审核的创作
     * 
     * @param pageable 分页参数
     * @return 创作分页列表
     */
    Page<Creation> findByStatusOrderByCreatedAtAsc(Integer status, Pageable pageable);
    
    /**
     * 统计用户在指定时间范围内的创作数量
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 创作数量
     */
    long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找所有风格列表
     * 
     * @return 风格列表
     */
    @Query(value = "{ 'style': { $exists: true, $ne: null } }", fields = "{ 'style' : 1 }")
    List<Creation> findAllStyles();
}
// {{END_MODIFICATIONS}}
