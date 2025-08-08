package com.poem.education.service;

import com.poem.education.dto.request.CommentRequest;
import com.poem.education.dto.response.CommentDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.entity.mongodb.Comment;
import com.poem.education.entity.mysql.User;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mongodb.CommentRepository;
import com.poem.education.repository.mysql.UserRepository;
import com.poem.education.service.impl.CommentServiceImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CommentService单元测试
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    
    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private CommentServiceImpl commentService;
    
    private Comment testComment;
    private User testUser;
    private CommentRequest commentRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("Test User");
        testUser.setAvatar("avatar.jpg");
        
        testComment = new Comment();
        testComment.setId("507f1f77bcf86cd799439011");
        testComment.setTargetId(new ObjectId("507f1f77bcf86cd799439012"));
        testComment.setTargetType("guwen");
        testComment.setUserId(1L);
        testComment.setContent("这首诗写得真好！");
        testComment.setLevel(1);
        testComment.setPath("507f1f77bcf86cd799439011");
        testComment.setStatus(1);
        testComment.setLikeCount(0);
        testComment.setReplyCount(0);
        testComment.setCreatedAt(LocalDateTime.now());
        testComment.setUpdatedAt(LocalDateTime.now());
        
        commentRequest = new CommentRequest();
        commentRequest.setTargetId("507f1f77bcf86cd799439012");
        commentRequest.setTargetType("guwen");
        commentRequest.setContent("这首诗写得真好！");
    }
    
    @Test
    void testGetCommentsByTarget_Success() {
        // Given
        List<Comment> comments = Arrays.asList(testComment);
        Page<Comment> commentPage = new PageImpl<>(comments, Pageable.unpaged(), comments.size());
        when(commentRepository.findByTargetIdAndTargetTypeAndStatus(eq(new ObjectId("507f1f77bcf86cd799439012")), eq("guwen"), eq(1), any(Pageable.class)))
                .thenReturn(commentPage);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // When
        PageResult<CommentDTO> result = commentService.getCommentsByTarget("507f1f77bcf86cd799439012", "guwen", 1, 20);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getContent()).isEqualTo("这首诗写得真好！");

        verify(commentRepository).findByTargetIdAndTargetTypeAndStatus(eq(new ObjectId("507f1f77bcf86cd799439012")), eq("guwen"), eq(1), any(Pageable.class));
    }
    
    @Test
    void testCreateComment_TopLevel_Success() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        CommentDTO result = commentService.createComment(1L, commentRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("这首诗写得真好！");
        assertThat(result.getUserInfo()).isNotNull();
        assertThat(result.getUserInfo().getNickname()).isEqualTo("Test User");
        
        verify(userRepository, times(2)).findById(1L); // 创建评论时调用一次，转换DTO时调用一次
        verify(commentRepository, times(2)).save(any(Comment.class)); // 保存两次：第一次创建，第二次设置path
    }
    
    @Test
    void testCreateComment_Reply_Success() {
        // Given
        Comment parentComment = new Comment();
        parentComment.setId("507f1f77bcf86cd799439010");
        parentComment.setLevel(1);
        parentComment.setPath("507f1f77bcf86cd799439010");
        parentComment.setReplyCount(0);
        
        commentRequest.setParentId("507f1f77bcf86cd799439010");
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(commentRepository.findById("507f1f77bcf86cd799439010")).thenReturn(Optional.of(parentComment));
        when(commentRepository.countByParentIdAndStatus("507f1f77bcf86cd799439010", 1)).thenReturn(0L);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        CommentDTO result = commentService.createComment(1L, commentRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("这首诗写得真好！");
        
        verify(userRepository, times(2)).findById(1L); // 创建评论时调用一次，转换DTO时调用一次
        verify(commentRepository, times(3)).findById("507f1f77bcf86cd799439010"); // 创建评论时调用1次，计算路径时调用1次，更新回复数时调用1次
        verify(commentRepository, times(2)).save(any(Comment.class)); // 保存新评论1次，更新父评论回复数1次
    }
    
    @Test
    void testCreateComment_UserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> commentService.createComment(1L, commentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户不存在");
        
        verify(userRepository).findById(1L);
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    void testCreateComment_ParentNotFound() {
        // Given
        commentRequest.setParentId("nonexistent");
        
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(commentRepository.findById("nonexistent")).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> commentService.createComment(1L, commentRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("父评论不存在");
        
        verify(userRepository).findById(1L);
        verify(commentRepository).findById("nonexistent");
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    void testGetCommentById_Success() {
        // Given
        when(commentRepository.findById(anyString())).thenReturn(Optional.of(testComment));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        
        // When
        CommentDTO result = commentService.getCommentById("507f1f77bcf86cd799439011");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("507f1f77bcf86cd799439011");
        assertThat(result.getContent()).isEqualTo("这首诗写得真好！");
        
        verify(commentRepository).findById("507f1f77bcf86cd799439011");
    }
    
    @Test
    void testGetCommentById_NotFound() {
        // Given
        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> commentService.getCommentById("nonexistent"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("评论不存在");
        
        verify(commentRepository).findById("nonexistent");
    }
    
    @Test
    void testDeleteComment_Success() {
        // Given
        when(commentRepository.findById(anyString())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        commentService.deleteComment("507f1f77bcf86cd799439011", 1L);
        
        // Then
        verify(commentRepository).findById("507f1f77bcf86cd799439011");
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testDeleteComment_NotFound() {
        // Given
        when(commentRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> commentService.deleteComment("nonexistent", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("评论不存在");
        
        verify(commentRepository).findById("nonexistent");
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    void testDeleteComment_NoPermission() {
        // Given
        when(commentRepository.findById(anyString())).thenReturn(Optional.of(testComment));
        
        // When & Then
        assertThatThrownBy(() -> commentService.deleteComment("507f1f77bcf86cd799439011", 2L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("无权限删除此评论");
        
        verify(commentRepository).findById("507f1f77bcf86cd799439011");
        verify(commentRepository, never()).save(any(Comment.class));
    }
    
    @Test
    void testLikeComment_Success() {
        // Given
        when(commentRepository.findById(anyString())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        boolean result = commentService.likeComment("507f1f77bcf86cd799439011", 1L);
        
        // Then
        assertThat(result).isTrue();
        verify(commentRepository).findById("507f1f77bcf86cd799439011");
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testUnlikeComment_Success() {
        // Given
        testComment.setLikeCount(1);
        when(commentRepository.findById(anyString())).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        boolean result = commentService.unlikeComment("507f1f77bcf86cd799439011", 1L);
        
        // Then
        assertThat(result).isTrue();
        verify(commentRepository).findById("507f1f77bcf86cd799439011");
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testGetUserComments_Success() {
        // Given
        List<Comment> comments = Arrays.asList(testComment);
        Page<Comment> commentPage = new PageImpl<>(comments, Pageable.unpaged(), comments.size());
        when(commentRepository.findByUserIdAndStatus(eq(1L), eq(1), any(Pageable.class)))
                .thenReturn(commentPage);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        
        // When
        PageResult<CommentDTO> result = commentService.getUserComments(1L, 1, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        
        verify(commentRepository).findByUserIdAndStatus(eq(1L), eq(1), any(Pageable.class));
    }
    
    @Test
    void testGetHotComments_Success() {
        // Given
        List<Comment> comments = Arrays.asList(testComment);
        Page<Comment> commentPage = new PageImpl<>(comments, Pageable.unpaged(), comments.size());
        when(commentRepository.findByTargetIdAndTargetTypeAndStatus(any(ObjectId.class), anyString(), anyInt(), any(Pageable.class)))
                .thenReturn(commentPage);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // When
        List<CommentDTO> result = commentService.getHotComments("507f1f77bcf86cd799439012", "guwen", 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(commentRepository).findByTargetIdAndTargetTypeAndStatus(eq(new ObjectId("507f1f77bcf86cd799439012")), eq("guwen"), eq(1), any(Pageable.class));
    }
    
    @Test
    void testGetLatestComments_Success() {
        // Given
        List<Comment> comments = Arrays.asList(testComment);
        Page<Comment> commentPage = new PageImpl<>(comments, Pageable.unpaged(), comments.size());
        when(commentRepository.findByTargetIdAndTargetTypeAndStatus(eq(new ObjectId("507f1f77bcf86cd799439012")), eq("guwen"), eq(1), any(Pageable.class)))
                .thenReturn(commentPage);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // When
        List<CommentDTO> result = commentService.getLatestComments("507f1f77bcf86cd799439012", "guwen", 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(commentRepository).findByTargetIdAndTargetTypeAndStatus(eq(new ObjectId("507f1f77bcf86cd799439012")), eq("guwen"), eq(1), any(Pageable.class));
    }
    
    @Test
    void testGetCommentCount_Success() {
        // Given
        when(commentRepository.countByTargetIdAndTargetTypeAndStatus(anyString(), anyString(), anyInt()))
                .thenReturn(5L);
        
        // When
        long result = commentService.getCommentCount("507f1f77bcf86cd799439012", "guwen");
        
        // Then
        assertThat(result).isEqualTo(5L);
        
        verify(commentRepository).countByTargetIdAndTargetTypeAndStatus("507f1f77bcf86cd799439012", "guwen", 1);
    }
    
    @Test
    void testCalculateCommentPath_Success() {
        // Given
        Comment parentComment = new Comment();
        parentComment.setPath("507f1f77bcf86cd799439010");
        
        when(commentRepository.findById("507f1f77bcf86cd799439010")).thenReturn(Optional.of(parentComment));
        when(commentRepository.countByParentIdAndStatus("507f1f77bcf86cd799439010", 1)).thenReturn(2L);
        
        // When
        String result = commentService.calculateCommentPath("507f1f77bcf86cd799439010");
        
        // Then
        assertThat(result).isEqualTo("507f1f77bcf86cd799439010.3");
        
        verify(commentRepository).findById("507f1f77bcf86cd799439010");
        verify(commentRepository).countByParentIdAndStatus("507f1f77bcf86cd799439010", 1);
    }
    
    @Test
    void testUpdateReplyCount_Success() {
        // Given
        Comment parentComment = new Comment();
        parentComment.setReplyCount(5);
        
        when(commentRepository.findById("507f1f77bcf86cd799439010")).thenReturn(Optional.of(parentComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(parentComment);
        
        // When
        commentService.updateReplyCount("507f1f77bcf86cd799439010", 1);
        
        // Then
        verify(commentRepository).findById("507f1f77bcf86cd799439010");
        verify(commentRepository).save(any(Comment.class));
    }
    
    @Test
    void testUpdateLikeCount_Success() {
        // Given
        when(commentRepository.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        
        // When
        commentService.updateLikeCount("507f1f77bcf86cd799439011", 1);
        
        // Then
        verify(commentRepository).findById("507f1f77bcf86cd799439011");
        verify(commentRepository).save(any(Comment.class));
    }
}
