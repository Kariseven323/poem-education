// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "poem-community-main-component"
//   Timestamp: "2025-08-08T15:43:55+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "React组件设计，用户体验优化。"
// }}
// {{START_MODIFICATIONS}}
import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Input, Select, Button, Pagination, message, Tag, Avatar, Space, Tooltip } from 'antd';
import { SearchOutlined, HeartOutlined, HeartFilled, CommentOutlined, UserOutlined, CalendarOutlined } from '@ant-design/icons';
import { searchCreations, getUserCreations, toggleLike, creationAPI } from '../utils/api';
import './PoemCommunity.css';

const { Search } = Input;
const { Option } = Select;

/**
 * 诗词社区组件
 * 用户可以在这里浏览、搜索和管理自己创建的诗词
 */
const PoemCommunity = () => {
    // 状态管理
    const [poems, setPoems] = useState([]);
    const [loading, setLoading] = useState(false);
    const [searchKeyword, setSearchKeyword] = useState('');
    const [selectedStyle, setSelectedStyle] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(12);
    const [total, setTotal] = useState(0);
    const [viewMode, setViewMode] = useState('all'); // 'all' | 'my'
    const [user, setUser] = useState(null);

    // 诗词风格选项
    const styleOptions = [
        { value: '', label: '全部风格' },
        { value: '律诗', label: '律诗' },
        { value: '绝句', label: '绝句' },
        { value: '词', label: '词' },
        { value: '散文', label: '散文' },
        { value: '现代诗', label: '现代诗' },
        { value: '其他', label: '其他' }
    ];

    // 初始化用户信息
    useEffect(() => {
        const token = localStorage.getItem('token');
        const userInfo = localStorage.getItem('userInfo');
        if (token && userInfo) {
            setUser(JSON.parse(userInfo));
        }
    }, []);

    // 加载诗词列表
    const loadPoems = async (page = 1, keyword = '', style = '', mode = 'all') => {
        console.log('=== 前端：开始加载诗词列表 ===');
        console.log('参数:', { page, keyword, style, mode, pageSize });

        setLoading(true);
        try {
            let response;
            if (mode === 'my' && user) {
                // 获取用户自己的创作
                console.log('调用getUserCreations API');
                response = await getUserCreations(user.id, page, pageSize, style);
            } else {
                // 获取公开创作
                if (keyword.trim()) {
                    // 有关键词时使用搜索API
                    console.log('调用searchCreations API，关键词:', keyword);
                    response = await searchCreations(keyword, page, pageSize, style);
                } else {
                    // 无关键词时使用公开创作列表API
                    const params = { page, size: pageSize };
                    if (style) params.style = style;
                    console.log('调用creationAPI.getPublicList，参数:', params);
                    response = await creationAPI.getPublicList(params);
                }
            }

            console.log('API响应:', response);

            if (response.code === 200) {
                // PageResult的字段名是list，不是items
                const items = response.data.list || [];
                console.log('解析的数据:', {
                    total: response.data.total,
                    itemsCount: items.length,
                    firstItem: items[0]
                });
                setPoems(items);
                setTotal(response.data.total || 0);
            } else {
                console.error('API返回错误:', response);
                message.error(response.message || '加载失败');
            }
        } catch (error) {
            console.error('加载诗词列表失败:', error);
            message.error('加载失败，请稍后重试');
        } finally {
            setLoading(false);
        }
    };

    // 初始加载
    useEffect(() => {
        loadPoems(currentPage, searchKeyword, selectedStyle, viewMode);
    }, [currentPage, selectedStyle, viewMode, user]);

    // 搜索处理
    const handleSearch = (value) => {
        setSearchKeyword(value);
        setCurrentPage(1);
        loadPoems(1, value, selectedStyle, viewMode);
    };

    // 风格筛选
    const handleStyleChange = (value) => {
        setSelectedStyle(value);
        setCurrentPage(1);
        loadPoems(1, searchKeyword, value, viewMode);
    };

    // 切换查看模式
    const handleViewModeChange = (mode) => {
        setViewMode(mode);
        setCurrentPage(1);
        setSearchKeyword('');
        loadPoems(1, '', selectedStyle, mode);
    };

    // 分页处理
    const handlePageChange = (page) => {
        setCurrentPage(page);
        loadPoems(page, searchKeyword, selectedStyle, viewMode);
    };

    // 点赞处理
    const handleLike = async (poemId) => {
        if (!user) {
            message.warning('请先登录');
            return;
        }

        try {
            const response = await toggleLike(user.id, poemId);
            if (response.success) {
                // 更新本地状态
                setPoems(poems.map(poem => 
                    poem.id === poemId 
                        ? { ...poem, likeCount: response.data.likeCount, isLiked: !poem.isLiked }
                        : poem
                ));
                message.success(response.data.isLiked ? '点赞成功' : '取消点赞');
            } else {
                message.error(response.message || '操作失败');
            }
        } catch (error) {
            console.error('点赞操作失败:', error);
            message.error('操作失败，请稍后重试');
        }
    };

    // 格式化时间
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('zh-CN', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    };

    // 截取内容预览
    const getContentPreview = (content, maxLength = 100) => {
        if (content.length <= maxLength) return content;
        return content.substring(0, maxLength) + '...';
    };

    return (
        <div className="poem-community">
            {/* 页面标题 */}
            <div className="community-header">
                <h1>诗词社区</h1>
                <p>发现和分享美好的诗词创作</p>
            </div>

            {/* 搜索和筛选区域 */}
            <div className="search-filters">
                <Row gutter={[16, 16]} align="middle">
                    <Col xs={24} sm={12} md={8}>
                        <Search
                            placeholder="搜索诗词标题或内容..."
                            allowClear
                            enterButton={<SearchOutlined />}
                            size="large"
                            onSearch={handleSearch}
                            value={searchKeyword}
                            onChange={(e) => setSearchKeyword(e.target.value)}
                        />
                    </Col>
                    <Col xs={12} sm={6} md={4}>
                        <Select
                            value={selectedStyle}
                            onChange={handleStyleChange}
                            size="large"
                            style={{ width: '100%' }}
                        >
                            {styleOptions.map(option => (
                                <Option key={option.value} value={option.value}>
                                    {option.label}
                                </Option>
                            ))}
                        </Select>
                    </Col>
                    {user && (
                        <Col xs={12} sm={6} md={4}>
                            <Button.Group size="large" style={{ width: '100%' }}>
                                <Button 
                                    type={viewMode === 'all' ? 'primary' : 'default'}
                                    onClick={() => handleViewModeChange('all')}
                                    style={{ width: '50%' }}
                                >
                                    全部
                                </Button>
                                <Button 
                                    type={viewMode === 'my' ? 'primary' : 'default'}
                                    onClick={() => handleViewModeChange('my')}
                                    style={{ width: '50%' }}
                                >
                                    我的
                                </Button>
                            </Button.Group>
                        </Col>
                    )}
                </Row>
            </div>

            {/* 诗词列表 */}
            <div className="poems-grid">
                <Row gutter={[16, 16]}>
                    {poems.map(poem => (
                        <Col xs={24} sm={12} md={8} lg={6} key={poem.id}>
                            <Card
                                className="poem-card"
                                hoverable
                                actions={[
                                    <Tooltip title={poem.isLiked ? '取消点赞' : '点赞'}>
                                        <Button
                                            type="text"
                                            icon={poem.isLiked ? <HeartFilled style={{ color: '#ff4d4f' }} /> : <HeartOutlined />}
                                            onClick={() => handleLike(poem.id)}
                                        >
                                            {poem.likeCount || 0}
                                        </Button>
                                    </Tooltip>,
                                    <Tooltip title="评论">
                                        <Button
                                            type="text"
                                            icon={<CommentOutlined />}
                                        >
                                            {poem.commentCount || 0}
                                        </Button>
                                    </Tooltip>
                                ]}
                            >
                                <Card.Meta
                                    title={
                                        <div className="poem-title">
                                            <span>{poem.title}</span>
                                            {poem.style && (
                                                <Tag color="blue" size="small">
                                                    {poem.style}
                                                </Tag>
                                            )}
                                        </div>
                                    }
                                    description={
                                        <div className="poem-content">
                                            <p className="content-preview">
                                                {getContentPreview(poem.content)}
                                            </p>
                                            <div className="poem-meta">
                                                <Space size="small">
                                                    <Avatar size="small" icon={<UserOutlined />} />
                                                    <span className="author-name">
                                                        {poem.authorName || '匿名'}
                                                    </span>
                                                </Space>
                                                <Space size="small" className="poem-date">
                                                    <CalendarOutlined />
                                                    <span>{formatDate(poem.createdAt)}</span>
                                                </Space>
                                            </div>
                                        </div>
                                    }
                                />
                            </Card>
                        </Col>
                    ))}
                </Row>

                {/* 空状态 */}
                {!loading && poems.length === 0 && (
                    <div className="empty-state">
                        <p>
                            {viewMode === 'my' 
                                ? '您还没有创作任何诗词，快去创作吧！' 
                                : searchKeyword 
                                    ? '没有找到相关诗词，试试其他关键词吧' 
                                    : '暂无诗词作品'
                            }
                        </p>
                    </div>
                )}
            </div>

            {/* 分页 */}
            {total > 0 && (
                <div className="pagination-wrapper">
                    <Pagination
                        current={currentPage}
                        total={total}
                        pageSize={pageSize}
                        onChange={handlePageChange}
                        showSizeChanger={false}
                        showQuickJumper
                        showTotal={(total, range) => 
                            `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
                        }
                    />
                </div>
            )}
        </div>
    );
};

export default PoemCommunity;
// {{END_MODIFICATIONS}}
