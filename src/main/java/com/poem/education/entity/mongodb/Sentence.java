// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "0e54f46e-6ced-46bf-9b54-3a6819f266b3"
//   Timestamp: "2025-08-07T11:25:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "MongoDB实体映射最佳实践，严格按照集合结构定义"
//   Quality_Check: "编译通过，字段映射与MongoDB集合结构完全一致。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.entity.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 名句实体类
 * 对应MongoDB集合：sentences
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Document(collection = "sentences")
public class Sentence {
    
    /**
     * 主键ID
     * MongoDB ObjectId
     */
    @Id
    private String id;
    
    /**
     * 名句内容
     * 建立索引，支持全文搜索
     */
    @NotBlank(message = "名句内容不能为空")
    @Indexed(name = "name_1")
    @TextIndexed(weight = 1)
    @Field("name")
    private String name;
    
    /**
     * 出处
     * 建立索引，支持全文搜索
     */
    @Indexed(name = "from_1")
    @TextIndexed(weight = 1)
    @Field("from")
    private String from;
    
    /**
     * 作者
     */
    @Field("author")
    private String author;
    
    /**
     * 朝代
     */
    @Field("dynasty")
    private String dynasty;
    
    /**
     * 释义
     */
    @Field("meaning")
    private String meaning;
    
    /**
     * 赏析
     */
    @Field("appreciation")
    private String appreciation;
    
    /**
     * 标签
     */
    @Field("tags")
    private String[] tags;
    
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
    public Sentence() {
    }
    
    // 构造函数
    public Sentence(String name, String from) {
        this.name = name;
        this.from = from;
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
        return "Sentence{" +
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
