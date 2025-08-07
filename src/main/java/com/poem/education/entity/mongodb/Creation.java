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
         * 详细评分
         */
        @Field("details")
        private String details;
        
        /**
         * 评分时间
         */
        @Field("scoredAt")
        private LocalDateTime scoredAt;
        
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
        
        public String getDetails() {
            return details;
        }
        
        public void setDetails(String details) {
            this.details = details;
        }
        
        public LocalDateTime getScoredAt() {
            return scoredAt;
        }
        
        public void setScoredAt(LocalDateTime scoredAt) {
            this.scoredAt = scoredAt;
        }
        
        @Override
        public String toString() {
            return "AiScore{" +
                    "totalScore=" + totalScore +
                    ", details='" + details + '\'' +
                    ", scoredAt=" + scoredAt +
                    '}';
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
}
// {{END_MODIFICATIONS}}
