// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "b4a42fdc-2d03-4831-9d30-9278970f029a"
//   Timestamp: "2025-08-07T12:00:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，字段验证完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 评论请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class CommentRequest {
    
    /**
     * 目标ID
     * 必填字段，MongoDB ObjectId
     */
    @NotBlank(message = "目标ID不能为空")
    @Size(min = 24, max = 24, message = "目标ID必须为24个字符")
    private String targetId;
    
    /**
     * 目标类型
     * 必填字段，枚举值：guwen/creation/sentence/writer
     */
    @NotBlank(message = "目标类型不能为空")
    @Pattern(regexp = "^(guwen|creation|sentence|writer)$", message = "目标类型必须为：guwen、creation、sentence、writer之一")
    private String targetType;
    
    /**
     * 评论内容
     * 必填字段，长度1-1000字符
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000字符之间")
    private String content;
    
    /**
     * 父评论ID
     * 可选字段，用于回复评论
     */
    @Size(min = 24, max = 24, message = "父评论ID必须为24个字符")
    private String parentId;
    
    // 默认构造函数
    public CommentRequest() {
    }
    
    // 构造函数
    public CommentRequest(String targetId, String targetType, String content) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.content = content;
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
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    @Override
    public String toString() {
        return "CommentRequest{" +
                "targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", content='" + content + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
