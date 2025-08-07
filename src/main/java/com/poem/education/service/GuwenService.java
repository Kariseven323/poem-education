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

import com.poem.education.dto.request.GuwenSearchRequest;
import com.poem.education.dto.response.GuwenDTO;
import com.poem.education.dto.response.PageResult;

import java.util.List;

/**
 * 古文服务接口
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public interface GuwenService {
    
    /**
     * 获取古文列表（分页查询）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param dynasty 朝代（可选）
     * @param writer 作者（可选）
     * @param type 类型（可选）
     * @return 古文分页列表
     */
    PageResult<GuwenDTO> getGuwenList(Integer page, Integer size, String dynasty, String writer, String type);
    
    /**
     * 根据ID获取古文详情
     * 
     * @param id 古文ID
     * @return 古文详情
     */
    GuwenDTO getGuwenById(String id);
    
    /**
     * 搜索古文
     * 
     * @param request 搜索请求
     * @return 古文分页列表
     */
    PageResult<GuwenDTO> searchGuwen(GuwenSearchRequest request);
    
    /**
     * 获取热门古文
     * 
     * @param period 时间周期（daily、weekly、monthly）
     * @param limit 限制数量
     * @return 热门古文列表
     */
    List<GuwenDTO> getHotGuwen(String period, Integer limit);
    
    /**
     * 获取随机古文
     * 
     * @param limit 限制数量
     * @return 随机古文列表
     */
    List<GuwenDTO> getRandomGuwen(Integer limit);
    
    /**
     * 根据作者获取古文列表
     * 
     * @param writer 作者
     * @param page 页码
     * @param size 每页大小
     * @return 古文分页列表
     */
    PageResult<GuwenDTO> getGuwenByWriter(String writer, Integer page, Integer size);
    
    /**
     * 根据朝代获取古文列表
     * 
     * @param dynasty 朝代
     * @param page 页码
     * @param size 每页大小
     * @return 古文分页列表
     */
    PageResult<GuwenDTO> getGuwenByDynasty(String dynasty, Integer page, Integer size);
    
    /**
     * 获取所有朝代列表
     * 
     * @return 朝代列表
     */
    List<String> getAllDynasties();
    
    /**
     * 获取所有作者列表
     * 
     * @return 作者列表
     */
    List<String> getAllWriters();
    
    /**
     * 获取所有类型列表
     * 
     * @return 类型列表
     */
    List<String> getAllTypes();
    
    /**
     * 统计古文总数
     * 
     * @return 古文总数
     */
    long getTotalCount();
    
    /**
     * 根据作者统计古文数量
     * 
     * @param writer 作者
     * @return 古文数量
     */
    long getCountByWriter(String writer);
    
    /**
     * 根据朝代统计古文数量
     * 
     * @param dynasty 朝代
     * @return 古文数量
     */
    long getCountByDynasty(String dynasty);
}
// {{END_MODIFICATIONS}}
