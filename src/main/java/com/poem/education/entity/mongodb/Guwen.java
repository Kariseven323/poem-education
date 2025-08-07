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
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 古文实体类
 * 对应MongoDB集合：guwen
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Document(collection = "guwen")
@CompoundIndexes({
    @CompoundIndex(name = "title_1_writer_1", def = "{'title': 1, 'writer': 1}"),
    @CompoundIndex(name = "dynasty_1_writer_1", def = "{'dynasty': 1, 'writer': 1}"),
    @CompoundIndex(name = "writer_dynasty_title_1", def = "{'writer': 1, 'dynasty': 1, 'title': 1}")
})
public class Guwen {
    
    /**
     * 主键ID
     * MongoDB ObjectId
     */
    @Id
    private String id;
    
    /**
     * 标题
     * 建立索引，支持全文搜索
     */
    @NotBlank(message = "标题不能为空")
    @Indexed(name = "title_1")
    @TextIndexed(weight = 1)
    @Field("title")
    private String title;
    
    /**
     * 朝代
     * 建立索引
     */
    @Indexed(name = "dynasty_1")
    @Field("dynasty")
    private String dynasty;
    
    /**
     * 作者
     * 建立索引，支持全文搜索
     */
    @Indexed(name = "writer_1")
    @TextIndexed(weight = 1)
    @Field("writer")
    private String writer;
    
    /**
     * 内容
     * 支持全文搜索
     */
    @TextIndexed(weight = 1)
    @Field("content")
    private String content;
    
    /**
     * 类型
     * 建立索引
     */
    @Indexed(name = "type_1")
    @Field("type")
    private String type;
    
    /**
     * 备注
     */
    @Field("remark")
    private String remark;
    
    /**
     * 赏析
     */
    @Field("shangxi")
    private String shangxi;
    
    /**
     * 翻译
     */
    @Field("translation")
    private String translation;
    
    /**
     * 音频URL
     */
    @Field("audioUrl")
    private String audioUrl;
    
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
    public Guwen() {
    }
    
    // 构造函数
    public Guwen(String title, String dynasty, String writer, String content) {
        this.title = title;
        this.dynasty = dynasty;
        this.writer = writer;
        this.content = content;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDynasty() {
        return dynasty;
    }
    
    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }
    
    public String getWriter() {
        return writer;
    }
    
    public void setWriter(String writer) {
        this.writer = writer;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getShangxi() {
        return shangxi;
    }
    
    public void setShangxi(String shangxi) {
        this.shangxi = shangxi;
    }
    
    public String getTranslation() {
        return translation;
    }
    
    public void setTranslation(String translation) {
        this.translation = translation;
    }
    
    public String getAudioUrl() {
        return audioUrl;
    }
    
    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
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
        return "Guwen{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dynasty='" + dynasty + '\'' +
                ", writer='" + writer + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", remark='" + remark + '\'' +
                ", shangxi='" + shangxi + '\'' +
                ", translation='" + translation + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
// {{END_MODIFICATIONS}}
