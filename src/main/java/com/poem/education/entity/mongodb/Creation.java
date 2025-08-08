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
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创作实体类
 * 对应MongoDB集合：creations
 * 严格按照集合验证规则定义
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Document(collection = "creations")
@CompoundIndexes({
    @CompoundIndex(name = "user_status_time_1", def = "{'userId': 1, 'status': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "style_status_1", def = "{'style': 1, 'status': 1}")
})
public class Creation {
    
    /**
     * 主键ID
     * MongoDB ObjectId
     */
    @Id
    private String id;
    
    /**
     * 用户ID
     * 必填字段，长整型
     */
    @NotNull(message = "用户ID不能为空")
    @Indexed(name = "userId_1")
    @Field("userId")
    private Long userId;
    
    /**
     * 标题
     * 必填字段，长度1-100字符
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100字符之间")
    @TextIndexed(weight = 1)
    @Field("title")
    private String title;
    
    /**
     * 内容
     * 必填字段，长度1-5000字符
     */
    @NotBlank(message = "内容不能为空")
    @Size(min = 1, max = 5000, message = "内容长度必须在1-5000字符之间")
    @TextIndexed(weight = 1)
    @Field("content")
    private String content;
    
    /**
     * 风格
     * 可选字段，枚举值：律诗/绝句/词/散文/现代诗/其他
     */
    @Pattern(regexp = "^(律诗|绝句|词|散文|现代诗|其他)$", message = "风格必须为：律诗、绝句、词、散文、现代诗、其他之一")
    @Field("style")
    private String style;
    
    /**
     * 状态
     * 必填字段，枚举值：-1（审核不通过）、0（待审核）、1（已发布）
     */
    @NotNull(message = "状态不能为空")
    @Pattern(regexp = "^(-1|0|1)$", message = "状态必须为-1、0、1之一")
    @Indexed(name = "status_1")
    @Field("status")
    private Integer status;
    
    /**
     * AI评分信息
     * 嵌套对象
     */
    @Field("aiScore")
    private AiScore aiScore;

    /**
     * 雷达图数据
     * 按照数据库设计，独立存储雷达图数据以提高查询性能
     */
    @Field("radarData")
    private RadarData radarData;
    
    /**
     * 描述
     * 可选字段，支持全文搜索
     */
    @TextIndexed(weight = 1)
    @Field("description")
    private String description;
    
    /**
     * 创建时间
     */
    @Indexed(name = "createdAt_-1", direction = IndexDirection.DESCENDING)
    @Field("createdAt")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Field("updatedAt")
    private LocalDateTime updatedAt;

    /**
     * 是否公开
     * 默认为false（私有）
     */
    @Field("isPublic")
    private Boolean isPublic;

    /**
     * 点赞数
     * 默认为0
     */
    @Field("likeCount")
    private Integer likeCount;

    /**
     * 评论数
     * 默认为0
     */
    @Field("commentCount")
    private Integer commentCount;
    
    /**
     * AI评分嵌套类
     */
    public static class AiScore {
        /**
         * 总分
         * 范围0-100
         */
        @Min(value = 0, message = "AI评分总分最小为0")
        @Max(value = 100, message = "AI评分总分最大为100")
        @Indexed(name = "totalScore_-1", direction = IndexDirection.DESCENDING)
        @Field("totalScore")
        private Integer totalScore;

        /**
         * AI反馈建议
         * 按照数据库设计，字段名为feedback而非details
         */
        @Field("feedback")
        private String feedback;

        /**
         * 评分时间
         */
        @Field("scoredAt")
        private LocalDateTime scoredAt;

        /**
         * 多维度评分
         * 包含韵律、意象、情感、技法、创新五个维度
         */
        @Field("dimensions")
        private ScoreDimensions dimensions;

        /**
         * AI思考过程
         * 记录AI评分时的思考分析过程，用于展示给用户
         */
        @Field("thinkingProcess")
        private String thinkingProcess;

        // 构造函数
        public AiScore() {
        }
        
        public AiScore(Integer totalScore) {
            this.totalScore = totalScore;
            this.scoredAt = LocalDateTime.now();
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
         * 兼容性方法：保持向后兼容
         * @deprecated 使用getFeedback()替代
         */
        @Deprecated
        public String getDetails() {
            return feedback;
        }

        /**
         * 兼容性方法：保持向后兼容
         * @deprecated 使用setFeedback()替代
         */
        @Deprecated
        public void setDetails(String details) {
            this.feedback = details;
        }
        
        public LocalDateTime getScoredAt() {
            return scoredAt;
        }
        
        public void setScoredAt(LocalDateTime scoredAt) {
            this.scoredAt = scoredAt;
        }
        
        public ScoreDimensions getDimensions() {
            return dimensions;
        }

        public void setDimensions(ScoreDimensions dimensions) {
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
            return "AiScore{" +
                    "totalScore=" + totalScore +
                    ", feedback='" + feedback + '\'' +
                    ", scoredAt=" + scoredAt +
                    ", dimensions=" + dimensions +
                    ", thinkingProcess='" + thinkingProcess + '\'' +
                    '}';
        }

        /**
         * 多维度评分嵌套类
         * 包含诗词评价的五个维度
         */
        public static class ScoreDimensions {
            /**
             * 韵律评分
             * 范围0-100，评价诗词的韵律美感
             */
            @Min(value = 0, message = "韵律评分最小为0")
            @Max(value = 100, message = "韵律评分最大为100")
            @Field("rhythm")
            private Integer rhythm;

            /**
             * 意象评分
             * 范围0-100，评价诗词的意象表达
             */
            @Min(value = 0, message = "意象评分最小为0")
            @Max(value = 100, message = "意象评分最大为100")
            @Field("imagery")
            private Integer imagery;

            /**
             * 情感评分
             * 范围0-100，评价诗词的情感表达深度
             */
            @Min(value = 0, message = "情感评分最小为0")
            @Max(value = 100, message = "情感评分最大为100")
            @Field("emotion")
            private Integer emotion;

            /**
             * 技法评分
             * 范围0-100，评价诗词的写作技巧运用
             */
            @Min(value = 0, message = "技法评分最小为0")
            @Max(value = 100, message = "技法评分最大为100")
            @Field("technique")
            private Integer technique;

            /**
             * 创新评分
             * 范围0-100，评价诗词的创新性和独特性
             */
            @Min(value = 0, message = "创新评分最小为0")
            @Max(value = 100, message = "创新评分最大为100")
            @Field("innovation")
            private Integer innovation;

            // 构造函数
            public ScoreDimensions() {
            }

            public ScoreDimensions(Integer rhythm, Integer imagery, Integer emotion,
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
                return "ScoreDimensions{" +
                        "rhythm=" + rhythm +
                        ", imagery=" + imagery +
                        ", emotion=" + emotion +
                        ", technique=" + technique +
                        ", innovation=" + innovation +
                        '}';
            }
        }
    }
    
    // 默认构造函数
    public Creation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public Creation(Long userId, String title, String content, Integer status) {
        this.userId = userId;
        this.title = title;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public AiScore getAiScore() {
        return aiScore;
    }

    public void setAiScore(AiScore aiScore) {
        this.aiScore = aiScore;
    }

    public RadarData getRadarData() {
        return radarData;
    }

    public void setRadarData(RadarData radarData) {
        this.radarData = radarData;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
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
    
    @Override
    public String toString() {
        return "Creation{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", style='" + style + '\'' +
                ", status=" + status +
                ", aiScore=" + aiScore +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    /**
     * 风格枚举
     */
    public static class Style {
        public static final String LU_SHI = "律诗";
        public static final String JUE_JU = "绝句";
        public static final String CI = "词";
        public static final String SAN_WEN = "散文";
        public static final String XIAN_DAI_SHI = "现代诗";
        public static final String OTHER = "其他";
    }
    
    /**
     * 状态枚举
     */
    public static class Status {
        public static final Integer REJECTED = -1;  // 审核不通过
        public static final Integer PENDING = 0;    // 待审核
        public static final Integer PUBLISHED = 1;  // 已发布
    }

    /**
     * 雷达图数据嵌套类
     * 按照数据库设计，独立存储雷达图数据
     */
    public static class RadarData {
        /**
         * 雷达图标签
         * 固定为["韵律", "意象", "情感", "技法", "创新"]
         */
        @Field("labels")
        private List<String> labels;

        /**
         * 雷达图数值
         * 对应各维度的评分值
         */
        @Field("values")
        private List<Integer> values;

        // 构造函数
        public RadarData() {
        }

        public RadarData(List<String> labels, List<Integer> values) {
            this.labels = labels;
            this.values = values;
        }

        // Getter and Setter methods
        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Integer> getValues() {
            return values;
        }

        public void setValues(List<Integer> values) {
            this.values = values;
        }

        @Override
        public String toString() {
            return "RadarData{" +
                    "labels=" + labels +
                    ", values=" + values +
                    '}';
        }
    }
}
// {{END_MODIFICATIONS}}
