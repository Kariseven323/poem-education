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
 * 用户行为请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class UserActionRequest {
    
    /**
     * 目标ID
     * 必填字段，MongoDB ObjectId字符串(24字符)
     */
    @NotBlank(message = "目标ID不能为空")
    @Size(min = 24, max = 24, message = "目标ID必须为24个字符")
    private String targetId;
    
    /**
     * 目标类型
     * 必填字段，枚举值：guwen/creation/comment/sentence
     */
    @NotBlank(message = "目标类型不能为空")
    @Pattern(regexp = "^(guwen|creation|comment|sentence)$", 
             message = "目标类型必须为：guwen、creation、comment、sentence之一")
    private String targetType;
    
    /**
     * 行为类型
     * 必填字段，枚举值：like/favorite/view/share
     */
    @NotBlank(message = "行为类型不能为空")
    @Pattern(regexp = "^(like|favorite|view|share)$", 
             message = "行为类型必须为：like、favorite、view、share之一")
    private String actionType;
    
    // 默认构造函数
    public UserActionRequest() {
    }
    
    // 构造函数
    public UserActionRequest(String targetId, String targetType, String actionType) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.actionType = actionType;
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
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    @Override
    public String toString() {
        return "UserActionRequest{" +
                "targetId='" + targetId + '\'' +
                ", targetType='" + targetType + '\'' +
                ", actionType='" + actionType + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
