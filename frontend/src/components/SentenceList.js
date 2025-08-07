import React, { useState, useEffect } from 'react';
import { Card, List, Input, Select, Button, Space, Typography, Tag, Pagination, Spin, Empty } from 'antd';
import { SearchOutlined, EditOutlined, BookOutlined, UserOutlined } from '@ant-design/icons';
import { sentenceAPI } from '../utils/api';

const { Search } = Input;
const { Option } = Select;
const { Title, Text, Paragraph } = Typography;

const SentenceList = () => {
  const [sentences, setSentences] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0
  });
  const [filters, setFilters] = useState({
    keyword: '',
    dynasty: '',
    author: ''
  });

  useEffect(() => {
    loadSentences();
  }, [pagination.current, pagination.pageSize]);

  const loadSentences = async () => {
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

      const response = await sentenceAPI.getList(params);
      if (response.code === 200) {
        setSentences(response.data?.list || []);
        setPagination(prev => ({
          ...prev,
          total: response.data?.total || 0
        }));
      }
    } catch (error) {
      console.error('Failed to load sentences:', error);
      setSentences([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPagination(prev => ({ ...prev, current: 1 }));
    loadSentences();
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
          <EditOutlined /> 名句摘录
        </Title>
        <Paragraph>
          精选千古名句，配以深度解析，感受中华文化的博大精深
        </Paragraph>
        
        {/* 搜索和筛选 */}
        <Space direction="vertical" style={{ width: '100%' }}>
          <Space wrap>
            <Search
              placeholder="搜索名句内容或出处"
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
            
            <Input
              placeholder="作者姓名"
              style={{ width: 120 }}
              value={filters.author}
              onChange={(e) => handleFilterChange('author', e.target.value)}
            />
            
            <Button type="primary" onClick={handleSearch} icon={<SearchOutlined />}>
              搜索
            </Button>
          </Space>
        </Space>
      </Card>

      <Spin spinning={loading}>
        {sentences.length === 0 && !loading ? (
          <Empty description="暂无名句数据" />
        ) : (
          <List
            dataSource={sentences}
            renderItem={(sentence) => (
              <List.Item>
                <Card 
                  hoverable 
                  style={{ width: '100%' }}
                  bodyStyle={{ padding: '20px' }}
                >
                  {/* 名句内容 */}
                  <div style={{ marginBottom: 16 }}>
                    <div 
                      className="poem-content" 
                      style={{ 
                        fontSize: '18px',
                        lineHeight: '1.8',
                        fontFamily: 'KaiTi, 楷体, serif',
                        textAlign: 'center',
                        padding: '16px',
                        background: '#fafafa',
                        borderRadius: '8px',
                        borderLeft: '4px solid #1890ff'
                      }}
                    >
                      {sentence.name}
                    </div>
                  </div>

                  {/* 出处信息 */}
                  <div style={{ marginBottom: 12 }}>
                    <Space>
                      <Tag color="blue" icon={<BookOutlined />}>
                        {sentence.from}
                      </Tag>
                      {sentence.dynasty && (
                        <Tag color="green">
                          {sentence.dynasty}
                        </Tag>
                      )}
                      {sentence.author && (
                        <Tag color="orange" icon={<UserOutlined />}>
                          {sentence.author}
                        </Tag>
                      )}
                    </Space>
                  </div>

                  {/* 释义 */}
                  {sentence.meaning && (
                    <div style={{ marginBottom: 12 }}>
                      <Text strong style={{ color: '#666', fontSize: '14px' }}>
                        释义：
                      </Text>
                      <Paragraph 
                        style={{ 
                          fontSize: '14px', 
                          lineHeight: '1.6',
                          marginTop: 4,
                          marginBottom: 0,
                          color: '#666'
                        }}
                      >
                        {sentence.meaning}
                      </Paragraph>
                    </div>
                  )}

                  {/* 赏析 */}
                  {sentence.appreciation && (
                    <div>
                      <Text strong style={{ color: '#666', fontSize: '14px' }}>
                        赏析：
                      </Text>
                      <Paragraph 
                        style={{ 
                          fontSize: '14px', 
                          lineHeight: '1.6',
                          marginTop: 4,
                          marginBottom: 0,
                          color: '#666'
                        }}
                        ellipsis={{ rows: 3, expandable: true, symbol: '展开' }}
                      >
                        {sentence.appreciation}
                      </Paragraph>
                    </div>
                  )}

                  {/* 标签 */}
                  {sentence.tags && sentence.tags.length > 0 && (
                    <div style={{ marginTop: 12 }}>
                      <Space wrap>
                        {sentence.tags.map((tag, index) => (
                          <Tag key={index} color="purple" style={{ fontSize: '12px' }}>
                            {tag}
                          </Tag>
                        ))}
                      </Space>
                    </div>
                  )}
                </Card>
              </List.Item>
            )}
          />
        )}
      </Spin>

      {/* 分页 */}
      {sentences.length > 0 && (
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
              `第 ${range[0]}-${range[1]} 条，共 ${total} 条名句`
            }
          />
        </div>
      )}
    </div>
  );
};

export default SentenceList;
