import React, { useState, useEffect, useRef } from 'react';
import { Card, List, Input, Select, Button, Space, Typography, Tag, Pagination, Spin, Empty, message, Tooltip } from 'antd';
import { SearchOutlined, EyeOutlined, HeartOutlined, HeartFilled, StarOutlined, BookOutlined } from '@ant-design/icons';
import { useLocation } from 'react-router-dom';
import { guwenAPI, statsAPI, userActionAPI } from '../utils/api';
import { normalizeType } from '../utils/dataUtils';
import viewTracker from '../utils/viewTracker';
import PoemDetailModal from './PoemDetailModal';
import FavoriteButton from './FavoriteButton';
import { getCurrentUser } from '../utils/auth';

const { Search } = Input;
const { Option } = Select;
const { Title, Text, Paragraph } = Typography;

const PoemList = () => {
  const location = useLocation();
  const [poems, setPoems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [dynasties, setDynasties] = useState([]);
  const [types, setTypes] = useState(['诗', '词', '曲', '赋', '文']);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 20,
    total: 0
  });

  // 从URL参数中获取初始筛选条件
  const getInitialFilters = () => {
    const searchParams = new URLSearchParams(location.search);
    return {
      keyword: searchParams.get('keyword') || '',
      dynasty: searchParams.get('dynasty') || '',
      writer: searchParams.get('writer') || '',
      type: searchParams.get('type') || '',
      searchType: 'smart' // 搜索类型：smart(智能), fuzzy(模糊), exact(精确), content(内容)
    };
  };

  const [filters, setFilters] = useState(getInitialFilters());

  // 弹窗状态
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedPoemId, setSelectedPoemId] = useState(null);

  // 统计数据状态
  const [poemStats, setPoemStats] = useState(new Map());
  const [statsLoading, setStatsLoading] = useState(false);

  // 用户点赞状态
  const [userLikeStatus, setUserLikeStatus] = useState(new Map());

  // 防抖记录点击行为
  const clickRecordTimeouts = useRef(new Map());

  // 组件挂载时加载朝代列表
  useEffect(() => {
    loadDynasties();
  }, []);

  // 监听URL参数变化，更新筛选条件
  useEffect(() => {
    const newFilters = getInitialFilters();
    setFilters(newFilters);
    setPagination(prev => ({ ...prev, current: 1 }));
  }, [location.search]);

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
        const poemList = response.data?.list || [];
        setPoems(poemList);
        setPagination(prev => ({
          ...prev,
          total: response.data?.total || 0
        }));

        // 异步加载统计数据，不阻塞诗词列表显示
        if (poemList.length > 0) {
          loadPoemStats(poemList);
          loadUserLikeStatus(poemList);
        }
      }
    } catch (error) {
      console.error('Failed to load poems:', error);
      setPoems([]);
    } finally {
      setLoading(false);
    }
  };

  // 批量获取诗词统计数据
  const loadPoemStats = async (poemList) => {
    if (!poemList || poemList.length === 0) {
      return;
    }

    setStatsLoading(true);
    const newStatsMap = new Map();

    try {
      // 批量并行获取统计数据，提升性能
      const statsPromises = poemList.map(async (poem) => {
        try {
          const response = await statsAPI.getContentStats(poem.id, 'guwen');
          if (response.success && response.data) {
            newStatsMap.set(poem.id, response.data);
          } else {
            // 如果获取失败，使用默认统计数据
            newStatsMap.set(poem.id, {
              viewCount: 0,
              likeCount: 0,
              favoriteCount: 0,
              commentCount: 0,
              shareCount: 0
            });
          }
        } catch (error) {
          console.warn('获取诗词统计失败:', poem.id, error);
          // 错误情况下使用默认统计数据
          newStatsMap.set(poem.id, {
            viewCount: 0,
            likeCount: 0,
            favoriteCount: 0,
            commentCount: 0,
            shareCount: 0
          });
        }
      });

      // 等待所有统计数据获取完成
      await Promise.all(statsPromises);

      // 更新统计数据状态
      setPoemStats(prevStats => {
        const updatedStats = new Map(prevStats);
        newStatsMap.forEach((stats, poemId) => {
          updatedStats.set(poemId, stats);
        });
        return updatedStats;
      });

      console.debug('批量获取诗词统计完成:', newStatsMap.size, '条记录');

    } catch (error) {
      console.error('批量获取诗词统计失败:', error);
    } finally {
      setStatsLoading(false);
    }
  };

  // 批量获取用户点赞状态
  const loadUserLikeStatus = async (poemList) => {
    const currentUser = getCurrentUser();
    if (!currentUser || !poemList || poemList.length === 0) {
      return;
    }

    const newLikeStatusMap = new Map();

    try {
      // 批量并行获取用户点赞状态
      const likeStatusPromises = poemList.map(async (poem) => {
        try {
          const response = await userActionAPI.hasAction({
            targetId: poem.id,
            targetType: 'guwen',
            actionType: 'like'
          });
          if (response.code === 200) {
            newLikeStatusMap.set(poem.id, response.data);
          } else {
            newLikeStatusMap.set(poem.id, false);
          }
        } catch (error) {
          console.warn('获取用户点赞状态失败:', poem.id, error);
          newLikeStatusMap.set(poem.id, false);
        }
      });

      // 等待所有点赞状态获取完成
      await Promise.all(likeStatusPromises);

      // 更新用户点赞状态
      setUserLikeStatus(prevStatus => {
        const updatedStatus = new Map(prevStatus);
        newLikeStatusMap.forEach((isLiked, poemId) => {
          updatedStatus.set(poemId, isLiked);
        });
        return updatedStatus;
      });

      console.debug('批量获取用户点赞状态完成:', newLikeStatusMap.size, '条记录');

    } catch (error) {
      console.error('批量获取用户点赞状态失败:', error);
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



  // 组件卸载时清理所有防抖定时器
  useEffect(() => {
    return () => {
      // 清理所有未完成的防抖定时器
      clickRecordTimeouts.current.forEach((timeoutId) => {
        clearTimeout(timeoutId);
      });
      clickRecordTimeouts.current.clear();
    };
  }, []);

  // 处理诗词点击
  const handlePoemClick = (poemId) => {
    // 立即显示弹窗，不等待记录完成
    setSelectedPoemId(poemId);
    setModalVisible(true);

    // 异步记录点击行为，使用防抖机制
    const timeoutId = clickRecordTimeouts.current.get(poemId);
    if (timeoutId) {
      clearTimeout(timeoutId);
    }

    const newTimeoutId = setTimeout(async () => {
      try {
        await viewTracker.recordPoemView(poemId, {
          source: 'poem_list_click',
          timestamp: new Date().toISOString()
        });
        console.debug('诗词点击记录成功:', poemId);
      } catch (error) {
        // 记录失败不影响用户体验，只在控制台输出警告
        console.warn('诗词点击记录失败:', poemId, error);
      } finally {
        // 清理防抖记录
        clickRecordTimeouts.current.delete(poemId);
      }
    }, 300); // 300ms防抖延迟

    clickRecordTimeouts.current.set(poemId, newTimeoutId);
  };

  // 处理点赞/取消点赞
  const handleLike = async (poemId, event) => {
    // 阻止事件冒泡，防止触发诗词详情弹窗
    event.stopPropagation();

    const currentUser = getCurrentUser();
    if (!currentUser) {
      message.warning('请先登录');
      return;
    }

    const isCurrentlyLiked = userLikeStatus.get(poemId) || false;

    try {
      if (isCurrentlyLiked) {
        // 取消点赞
        const response = await userActionAPI.cancelAction({
          targetId: poemId,
          targetType: 'guwen',
          actionType: 'like'
        });
        if (response.code === 200) {
          // 更新本地状态
          setUserLikeStatus(prev => {
            const updated = new Map(prev);
            updated.set(poemId, false);
            return updated;
          });
          // 更新统计数据
          setPoemStats(prev => {
            const updated = new Map(prev);
            const currentStats = updated.get(poemId) || {};
            updated.set(poemId, {
              ...currentStats,
              likeCount: Math.max(0, (currentStats.likeCount || 0) - 1)
            });
            return updated;
          });
          message.success('取消点赞成功');
        }
      } else {
        // 点赞
        const response = await userActionAPI.recordAction({
          targetId: poemId,
          targetType: 'guwen',
          actionType: 'like'
        });
        if (response.code === 200) {
          // 更新本地状态
          setUserLikeStatus(prev => {
            const updated = new Map(prev);
            updated.set(poemId, true);
            return updated;
          });
          // 更新统计数据
          setPoemStats(prev => {
            const updated = new Map(prev);
            const currentStats = updated.get(poemId) || {};
            updated.set(poemId, {
              ...currentStats,
              likeCount: (currentStats.likeCount || 0) + 1
            });
            return updated;
          });
          message.success('点赞成功');
        }
      }
    } catch (error) {
      console.error('点赞操作失败:', error);
      message.error(isCurrentlyLiked ? '取消点赞失败' : '点赞失败');
    }
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

        {/* 显示当前筛选条件 */}
        {(filters.writer || filters.dynasty || filters.type) && (
          <div style={{ marginBottom: 16 }}>
            <Space wrap>
              <Text type="secondary">当前筛选：</Text>
              {filters.writer && (
                <Tag color="green" closable onClose={() => handleFilterChange('writer', '')}>
                  作者：{filters.writer}
                </Tag>
              )}
              {filters.dynasty && (
                <Tag color="blue" closable onClose={() => handleFilterChange('dynasty', '')}>
                  朝代：{filters.dynasty}
                </Tag>
              )}
              {filters.type && (
                <Tag color="orange" closable onClose={() => handleFilterChange('type', '')}>
                  类型：{filters.type}
                </Tag>
              )}
            </Space>
          </div>
        )}
        
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
                      {poemStats.get(poem.id)?.viewCount || poem.stats?.viewCount || 0}
                    </Space>,
                    <Tooltip title={userLikeStatus.get(poem.id) ? '取消点赞' : '点赞'}>
                      <Button
                        type="text"
                        icon={userLikeStatus.get(poem.id) ?
                          <HeartFilled style={{ color: '#ff4d4f' }} /> :
                          <HeartOutlined />
                        }
                        onClick={(e) => handleLike(poem.id, e)}
                        style={{ border: 'none', padding: 0, height: 'auto' }}
                      >
                        {poemStats.get(poem.id)?.likeCount || poem.stats?.likeCount || 0}
                      </Button>
                    </Tooltip>,
                    <FavoriteButton
                      targetId={poem.id}
                      targetType="guwen"
                      size="small"
                      type="text"
                      showText={false}
                      style={{ border: 'none', padding: 0, height: 'auto' }}
                      onFavoriteChange={(isFavorited, folderName) => {
                        // 更新本地统计信息
                        const currentStats = poemStats.get(poem.id) || poem.stats || {};
                        const newFavoriteCount = isFavorited ?
                          (currentStats.favoriteCount || 0) + 1 :
                          Math.max((currentStats.favoriteCount || 0) - 1, 0);

                        const newStats = new Map(poemStats);
                        newStats.set(poem.id, {
                          ...currentStats,
                          favoriteCount: newFavoriteCount
                        });
                        setPoemStats(newStats);
                      }}
                    />
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
