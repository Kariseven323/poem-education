// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "6fbd2d10-468b-4ef3-ac5b-ddd4dddcf277"
//   Timestamp: "2025-08-07T11:50:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，字段映射完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 古文响应DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class GuwenDTO {
    
    /**
     * 古文ID（MongoDB ObjectId）
     */
    private String id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 朝代
     */
    private String dynasty;
    
    /**
     * 作者
     */
    private String writer;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 类型
     */
    private String type;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 赏析
     */
    private String shangxi;
    
    /**
     * 翻译
     */
    private String translation;
    
    /**
     * 音频URL
     */
    private String audioUrl;
    
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
    public GuwenDTO() {
    }
    
    // 构造函数
    public GuwenDTO(String id, String title, String dynasty, String writer, String content) {
        this.id = id;
        this.title = title;
        this.dynasty = dynasty;
        this.writer = writer;
        this.content = content;
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
        return "GuwenDTO{" +
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
