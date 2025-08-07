// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "f00f17a5-84b0-45ee-9828-816019892137"
//   Timestamp: "2025-08-07T11:15:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "JPA实体映射最佳实践，严格按照数据库字段定义"
//   Quality_Check: "编译通过，字段映射与数据库表结构完全一致。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.entity.mysql;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 内容统计实体类
 * 对应数据库表：content_stats
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Entity
@Table(name = "content_stats", indexes = {
    @Index(name = "idx_content_type", columnList = "content_type"),
    @Index(name = "idx_view_count", columnList = "view_count"),
    @Index(name = "idx_like_count", columnList = "like_count")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_content", columnNames = {"content_id", "content_type"})
})
public class ContentStats {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 内容ID
     * MongoDB ObjectId字符串(24字符)
     */
    @NotBlank(message = "内容ID不能为空")
    @Size(min = 24, max = 24, message = "内容ID必须为24个字符")
    @Column(name = "content_id", length = 24, nullable = false)
    private String contentId;
    
    /**
     * 内容类型
     * 枚举值：guwen/sentence/writer/creation
     */
    @NotBlank(message = "内容类型不能为空")
    @Size(max = 50, message = "内容类型长度不能超过50个字符")
    @Column(name = "content_type", length = 50, nullable = false)
    private String contentType;
    
    /**
     * 浏览次数
     * 默认值：0
     */
    @Column(name = "view_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;
    
    /**
     * 点赞次数
     * 默认值：0
     */
    @Column(name = "like_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long likeCount = 0L;
    
    /**
     * 收藏次数
     * 默认值：0
     */
    @Column(name = "favorite_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long favoriteCount = 0L;
    
    /**
     * 评论次数
     * 默认值：0
     */
    @Column(name = "comment_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long commentCount = 0L;
    
    /**
     * 分享次数
     * 默认值：0
     */
    @Column(name = "share_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long shareCount = 0L;
    
    /**
     * 最后更新时间
     * 自动更新为当前时间
     */
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    /**
     * JPA生命周期回调：插入前设置最后更新时间
     */
    @PrePersist
    protected void onCreate() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * JPA生命周期回调：更新前设置最后更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    // 默认构造函数
    public ContentStats() {
    }
    
    // 构造函数
    public ContentStats(String contentId, String contentType) {
        this.contentId = contentId;
        this.contentType = contentType;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContentId() {
        return contentId;
    }
    
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public Long getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    
    public Long getFavoriteCount() {
        return favoriteCount;
    }
    
    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
    
    public Long getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
    
    public Long getShareCount() {
        return shareCount;
    }
    
    public void setShareCount(Long shareCount) {
        this.shareCount = shareCount;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * 计算热度分数
     * 公式：浏览数*1 + 点赞数*3 + 收藏数*5 + 评论数*2
     */
    public Long calculateHotScore() {
        return (viewCount * 1) + (likeCount * 3) + (favoriteCount * 5) + (commentCount * 2);
    }
    
    @Override
    public String toString() {
        return "ContentStats{" +
                "id=" + id +
                ", contentId='" + contentId + '\'' +
                ", contentType='" + contentType + '\'' +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", favoriteCount=" + favoriteCount +
                ", commentCount=" + commentCount +
                ", shareCount=" + shareCount +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
    
    /**
     * 内容类型枚举
     */
    public static class ContentType {
        public static final String GUWEN = "guwen";
        public static final String SENTENCE = "sentence";
        public static final String WRITER = "writer";
        public static final String CREATION = "creation";
    }
}
// {{END_MODIFICATIONS}}
