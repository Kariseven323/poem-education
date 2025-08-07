package com.poem.education.service;

import com.poem.education.dto.request.GuwenSearchRequest;
import com.poem.education.dto.response.GuwenDTO;
import com.poem.education.dto.response.PageResult;
import com.poem.education.entity.mongodb.Guwen;
import com.poem.education.exception.BusinessException;
import com.poem.education.repository.mongodb.GuwenRepository;
import com.poem.education.service.impl.GuwenServiceImpl;
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
 * GuwenService单元测试
 * 
 * @author poem-education-team
 * @since 2025-08-07
 */
@ExtendWith(MockitoExtension.class)
class GuwenServiceTest {
    
    @Mock
    private GuwenRepository guwenRepository;
    
    @InjectMocks
    private GuwenServiceImpl guwenService;
    
    private Guwen testGuwen;
    private List<Guwen> guwenList;
    
    @BeforeEach
    void setUp() {
        testGuwen = new Guwen();
        testGuwen.setId("507f1f77bcf86cd799439011");
        testGuwen.setTitle("静夜思");
        testGuwen.setDynasty("唐");
        testGuwen.setWriter("李白");
        testGuwen.setContent("床前明月光，疑是地上霜。举头望明月，低头思故乡。");
        testGuwen.setType("诗");
        testGuwen.setCreatedAt(LocalDateTime.now());
        testGuwen.setUpdatedAt(LocalDateTime.now());
        
        Guwen guwen2 = new Guwen();
        guwen2.setId("507f1f77bcf86cd799439012");
        guwen2.setTitle("春晓");
        guwen2.setDynasty("唐");
        guwen2.setWriter("孟浩然");
        guwen2.setContent("春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。");
        guwen2.setType("诗");
        guwen2.setCreatedAt(LocalDateTime.now());
        guwen2.setUpdatedAt(LocalDateTime.now());
        
        guwenList = Arrays.asList(testGuwen, guwen2);
    }
    
    @Test
    void testGetGuwenList_Success() {
        // Given
        Page<Guwen> guwenPage = new PageImpl<>(guwenList, Pageable.unpaged(), guwenList.size());
        when(guwenRepository.findAll(any(Pageable.class))).thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.getGuwenList(1, 20, null, null, null);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(20);
        
        verify(guwenRepository).findAll(any(Pageable.class));
    }
    
    @Test
    void testGetGuwenList_WithDynasty() {
        // Given
        Page<Guwen> guwenPage = new PageImpl<>(Arrays.asList(testGuwen), Pageable.unpaged(), 1);
        when(guwenRepository.findByDynasty(eq("唐"), any(Pageable.class))).thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.getGuwenList(1, 20, "唐", null, null);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getDynasty()).isEqualTo("唐");
        
