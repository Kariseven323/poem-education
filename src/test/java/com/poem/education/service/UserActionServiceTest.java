package com.poem.education.service;

import com.poem.education.dto.request.UserActionRequest;
import com.poem.education.dto.response.PageResult;
import com.poem.education.dto.response.UserActionDTO;
import com.poem.education.entity.mysql.UserAction;
import com.poem.education.repository.mysql.UserActionRepository;
import com.poem.education.service.impl.UserActionServiceImpl;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserActionService单元测试
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@ExtendWith(MockitoExtension.class)
class UserActionServiceTest {
    
    @Mock
    private UserActionRepository userActionRepository;
    
    @InjectMocks
    private UserActionServiceImpl userActionService;
    
    private UserAction testUserAction;
    private UserActionRequest actionRequest;
    
    @BeforeEach
    void setUp() {
        testUserAction = new UserAction();
        testUserAction.setId(1L);
        testUserAction.setUserId(1L);
        testUserAction.setTargetId("507f1f77bcf86cd799439011");
        testUserAction.setTargetType("guwen");
        testUserAction.setActionType("like");
        testUserAction.setCreatedAt(LocalDateTime.now());
        
        actionRequest = new UserActionRequest();
        actionRequest.setTargetId("507f1f77bcf86cd799439011");
        actionRequest.setTargetType("guwen");
        actionRequest.setActionType("like");
    }
    
    @Test
    void testRecordAction_NewAction_Success() {
        // Given
        when(userActionRepository.findByUserIdAndTargetIdAndTargetTypeAndActionType(
                anyLong(), anyString(), anyString(), anyString())).thenReturn(Optional.empty());
        when(userActionRepository.save(any(UserAction.class))).thenReturn(testUserAction);
        
        // When
        UserActionDTO result = userActionService.recordAction(1L, actionRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getTargetId()).isEqualTo("507f1f77bcf86cd799439011");
        assertThat(result.getActionType()).isEqualTo("like");
        
        verify(userActionRepository).findByUserIdAndTargetIdAndTargetTypeAndActionType(
                1L, "507f1f77bcf86cd799439011", "guwen", "like");
        verify(userActionRepository).save(any(UserAction.class));
    }
    
