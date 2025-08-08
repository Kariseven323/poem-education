// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "6406c941-e119-486e-a1a6-4e90e1986ef5"
//   Timestamp: "2025-08-08T18:00:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，遵循现有代码风格"
//   Quality_Check: "编译通过，参数验证完整，JSON序列化兼容。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 移动收藏项请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class MoveFavoriteRequest {
    
    /**
     * 收藏项ID
     * 必填字段
     */
    @NotNull(message = "收藏项ID不能为空")
    private Long favoriteId;
    
    /**
     * 目标收藏夹名称
     * 必填字段
     */
    @NotBlank(message = "目标收藏夹名称不能为空")
    @Size(max = 100, message = "收藏夹名称长度不能超过100个字符")
    private String targetFolderName;
    
    // 默认构造函数
    public MoveFavoriteRequest() {
    }
    
    // 构造函数
    public MoveFavoriteRequest(Long favoriteId, String targetFolderName) {
        this.favoriteId = favoriteId;
        this.targetFolderName = targetFolderName;
    }
    
    // Getter and Setter methods
    public Long getFavoriteId() {
        return favoriteId;
    }
    
    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }
    
    public String getTargetFolderName() {
        return targetFolderName;
    }
    
    public void setTargetFolderName(String targetFolderName) {
        this.targetFolderName = targetFolderName;
    }
    
    @Override
    public String toString() {
        return "MoveFavoriteRequest{" +
                "favoriteId=" + favoriteId +
                ", targetFolderName='" + targetFolderName + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
