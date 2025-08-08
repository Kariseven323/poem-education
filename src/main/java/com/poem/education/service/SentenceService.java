// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "sentence-service-interface"
//   Timestamp: "2025-08-08T13:20:59+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "服务接口设计最佳实践，遵循SOLID原则"
//   Quality_Check: "编译通过，接口定义清晰完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service;

import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.SentenceDTO;

import java.util.List;

/**
 * 名句服务接口
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
public interface SentenceService {
    
    /**
     * 获取名句列表（分页查询）
     * 
     * @param page 页码
     * @param size 每页大小
     * @param author 作者（可选）
     * @param dynasty 朝代（可选）
     * @param from 出处（可选）
     * @return 名句分页列表
     */
    PageResult<SentenceDTO> getSentenceList(Integer page, Integer size, String author, String dynasty, String from);
    
    /**
     * 根据ID获取名句详情
     * 
     * @param id 名句ID
     * @return 名句详情
     */
    SentenceDTO getSentenceById(String id);
    
    /**
     * 根据名句内容查找
     * 
     * @param name 名句内容
     * @return 名句信息
     */
    SentenceDTO getSentenceByName(String name);
    
    /**
     * 搜索名句
     * 
     * @param keyword 搜索关键字
     * @param page 页码
     * @param size 每页大小
     * @return 名句分页列表
     */
    PageResult<SentenceDTO> searchSentences(String keyword, Integer page, Integer size);
    
    /**
     * 获取热门名句
     * 
     * @param limit 限制数量
     * @return 热门名句列表
     */
    List<SentenceDTO> getHotSentences(Integer limit);
    
    /**
     * 获取随机名句
     * 
     * @param limit 限制数量
     * @return 随机名句列表
     */
    List<SentenceDTO> getRandomSentences(Integer limit);
    
    /**
     * 根据作者获取名句列表
     * 
     * @param author 作者
     * @param page 页码
     * @param size 每页大小
     * @return 名句分页列表
     */
    PageResult<SentenceDTO> getSentencesByAuthor(String author, Integer page, Integer size);
    
    /**
     * 根据朝代获取名句列表
     * 
     * @param dynasty 朝代
     * @param page 页码
     * @param size 每页大小
     * @return 名句分页列表
     */
    PageResult<SentenceDTO> getSentencesByDynasty(String dynasty, Integer page, Integer size);
    
    /**
     * 根据出处获取名句列表
     * 
     * @param from 出处
     * @param page 页码
     * @param size 每页大小
     * @return 名句分页列表
     */
    PageResult<SentenceDTO> getSentencesByFrom(String from, Integer page, Integer size);
    
    /**
     * 获取所有作者列表
     * 
     * @return 作者列表
     */
    List<String> getAllAuthors();
    
    /**
     * 获取所有朝代列表
     * 
     * @return 朝代列表
     */
    List<String> getAllDynasties();
    
    /**
     * 获取所有出处列表
     * 
     * @return 出处列表
     */
    List<String> getAllSources();
    
    /**
     * 统计名句总数
     * 
     * @return 名句总数
     */
    long getTotalCount();
    
    /**
     * 根据作者统计名句数量
     * 
     * @param author 作者
     * @return 名句数量
     */
    long getCountByAuthor(String author);
    
    /**
     * 根据朝代统计名句数量
     * 
     * @param dynasty 朝代
     * @return 名句数量
     */
    long getCountByDynasty(String dynasty);
    
    /**
     * 根据出处统计名句数量
     * 
     * @param from 出处
     * @return 名句数量
     */
    long getCountByFrom(String from);
}
// {{END_MODIFICATIONS}}
