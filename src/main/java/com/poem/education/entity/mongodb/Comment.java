// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "0e54f46e-6ced-46bf-9b54-3a6819f266b3"
//   Timestamp: "2025-08-07T11:25:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "MongoDB实体映射最佳实践，严格按照集合验证规则定义"
//   Quality_Check: "编译通过，字段映射与MongoDB集合验证规则完全一致。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 评论实体类
 * 对应MongoDB集合：comments
 * 严格按照集合验证规则定义
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Document(collection = "comments")
@CompoundIndexes({
    @CompoundIndex(name = "target_compound_1", def = "{'targetId': 1, 'targetType': 1}"),
    @CompoundIndex(name = "target_time_1", def = "{'targetId': 1, 'targetType': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "status_time_1", def = "{'status': 1, 'createdAt': -1}")
})
public class Comment {
    
    /**
     * 主键ID
     * MongoDB ObjectId
     */
    @Id
    private String id;
    
    /**
     * 目标ID
     * 必填字段，ObjectId类型
     */
    @NotBlank(message = "目标ID不能为空")
    @Field("targetId")
    private String targetId;
    
    /**
     * 目标类型
     * 必填字段，枚举值：guwen/creation/sentence/writer
     */
    @NotBlank(message = "目标类型不能为空")
    @Pattern(regexp = "^(guwen|creation|sentence|writer)$", message = "目标类型必须为：guwen、creation、sentence、writer之一")
    @Field("targetType")
    private String targetType;
    
    /**
     * 用户ID
     * 必填字段，长整型
     */
    @NotNull(message = "用户ID不能为空")
    @Indexed(name = "userId_1")
    @Field("userId")
    private Long userId;
    
    /**
     * 评论内容
     * 必填字段，长度1-1000字符
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000字符之间")
    @Field("content")
    private String content;
    
    /**
     * 评论层级
     * 可选字段，范围1-10
     */
    @Min(value = 1, message = "评论层级最小为1")
    @Max(value = 10, message = "评论层级最大为10")
    @Field("level")
    private Integer level;
    
    /**
     * 状态
     * 必填字段，枚举值：0（隐藏）、1（显示）
     */
    @NotNull(message = "状态不能为空")
    @Pattern(regexp = "^[01]$", message = "状态必须为0或1")
    @Field("status")
    private Integer status;
    
    /**
     * 点赞数
     * 可选字段，最小值0
     */
    @Min(value = 0, message = "点赞数不能为负数")
    @Field("likeCount")
    private Integer likeCount = 0;
    
    /**
     * 回复数
     * 可选字段，最小值0
     */
    @Min(value = 0, message = "回复数不能为负数")
    @Field("replyCount")
    private Integer replyCount = 0;
    
    /**
     * 父评论ID
     * 可选字段，用于层级评论
     */
    @Indexed(name = "parentId_1")
    @Field("parentId")
    private String parentId;
    
    /**
     * 评论路径
     * 用于高效查询层级评论，格式如：/root/child1/child2
     */
    @Indexed(name = "path_1")
    @Field("path")
    private String path;
    
    /**
     * 创建时间
     */
    @Field("createdAt")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Field("updatedAt")
    private LocalDateTime updatedAt;
    
    // 默认构造函数
    public Comment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public Comment(String targetId, String targetType, Long userId, String content, Integer status) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.userId = userId;
        this.content = content;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter and Setter methods
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getReplyCount() {
        return replyCount;
    }
    
    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
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
        return "Comment{" +
                "id='" + id + '\'' +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", level=" + level +
                ", status=" + status +
                ", likeCount=" + likeCount +
                ", replyCount=" + replyCount +
                ", parentId='" + parentId + '\'' +
                ", path='" + path + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    /**
     * 目标类型枚举
     */
    public static class TargetType {
        public static final String GUWEN = "guwen";
        public static final String CREATION = "creation";
        public static final String SENTENCE = "sentence";
        public static final String WRITER = "writer";
    }
    
    /**
     * 状态枚举
     */
    public static class Status {
        public static final Integer HIDDEN = 0;  // 隐藏
        public static final Integer VISIBLE = 1; // 显示
    }
}
// {{END_MODIFICATIONS}}
