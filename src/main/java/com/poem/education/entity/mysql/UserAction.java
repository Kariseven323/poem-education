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
 * 用户行为记录实体类
 * 对应数据库表：user_actions
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Entity
@Table(name = "user_actions", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_target", columnList = "target_id, target_type"),
    @Index(name = "idx_action_type", columnList = "action_type"),
    @Index(name = "idx_created_at", columnList = "created_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_target_action", 
                     columnNames = {"user_id", "target_id", "target_type", "action_type"})
})
public class UserAction {
    
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
     * 目标类型
     * 枚举值：guwen/creation/comment/sentence
     */
    @NotBlank(message = "目标类型不能为空")
    @Size(max = 50, message = "目标类型长度不能超过50个字符")
    @Column(name = "target_type", length = 50, nullable = false)
    private String targetType;
    
    /**
     * 行为类型
     * 枚举值：like/favorite/view/share
     */
    @NotBlank(message = "行为类型不能为空")
    @Size(max = 50, message = "行为类型长度不能超过50个字符")
    @Column(name = "action_type", length = 50, nullable = false)
    private String actionType;
    
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
    }
    
    // 默认构造函数
    public UserAction() {
    }
    
    // 构造函数
    public UserAction(Long userId, String targetId, String targetType, String actionType) {
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.actionType = actionType;
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
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
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
        return "UserAction{" +
                "id=" + id +
                ", userId=" + userId +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", actionType='" + actionType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    /**
     * 目标类型枚举
     */
    public static class TargetType {
        public static final String GUWEN = "guwen";
        public static final String CREATION = "creation";
        public static final String COMMENT = "comment";
        public static final String SENTENCE = "sentence";
    }
    
    /**
     * 行为类型枚举
     */
    public static class ActionType {
        public static final String LIKE = "like";
        public static final String FAVORITE = "favorite";
        public static final String VIEW = "view";
        public static final String SHARE = "share";
    }
}
// {{END_MODIFICATIONS}}