    @Test
    void testRecordAction_ExistingAction_ReturnExisting() {
        // Given
        when(userActionRepository.findByUserIdAndTargetIdAndTargetTypeAndActionType(
                anyLong(), anyString(), anyString(), anyString())).thenReturn(Optional.of(testUserAction));
        
        // When
        UserActionDTO result = userActionService.recordAction(1L, actionRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        
        verify(userActionRepository).findByUserIdAndTargetIdAndTargetTypeAndActionType(
                1L, "507f1f77bcf86cd799439011", "guwen", "like");
        verify(userActionRepository, never()).save(any(UserAction.class));
    }
    
    @Test
    void testCancelAction_Success() {
        // Given
        when(userActionRepository.findByUserIdAndTargetIdAndTargetTypeAndActionType(
                anyLong(), anyString(), anyString(), anyString())).thenReturn(Optional.of(testUserAction));
        
        // When
        boolean result = userActionService.cancelAction(1L, "507f1f77bcf86cd799439011", "guwen", "like");
        
        // Then
        assertThat(result).isTrue();
        
        verify(userActionRepository).findByUserIdAndTargetIdAndTargetTypeAndActionType(
                1L, "507f1f77bcf86cd799439011", "guwen", "like");
        verify(userActionRepository).delete(testUserAction);
    }
    
    @Test
    void testCancelAction_NotFound() {
        // Given
        when(userActionRepository.findByUserIdAndTargetIdAndTargetTypeAndActionType(
                anyLong(), anyString(), anyString(), anyString())).thenReturn(Optional.empty());
        
        // When
        boolean result = userActionService.cancelAction(1L, "507f1f77bcf86cd799439011", "guwen", "like");
        
        // Then
        assertThat(result).isFalse();
        
        verify(userActionRepository).findByUserIdAndTargetIdAndTargetTypeAndActionType(
                1L, "507f1f77bcf86cd799439011", "guwen", "like");
        verify(userActionRepository, never()).delete(any(UserAction.class));
    }
    
    @Test
    void testHasAction_True() {
        // Given
        when(userActionRepository.existsByUserIdAndTargetIdAndTargetTypeAndActionType(
                anyLong(), anyString(), anyString(), anyString())).thenReturn(true);
        
        // When
        boolean result = userActionService.hasAction(1L, "507f1f77bcf86cd799439011", "guwen", "like");
        
        // Then
        assertThat(result).isTrue();
        
        verify(userActionRepository).existsByUserIdAndTargetIdAndTargetTypeAndActionType(
                1L, "507f1f77bcf86cd799439011", "guwen", "like");
    }
    
    @Test
    void testHasAction_False() {
        // Given
        when(userActionRepository.existsByUserIdAndTargetIdAndTargetTypeAndActionType(
                anyLong(), anyString(), anyString(), anyString())).thenReturn(false);
        
        // When
        boolean result = userActionService.hasAction(1L, "507f1f77bcf86cd799439011", "guwen", "like");
        
        // Then
        assertThat(result).isFalse();
        
        verify(userActionRepository).existsByUserIdAndTargetIdAndTargetTypeAndActionType(
                1L, "507f1f77bcf86cd799439011", "guwen", "like");
    }
    
    @Test
    void testGetUserActions_Success() {
        // Given
        List<UserAction> actions = Arrays.asList(testUserAction);
        Page<UserAction> actionPage = new PageImpl<>(actions, Pageable.unpaged(), actions.size());
        when(userActionRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(actionPage);
        
        // When
        PageResult<UserActionDTO> result = userActionService.getUserActions(1L, 1, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getUserId()).isEqualTo(1L);
        
        verify(userActionRepository).findByUserId(eq(1L), any(Pageable.class));
    }
    
    @Test
    void testGetUserActionsByType_Success() {
        // Given
        List<UserAction> actions = Arrays.asList(testUserAction);
        Page<UserAction> actionPage = new PageImpl<>(actions, Pageable.unpaged(), actions.size());
        when(userActionRepository.findByUserIdAndActionType(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(actionPage);
        
        // When
        PageResult<UserActionDTO> result = userActionService.getUserActionsByType(1L, "like", 1, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getActionType()).isEqualTo("like");
        
        verify(userActionRepository).findByUserIdAndActionType(eq(1L), eq("like"), any(Pageable.class));
    }
    
    @Test
    void testGetTargetActionStats_Success() {
        // Given
        when(userActionRepository.countByTargetIdAndTargetTypeAndActionType(
                anyString(), anyString(), anyString())).thenReturn(10L, 5L, 20L, 3L);
        
        // When
        Object result = userActionService.getTargetActionStats("507f1f77bcf86cd799439011", "guwen");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Map.class);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) result;
        assertThat(stats.get("targetId")).isEqualTo("507f1f77bcf86cd799439011");
        assertThat(stats.get("targetType")).isEqualTo("guwen");
        assertThat(stats.get("likeCount")).isEqualTo(10L);
        assertThat(stats.get("favoriteCount")).isEqualTo(5L);
        assertThat(stats.get("viewCount")).isEqualTo(20L);
        assertThat(stats.get("shareCount")).isEqualTo(3L);
        assertThat(stats.get("totalCount")).isEqualTo(38L);
        
        verify(userActionRepository, times(4)).countByTargetIdAndTargetTypeAndActionType(
                anyString(), anyString(), anyString());
    }
    
    @Test
    void testGetUserActionsByTimeRange_Success() {
        // Given
        List<UserAction> actions = Arrays.asList(testUserAction);
        Page<UserAction> actionPage = new PageImpl<>(actions, Pageable.unpaged(), actions.size());
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        
        when(userActionRepository.findByUserIdAndCreatedAtBetween(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(actionPage);
        
        // When
        PageResult<UserActionDTO> result = userActionService.getUserActionsByTimeRange(1L, startTime, endTime, 1, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        
        verify(userActionRepository).findByUserIdAndCreatedAtBetween(eq(1L), eq(startTime), eq(endTime), any(Pageable.class));
    }
    
    @Test
    void testGetHotContentByLikes_Success() {
        // Given
        Object[] result1 = {"507f1f77bcf86cd799439011", 10L};
        Object[] result2 = {"507f1f77bcf86cd799439012", 8L};
        List<Object[]> results = Arrays.asList(result1, result2);
        
        when(userActionRepository.findHotContentsByAction(
                anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(results);
        
        // When
        List<Object> hotContent = userActionService.getHotContentByLikes("guwen", 10);
        
        // Then
        assertThat(hotContent).isNotNull();
        assertThat(hotContent).hasSize(2);
        
        verify(userActionRepository).findHotContentsByAction(
                eq("guwen"), eq(UserAction.ActionType.LIKE), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }
    
    @Test
    void testGetRecentActions_Success() {
        // Given
        List<UserAction> actions = Arrays.asList(testUserAction);
        Page<UserAction> actionPage = new PageImpl<>(actions, Pageable.unpaged(), actions.size());
        when(userActionRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(actionPage);
        
        // When
        List<UserActionDTO> result = userActionService.getRecentActions(1L, 10);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        
        verify(userActionRepository).findByUserId(eq(1L), any(Pageable.class));
    }
    
    @Test
    void testBatchRecordActions_Success() {
        // Given
        UserActionRequest request2 = new UserActionRequest();
        request2.setTargetId("507f1f77bcf86cd799439012");
        request2.setTargetType("guwen");
        request2.setActionType("favorite");
        
        List<UserActionRequest> requests = Arrays.asList(actionRequest, request2);
        
        when(userActionRepository.findByUserIdAndTargetIdAndTargetTypeAndActionType(
                anyLong(), anyString(), anyString(), anyString())).thenReturn(Optional.empty());
        when(userActionRepository.save(any(UserAction.class))).thenReturn(testUserAction);
        
        // When
        List<UserActionDTO> result = userActionService.batchRecordActions(1L, requests);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        verify(userActionRepository, times(2)).save(any(UserAction.class));
    }
    
    @Test
    void testCountUserActions_WithActionType() {
        // Given
        when(userActionRepository.countByUserIdAndActionType(anyLong(), anyString())).thenReturn(15L);
        
        // When
        long result = userActionService.countUserActions(1L, "like");
        
        // Then
        assertThat(result).isEqualTo(15L);
        
        verify(userActionRepository).countByUserIdAndActionType(1L, "like");
        verify(userActionRepository, never()).countByUserId(anyLong());
    }
    
    @Test
    void testCountUserActions_WithoutActionType() {
        // Given
        when(userActionRepository.countByUserId(anyLong())).thenReturn(50L);
        
        // When
        long result = userActionService.countUserActions(1L, null);
        
        // Then
        assertThat(result).isEqualTo(50L);
        
        verify(userActionRepository).countByUserId(1L);
        verify(userActionRepository, never()).countByUserIdAndActionType(anyLong(), anyString());
    }
    
    @Test
    void testCountTargetActions_Success() {
        // Given
        when(userActionRepository.countByTargetIdAndTargetTypeAndActionType(
                anyString(), anyString(), anyString())).thenReturn(25L);
        
        // When
        long result = userActionService.countTargetActions("507f1f77bcf86cd799439011", "guwen", "like");
        
        // Then
        assertThat(result).isEqualTo(25L);
        
        verify(userActionRepository).countByTargetIdAndTargetTypeAndActionType(
                "507f1f77bcf86cd799439011", "guwen", "like");
    }
    
    @Test
    void testDeleteUserActions_Success() {
        // Given
        when(userActionRepository.deleteByUserId(anyLong())).thenReturn(10L);
        
        // When
        long result = userActionService.deleteUserActions(1L);
        
        // Then
        assertThat(result).isEqualTo(10L);
        
        verify(userActionRepository).deleteByUserId(1L);
    }
}
