// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "cbae72cf-b030-48a2-b381-ce2a1484c281"
//   Timestamp: "2025-08-07T12:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照数据库表结构定义"
//   Quality_Check: "编译通过，字段验证完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 收藏请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class FavoriteRequest {
    
    /**
     * 目标ID
     * 必填字段，MongoDB ObjectId字符串(24字符)
     */
    @NotBlank(message = "目标ID不能为空")
    @Size(min = 24, max = 24, message = "目标ID必须为24个字符")
    private String targetId;
    
    /**
     * 收藏类型
     * 必填字段，枚举值：guwen/sentence/writer
     */
    @NotBlank(message = "收藏类型不能为空")
    @Pattern(regexp = "^(guwen|sentence|writer)$", 
             message = "收藏类型必须为：guwen、sentence、writer之一")
    private String targetType;
    
    /**
     * 收藏夹名称
     * 可选字段，默认值：默认收藏夹
     */
    @Size(max = 100, message = "收藏夹名称长度不能超过100个字符")
    private String folderName = "默认收藏夹";
    
    /**
     * 收藏备注
     * 可选字段
     */
    @Size(max = 1000, message = "收藏备注长度不能超过1000个字符")
    private String notes;
    
    // 默认构造函数
    public FavoriteRequest() {
    }
    
    // 构造函数
    public FavoriteRequest(String targetId, String targetType) {
        this.targetId = targetId;
        this.targetType = targetType;
    }
    
    // 构造函数（带收藏夹名称）
    public FavoriteRequest(String targetId, String targetType, String folderName) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.folderName = folderName;
    }
    
    // Getter and Setter methods
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
    
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "FavoriteRequest{" +
                "targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", folderName='" + folderName + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
