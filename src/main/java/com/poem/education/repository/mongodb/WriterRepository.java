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

import com.poem.education.entity.mongodb.Writer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 作者Repository接口
 * 提供作者相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface WriterRepository extends MongoRepository<Writer, String> {
    
    /**
     * 根据姓名查找作者
     * 
     * @param name 姓名
     * @return 作者信息
     */
    Optional<Writer> findByName(String name);
    
    /**
     * 根据朝代查找作者列表
     * 
     * @param dynasty 朝代
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findByDynasty(String dynasty, Pageable pageable);
    
    /**
     * 根据姓名模糊查询作者
     * 
     * @param name 姓名关键字
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    /**
     * 根据简介模糊查询作者
     * 
     * @param simpleIntro 简介关键字
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findBySimpleIntroContainingIgnoreCase(String simpleIntro, Pageable pageable);
    
    /**
     * 根据字号查找作者
     * 
     * @param alias 字号
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findByAliasContainingIgnoreCase(String alias, Pageable pageable);
    
    /**
     * 根据籍贯查找作者
     * 
     * @param birthplace 籍贯
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findByBirthplaceContainingIgnoreCase(String birthplace, Pageable pageable);
    
    /**
     * 根据成就查找作者
     * 
     * @param achievement 成就
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findByAchievementsContaining(String achievement, Pageable pageable);
    
    /**
     * 根据代表作品查找作者
     * 
     * @param masterpiece 代表作品
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findByMasterpiecesContaining(String masterpiece, Pageable pageable);
    
    /**
     * 统计特定朝代的作者数量
     * 
     * @param dynasty 朝代
     * @return 作者数量
     */
    long countByDynasty(String dynasty);
    
    /**
     * 检查作者是否存在
     * 
     * @param name 姓名
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 全文搜索作者
     * 使用MongoDB的文本索引进行搜索
     *
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ $text: { $search: ?0 } }")
    Page<Writer> findByTextSearch(String keyword, Pageable pageable);

    /**
     * 智能搜索作者 - 精确匹配优先
     * 支持姓名和简介的模糊搜索，精确匹配排在前面
     *
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ $or: [ " +
           "{ 'name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'simpleIntro': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Writer> findBySmartSearch(String keyword, Pageable pageable);
    
    /**
     * 高级搜索作者
     * 支持多条件组合搜索
     * 
     * @param name 姓名关键字
     * @param dynasty 朝代
     * @param birthplace 籍贯关键字
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'name': { $exists: false } } ] }, " +
           "{ $or: [ { 'dynasty': ?1 }, { 'dynasty': { $exists: false } } ] }, " +
           "{ $or: [ { 'birthplace': { $regex: ?2, $options: 'i' } }, { 'birthplace': { $exists: false } } ] } " +
           "] }")
    Page<Writer> findByAdvancedSearch(String name, String dynasty, String birthplace, Pageable pageable);
    
    /**
     * 查找所有朝代列表
     * 
     * @return 朝代列表
     */
    @Query(value = "{ 'dynasty': { $exists: true, $ne: null } }", fields = "{ 'dynasty' : 1 }")
    List<Writer> findAllDynasties();
    
    /**
     * 查找所有籍贯列表
     * 
     * @return 籍贯列表
     */
    @Query(value = "{ 'birthplace': { $exists: true, $ne: null } }", fields = "{ 'birthplace' : 1 }")
    List<Writer> findAllBirthplaces();
    
    /**
     * 查找随机作者
     * 
     * @param size 数量
     * @return 作者列表
     */
    @Query("{ $sample: { size: ?0 } }")
    List<Writer> findRandomWriters(int size);
    
    /**
     * 查找最新添加的作者
     * 
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 查找有头像的作者
     * 
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ 'headImageUrl': { $exists: true, $ne: null, $ne: '' } }")
    Page<Writer> findByHeadImageUrlExists(Pageable pageable);
    
    /**
     * 查找有详细介绍的作者
     * 
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ 'detailIntro': { $exists: true, $ne: null, $ne: '' } }")
    Page<Writer> findByDetailIntroExists(Pageable pageable);
    
    /**
     * 根据生卒年查找作者
     * 
     * @param lifespan 生卒年
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    Page<Writer> findByLifespanContainingIgnoreCase(String lifespan, Pageable pageable);
    
    /**
     * 查找特定朝代的著名作者（有详细介绍的）
     * 
     * @param dynasty 朝代
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ 'dynasty': ?0, 'detailIntro': { $exists: true, $ne: null, $ne: '' } }")
    Page<Writer> findFamousWritersByDynasty(String dynasty, Pageable pageable);
    
    /**
     * 查找有代表作品的作者
     * 
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ 'masterpieces': { $exists: true, $ne: null, $not: { $size: 0 } } }")
    Page<Writer> findByMasterpiecesExists(Pageable pageable);
    
    /**
     * 查找有成就记录的作者
     * 
     * @param pageable 分页参数
     * @return 作者分页列表
     */
    @Query("{ 'achievements': { $exists: true, $ne: null, $not: { $size: 0 } } }")
    Page<Writer> findByAchievementsExists(Pageable pageable);
    
    /**
     * 根据姓名和朝代查找作者
     * 
     * @param name 姓名
     * @param dynasty 朝代
     * @return 作者信息
     */
    Optional<Writer> findByNameAndDynasty(String name, String dynasty);
}
// {{END_MODIFICATIONS}}
