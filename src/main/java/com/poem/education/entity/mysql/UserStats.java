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
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 用户统计实体类
 * 对应数据库表：user_stats
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Entity
@Table(name = "user_stats", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_stats", columnNames = {"user_id"})
})
public class UserStats {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 用户ID
     * 外键关联users表，唯一
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    /**
     * 总浏览数
     * 默认值：0
     */
    @Column(name = "total_views", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalViews = 0L;
    
    /**
     * 总点赞数
     * 默认值：0
     */
    @Column(name = "total_likes", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalLikes = 0L;
    
    /**
     * 总收藏数
     * 默认值：0
     */
    @Column(name = "total_favorites", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalFavorites = 0L;
    
    /**
     * 总评论数
     * 默认值：0
     */
    @Column(name = "total_comments", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalComments = 0L;
    
    /**
     * 总创作数
     * 默认值：0
     */
    @Column(name = "total_creations", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalCreations = 0L;
    
    /**
     * 学习天数
     * 默认值：0
     */
    @Column(name = "study_days", columnDefinition = "INT DEFAULT 0")
    private Integer studyDays = 0;
    
    /**
     * 最后活跃时间
     * 默认当前时间
     */
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;
    
    /**
     * 创建时间
     * 默认当前时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     * 自动更新为当前时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 用户实体关联
     * 一对一关系，延迟加载
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    /**
     * JPA生命周期回调：插入前设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.lastActiveAt == null) {
            this.lastActiveAt = now;
        }
    }
    
    /**
     * JPA生命周期回调：更新前设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 默认构造函数
    public UserStats() {
    }
    
    // 构造函数
    public UserStats(Long userId) {
        this.userId = userId;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getTotalViews() {
        return totalViews;
    }
    
    public void setTotalViews(Long totalViews) {
        this.totalViews = totalViews;
    }
    
    public Long getTotalLikes() {
        return totalLikes;
    }
    
    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }
    
    public Long getTotalFavorites() {
        return totalFavorites;
    }
    
    public void setTotalFavorites(Long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }
    
    public Long getTotalComments() {
        return totalComments;
    }
    
    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }
    
    public Long getTotalCreations() {
        return totalCreations;
    }
    
    public void setTotalCreations(Long totalCreations) {
        this.totalCreations = totalCreations;
    }
    
    public Integer getStudyDays() {
        return studyDays;
    }
    
    public void setStudyDays(Integer studyDays) {
        this.studyDays = studyDays;
    }
    
    public LocalDateTime getLastActiveAt() {
        return lastActiveAt;
    }
    
    public void setLastActiveAt(LocalDateTime lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "UserStats{" +
                "id=" + id +
                ", userId=" + userId +
                ", totalViews=" + totalViews +
                ", totalLikes=" + totalLikes +
                ", totalFavorites=" + totalFavorites +
                ", totalComments=" + totalComments +
                ", totalCreations=" + totalCreations +
                ", studyDays=" + studyDays +
                ", lastActiveAt=" + lastActiveAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
// {{END_MODIFICATIONS}}
