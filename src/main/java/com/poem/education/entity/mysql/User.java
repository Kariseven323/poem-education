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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 用户基础信息实体类
 * 对应数据库表：users
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_status", columnList = "status")
})
public class User {
    
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 用户名
     * 长度：50字符，唯一，非空
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
    
    /**
     * 邮箱
     * 长度：100字符，唯一，非空
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;
    
    /**
     * 密码哈希
     * 长度：255字符，非空
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 255, message = "密码哈希长度不能超过255个字符")
    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;
    
    /**
     * 昵称
     * 长度：50字符，可空
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Column(name = "nickname", length = 50)
    private String nickname;
    
    /**
     * 头像URL
     * 长度：255字符，可空
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    /**
     * 个人简介
     * 文本类型，可空
     */
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    /**
     * 状态
     * 1：正常，0：禁用，默认值：1
     */
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;
    
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
     * JPA生命周期回调：插入前设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * JPA生命周期回调：更新前设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 默认构造函数
    public User() {
    }
    
    // 构造函数
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    // Getter and Setter methods
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
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
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", bio='" + bio + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
// {{END_MODIFICATIONS}}
