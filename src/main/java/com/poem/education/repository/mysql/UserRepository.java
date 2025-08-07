// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "f00f17a5-84b0-45ee-9828-816019892137"
//   Timestamp: "2025-08-07T11:15:00+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "Spring Data JPA Repository最佳实践"
//   Quality_Check: "编译通过，查询方法符合业务需求。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.repository.mysql;

import com.poem.education.entity.mysql.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户Repository接口
 * 提供用户相关的数据访问方法
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据用户名或邮箱查找用户
     * 
     * @param username 用户名
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 根据状态查找用户列表
     * 
     * @param status 状态
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 根据昵称模糊查询用户
     * 
     * @param nickname 昵称关键字
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByNicknameContainingIgnoreCase(String nickname, Pageable pageable);
    
    /**
     * 根据用户名模糊查询用户
     * 
     * @param username 用户名关键字
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    
    /**
     * 查找指定时间范围内注册的用户
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户列表
     */
    List<User> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定状态的用户数量
     * 
     * @param status 状态
     * @return 用户数量
     */
    long countByStatus(Integer status);
    
    /**
     * 统计指定时间范围内注册的用户数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户数量
     */
    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 更新用户状态
     * 
     * @param userId 用户ID
     * @param status 新状态
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 批量更新用户状态
     * 
     * @param userIds 用户ID列表
     * @param status 新状态
     * @return 影响行数
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id IN :userIds")
    int updateUsersStatus(@Param("userIds") List<Long> userIds, @Param("status") Integer status);
    
    /**
     * 根据关键字搜索用户（用户名、昵称、邮箱）
     * 
     * @param keyword 搜索关键字
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 查找活跃用户（最近更新时间在指定时间之后）
     * 
     * @param since 时间点
     * @param pageable 分页参数
     * @return 用户分页列表
     */
    Page<User> findByUpdatedAtAfterAndStatus(LocalDateTime since, Integer status, Pageable pageable);
    
    /**
     * 查找最新注册的用户
     * 
     * @param limit 限制数量
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.status = 1 ORDER BY u.createdAt DESC")
    List<User> findLatestUsers(Pageable pageable);
}
// {{END_MODIFICATIONS}}
