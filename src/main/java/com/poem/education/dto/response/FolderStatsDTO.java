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

import java.util.List;
import java.util.Map;

/**
 * 收藏夹统计信息DTO
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class FolderStatsDTO {
    
    /**
     * 收藏夹总数
     */
    private Integer totalFolders;
    
    /**
     * 收藏项总数
     */
    private Long totalItems;
    
    /**
     * 各收藏夹详情列表
     */
    private List<FolderDTO> folderDetails;
    
    /**
     * 收藏夹名称到数量的映射
     */
    private Map<String, Long> folderCounts;
    
    /**
     * 收藏夹名称列表
     */
    private List<String> folders;
    
    // 默认构造函数
    public FolderStatsDTO() {
    }
    
    // 构造函数
    public FolderStatsDTO(Integer totalFolders, Long totalItems) {
        this.totalFolders = totalFolders;
        this.totalItems = totalItems;
    }
    
    // 完整构造函数
    public FolderStatsDTO(Integer totalFolders, Long totalItems, 
                         List<FolderDTO> folderDetails, Map<String, Long> folderCounts, 
                         List<String> folders) {
        this.totalFolders = totalFolders;
        this.totalItems = totalItems;
        this.folderDetails = folderDetails;
        this.folderCounts = folderCounts;
        this.folders = folders;
    }
    
    // Getter and Setter methods
    public Integer getTotalFolders() {
        return totalFolders;
    }
    
    public void setTotalFolders(Integer totalFolders) {
        this.totalFolders = totalFolders;
    }
    
    public Long getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }
    
    public List<FolderDTO> getFolderDetails() {
        return folderDetails;
    }
    
    public void setFolderDetails(List<FolderDTO> folderDetails) {
        this.folderDetails = folderDetails;
    }
    
    public Map<String, Long> getFolderCounts() {
        return folderCounts;
    }
    
    public void setFolderCounts(Map<String, Long> folderCounts) {
        this.folderCounts = folderCounts;
    }
    
    public List<String> getFolders() {
        return folders;
    }
    
    public void setFolders(List<String> folders) {
        this.folders = folders;
    }
    
    @Override
    public String toString() {
        return "FolderStatsDTO{" +
                "totalFolders=" + totalFolders +
                ", totalItems=" + totalItems +
                ", folderDetails=" + folderDetails +
                ", folderCounts=" + folderCounts +
                ", folders=" + folders +
                '}';
    }
}
// {{END_MODIFICATIONS}}