        verify(guwenRepository).findByDynasty(eq("唐"), any(Pageable.class));
    }
    
    @Test
    void testGetGuwenList_WithWriter() {
        // Given
        Page<Guwen> guwenPage = new PageImpl<>(Arrays.asList(testGuwen), Pageable.unpaged(), 1);
        when(guwenRepository.findByWriter(eq("李白"), any(Pageable.class))).thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.getGuwenList(1, 20, null, "李白", null);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getWriter()).isEqualTo("李白");
        
        verify(guwenRepository).findByWriter(eq("李白"), any(Pageable.class));
    }
    
    @Test
    void testGetGuwenList_WithWriterAndDynasty() {
        // Given
        Page<Guwen> guwenPage = new PageImpl<>(Arrays.asList(testGuwen), Pageable.unpaged(), 1);
        when(guwenRepository.findByWriterAndDynasty(eq("李白"), eq("唐"), any(Pageable.class)))
                .thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.getGuwenList(1, 20, "唐", "李白", null);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getWriter()).isEqualTo("李白");
        assertThat(result.getList().get(0).getDynasty()).isEqualTo("唐");
        
        verify(guwenRepository).findByWriterAndDynasty(eq("李白"), eq("唐"), any(Pageable.class));
    }
    
    @Test
    void testGetGuwenById_Success() {
        // Given
        when(guwenRepository.findById(anyString())).thenReturn(Optional.of(testGuwen));
        
        // When
        GuwenDTO result = guwenService.getGuwenById("507f1f77bcf86cd799439011");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("507f1f77bcf86cd799439011");
        assertThat(result.getTitle()).isEqualTo("静夜思");
        assertThat(result.getWriter()).isEqualTo("李白");
        
        verify(guwenRepository).findById("507f1f77bcf86cd799439011");
    }
    
    @Test
    void testGetGuwenById_NotFound() {
        // Given
        when(guwenRepository.findById(anyString())).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> guwenService.getGuwenById("nonexistent"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("古文不存在");
        
        verify(guwenRepository).findById("nonexistent");
    }
    
    @Test
    void testSearchGuwen_WithKeyword() {
        // Given
        GuwenSearchRequest request = new GuwenSearchRequest();
        request.setKeyword("明月");
        request.setPage(1);
        request.setSize(20);
        
        Page<Guwen> guwenPage = new PageImpl<>(Arrays.asList(testGuwen), Pageable.unpaged(), 1);
        when(guwenRepository.findByTextSearch(eq("明月"), any(Pageable.class))).thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.searchGuwen(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getContent()).contains("明月");
        
        verify(guwenRepository).findByTextSearch(eq("明月"), any(Pageable.class));
    }
    
    @Test
    void testSearchGuwen_AdvancedSearch() {
        // Given
        GuwenSearchRequest request = new GuwenSearchRequest();
        request.setWriter("李白");
        request.setDynasty("唐");
        request.setPage(1);
        request.setSize(20);
        
        Page<Guwen> guwenPage = new PageImpl<>(Arrays.asList(testGuwen), Pageable.unpaged(), 1);
        when(guwenRepository.findByAdvancedSearch(any(), eq("李白"), eq("唐"), any(), any(Pageable.class)))
                .thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.searchGuwen(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        
        verify(guwenRepository).findByAdvancedSearch(eq(null), eq("李白"), eq("唐"), eq(null), any(Pageable.class));
    }
    
    @Test
    void testGetHotGuwen_Success() {
        // Given
        Page<Guwen> guwenPage = new PageImpl<>(guwenList, Pageable.unpaged(), guwenList.size());
        when(guwenRepository.findAll(any(Pageable.class))).thenReturn(guwenPage);
        
        // When
        List<GuwenDTO> result = guwenService.getHotGuwen("daily", 10);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        verify(guwenRepository).findAll(any(Pageable.class));
    }
    
    @Test
    void testGetRandomGuwen_Success() {
        // Given
        when(guwenRepository.findRandomGuwen(anyInt())).thenReturn(guwenList);
        
        // When
        List<GuwenDTO> result = guwenService.getRandomGuwen(5);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        
        verify(guwenRepository).findRandomGuwen(5);
    }
    
    @Test
    void testGetGuwenByWriter_Success() {
        // Given
        Page<Guwen> guwenPage = new PageImpl<>(Arrays.asList(testGuwen), Pageable.unpaged(), 1);
        when(guwenRepository.findByWriter(eq("李白"), any(Pageable.class))).thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.getGuwenByWriter("李白", 1, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(1);
        assertThat(result.getList().get(0).getWriter()).isEqualTo("李白");
        
        verify(guwenRepository).findByWriter(eq("李白"), any(Pageable.class));
    }
    
    @Test
    void testGetGuwenByDynasty_Success() {
        // Given
        Page<Guwen> guwenPage = new PageImpl<>(guwenList, Pageable.unpaged(), guwenList.size());
        when(guwenRepository.findByDynasty(eq("唐"), any(Pageable.class))).thenReturn(guwenPage);
        
        // When
        PageResult<GuwenDTO> result = guwenService.getGuwenByDynasty("唐", 1, 20);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getList()).hasSize(2);
        result.getList().forEach(guwen -> assertThat(guwen.getDynasty()).isEqualTo("唐"));
        
        verify(guwenRepository).findByDynasty(eq("唐"), any(Pageable.class));
    }
    
    @Test
    void testGetAllDynasties_Success() {
        // Given
        when(guwenRepository.findAllDynasties()).thenReturn(guwenList);
        
        // When
        List<String> result = guwenService.getAllDynasties();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("唐");
        
        verify(guwenRepository).findAllDynasties();
    }
    
    @Test
    void testGetAllWriters_Success() {
        // Given
        when(guwenRepository.findAllWriters()).thenReturn(guwenList);
        
        // When
        List<String> result = guwenService.getAllWriters();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("李白", "孟浩然");
        
        verify(guwenRepository).findAllWriters();
    }
    
    @Test
    void testGetAllTypes_Success() {
        // Given
        when(guwenRepository.findAllTypes()).thenReturn(guwenList);
        
        // When
        List<String> result = guwenService.getAllTypes();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("诗");
        
        verify(guwenRepository).findAllTypes();
    }
    
    @Test
    void testGetTotalCount_Success() {
        // Given
        when(guwenRepository.count()).thenReturn(100L);
        
        // When
        long result = guwenService.getTotalCount();
        
        // Then
        assertThat(result).isEqualTo(100L);
        
        verify(guwenRepository).count();
    }
    
    @Test
    void testGetCountByWriter_Success() {
        // Given
        when(guwenRepository.countByWriter(anyString())).thenReturn(50L);
        
        // When
        long result = guwenService.getCountByWriter("李白");
        
        // Then
        assertThat(result).isEqualTo(50L);
        
        verify(guwenRepository).countByWriter("李白");
    }
    
    @Test
    void testGetCountByDynasty_Success() {
        // Given
        when(guwenRepository.countByDynasty(anyString())).thenReturn(200L);
        
        // When
        long result = guwenService.getCountByDynasty("唐");
        
        // Then
        assertThat(result).isEqualTo(200L);
        
        verify(guwenRepository).countByDynasty("唐");
    }
}
