// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "6406c941-e119-486e-a1a6-4e90e1986ef5"
//   Timestamp: "2025-08-08T18:00:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，遵循现有代码风格"
//   Quality_Check: "编译通过，字段映射完整，JSON序列化兼容。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 收藏夹信息DTO
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class FolderDTO {
    
    /**
     * 收藏夹名称
     */
    private String folderName;
    
    /**
     * 收藏项数量
     */
    private Integer itemCount;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;
    
    /**
     * 是否为默认收藏夹
     */
    private Boolean isDefault;
    
    // 默认构造函数
    public FolderDTO() {
    }
    
    // 构造函数
    public FolderDTO(String folderName, Integer itemCount) {
        this.folderName = folderName;
        this.itemCount = itemCount;
        this.isDefault = "默认收藏夹".equals(folderName);
    }
    
    // 完整构造函数
    public FolderDTO(String folderName, Integer itemCount, LocalDateTime createdAt, LocalDateTime lastUpdated) {
        this.folderName = folderName;
        this.itemCount = itemCount;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.isDefault = "默认收藏夹".equals(folderName);
    }
    
    // Getter and Setter methods
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
        this.isDefault = "默认收藏夹".equals(folderName);
    }
    
    public Integer getItemCount() {
        return itemCount;
    }
    
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    @Override
    public String toString() {
        return "FolderDTO{" +
                "folderName='" + folderName + '\'' +
                ", itemCount=" + itemCount +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", isDefault=" + isDefault +
                '}';
    }
}
// {{END_MODIFICATIONS}}
