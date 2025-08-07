// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "5a312240-0eee-4528-b331-40ce70d611fb"
//   Timestamp: "2025-08-07T11:10:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "分页响应格式最佳实践"
//   Quality_Check: "编译通过，分页格式符合API文档规范。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.dto.response;

import java.util.List;

/**
 * 分页响应结果类
 * 用于统一分页数据的返回格式
 * 
 * @author poem-education-team
 * @since 2025-08-07
 * @param <T> 数据类型
 */
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 当前页码（从1开始）
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    /**
     * 是否有下一页
     */
    private Boolean hasNext;
    
    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;
    
    /**
     * 默认构造函数
     */
    public PageResult() {
    }
    
    /**
     * 构造函数
     * 
     * @param list 数据列表
     * @param page 当前页码
     * @param size 每页大小
     * @param total 总记录数
     */
    public PageResult(List<T> list, Integer page, Integer size, Long total) {
        this.list = list;
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = calculatePages(total, size);
        this.hasNext = page < this.pages;
        this.hasPrevious = page > 1;
    }
    
    /**
     * 创建分页结果
     * 
     * @param list 数据列表
     * @param page 当前页码
     * @param size 每页大小
     * @param total 总记录数
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> list, Integer page, Integer size, Long total) {
        return new PageResult<>(list, page, size, total);
    }
    
    /**
     * 创建空的分页结果
     * 
     * @param page 当前页码
     * @param size 每页大小
     * @param <T> 数据类型
     * @return 空的分页结果
     */
    public static <T> PageResult<T> empty(Integer page, Integer size) {
        return new PageResult<>(List.of(), page, size, 0L);
    }
    
    /**
     * 计算总页数
     * 
     * @param total 总记录数
     * @param size 每页大小
     * @return 总页数
     */
    private Integer calculatePages(Long total, Integer size) {
        if (total == null || total <= 0 || size == null || size <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / size);
    }
    
    /**
     * 判断是否为空
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }
    
    // Getter and Setter methods
    public List<T> getList() {
        return list;
    }
    
    public void setList(List<T> list) {
        this.list = list;
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
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
        this.pages = calculatePages(total, this.size);
        if (this.page != null) {
            this.hasNext = this.page < this.pages;
            this.hasPrevious = this.page > 1;
        }
    }
    
    public Integer getPages() {
        return pages;
    }
    
    public void setPages(Integer pages) {
        this.pages = pages;
    }
    
    public Boolean getHasNext() {
        return hasNext;
    }
    
    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public Boolean getHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
    
    @Override
    public String toString() {
        return "PageResult{" +
                "list=" + (list != null ? list.size() + " items" : "null") +
                ", page=" + page +
                ", size=" + size +
                ", total=" + total +
                ", pages=" + pages +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
}
// {{END_MODIFICATIONS}}
