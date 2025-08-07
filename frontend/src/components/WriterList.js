import React, { useState, useEffect } from 'react';
import { Card, List, Input, Select, Button, Space, Typography, Tag, Pagination, Spin, Empty, Avatar } from 'antd';
import { SearchOutlined, UserOutlined, BookOutlined } from '@ant-design/icons';
import { writerAPI } from '../utils/api';

const { Search } = Input;
const { Option } = Select;
const { Title, Text, Paragraph } = Typography;

const WriterList = () => {
  const [writers, setWriters] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0
  });
  const [filters, setFilters] = useState({
    keyword: '',
    dynasty: ''
  });

  useEffect(() => {
    loadWriters();
  }, [pagination.current, pagination.pageSize]);

  const loadWriters = async () => {
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

      const response = await writerAPI.getList(params);
      if (response.code === 200) {
        setWriters(response.data?.list || []);
        setPagination(prev => ({
          ...prev,
          total: response.data?.total || 0
        }));
      }
    } catch (error) {
      console.error('Failed to load writers:', error);
      setWriters([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPagination(prev => ({ ...prev, current: 1 }));
    loadWriters();
  };

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  const handlePageChange = (page, pageSize) => {
    setPagination(prev => ({
      ...prev,
      current: page,
      pageSize: pageSize
    }));
  };

  const dynasties = ['唐', '宋', '元', '明', '清', '汉', '魏晋', '南北朝'];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Title level={2}>
          <UserOutlined /> 文人墨客
        </Title>
        <Paragraph>
          了解历代文人的生平事迹，感受他们的人生智慧和文学造诣
        </Paragraph>
        
        {/* 搜索和筛选 */}
        <Space direction="vertical" style={{ width: '100%' }}>
          <Space wrap>
            <Search
              placeholder="搜索作者姓名或简介"
              style={{ width: 300 }}
              value={filters.keyword}
              onChange={(e) => handleFilterChange('keyword', e.target.value)}
              onSearch={handleSearch}
              enterButton={<SearchOutlined />}
            />
            
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
            
            <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
              搜索
            </Button>
          </Space>
        </Space>
      </Card>

      <Spin spinning={loading}>
        {writers.length === 0 && !loading ? (
          <Empty description="暂无作者数据" />
        ) : (
          <List
            grid={{ gutter: 16, xs: 1, sm: 2, md: 2, lg: 3, xl: 4, xxl: 4 }}
            dataSource={writers}
            renderItem={(writer) => (
              <List.Item>
                <Card
                  hoverable
                  className="poem-card"
                  onClick={() => window.location.href = `/writers/${writer._id}`}
                  cover={
                    <div style={{ 
                      height: 200, 
                      display: 'flex', 
                      alignItems: 'center', 
                      justifyContent: 'center',
                      background: '#f5f5f5'
                    }}>
                      {writer.headImageUrl ? (
                        <img 
                          src={writer.headImageUrl} 
                          alt={writer.name}
                          style={{ 
                            width: '100%', 
                            height: '100%', 
                            objectFit: 'cover' 
                          }}
                        />
                      ) : (
                        <Avatar 
                          size={80} 
                          icon={<UserOutlined />}
                          style={{ backgroundColor: '#1890ff' }}
                        >
                          {writer.name?.charAt(0)}
                        </Avatar>
                      )}
                    </div>
                  }
                >
                  <Card.Meta
                    title={
                      <div style={{ textAlign: 'center' }}>
                        <Text strong style={{ fontSize: '16px' }}>
                          {writer.name}
                        </Text>
                        <div style={{ marginTop: 4 }}>
                          <Tag color="blue">{writer.dynasty}</Tag>
                        </div>
                        {writer.lifespan && (
                          <div style={{ marginTop: 4 }}>
                            <Text type="secondary" style={{ fontSize: '12px' }}>
                              {writer.lifespan}
                            </Text>
                          </div>
                        )}
                      </div>
                    }
                    description={
                      <div>
                        {writer.alias && (
                          <div style={{ marginBottom: 8, textAlign: 'center' }}>
                            <Text type="secondary" style={{ fontSize: '12px' }}>
                              字号：{writer.alias}
                            </Text>
                          </div>
                        )}
                        <div style={{ 
                          fontSize: '12px',
                          color: '#666',
                          lineHeight: '1.4',
                          maxHeight: '60px',
                          overflow: 'hidden',
                          textAlign: 'justify'
                        }}>
                          {writer.simpleIntro || '暂无简介'}
                        </div>
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
      {writers.length > 0 && (
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
              `第 ${range[0]}-${range[1]} 条，共 ${total} 位作者`
            }
          />
        </div>
      )}
    </div>
  );
};

export default WriterList;
