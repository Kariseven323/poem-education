// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "b4a42fdc-2d03-4831-9d30-9278970f029a"
//   Timestamp: "2025-08-07T12:00:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，字段映射完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class CommentDTO {
    
    /**
     * 评论ID（MongoDB ObjectId）
     */
    @JsonProperty("_id")
    private String id;
    
    /**
     * 目标ID
     */
    private String targetId;
    
    /**
     * 目标类型
     */
    private String targetType;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 父评论ID
     */
    private String parentId;
    
    /**
     * 评论层级
     */
    private Integer level;
    
    /**
     * 路径（用于层级查询）
     */
    private String path;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 回复数
     */
    private Integer replyCount;
    
    /**
     * 状态（1:正常 0:删除）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime updatedAt;
    
    /**
     * 用户信息
     */
    private UserInfo userInfo;
    
    /**
     * 子评论列表
     */
    private List<CommentDTO> children;
    
    /**
     * 用户信息内部类
     */
    public static class UserInfo {
        private String nickname;
        private String avatar;
        
        public UserInfo() {
        }
        
        public UserInfo(String nickname, String avatar) {
            this.nickname = nickname;
            this.avatar = avatar;
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
        
        @Override
        public String toString() {
            return "UserInfo{" +
                    "nickname='" + nickname + '\'' +
                    ", avatar='" + avatar + '\'' +
                    '}';
        }
    }
    
    // 默认构造函数
    public CommentDTO() {
    }
    
    // 构造函数
    public CommentDTO(String id, String targetId, String targetType, Long userId, String content) {
        this.id = id;
        this.targetId = targetId;
        this.targetType = targetType;
        this.userId = userId;
        this.content = content;
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
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
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
    
    public UserInfo getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    public List<CommentDTO> getChildren() {
        return children;
    }
    
    public void setChildren(List<CommentDTO> children) {
        this.children = children;
    }
    
    @Override
    public String toString() {
        return "CommentDTO{" +
                "id='" + id + '\'' +
                ", targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", parentId='" + parentId + '\'' +
                ", level=" + level +
                ", path='" + path + '\'' +
                ", likeCount=" + likeCount +
                ", replyCount=" + replyCount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userInfo=" + userInfo +
                ", children=" + (children != null ? children.size() + " items" : "null") +
                '}';
    }
}
// {{END_MODIFICATIONS}}
