package com.poem.education.util;

import com.poem.education.dto.request.*;
import com.poem.education.dto.response.*;
import com.poem.education.entity.mongodb.Comment;
import com.poem.education.entity.mongodb.Guwen;
import com.poem.education.entity.mysql.User;
import com.poem.education.entity.mysql.UserAction;
import com.poem.education.entity.mysql.UserFavorite;

import java.time.LocalDateTime;

/**
 * 测试数据构建工具类
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
public class TestDataBuilder {
    
    /**
     * 创建测试用户
     */
    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("encodedPassword");
        user.setNickname("Test User");
        user.setAvatar("avatar.jpg");
        user.setBio("Test bio");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
    
    /**
     * 创建测试用户DTO
     */
    public static UserDTO createTestUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setNickname("Test User");
        userDTO.setAvatar("avatar.jpg");
        userDTO.setBio("Test bio");
        userDTO.setStatus(1);
        userDTO.setCreatedAt(LocalDateTime.now());
        userDTO.setUpdatedAt(LocalDateTime.now());
        return userDTO;
    }
    
    /**
     * 创建注册请求
     */
    public static RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setNickname("Test User");
        return request;
    }
    
    /**
     * 创建登录请求
     */
    public static LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        return request;
    }
    
    /**
     * 创建更新个人信息请求
     */
    public static UpdateProfileRequest createUpdateProfileRequest() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNickname("Updated Nickname");
        request.setAvatar("new-avatar.jpg");
        request.setBio("Updated bio");
        return request;
    }
    
    /**
     * 创建登录响应
     */
    public static LoginResponse createLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setAccessToken("jwt-token");
        response.setTokenType("Bearer");
        response.setExpiresIn(3600L);
        response.setUser(createTestUserDTO());
        return response;
    }
    
    /**
     * 创建测试古文
     */
    public static Guwen createTestGuwen() {
        Guwen guwen = new Guwen();
        guwen.setId("507f1f77bcf86cd799439011");
        guwen.setTitle("静夜思");
        guwen.setDynasty("唐");
        guwen.setWriter("李白");
        guwen.setContent("床前明月光，疑是地上霜。举头望明月，低头思故乡。");
        guwen.setType("诗");
        guwen.setRemark("李白的代表作之一");
        guwen.setShangxi("这首诗表达了诗人对故乡的思念之情");
        guwen.setTranslation("床前洒满了明亮的月光，以为是地上结了霜。抬头看看天上的明月，低头思念远方的故乡。");
        guwen.setAudioUrl("http://example.com/audio/jingyesi.mp3");
        guwen.setCreatedAt(LocalDateTime.now());
        guwen.setUpdatedAt(LocalDateTime.now());
        return guwen;
    }
    
    /**
     * 创建古文DTO
     */
    public static GuwenDTO createTestGuwenDTO() {
        GuwenDTO guwenDTO = new GuwenDTO();
        guwenDTO.setId("507f1f77bcf86cd799439011");
        guwenDTO.setTitle("静夜思");
        guwenDTO.setDynasty("唐");
        guwenDTO.setWriter("李白");
        guwenDTO.setContent("床前明月光，疑是地上霜。举头望明月，低头思故乡。");
        guwenDTO.setType("诗");
        guwenDTO.setCreatedAt(LocalDateTime.now());
        guwenDTO.setUpdatedAt(LocalDateTime.now());
        return guwenDTO;
    }
    
    /**
     * 创建古文搜索请求
     */
    public static GuwenSearchRequest createGuwenSearchRequest() {
        GuwenSearchRequest request = new GuwenSearchRequest();
        request.setKeyword("明月");
        request.setWriter("李白");
        request.setDynasty("唐");
        request.setPage(1);
        request.setSize(20);
        return request;
    }
    
    /**
     * 创建测试评论
     */
    public static Comment createTestComment() {
        Comment comment = new Comment();
        comment.setId("507f1f77bcf86cd799439011");
        comment.setTargetId("507f1f77bcf86cd799439012");
        comment.setTargetType("guwen");
        comment.setUserId(1L);
        comment.setContent("这首诗写得真好！");
        comment.setLevel(1);
        comment.setPath("507f1f77bcf86cd799439011");
        comment.setStatus(1);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return comment;
    }
    
    /**
     * 创建评论请求
     */
    public static CommentRequest createCommentRequest() {
        CommentRequest request = new CommentRequest();
        request.setTargetId("507f1f77bcf86cd799439012");
        request.setTargetType("guwen");
        request.setContent("这首诗写得真好！");
        return request;
    }
    
    /**
     * 创建测试用户行为
     */
    public static UserAction createTestUserAction() {
        UserAction userAction = new UserAction();
        userAction.setId(1L);
        userAction.setUserId(1L);
        userAction.setTargetId("507f1f77bcf86cd799439011");
        userAction.setTargetType("guwen");
        userAction.setActionType("like");
        userAction.setCreatedAt(LocalDateTime.now());
        return userAction;
    }
    
    /**
     * 创建用户行为请求
     */
    public static UserActionRequest createUserActionRequest() {
        UserActionRequest request = new UserActionRequest();
        request.setTargetId("507f1f77bcf86cd799439011");
        request.setTargetType("guwen");
        request.setActionType("like");
        return request;
    }
    
    /**
     * 创建测试收藏
     */
    public static UserFavorite createTestUserFavorite() {
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setId(1L);
        userFavorite.setUserId(1L);
        userFavorite.setTargetId("507f1f77bcf86cd799439011");
        userFavorite.setTargetType("guwen");
        userFavorite.setFolderName("默认收藏夹");
        userFavorite.setNotes("很喜欢这首诗");
        userFavorite.setCreatedAt(LocalDateTime.now());
        return userFavorite;
    }
    
    /**
     * 创建收藏请求
     */
    public static FavoriteRequest createFavoriteRequest() {
        FavoriteRequest request = new FavoriteRequest();
        request.setTargetId("507f1f77bcf86cd799439011");
        request.setTargetType("guwen");
        request.setFolderName("默认收藏夹");
        request.setNotes("很喜欢这首诗");
        return request;
    }
    
    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> createPageResult(java.util.List<T> list, int page, int size, long total) {
        return PageResult.of(list, page, size, total);
    }
}
