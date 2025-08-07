// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "6fbd2d10-468b-4ef3-ac5b-ddd4dddcf277"
//   Timestamp: "2025-08-07T11:50:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Service接口设计最佳实践"
//   Quality_Check: "编译通过，接口定义完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.WriterDTO;

import java.util.List;

/**
 * 作者服务接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public interface WriterService {
    
    /**
     * 根据ID获取作者详情
     * 
     * @param id 作者ID
     * @return 作者详情
     */
    WriterDTO getWriterById(String id);
    
    /**
     * 根据姓名获取作者信息
     * 
     * @param name 作者姓名
     * @return 作者信息
     */
    WriterDTO getWriterByName(String name);
    
    /**
     * 获取作者列表（分页查询）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param dynasty 朝代（可选）
     * @return 作者分页列表
     */
    PageResult<WriterDTO> getWriterList(Integer page, Integer size, String dynasty);
    
    /**
     * 搜索作者
     * 
     * @param keyword 搜索关键字
     * @param page 页码
     * @param size 每页大小
     * @return 作者分页列表
     */
    PageResult<WriterDTO> searchWriters(String keyword, Integer page, Integer size);
    
    /**
     * 根据朝代获取作者列表
     * 
     * @param dynasty 朝代
     * @param page 页码
     * @param size 每页大小
     * @return 作者分页列表
     */
    PageResult<WriterDTO> getWritersByDynasty(String dynasty, Integer page, Integer size);
    
    /**
     * 获取热门作者
     * 
     * @param limit 限制数量
     * @return 热门作者列表
     */
    List<WriterDTO> getHotWriters(Integer limit);
    
    /**
     * 获取随机作者
     * 
     * @param limit 限制数量
     * @return 随机作者列表
     */
    List<WriterDTO> getRandomWriters(Integer limit);
    
    /**
     * 获取所有朝代列表
     * 
     * @return 朝代列表
     */
    List<String> getAllDynasties();
    
    /**
     * 统计作者总数
     * 
     * @return 作者总数
     */
    long getTotalCount();
    
    /**
     * 根据朝代统计作者数量
     * 
     * @param dynasty 朝代
     * @return 作者数量
     */
    long getCountByDynasty(String dynasty);
}
// {{END_MODIFICATIONS}}
