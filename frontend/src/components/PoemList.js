import React, { useState, useEffect } from 'react';
import { Card, List, Input, Select, Button, Space, Typography, Tag, Pagination, Spin, Empty } from 'antd';
import { SearchOutlined, EyeOutlined, HeartOutlined, StarOutlined, BookOutlined } from '@ant-design/icons';
import { guwenAPI } from '../utils/api';
import { normalizeType } from '../utils/dataUtils';
import PoemDetailModal from './PoemDetailModal';

const { Search } = Input;
const { Option } = Select;
const { Title, Text, Paragraph } = Typography;

const PoemList = () => {
  const [poems, setPoems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [dynasties, setDynasties] = useState([]);
  const [types, setTypes] = useState(['诗', '词', '曲', '赋', '文']);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0
  });
  const [filters, setFilters] = useState({
    keyword: '',
    dynasty: '',
    writer: '',
    type: '',
    searchType: 'smart' // 搜索类型：smart(智能), fuzzy(模糊), exact(精确), content(内容)
  });

  // 弹窗状态
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedPoemId, setSelectedPoemId] = useState(null);

  // 组件挂载时加载朝代列表
  useEffect(() => {
    loadDynasties();
  }, []);

  // 监听分页和筛选条件变化
  useEffect(() => {
    loadPoems();
  }, [pagination.current, pagination.pageSize, filters.keyword, filters.dynasty, filters.writer, filters.type, filters.searchType]);

  const loadDynasties = async () => {
    try {
      const response = await guwenAPI.getDynasties();
      if (response.code === 200) {
        setDynasties(response.data || []);
      }
    } catch (error) {
      console.error('Failed to load dynasties:', error);
      // 如果API失败，使用默认值
      setDynasties(['唐代', '宋代', '元代', '明代', '清代', '汉代', '魏晋', '南北朝']);
    }
  };

  const getSearchPlaceholder = () => {
    switch (filters.searchType) {
      case 'smart':
        return '智能搜索：标题、内容、作者、类型等';
      case 'fuzzy':
        return '模糊搜索：支持部分匹配';
      case 'content':
        return '内容搜索：在诗词正文中搜索';
      case 'exact':
        return '精确搜索：完全匹配';
      default:
        return '搜索诗词标题、内容或作者';
    }
  };

  const loadPoems = async () => {
    setLoading(true);
    try {
      const params = {
        page: pagination.current,
        size: pagination.pageSize,
        ...filters
      };

      // 移除空值
      Object.keys(params).forEach(key => {
        if (!params[key]) delete params[key];
      });

      let response;
      // 如果有keyword，使用POST搜索接口；否则使用GET列表接口
      if (filters.keyword) {
        // 根据搜索类型添加额外参数
        const searchParams = {
          ...params,
          searchType: filters.searchType
        };
        response = await guwenAPI.search(searchParams);
      } else {
        response = await guwenAPI.getList(params);
      }

      if (response.code === 200) {
        setPoems(response.data?.list || []);
        setPagination(prev => ({
          ...prev,
          total: response.data?.total || 0
        }));
      }
    } catch (error) {
      console.error('Failed to load poems:', error);
      setPoems([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPagination(prev => ({ ...prev, current: 1 }));
    loadPoems();
  };

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
    // 当筛选条件变化时，重置到第1页
    if (pagination.current !== 1) {
      setPagination(prev => ({ ...prev, current: 1 }));
    }
  };

  const handlePageChange = (page, pageSize) => {
    setPagination(prev => ({
      ...prev,
      current: page,
      pageSize: pageSize
    }));
  };

  // 处理诗词点击
  const handlePoemClick = (poemId) => {
    setSelectedPoemId(poemId);
    setModalVisible(true);
  };

  // 关闭弹窗
  const handleModalClose = () => {
    setModalVisible(false);
    setSelectedPoemId(null);
  };



  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Title level={2}>
          <BookOutlined /> 诗词鉴赏
        </Title>
        <Paragraph>
          探索中华诗词的瑰丽世界，感受千年文化的深厚底蕴
        </Paragraph>
        
        {/* 搜索和筛选 */}
        <Space direction="vertical" style={{ width: '100%' }}>
          <Space wrap>
            <Space.Compact>
              <Select
                value={filters.searchType}
                onChange={(value) => handleFilterChange('searchType', value)}
                style={{ width: 100 }}
                options={[
                  { value: 'smart', label: '智能' },
                  { value: 'fuzzy', label: '模糊' },
                  { value: 'content', label: '内容' },
                  { value: 'exact', label: '精确' }
                ]}
              />
              <Search
                placeholder={getSearchPlaceholder()}
                style={{ width: 280 }}
                value={filters.keyword}
                onChange={(e) => handleFilterChange('keyword', e.target.value)}
                onSearch={handleSearch}
                enterButton={<SearchOutlined />}
              />
            </Space.Compact>
            
            <Select
              placeholder="选择朝代"
              style={{ width: 120 }}
              value={filters.dynasty}
              onChange={(value) => handleFilterChange('dynasty', value)}
              allowClear
            >
              {dynasties.map(dynasty => (
                <Option key={dynasty} value={dynasty}>{dynasty}</Option>
              ))}
            </Select>
            
            <Input
              placeholder="作者姓名"
              style={{ width: 120 }}
              value={filters.writer}
              onChange={(e) => handleFilterChange('writer', e.target.value)}
            />
            
            <Select
              placeholder="文体类型"
              style={{ width: 100 }}
              value={filters.type}
              onChange={(value) => handleFilterChange('type', value)}
              allowClear
            >
              {types.map(type => (
                <Option key={type} value={type}>{type}</Option>
              ))}
            </Select>
            
            <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
              搜索
            </Button>
          </Space>
        </Space>
      </Card>

      <Spin spinning={loading}>
        {poems.length === 0 && !loading ? (
          <Empty description="暂无诗词数据" />
        ) : (
          <List
            grid={{ gutter: 16, xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 3 }}
            dataSource={poems}
            renderItem={(poem) => (
              <List.Item>
                <Card
                  hoverable
                  className="poem-card"
                  onClick={() => handlePoemClick(poem.id)}
                  actions={[
                    <Space>
                      <EyeOutlined />
                      {poem.stats?.viewCount || 0}
                    </Space>,
                    <Space>
                      <HeartOutlined />
                      {poem.stats?.likeCount || 0}
                    </Space>,
                    <Space>
                      <StarOutlined />
                      {poem.stats?.favoriteCount || 0}
                    </Space>
                  ]}
                >
                  <Card.Meta
                    title={
                      <div>
                        <Text strong style={{ fontSize: '16px' }}>
                          {poem.title}
                        </Text>
                        <div style={{ marginTop: 4 }}>
                          <Space>
                            <Tag color="blue">{poem.dynasty}</Tag>
                            <Tag color="green">{poem.writer}</Tag>
                            {normalizeType(poem.type).map(t => (
                              <Tag key={t} color="orange">{t}</Tag>
                            ))}
                          </Space>
                        </div>
                      </div>
                    }
                    description={
                      <div>
                        <div 
                          className="poem-content" 
                          style={{ 
                            marginTop: 12,
                            maxHeight: '120px',
                            overflow: 'hidden',
                            lineHeight: '1.6'
                          }}
                        >
                          {poem.content}
                        </div>
                        {poem.remark && (
                          <div style={{ 
                            marginTop: 8, 
                            fontSize: '12px', 
                            color: '#666',
                            maxHeight: '40px',
                            overflow: 'hidden'
                          }}>
                            {poem.remark.substring(0, 100)}...
                          </div>
                        )}
                      </div>
                    }
                  />
                </Card>
              </List.Item>
            )}
          />
        )}
      </Spin>

      {/* 分页 */}
      {poems.length > 0 && (
        <div style={{ textAlign: 'center', marginTop: 24 }}>
          <Pagination
            current={pagination.current}
            pageSize={pagination.pageSize}
            total={pagination.total}
            onChange={handlePageChange}
            onShowSizeChange={handlePageChange}
            showSizeChanger
            showQuickJumper
            showTotal={(total, range) => 
              `第 ${range[0]}-${range[1]} 条，共 ${total} 条诗词`
            }
          />
        </div>
      )}

      {/* 诗词详情弹窗 */}
      <PoemDetailModal
        visible={modalVisible}
        onClose={handleModalClose}
        poemId={selectedPoemId}
      />
    </div>
  );
};

export default PoemList;
