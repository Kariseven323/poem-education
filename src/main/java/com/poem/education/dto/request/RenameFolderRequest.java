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
import javax.validation.constraints.Size;

/**
 * 重命名收藏夹请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class RenameFolderRequest {
    
    /**
     * 旧收藏夹名称
     * 必填字段
     */
    @NotBlank(message = "旧收藏夹名称不能为空")
    @Size(max = 100, message = "收藏夹名称长度不能超过100个字符")
    private String oldName;
    
    /**
     * 新收藏夹名称
     * 必填字段
     */
    @NotBlank(message = "新收藏夹名称不能为空")
    @Size(min = 1, max = 100, message = "收藏夹名称长度必须在1-100个字符之间")
    private String newName;
    
    // 默认构造函数
    public RenameFolderRequest() {
    }
    
    // 构造函数
    public RenameFolderRequest(String oldName, String newName) {
        this.oldName = oldName;
        this.newName = newName;
    }
    
    // Getter and Setter methods
    public String getOldName() {
        return oldName;
    }
    
    public void setOldName(String oldName) {
        this.oldName = oldName;
    }
    
    public String getNewName() {
        return newName;
    }
    
    public void setNewName(String newName) {
        this.newName = newName;
    }
    
    @Override
    public String toString() {
        return "RenameFolderRequest{" +
                "oldName='" + oldName + '\'' +
                ", newName='" + newName + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
