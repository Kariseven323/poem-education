// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "d52718cc-6477-4916-a3a9-47de479ab99b"
//   Timestamp: "2025-08-07T11:35:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，字段验证完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.request;

import javax.validation.constraints.Size;

/**
 * 更新用户信息请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class UpdateProfileRequest {
    
    /**
     * 昵称
     * 可选，长度1-50字符
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    /**
     * 头像URL
     * 可选，长度不超过255字符
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
    
    /**
     * 个人简介
     * 可选，长度不超过500字符
     */
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;
    
    // 默认构造函数
    public UpdateProfileRequest() {
    }
    
    // 构造函数
    public UpdateProfileRequest(String nickname, String avatar, String bio) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.bio = bio;
    }
    
    // Getter and Setter methods
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
