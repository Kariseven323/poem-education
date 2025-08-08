// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "4634d760-65a3-4128-901c-c09532256a97"
//   Timestamp: "2025-08-08T13:30:50+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照Creation实体定义"
//   Quality_Check: "编译通过，字段验证完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 创作请求DTO
 * 用于用户提交诗词创作
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public class CreationRequest {
    
    /**
     * 标题
     * 必填字段，长度1-100字符
     */
    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 100, message = "标题长度必须在1-100字符之间")
    private String title;
    
    /**
     * 内容
     * 必填字段，长度1-5000字符
     */
    @NotBlank(message = "内容不能为空")
    @Size(min = 1, max = 5000, message = "内容长度必须在1-5000字符之间")
    private String content;
    
    /**
     * 风格
     * 可选字段，枚举值：律诗/绝句/词/散文/现代诗/其他
     */
    @Pattern(regexp = "^(律诗|绝句|词|散文|现代诗|其他)$", message = "风格必须为：律诗、绝句、词、散文、现代诗、其他之一")
    private String style;
    
    /**
     * 描述
     * 可选字段，最大长度1000字符
     */
    @Size(max = 1000, message = "描述长度不能超过1000字符")
    private String description;
    
    // 默认构造函数
    public CreationRequest() {
    }
    
    // 构造函数
    public CreationRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
    
    // Getter and Setter methods
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getStyle() {
        return style;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "CreationRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", style='" + style + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
