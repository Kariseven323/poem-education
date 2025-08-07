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
 * 作者响应DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class WriterDTO {
    
    /**
     * 作者ID（MongoDB ObjectId）
     */
    private String id;
    
    /**
     * 作者姓名
     */
    private String name;
    
    /**
     * 朝代
     */
    private String dynasty;
    
    /**
     * 简介
     */
    private String simpleIntro;
    
    /**
     * 详细介绍
     */
    private String detailIntro;
    
    /**
     * 头像图片URL
     */
    private String headImageUrl;
    
    /**
     * 生卒年
     */
    private String lifespan;
    
    /**
     * 字号
     */
    private String alias;
    
    /**
     * 籍贯
     */
    private String birthplace;
    
    /**
     * 主要成就
     */
    private String[] achievements;
    
    /**
     * 代表作品
     */
    private String[] masterpieces;
    
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
    public WriterDTO() {
    }
    
    // 构造函数
    public WriterDTO(String id, String name, String dynasty) {
        this.id = id;
        this.name = name;
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
    
    public String getDynasty() {
        return dynasty;
    }
    
    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }
    
    public String getSimpleIntro() {
        return simpleIntro;
    }
    
    public void setSimpleIntro(String simpleIntro) {
        this.simpleIntro = simpleIntro;
    }
    
    public String getDetailIntro() {
        return detailIntro;
    }
    
    public void setDetailIntro(String detailIntro) {
        this.detailIntro = detailIntro;
    }
    
    public String getHeadImageUrl() {
        return headImageUrl;
    }
    
    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }
    
    public String getLifespan() {
        return lifespan;
    }
    
    public void setLifespan(String lifespan) {
        this.lifespan = lifespan;
    }
    
    public String getAlias() {
        return alias;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    public String getBirthplace() {
        return birthplace;
    }
    
    public void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }
    
    public String[] getAchievements() {
        return achievements;
    }
    
    public void setAchievements(String[] achievements) {
        this.achievements = achievements;
    }
    
    public String[] getMasterpieces() {
        return masterpieces;
    }
    
    public void setMasterpieces(String[] masterpieces) {
        this.masterpieces = masterpieces;
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
        return "WriterDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dynasty='" + dynasty + '\'' +
                ", simpleIntro='" + simpleIntro + '\'' +
                ", detailIntro='" + detailIntro + '\'' +
                ", headImageUrl='" + headImageUrl + '\'' +
                ", lifespan='" + lifespan + '\'' +
                ", alias='" + alias + '\'' +
                ", birthplace='" + birthplace + '\'' +
                ", achievements=" + java.util.Arrays.toString(achievements) +
                ", masterpieces=" + java.util.Arrays.toString(masterpieces) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
// {{END_MODIFICATIONS}}
