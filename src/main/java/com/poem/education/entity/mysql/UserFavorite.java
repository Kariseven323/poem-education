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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 * 对应数据库表：user_favorites
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Entity
@Table(name = "user_favorites", indexes = {
    @Index(name = "idx_user_folder", columnList = "user_id, folder_name"),
    @Index(name = "idx_target_type", columnList = "target_type")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_favorite", 
                     columnNames = {"user_id", "target_id", "target_type"})
})
public class UserFavorite {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 用户ID
     * 外键关联users表
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 目标ID
     * MongoDB ObjectId字符串(24字符)
     */
    @NotBlank(message = "目标ID不能为空")
    @Size(min = 24, max = 24, message = "目标ID必须为24个字符")
    @Column(name = "target_id", length = 24, nullable = false)
    private String targetId;
    
    /**
     * 收藏类型
     * 枚举值：guwen/sentence/writer/creation
     */
    @NotBlank(message = "收藏类型不能为空")
    @Size(max = 50, message = "收藏类型长度不能超过50个字符")
    @Column(name = "target_type", length = 50, nullable = false)
    private String targetType;
    
    /**
     * 收藏夹名称
     * 默认值：默认收藏夹
     */
    @Size(max = 100, message = "收藏夹名称长度不能超过100个字符")
    @Column(name = "folder_name", length = 100)
    private String folderName = "默认收藏夹";
    
    /**
     * 收藏备注
     * 文本类型，可空
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    /**
     * 创建时间
     * 默认当前时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 用户实体关联
     * 多对一关系，延迟加载
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    /**
     * JPA生命周期回调：插入前设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.folderName == null || this.folderName.trim().isEmpty()) {
            this.folderName = "默认收藏夹";
        }
    }
    
    // 默认构造函数
    public UserFavorite() {
    }
    
    // 构造函数
    public UserFavorite(Long userId, String targetId, String targetType) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
    }
    
    // 构造函数（带收藏夹名称）
    public UserFavorite(Long userId, String targetId, String targetType, String folderName) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.folderName = folderName;
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
    
    public String getTargetId() {
        return targetId;
    }
    
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "UserFavorite{" +
                "id=" + id +
                ", userId=" + userId +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", folderName='" + folderName + '\'' +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    /**
     * 收藏类型枚举
     */
    public static class TargetType {
        public static final String GUWEN = "guwen";
        public static final String SENTENCE = "sentence";
        public static final String WRITER = "writer";
    }
}
// {{END_MODIFICATIONS}}
