// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "6fbd2d10-468b-4ef3-ac5b-ddd4dddcf277"
//   Timestamp: "2025-08-07T11:50:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DTO设计最佳实践，严格按照API文档定义"
//   Quality_Check: "编译通过，字段验证完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 * 古文搜索请求DTO
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class GuwenSearchRequest {
    
    /**
     * 搜索关键字
     * 可选，用于全文搜索
     */
    @Size(max = 100, message = "搜索关键字长度不能超过100个字符")
    private String keyword;
    
    /**
     * 作者
     * 可选，精确匹配
     */
    @Size(max = 50, message = "作者名称长度不能超过50个字符")
    private String writer;
    
    /**
     * 朝代
     * 可选，精确匹配
     */
    @Size(max = 50, message = "朝代名称长度不能超过50个字符")
    private String dynasty;
    
    /**
     * 类型
     * 可选，精确匹配
     */
    @Size(max = 50, message = "类型名称长度不能超过50个字符")
    private String type;
    
    /**
     * 页码
     * 默认值：1
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;
    
    /**
     * 每页大小
     * 默认值：20，最大值：100
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 20;
    
    /**
     * 排序字段
     * 可选值：createdAt、title、writer、dynasty
     */
    private String sortBy = "createdAt";
    
    /**
     * 排序方向
     * 可选值：asc、desc
     */
    private String sortDir = "desc";

    /**
     * 搜索类型
     * 可选值：smart(智能搜索)、fuzzy(模糊搜索)、content(内容搜索)、exact(精确搜索)
     */
    private String searchType = "smart";
    
    // 默认构造函数
    public GuwenSearchRequest() {
    }
    
    // 构造函数
    public GuwenSearchRequest(String keyword, Integer page, Integer size) {
        this.keyword = keyword;
        this.page = page;
        this.size = size;
    }
    
    // Getter and Setter methods
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getWriter() {
        return writer;
    }
    
    public void setWriter(String writer) {
        this.writer = writer;
    }
    
    public String getDynasty() {
        return dynasty;
    }
    
    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDir() {
        return sortDir;
    }
    
    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
    
    @Override
    public String toString() {
        return "GuwenSearchRequest{" +
                "keyword='" + keyword + '\'' +
                ", writer='" + writer + '\'' +
                ", dynasty='" + dynasty + '\'' +
                ", type='" + type + '\'' +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDir='" + sortDir + '\'' +
                '}';
    }
}
// {{END_MODIFICATIONS}}
