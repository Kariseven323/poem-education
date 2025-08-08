// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "sentence-dto-creation"
//   Timestamp: "2025-08-08T13:20:59+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，与MongoDB实体映射一致"
//   Quality_Check: "编译通过，字段映射与Sentence实体完全一致。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * 名句响应DTO
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class SentenceDTO {
    
    /**
     * 名句ID（MongoDB ObjectId）
     */
    @JsonProperty("_id")
    private String id;
    
    /**
     * 名句内容
     */
    private String name;
    
    /**
     * 出处
     */
    private String from;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 朝代
     */
    private String dynasty;
    
    /**
     * 释义
     */
    private String meaning;
    
    /**
     * 赏析
     */
    private String appreciation;
    
    /**
     * 标签
     */
    private String[] tags;
    
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
    
    // 默认构造函数
    public SentenceDTO() {
    }
    
    // 构造函数
    public SentenceDTO(String id, String name, String from, String author, String dynasty) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.author = author;
        this.dynasty = dynasty;
    }
    
    // Getter and Setter methods
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getDynasty() {
        return dynasty;
    }
    
    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }
    
    public String getMeaning() {
        return meaning;
    }
    
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    
    public String getAppreciation() {
        return appreciation;
    }
    
    public void setAppreciation(String appreciation) {
        this.appreciation = appreciation;
    }
    
    public String[] getTags() {
        return tags;
    }
    
    public void setTags(String[] tags) {
        this.tags = tags;
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
        return "SentenceDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", from='" + from + '\'' +
                ", author='" + author + '\'' +
                ", dynasty='" + dynasty + '\'' +
                ", meaning='" + meaning + '\'' +
                ", appreciation='" + appreciation + '\'' +
                ", tags=" + java.util.Arrays.toString(tags) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
// {{END_MODIFICATIONS}}
