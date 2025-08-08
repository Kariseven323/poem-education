// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "0e54f46e-6ced-46bf-9b54-3a6819f266b3"
//   Timestamp: "2025-08-07T11:25:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Data MongoDB Repository最佳实践"
//   Quality_Check: "编译通过，查询方法符合业务需求。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.repository.mongodb;

import com.poem.education.entity.mongodb.Sentence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 名句Repository接口
 * 提供名句相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface SentenceRepository extends MongoRepository<Sentence, String> {
    
    /**
     * 根据名句内容查找
     * 
     * @param name 名句内容
     * @return 名句信息
     */
    Optional<Sentence> findByName(String name);
    
    /**
     * 根据出处查找名句列表
     * 
     * @param from 出处
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findByFrom(String from, Pageable pageable);
    
    /**
     * 根据作者查找名句列表
     * 
     * @param author 作者
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findByAuthor(String author, Pageable pageable);
    
    /**
     * 根据朝代查找名句列表
     * 
     * @param dynasty 朝代
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findByDynasty(String dynasty, Pageable pageable);
    
    /**
     * 根据标签查找名句列表
     * 
     * @param tag 标签
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findByTagsContaining(String tag, Pageable pageable);
    
    /**
     * 根据名句内容模糊查询
     * 
     * @param name 名句关键字
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * 根据出处模糊查询
     * 
     * @param from 出处关键字
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findByFromContainingIgnoreCase(String from, Pageable pageable);
    
    /**
     * 根据作者模糊查询
     * 
     * @param author 作者关键字
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findByAuthorContainingIgnoreCase(String author, Pageable pageable);
    
    /**
     * 统计特定作者的名句数量
     * 
     * @param author 作者
     * @return 名句数量
     */
    long countByAuthor(String author);
    
    /**
     * 统计特定朝代的名句数量
     * 
     * @param dynasty 朝代
     * @return 名句数量
     */
    long countByDynasty(String dynasty);
    
    /**
     * 统计特定出处的名句数量
     * 
     * @param from 出处
     * @return 名句数量
     */
    long countByFrom(String from);
    
    /**
     * 全文搜索名句
     * 使用MongoDB的文本索引进行搜索
     * 
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    @Query("{ $text: { $search: ?0 } }")
    Page<Sentence> findByTextSearch(String keyword, Pageable pageable);
    
    /**
     * 高级搜索名句
     * 支持多条件组合搜索
     * 
     * @param name 名句关键字
     * @param from 出处关键字
     * @param author 作者
     * @param dynasty 朝代
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'name': { $exists: false } } ] }, " +
           "{ $or: [ { 'from': { $regex: ?1, $options: 'i' } }, { 'from': { $exists: false } } ] }, " +
           "{ $or: [ { 'author': ?2 }, { 'author': { $exists: false } } ] }, " +
           "{ $or: [ { 'dynasty': ?3 }, { 'dynasty': { $exists: false } } ] } " +
           "] }")
    Page<Sentence> findByAdvancedSearch(String name, String from, String author, String dynasty, Pageable pageable);
    
    // 随机查询将在Service层使用MongoTemplate实现
    
    /**
     * 查找所有作者列表
     * 
     * @return 作者列表
     */
    @Query(value = "{ 'author': { $exists: true, $ne: null } }", fields = "{ 'author' : 1, 'dynasty' : 1 }")
    List<Sentence> findAllAuthors();
    
    /**
     * 查找所有朝代列表
     * 
     * @return 朝代列表
     */
    @Query(value = "{ 'dynasty': { $exists: true, $ne: null } }", fields = "{ 'dynasty' : 1 }")
    List<Sentence> findAllDynasties();
    
    /**
     * 查找所有出处列表
     * 
     * @return 出处列表
     */
    @Query(value = "{ 'from': { $exists: true, $ne: null } }", fields = "{ 'from' : 1, 'author' : 1 }")
    List<Sentence> findAllSources();
    
    /**
     * 查找所有标签列表
     * 
     * @return 标签列表
     */
    @Query(value = "{ 'tags': { $exists: true, $ne: null } }", fields = "{ 'tags' : 1 }")
    List<Sentence> findAllTags();
    
    /**
     * 查找最新添加的名句
     * 
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    Page<Sentence> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 查找特定作者的代表名句
     * 
     * @param author 作者
     * @param pageable 分页参数
     * @return 名句列表
     */
    List<Sentence> findByAuthorOrderByCreatedAtDesc(String author, Pageable pageable);
    
    /**
     * 检查名句是否存在
     * 
     * @param name 名句内容
     * @param from 出处
     * @return 是否存在
     */
    boolean existsByNameAndFrom(String name, String from);
    
    /**
     * 根据多个标签查找名句
     * 
     * @param tags 标签数组
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    @Query("{ 'tags': { $in: ?0 } }")
    Page<Sentence> findByTagsIn(String[] tags, Pageable pageable);
    
    /**
     * 查找包含所有指定标签的名句
     * 
     * @param tags 标签数组
     * @param pageable 分页参数
     * @return 名句分页列表
     */
    @Query("{ 'tags': { $all: ?0 } }")
    Page<Sentence> findByTagsContainingAll(String[] tags, Pageable pageable);
}
// {{END_MODIFICATIONS}}
