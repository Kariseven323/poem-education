// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "4634d760-65a3-4128-901c-c09532256a97"
//   Timestamp: "2025-08-08T13:30:50+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照Creation实体定义"
//   Quality_Check: "编译通过，字段映射完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 创作响应DTO
 * 用于返回诗词创作信息
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class CreationDTO {
    
    /**
     * 创作ID（MongoDB ObjectId）
     */
    private String id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 风格
     */
    private String style;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 作者用户名
     */
    private String authorUsername;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * AI评分信息
     */
    private AiScoreDTO aiScore;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 是否公开
     */
    private Boolean isPublic;
    
    // 默认构造函数
    public CreationDTO() {
    }
    
    // Getter and Setter methods
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getStyle() {
        return style;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    
    public String getAuthorUsername() {
        return authorUsername;
    }
    
    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
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
    
    public AiScoreDTO getAiScore() {
        return aiScore;
    }
    
    public void setAiScore(AiScoreDTO aiScore) {
        this.aiScore = aiScore;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    @Override
    public String toString() {
        return "CreationDTO{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", style='" + style + '\'' +
                ", description='" + description + '\'' +
                ", authorId=" + authorId +
                ", authorUsername='" + authorUsername + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", aiScore=" + aiScore +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", isPublic=" + isPublic +
                '}';
    }
    
    /**
     * AI评分DTO嵌套类
     */
    public static class AiScoreDTO {
        /**
         * 总分
         */
        private Integer totalScore;
        
        /**
         * AI反馈建议
         * 按照数据库设计，字段名为feedback
         */
        private String feedback;

        /**
         * 兼容性字段：评分详情
         * @deprecated 使用feedback替代
         */
        @Deprecated
        private String details;
        
        /**
         * 评分时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime scoredAt;
        
        /**
         * 多维度评分
         */
        private ScoreDimensionsDTO dimensions;

        /**
         * AI思考过程
         * 记录AI评分时的思考分析过程
         */
        private String thinkingProcess;
        
        // 构造函数
        public AiScoreDTO() {
        }
        
        // Getter and Setter methods
        public Integer getTotalScore() {
            return totalScore;
        }
        
        public void setTotalScore(Integer totalScore) {
            this.totalScore = totalScore;
        }
        
        public String getFeedback() {
            return feedback;
        }

        public void setFeedback(String feedback) {
            this.feedback = feedback;
        }

        /**
         * 兼容性方法
         * @deprecated 使用getFeedback()替代
         */
        @Deprecated
        public String getDetails() {
            return feedback;  // 返回feedback字段的值
        }

        /**
         * 兼容性方法
         * @deprecated 使用setFeedback()替代
         */
        @Deprecated
        public void setDetails(String details) {
            this.feedback = details;  // 设置到feedback字段
        }
        
        public LocalDateTime getScoredAt() {
            return scoredAt;
        }
        
        public void setScoredAt(LocalDateTime scoredAt) {
            this.scoredAt = scoredAt;
        }
        
        public ScoreDimensionsDTO getDimensions() {
            return dimensions;
        }
        
        public void setDimensions(ScoreDimensionsDTO dimensions) {
            this.dimensions = dimensions;
        }

        public String getThinkingProcess() {
            return thinkingProcess;
        }

        public void setThinkingProcess(String thinkingProcess) {
            this.thinkingProcess = thinkingProcess;
        }

        @Override
        public String toString() {
            return "AiScoreDTO{" +
                    "totalScore=" + totalScore +
                    ", details='" + details + '\'' +
                    ", scoredAt=" + scoredAt +
                    ", dimensions=" + dimensions +
                    ", thinkingProcess='" + thinkingProcess + '\'' +
                    '}';
        }
    }
    
    /**
     * 多维度评分DTO
     */
    public static class ScoreDimensionsDTO {
        /**
         * 韵律评分
         */
        private Integer rhythm;
        
        /**
         * 意象评分
         */
        private Integer imagery;
        
        /**
         * 情感评分
         */
        private Integer emotion;
        
        /**
         * 技法评分
         */
        private Integer technique;
        
        /**
         * 创新评分
         */
        private Integer innovation;
        
        // 构造函数
        public ScoreDimensionsDTO() {
        }
        
        public ScoreDimensionsDTO(Integer rhythm, Integer imagery, Integer emotion, 
                                Integer technique, Integer innovation) {
            this.rhythm = rhythm;
            this.imagery = imagery;
            this.emotion = emotion;
            this.technique = technique;
            this.innovation = innovation;
        }
        
        // Getter and Setter methods
        public Integer getRhythm() {
            return rhythm;
        }
        
        public void setRhythm(Integer rhythm) {
            this.rhythm = rhythm;
        }
        
        public Integer getImagery() {
            return imagery;
        }
        
        public void setImagery(Integer imagery) {
            this.imagery = imagery;
        }
        
        public Integer getEmotion() {
            return emotion;
        }
        
        public void setEmotion(Integer emotion) {
            this.emotion = emotion;
        }
        
        public Integer getTechnique() {
            return technique;
        }
        
        public void setTechnique(Integer technique) {
            this.technique = technique;
        }
        
        public Integer getInnovation() {
            return innovation;
        }
        
        public void setInnovation(Integer innovation) {
            this.innovation = innovation;
        }
        
        @Override
        public String toString() {
            return "ScoreDimensionsDTO{" +
                    "rhythm=" + rhythm +
                    ", imagery=" + imagery +
                    ", emotion=" + emotion +
                    ", technique=" + technique +
                    ", innovation=" + innovation +
                    '}';
        }
    }
}
// {{END_MODIFICATIONS}}
