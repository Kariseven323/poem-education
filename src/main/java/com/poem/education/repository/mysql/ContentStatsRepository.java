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

import com.poem.education.entity.mysql.ContentStats;
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
 * 内容统计Repository接口
 * 提供内容统计相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface ContentStatsRepository extends JpaRepository<ContentStats, Long> {
    
    /**
     * 根据内容ID和类型查找统计信息
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 统计信息
     */
    Optional<ContentStats> findByContentIdAndContentType(String contentId, String contentType);
    
    /**
     * 检查内容统计是否存在
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 是否存在
     */
    boolean existsByContentIdAndContentType(String contentId, String contentType);
    
    /**
     * 根据内容类型查找统计信息
     * 
     * @param contentType 内容类型
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    Page<ContentStats> findByContentType(String contentType, Pageable pageable);
    
    /**
     * 根据浏览量排序查找热门内容
     * 
     * @param contentType 内容类型
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    Page<ContentStats> findByContentTypeOrderByViewCountDesc(String contentType, Pageable pageable);
    
    /**
     * 根据点赞量排序查找热门内容
     * 
     * @param contentType 内容类型
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    Page<ContentStats> findByContentTypeOrderByLikeCountDesc(String contentType, Pageable pageable);
    
    /**
     * 根据收藏量排序查找热门内容
     * 
     * @param contentType 内容类型
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    Page<ContentStats> findByContentTypeOrderByFavoriteCountDesc(String contentType, Pageable pageable);
    
    /**
     * 根据热度分数排序查找热门内容
     * 
     * @param contentType 内容类型
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    @Query("SELECT cs FROM ContentStats cs WHERE cs.contentType = :contentType " +
           "ORDER BY (cs.viewCount * 1 + cs.likeCount * 3 + cs.favoriteCount * 5 + cs.commentCount * 2) DESC")
    Page<ContentStats> findHotContentsByType(@Param("contentType") String contentType, Pageable pageable);
    
    /**
     * 查找浏览量大于指定值的内容
     * 
     * @param minViewCount 最小浏览量
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    Page<ContentStats> findByViewCountGreaterThan(Long minViewCount, Pageable pageable);
    
    /**
     * 查找点赞量大于指定值的内容
     * 
     * @param minLikeCount 最小点赞量
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    Page<ContentStats> findByLikeCountGreaterThan(Long minLikeCount, Pageable pageable);
    
    /**
     * 统计特定类型内容的总数
     * 
     * @param contentType 内容类型
     * @return 内容总数
     */
    long countByContentType(String contentType);
    
    /**
     * 统计特定类型内容的总浏览量
     * 
     * @param contentType 内容类型
     * @return 总浏览量
     */
    @Query("SELECT SUM(cs.viewCount) FROM ContentStats cs WHERE cs.contentType = :contentType")
    Long sumViewCountByContentType(@Param("contentType") String contentType);
    
    /**
     * 统计特定类型内容的总点赞量
     * 
     * @param contentType 内容类型
     * @return 总点赞量
     */
    @Query("SELECT SUM(cs.likeCount) FROM ContentStats cs WHERE cs.contentType = :contentType")
    Long sumLikeCountByContentType(@Param("contentType") String contentType);
    
    /**
     * 更新内容的浏览量
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param increment 增量
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE ContentStats cs SET cs.viewCount = cs.viewCount + :increment, " +
           "cs.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE cs.contentId = :contentId AND cs.contentType = :contentType")
    int incrementViewCount(@Param("contentId") String contentId, 
                          @Param("contentType") String contentType, 
                          @Param("increment") Long increment);
    
    /**
     * 更新内容的点赞量
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param increment 增量
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE ContentStats cs SET cs.likeCount = cs.likeCount + :increment, " +
           "cs.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE cs.contentId = :contentId AND cs.contentType = :contentType")
    int incrementLikeCount(@Param("contentId") String contentId, 
                          @Param("contentType") String contentType, 
                          @Param("increment") Long increment);
    
    /**
     * 更新内容的收藏量
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param increment 增量
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE ContentStats cs SET cs.favoriteCount = cs.favoriteCount + :increment, " +
           "cs.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE cs.contentId = :contentId AND cs.contentType = :contentType")
    int incrementFavoriteCount(@Param("contentId") String contentId, 
                              @Param("contentType") String contentType, 
                              @Param("increment") Long increment);
    
    /**
     * 更新内容的评论量
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param increment 增量
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE ContentStats cs SET cs.commentCount = cs.commentCount + :increment, " +
           "cs.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE cs.contentId = :contentId AND cs.contentType = :contentType")
    int incrementCommentCount(@Param("contentId") String contentId,
                             @Param("contentType") String contentType,
                             @Param("increment") Long increment);

    /**
     * 更新内容的分享量
     *
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @param increment 增量
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE ContentStats cs SET cs.shareCount = cs.shareCount + :increment, " +
           "cs.lastUpdated = CURRENT_TIMESTAMP " +
           "WHERE cs.contentId = :contentId AND cs.contentType = :contentType")
    int incrementShareCount(@Param("contentId") String contentId,
                           @Param("contentType") String contentType,
                           @Param("increment") Long increment);

    /**
     * 查找最近更新的内容统计
     * 
     * @param since 时间点
     * @param pageable 分页参数
     * @return 统计信息分页列表
     */
    Page<ContentStats> findByLastUpdatedAfter(LocalDateTime since, Pageable pageable);
    
    /**
     * 删除特定内容的统计信息
     * 
     * @param contentId 内容ID
     * @param contentType 内容类型
     * @return 删除的记录数
     */
    long deleteByContentIdAndContentType(String contentId, String contentType);
    
    /**
     * 查找综合热度最高的内容
     * 
     * @param limit 限制数量
     * @return 热门内容列表
     */
    @Query("SELECT cs FROM ContentStats cs " +
           "WHERE cs.viewCount > 0 " +
           "ORDER BY (cs.viewCount * 1 + cs.likeCount * 3 + cs.favoriteCount * 5 + cs.commentCount * 2) DESC")
    List<ContentStats> findTopHotContents(Pageable pageable);
}
// {{END_MODIFICATIONS}}
