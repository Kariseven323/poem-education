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
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * 作者实体类
 * 对应MongoDB集合：writers
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Document(collection = "writers")
public class Writer {
    
    /**
     * 主键ID
     * MongoDB ObjectId
     */
    @Id
    private String id;
    
    /**
     * 作者姓名
     * 支持全文搜索
     */
    @NotBlank(message = "作者姓名不能为空")
    @TextIndexed(weight = 1)
    @Field("name")
    private String name;
    
    /**
     * 朝代
     */
    @Field("dynasty")
    private String dynasty;
    
    /**
     * 简介
     * 支持全文搜索
     */
    @TextIndexed(weight = 1)
    @Field("simpleIntro")
    private String simpleIntro;
    
    /**
     * 详细介绍
     */
    @Field("detailIntro")
    private String detailIntro;
    
    /**
     * 头像图片URL
     */
    @Field("headImageUrl")
    private String headImageUrl;
    
    /**
     * 生卒年
     */
    @Field("lifespan")
    private String lifespan;
    
    /**
     * 字号
     */
    @Field("alias")
    private String alias;
    
    /**
     * 籍贯
     */
    @Field("birthplace")
    private String birthplace;
    
    /**
     * 主要成就
     */
    @Field("achievements")
    private String[] achievements;
    
    /**
     * 代表作品
     */
    @Field("masterpieces")
    private String[] masterpieces;
    
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
    public Writer() {
    }
    
    // 构造函数
    public Writer(String name, String dynasty) {
        this.name = name;
        this.dynasty = dynasty;
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
        return "Writer{" +
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
