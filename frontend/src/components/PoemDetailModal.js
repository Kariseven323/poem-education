import React, { useState, useEffect } from 'react';
import {
  Modal,
  Card,
  Typography,
  Tag,
  Space,
  Button,
  List,
  Input,
  Avatar,
  message,
  Spin,
  Empty,
  Divider,
  Row,
  Col,
  Popover
} from 'antd';
import {
  BookOutlined,
  UserOutlined,
  HeartOutlined,
  HeartFilled,
  MessageOutlined,
  SendOutlined,
  CloseOutlined,
  StarOutlined,
  LikeOutlined
} from '@ant-design/icons';
import { guwenAPI, commentAPI, userActionAPI } from '../utils/api';
import { normalizeType } from '../utils/dataUtils';
import moment from 'moment';
import './PoemDetailModal.css';

const { Title, Paragraph, Text } = Typography;
const { TextArea } = Input;

// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "poem-detail-modal-component"
//   Timestamp: "2025-08-07T19:36:53+08:00"
//   Authoring_Subagent: "react-frontend-expert"
//   Principle_Applied: "SOLID-S (单一职责原则)"
//   Quality_Check: "组件功能完整，支持诗词详情展示、评论和点赞功能。"
// }}
// {{START_MODIFICATIONS}}

const PoemDetailModal = ({ visible, onClose, poemId }) => {
  const [poem, setPoem] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [commentsLoading, setCommentsLoading] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);
  const [isLiked, setIsLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [likingPoem, setLikingPoem] = useState(false);

  // 回复相关状态
  const [replyingTo, setReplyingTo] = useState(null); // 当前回复的评论
  const [replyContent, setReplyContent] = useState(''); // 回复内容
  const [replyVisible, setReplyVisible] = useState({}); // 控制回复框显示

  // 获取当前用户信息
  const getCurrentUser = () => {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  };

  // 获取评论的正确ID（处理_id和id字段的兼容性）
  const getCommentId = (comment) => {
    return comment._id || comment.id;
  };

  // {{RIPER-5+SMART-6:
  //   Action: "Parallel-Added"
  //   Task_ID: "2c7a765d-bf83-4d7c-963e-1e6db7f54b97"
  //   Timestamp: "2025-08-08T11:09:49+08:00"
  //   Authoring_Subagent: "PM-内置顾问团"
  //   Principle_Applied: "SOLID-S (单一职责原则)"
  //   Quality_Check: "扁平化数据处理逻辑，支持无限层级。"
  // }}
  // {{START_MODIFICATIONS}}

  // 扁平化评论数据处理函数
  const flattenComments = (comments) => {
    if (!comments || comments.length === 0) {
      return [];
    }

    const result = [];

    // 递归遍历评论树，按时间顺序扁平化
    const traverse = (commentList, depth = 0) => {
      commentList.forEach(comment => {
        // 添加深度信息到评论对象
        result.push({
          ...comment,
          depth: depth,
          flatIndex: result.length // 扁平化索引，用于排序
        });

        // 递归处理子评论
        if (comment.children && comment.children.length > 0) {
          traverse(comment.children, depth + 1);
        }
      });
    };

    traverse(comments);

    // 按创建时间排序，确保回复顺序正确
    return result.sort((a, b) => {
      const timeA = new Date(a.createdAt);
      const timeB = new Date(b.createdAt);
      return timeA - timeB;
    });
  };

  // 根据parentId查找被回复评论信息
  const getReplyReference = (comment, allComments) => {
    if (!comment.parentId) {
      return null;
    }

    // 在扁平化数组中查找父评论
    const parentComment = allComments.find(c =>
      getCommentId(c) === comment.parentId
    );

    if (!parentComment) {
      return null;
    }

    return {
      id: getCommentId(parentComment),
      userInfo: parentComment.userInfo,
      content: parentComment.content,
      createdAt: parentComment.createdAt
    };
  };

  // 构建完整的回复链路（用于显示回复层级关系）
  const buildReplyChain = (comment, allComments) => {
    const chain = [];
    let currentComment = comment;

    // 向上追溯回复链
    while (currentComment && currentComment.parentId) {
      const parentComment = allComments.find(c =>
        getCommentId(c) === currentComment.parentId
      );

      if (parentComment) {
        chain.unshift({
          id: getCommentId(parentComment),
          userInfo: parentComment.userInfo,
          content: parentComment.content.substring(0, 30) + '...'
        });
        currentComment = parentComment;
      } else {
        break;
      }
    }

    return chain;
  };

  // {{END_MODIFICATIONS}}

  // {{RIPER-5+SMART-6:
  //   Action: "Parallel-Added"
  //   Task_ID: "94ec854c-f970-4fc1-bd5c-b704bcf69561"
  //   Timestamp: "2025-08-08T11:09:49+08:00"
  //   Authoring_Subagent: "PM-内置顾问团"
  //   Principle_Applied: "SOLID-S (单一职责原则)"
  //   Quality_Check: "引用框组件设计美观，支持点击交互。"
  // }}
  // {{START_MODIFICATIONS}}

  // 引用框组件 - 显示被回复评论的信息
  const ReplyQuote = ({ replyTo, onClick }) => {
    if (!replyTo) return null;

    const handleUserClick = () => {
      if (onClick) {
        onClick(replyTo);
      }
    };

    // 截取内容摘要，最多50字
    const contentSummary = replyTo.content && replyTo.content.length > 50
      ? replyTo.content.substring(0, 50) + '...'
      : replyTo.content;

    return (
      <div
        className="reply-quote"
        style={{
          background: '#f8f9fa',
          borderLeft: '3px solid #1890ff',
          padding: '8px 12px',
          borderRadius: '4px',
          margin: '8px 0',
          fontSize: '12px',
          lineHeight: '1.4'
        }}
      >
        <Space size={4} wrap>
          <Tag
            color="blue"
            style={{
              cursor: onClick ? 'pointer' : 'default',
              fontSize: '11px',
              padding: '2px 6px',
              margin: 0
            }}
            onClick={handleUserClick}
          >
            @{replyTo.userInfo?.nickname || '匿名用户'}
          </Tag>
          <Text
            type="secondary"
            style={{
              fontSize: '11px',
              color: '#8c8c8c',
              wordBreak: 'break-word'
            }}
          >
            {contentSummary}
          </Text>
        </Space>
        {replyTo.createdAt && (
          <Text
            type="secondary"
            style={{
              fontSize: '10px',
              color: '#bfbfbf',
              marginLeft: '8px'
            }}
          >
            {moment(replyTo.createdAt).fromNow()}
          </Text>
        )}
      </div>
    );
  };

  // 增强版引用框组件 - 支持显示回复链
  const ReplyChainQuote = ({ replyChain, onClick }) => {
    if (!replyChain || replyChain.length === 0) return null;

    return (
      <div
        className="reply-chain-quote"
        style={{
          background: 'linear-gradient(90deg, #f0f9ff 0%, #f8f9fa 100%)',
          border: '1px solid #e6f7ff',
          borderRadius: '6px',
          padding: '10px 12px',
          margin: '8px 0',
          fontSize: '11px'
        }}
      >
        <div style={{ marginBottom: '4px', color: '#666' }}>
          回复链:
        </div>
        <Space size={4} wrap>
          {replyChain.map((item, index) => (
            <React.Fragment key={item.id}>
              <Tag
                size="small"
                color="cyan"
                style={{
                  fontSize: '10px',
                  cursor: onClick ? 'pointer' : 'default'
                }}
                onClick={() => onClick && onClick(item)}
              >
                @{item.userInfo?.nickname || '匿名'}
              </Tag>
              {index < replyChain.length - 1 && (
                <Text type="secondary" style={{ fontSize: '10px' }}>→</Text>
              )}
            </React.Fragment>
          ))}
        </Space>
      </div>
    );
  };

  // {{END_MODIFICATIONS}}

  // {{RIPER-5+SMART-6:
  //   Action: "Modified"
  //   Task_ID: "a4067683-554f-4851-b5cf-90928e15c219"
  //   Timestamp: "2025-08-08T11:09:49+08:00"
  //   Authoring_Subagent: "PM-内置顾问团"
  //   Principle_Applied: "SOLID-S (单一职责原则)"
  //   Quality_Check: "扁平化渲染，移除嵌套逻辑，支持无限层级。"
  // }}
  // {{START_MODIFICATIONS}}

  // 扁平化渲染单个评论
  const renderFlatComment = (comment, replyInfo, allComments) => {
    const commentId = getCommentId(comment);

    // {{RIPER-5+SMART-6:
    //   Action: "Modified"
    //   Task_ID: "9768571f-3900-4562-91f9-1f6e2a019d1c"
    //   Timestamp: "2025-08-08T11:09:49+08:00"
    //   Authoring_Subagent: "PM-内置顾问团"
    //   Principle_Applied: "SOLID-S (单一职责原则)"
    //   Quality_Check: "响应式样式优化，移动端友好。"
    // }}
    // {{START_MODIFICATIONS}}

    // 检测是否为移动设备
    const isMobile = window.innerWidth <= 768;

    // 处理用户点击引用框中的用户名
    const handleQuoteUserClick = (replyToComment) => {
      // 可以在这里添加跳转到用户资料等功能
      console.log('点击了用户:', replyToComment.userInfo?.nickname);
    };

    // 响应式样式配置
    const cardStyle = {
      marginBottom: isMobile ? '8px' : '12px',
      borderRadius: isMobile ? '6px' : '8px',
      boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
      transition: 'all 0.2s ease'
    };

    const bodyStyle = {
      padding: isMobile ? '12px' : '16px'
    };

    // {{END_MODIFICATIONS}}

    return (
      <Card
        key={commentId}
        style={cardStyle}
        bodyStyle={bodyStyle}
        hoverable
      >
        {/* 引用框 - 显示被回复的评论信息 */}
        {replyInfo && (
          <ReplyQuote
            replyTo={replyInfo}
            onClick={handleQuoteUserClick}
          />
        )}

        {/* 评论主体内容 */}
        <div className="comment-main" style={{
          display: 'flex',
          gap: isMobile ? '8px' : '12px'
        }}>
          {/* 用户头像 */}
          <div className="comment-avatar">
            <Avatar
              size={isMobile ? 32 : 36}
              src={comment.userInfo?.avatar}
              icon={<UserOutlined />}
              style={{
                backgroundColor: comment.userInfo?.avatar ? 'transparent' : '#87d068',
                border: '2px solid #f0f0f0',
                flexShrink: 0
              }}
            />
          </div>

          {/* 评论内容区域 */}
          <div className="comment-content" style={{ flex: 1, minWidth: 0 }}>
            {/* 评论头部信息 */}
            <div className="comment-header" style={{
              marginBottom: isMobile ? '6px' : '8px'
            }}>
              <Space size="small" wrap>
                <Text strong style={{
                  fontSize: isMobile ? '13px' : '14px',
                  color: '#262626'
                }}>
                  {comment.userInfo?.nickname || '匿名用户'}
                </Text>
                <Text type="secondary" style={{
                  fontSize: isMobile ? '11px' : '12px'
                }}>
                  {moment(comment.createdAt).fromNow()}
                </Text>
                {comment.level && comment.level > 1 && (
                  <Tag size="small" color="blue" style={{
                    fontSize: isMobile ? '9px' : '10px',
                    padding: isMobile ? '0 4px' : '0 6px',
                    height: isMobile ? '16px' : '18px',
                    lineHeight: isMobile ? '16px' : '18px'
                  }}>
                    L{comment.level}
                  </Tag>
                )}
              </Space>
            </div>

            {/* 评论文本内容 */}
            <div className="comment-text" style={{ margin: '8px 0' }}>
              <Text style={{
                fontSize: isMobile ? '13px' : '14px',
                lineHeight: isMobile ? '1.5' : '1.6',
                color: '#595959',
                wordBreak: 'break-word'
              }}>
                {comment.content}
              </Text>
            </div>

            {/* 评论操作按钮 */}
            <div className="comment-actions">
              <Space size={isMobile ? "middle" : "large"}>
                <Button
                  type="text"
                  size="small"
                  icon={<LikeOutlined />}
                  style={{
                    color: '#8c8c8c',
                    fontSize: isMobile ? '11px' : '12px',
                    padding: isMobile ? '2px 6px' : '4px 8px',
                    height: isMobile ? '28px' : '32px'
                  }}
                >
                  {comment.likeCount || 0}
                </Button>
                <Button
                  type="text"
                  size="small"
                  style={{
                    color: '#8c8c8c',
                    fontSize: isMobile ? '11px' : '12px',
                    padding: isMobile ? '2px 6px' : '4px 8px',
                    height: isMobile ? '28px' : '32px'
                  }}
                  onClick={() => handleReplyClick(comment)}
                >
                  回复
                </Button>
              </Space>
            </div>

            {/* 回复输入框 */}
            {replyVisible[commentId] && (
              <div className="reply-box" style={{
                marginTop: isMobile ? '8px' : '12px'
              }}>
                <ReplyQuote
                  replyTo={{
                    userInfo: comment.userInfo,
                    content: comment.content,
                    createdAt: comment.createdAt
                  }}
                />
                <div className="reply-input">
                  <Input.TextArea
                    value={replyContent}
                    onChange={(e) => setReplyContent(e.target.value)}
                    placeholder="写下你的回复..."
                    style={{
                      fontSize: isMobile ? '13px' : '14px'
                    }}
                    autoSize={{ minRows: 2, maxRows: 4 }}
                    style={{ marginBottom: '8px' }}
                  />
                  <div className="reply-actions" style={{ textAlign: 'right' }}>
                    <Space>
                      <Button
                        size="small"
                        onClick={() => setReplyVisible({ ...replyVisible, [commentId]: false })}
                      >
                        取消
                      </Button>
                      <Button
                        type="primary"
                        size="small"
                        loading={submittingComment}
                        onClick={() => handleSubmitReply(comment)}
                      >
                        发布回复
                      </Button>
                    </Space>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </Card>
    );
  };

  // 保留原有的递归渲染函数作为备用（可在需要时切换回来）
  const renderComment = (comment, depth = 0) => {
    const commentId = getCommentId(comment);
    const maxDepth = 3; // 最大嵌套深度
    const indentSize = Math.min(depth * 30, maxDepth * 30); // 每层缩进30px，最大90px

    return (
      <div key={commentId}>
        <div className="comment-item" style={{
          marginLeft: `${indentSize}px`,
          marginBottom: '12px',
          padding: '12px',
          backgroundColor: depth > 0 ? '#f8f9fa' : '#fff',
          borderLeft: depth > 0 ? '3px solid #1890ff' : 'none',
          borderRadius: '6px',
          border: depth > 0 ? '1px solid #e8e8e8' : 'none'
        }}>
          <div className="comment-main" style={{ display: 'flex', gap: '12px' }}>
            <div className="comment-avatar">
              <Avatar
                size={depth > 0 ? 28 : 36}
                src={comment.userInfo?.avatar}
                icon={<UserOutlined />}
                style={{
                  backgroundColor: comment.userInfo?.avatar ? 'transparent' : '#87d068',
                  border: '2px solid #f0f0f0',
                  flexShrink: 0
                }}
              />
            </div>
            <div className="comment-content" style={{ flex: 1 }}>
              <div className="comment-header">
                <Text strong style={{ fontSize: '14px', color: '#262626' }}>
                  {comment.userInfo?.nickname || '匿名用户'}
                </Text>
                <Text type="secondary" style={{ fontSize: '12px', marginLeft: '8px' }}>
                  {moment(comment.createdAt).fromNow()}
                </Text>
                {depth > 0 && (
                  <Tag size="small" color="blue" style={{ marginLeft: '8px', fontSize: '10px' }}>
                    回复
                  </Tag>
                )}
                {comment.level && comment.level > 1 && (
                  <Text type="secondary" style={{ fontSize: '10px', marginLeft: '8px' }}>
                    L{comment.level}
                  </Text>
                )}
              </div>
              <div className="comment-text" style={{ margin: '8px 0' }}>
                <Text style={{
                  fontSize: '14px',
                  lineHeight: '1.6',
                  color: '#595959',
                  wordBreak: 'break-word'
                }}>
                  {comment.content}
                </Text>
              </div>
              <div className="comment-actions">
                <Space size="large">
                  <Button
                    type="text"
                    size="small"
                    icon={<LikeOutlined />}
                    style={{ color: '#8c8c8c', fontSize: '12px' }}
                  >
                    {comment.likeCount || 0}
                  </Button>
                  {depth < maxDepth && (
                    <Button
                      type="text"
                      size="small"
                      style={{ color: '#8c8c8c', fontSize: '12px' }}
                      onClick={() => handleReplyClick(comment)}
                    >
                      回复
                    </Button>
                  )}
                </Space>
              </div>

              {/* 回复框 */}
              {replyVisible[commentId] && (
                <div className="reply-box" style={{ marginTop: '12px' }}>
                  <div className="reply-reference" style={{
                    padding: '8px 12px',
                    backgroundColor: '#e6f7ff',
                    borderRadius: '4px',
                    marginBottom: '8px',
                    fontSize: '12px',
                    color: '#666'
                  }}>
                    回复 @{comment.userInfo?.nickname || '匿名用户'}: {comment.content.substring(0, 50)}...
                  </div>
                  <div className="reply-input">
                    <Input.TextArea
                      value={replyContent}
                      onChange={(e) => setReplyContent(e.target.value)}
                      placeholder="写下你的回复..."
                      autoSize={{ minRows: 2, maxRows: 4 }}
                      style={{ marginBottom: '8px' }}
                    />
                    <div className="reply-actions" style={{ textAlign: 'right' }}>
                      <Space>
                        <Button
                          size="small"
                          onClick={() => setReplyVisible({ ...replyVisible, [commentId]: false })}
                        >
                          取消
                        </Button>
                        <Button
                          type="primary"
                          size="small"
                          loading={submittingComment}
                          onClick={() => handleSubmitReply(comment)}
                        >
                          发布回复
                        </Button>
                      </Space>
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* 递归渲染子评论 */}
        {comment.children && comment.children.length > 0 && (
          <div className="comment-children">
            {comment.children.map(childComment => renderComment(childComment, depth + 1))}
          </div>
        )}
      </div>
    );
  };

  // {{END_MODIFICATIONS}}

  const currentUser = getCurrentUser();

  // 加载诗词详情
  const loadPoemDetail = async () => {
    if (!poemId) return;

    setLoading(true);
    try {
      const response = await guwenAPI.getById(poemId);
      if (response.code === 200) {
        setPoem(response.data);
        setLikeCount(response.data.stats?.likeCount || 0);

        // 检查用户是否已点赞
        if (currentUser) {
          checkUserLikeStatus();
        }
      } else {
        message.error(response.message || '获取诗词详情失败');
      }
    } catch (error) {
      console.error('Failed to load poem detail:', error);
      message.error('加载诗词详情失败');
    } finally {
      setLoading(false);
    }
  };

  // 检查用户点赞状态
  const checkUserLikeStatus = async () => {
    if (!currentUser || !poemId) return;
    
    try {
      const response = await userActionAPI.hasAction({
        targetId: poemId,
        targetType: 'guwen',
        actionType: 'like'
      });
      if (response.code === 200) {
        setIsLiked(response.data);
      }
    } catch (error) {
      console.error('Failed to check like status:', error);
    }
  };

  // 加载评论列表
  const loadComments = async () => {
    if (!poemId) return;

    setCommentsLoading(true);
    try {
      const response = await commentAPI.getList({
        targetId: poemId,
        targetType: 'guwen',
        page: 1,
        size: 50
      });
      if (response.code === 200) {
        const commentsList = response.data?.list || [];
        console.log('📥 加载评论列表:', commentsList);
        console.log('🔍 评论数据结构检查:', commentsList.map(c => ({
          id: c.id,
          _id: c._id,
          parentId: c.parentId,
          level: c.level,
          content: c.content.substring(0, 20) + '...',
          allKeys: Object.keys(c)
        })));

        // {{RIPER-5+SMART-6:
        //   Action: "Modified"
        //   Task_ID: "5ce679c8-0859-4480-82fc-dfdf81d5e348"
        //   Timestamp: "2025-08-08T11:09:49+08:00"
        //   Authoring_Subagent: "PM-内置顾问团"
        //   Principle_Applied: "SOLID-S (单一职责原则)"
        //   Quality_Check: "评论数据处理优化，支持扁平化渲染。"
        // }}
        // {{START_MODIFICATIONS}}

        // 设置原始评论数据（保持树形结构用于数据处理）
        setComments(commentsList);

        // {{RIPER-5+SMART-6:
        //   Action: "Parallel-Added"
        //   Task_ID: "7200d3a5-79bc-4356-b90c-563084ed94f1"
        //   Timestamp: "2025-08-08T11:09:49+08:00"
        //   Authoring_Subagent: "PM-内置顾问团"
        //   Principle_Applied: "SOLID-S (单一职责原则)"
        //   Quality_Check: "添加测试辅助功能和性能监控。"
        // }}
        // {{START_MODIFICATIONS}}

        // 测试辅助：性能监控
        if (process.env.NODE_ENV === 'development') {
          console.time('评论扁平化处理时间');
          const testFlattened = flattenComments(commentsList);
          console.timeEnd('评论扁平化处理时间');

          console.log('📊 评论系统性能指标:', {
            原始评论数: commentsList.length,
            扁平化后数量: testFlattened.length,
            最大层级: Math.max(...testFlattened.map(c => c.level || 1)),
            内存使用: performance.memory ?
              `${(performance.memory.usedJSHeapSize / 1024 / 1024).toFixed(2)}MB` :
              '不支持',
            时间戳: new Date().toISOString()
          });

          // 测试数据完整性
          const hasAllIds = testFlattened.every(c => c.id || c._id);
          const hasValidTimes = testFlattened.every(c => c.createdAt);
          const hasUserInfo = testFlattened.every(c => c.userInfo);

          console.log('🔍 数据完整性检查:', {
            ID完整性: hasAllIds ? '✅' : '❌',
            时间完整性: hasValidTimes ? '✅' : '❌',
            用户信息完整性: hasUserInfo ? '✅' : '❌'
          });
        }

        // {{END_MODIFICATIONS}}

        // {{END_MODIFICATIONS}}
      }
    } catch (error) {
      console.error('Failed to load comments:', error);
      message.error('加载评论失败');
    } finally {
      setCommentsLoading(false);
    }
  };

  // 处理点赞/取消点赞
  const handleLike = async () => {
    if (!currentUser) {
      message.warning('请先登录');
      return;
    }

    setLikingPoem(true);
    try {
      if (isLiked) {
        // 取消点赞
        const response = await userActionAPI.cancelAction({
          targetId: poemId,
          targetType: 'guwen',
          actionType: 'like'
        });
        if (response.code === 200) {
          setIsLiked(false);
          setLikeCount(prev => Math.max(0, prev - 1));
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
          setIsLiked(true);
          setLikeCount(prev => prev + 1);
          message.success('点赞成功');
        }
      }
    } catch (error) {
      console.error('Failed to toggle like:', error);
      message.error(isLiked ? '取消点赞失败' : '点赞失败');
    } finally {
      setLikingPoem(false);
    }
  };

  // 提交评论
  const handleSubmitComment = async () => {
    if (!currentUser) {
      message.warning('请先登录');
      return;
    }

    const content = replyingTo ? replyContent.trim() : newComment.trim();
    if (!content) {
      message.warning('请输入评论内容');
      return;
    }

    setSubmittingComment(true);
    try {
      const requestData = {
        targetId: poemId,
        targetType: 'guwen',
        content: content
      };

      // 如果是回复评论，添加父评论ID
      if (replyingTo) {
        // 使用_id字段，因为后端CommentDTO使用@JsonProperty("_id")
        const parentId = replyingTo._id || replyingTo.id;
        requestData.parentId = parentId;
        console.log('🔍 回复评论调试信息:', {
          replyingTo: replyingTo,
          parentId: parentId,
          replyingTo_id: replyingTo._id,
          replyingTo_id_field: replyingTo.id,
          requestData: requestData
        });
      }

      console.log('📤 发送评论请求:', requestData);
      const response = await commentAPI.create(requestData);
      console.log('📥 评论响应:', response);

      if (response.code === 200) {
        message.success(replyingTo ? '回复发表成功' : '评论发表成功');
        setNewComment('');
        setReplyContent('');
        setReplyingTo(null);
        setReplyVisible({});
        loadComments(); // 重新加载评论列表
      }
    } catch (error) {
      console.error('Failed to submit comment:', error);
      message.error(replyingTo ? '回复发表失败' : '评论发表失败');
    } finally {
      setSubmittingComment(false);
    }
  };

  // 处理回复按钮点击
  const handleReplyClick = (comment) => {
    if (!currentUser) {
      message.warning('请先登录');
      return;
    }
    setReplyingTo(comment);
    setReplyContent('');
    setReplyVisible({ [getCommentId(comment)]: true });
  };

  // 取消回复
  const handleCancelReply = () => {
    setReplyingTo(null);
    setReplyContent('');
    setReplyVisible({});
  };

  // 提交回复
  const handleSubmitReply = async (comment) => {
    if (!currentUser) {
      message.warning('请先登录');
      return;
    }

    if (!replyContent.trim()) {
      message.warning('请输入回复内容');
      return;
    }

    setSubmittingComment(true);
    try {
      // 使用_id字段，因为后端CommentDTO使用@JsonProperty("_id")
      const parentId = comment._id || comment.id;
      console.log('🔍 handleSubmitReply调试信息:', {
        comment: comment,
        parentId: parentId,
        comment_id: comment._id,
        comment_id_field: comment.id
      });

      const response = await commentAPI.create({
        targetId: poemId,
        targetType: 'guwen',
        content: replyContent.trim(),
        parentId: parentId
      });

      if (response.code === 200) {
        message.success('回复发表成功');
        setReplyContent('');
        const commentId = getCommentId(comment);
        setReplyVisible({ ...replyVisible, [commentId]: false });
        loadComments(); // 重新加载评论列表
      }
    } catch (error) {
      console.error('Failed to submit reply:', error);
      message.error('回复发表失败');
    } finally {
      setSubmittingComment(false);
    }
  };

  // 当弹窗打开时加载数据
  useEffect(() => {
    if (visible && poemId) {
      loadPoemDetail();
      loadComments();
    }
  }, [visible, poemId]);

  // 重置状态当弹窗关闭时
  useEffect(() => {
    if (!visible) {
      setPoem(null);
      setComments([]);
      setNewComment('');
      setIsLiked(false);
      setLikeCount(0);
    }
  }, [visible]);

  return (
    <>
    <Modal
      title={null}
      open={visible}
      onCancel={onClose}
      footer={null}
      width="90%"
      style={{ maxWidth: 1200 }}
      className="poem-detail-modal"
      closeIcon={<CloseOutlined style={{ fontSize: 18, color: '#666' }} />}
    >
      {loading ? (
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      ) : poem ? (
        <Row gutter={24} style={{ height: '100%', minHeight: '600px' }}>
          {/* 左侧：诗词详情 */}
          <Col span={14}>
            <div className="poem-content-section">
              <Card bordered={false} style={{ height: '100%' }}>
                {/* 整个左侧内容区域可滚动 */}
                <div className="poem-all-content-scroll">
                  {/* 诗词标题和基本信息 */}
                  <div style={{ textAlign: 'center', marginBottom: 24 }}>
                    <Title level={2} style={{ marginBottom: 8, color: '#1890ff' }}>
                      {poem.title}
                    </Title>
                    <Space>
                      <Tag color="blue" icon={<BookOutlined />}>{poem.dynasty}</Tag>
                      <Tag color="green" icon={<UserOutlined />}>{poem.writer}</Tag>
                      {normalizeType(poem.type).map(t => (
                        <Tag key={t} color="orange">{t}</Tag>
                      ))}
                    </Space>
                  </div>

                  {/* 诗词内容 */}
                  <div style={{
                    textAlign: 'center',
                    padding: '24px',
                    background: '#fafafa',
                    borderRadius: '8px',
                    marginBottom: 24
                  }}>
                    <div
                      className="poem-content"
                      style={{
                        fontSize: '18px',
                        lineHeight: '2',
                        fontFamily: 'KaiTi, 楷体, serif',
                        whiteSpace: 'pre-line'
                      }}
                    >
                      {poem.content}
                    </div>
                  </div>
                  {/* 基本信息 */}
                  <div style={{ marginBottom: 24 }}>
                    <Title level={4}>基本信息</Title>
                    <div style={{ background: '#f8f9fa', padding: '16px', borderRadius: '8px' }}>
                      <Row gutter={[16, 8]}>
                        <Col span={12}>
                          <Text strong>朝代：</Text>
                          <Text>{poem.dynasty || '未知'}</Text>
                        </Col>
                        <Col span={12}>
                          <Text strong>作者：</Text>
                          <Text>{poem.writer || '未知'}</Text>
                        </Col>
                        {poem.type && poem.type.length > 0 && (
                          <Col span={24}>
                            <Text strong>类型：</Text>
                            <div style={{ marginTop: 4 }}>
                              {normalizeType(poem.type).map(t => (
                                <Tag key={t} color="blue" style={{ marginBottom: 4 }}>
                                  {t}
                                </Tag>
                              ))}
                            </div>
                          </Col>
                        )}
                        {poem.audioUrl && (
                          <Col span={24}>
                            <Text strong>音频：</Text>
                            <div style={{ marginTop: 8 }}>
                              <audio controls style={{ width: '100%' }}>
                                <source src={poem.audioUrl} type="audio/mpeg" />
                                您的浏览器不支持音频播放。
                              </audio>
                            </div>
                          </Col>
                        )}
                        {(poem.createdAt || poem.updatedAt) && (
                          <Col span={24}>
                            <div style={{ fontSize: '12px', color: '#999', marginTop: 8 }}>
                              {poem.createdAt && (
                                <div>创建时间：{moment(poem.createdAt).format('YYYY-MM-DD HH:mm')}</div>
                              )}
                              {poem.updatedAt && (
                                <div>更新时间：{moment(poem.updatedAt).format('YYYY-MM-DD HH:mm')}</div>
                              )}
                            </div>
                          </Col>
                        )}
                      </Row>
                    </div>
                  </div>

                  {/* 注释 */}
                  {poem.remark && (
                    <div style={{ marginBottom: 24 }}>
                      <Title level={4}>
                        <BookOutlined style={{ marginRight: 8, color: '#1890ff' }} />
                        注释
                      </Title>
                      <div style={{
                        background: '#fff7e6',
                        border: '1px solid #ffd591',
                        borderRadius: '8px',
                        padding: '16px'
                      }}>
                        <Paragraph style={{
                          fontSize: '14px',
                          lineHeight: '1.8',
                          margin: 0,
                          whiteSpace: 'pre-line'
                        }}>
                          {poem.remark}
                        </Paragraph>
                      </div>
                    </div>
                  )}

                  {/* 翻译 */}
                  {poem.translation && (
                    <div style={{ marginBottom: 24 }}>
                      <Title level={4}>
                        <MessageOutlined style={{ marginRight: 8, color: '#52c41a' }} />
                        翻译
                      </Title>
                      <div style={{
                        background: '#f6ffed',
                        border: '1px solid #b7eb8f',
                        borderRadius: '8px',
                        padding: '16px'
                      }}>
                        <Paragraph style={{
                          fontSize: '14px',
                          lineHeight: '1.8',
                          margin: 0,
                          whiteSpace: 'pre-line'
                        }}>
                          {poem.translation}
                        </Paragraph>
                      </div>
                    </div>
                  )}

                  {/* 赏析 */}
                  {poem.shangxi && (
                    <div style={{ marginBottom: 24 }}>
                      <Title level={4}>
                        <StarOutlined style={{ marginRight: 8, color: '#722ed1' }} />
                        赏析
                      </Title>
                      <div style={{
                        background: '#f9f0ff',
                        border: '1px solid #d3adf7',
                        borderRadius: '8px',
                        padding: '16px'
                      }}>
                        <Paragraph style={{
                          fontSize: '14px',
                          lineHeight: '1.8',
                          margin: 0,
                          whiteSpace: 'pre-line'
                        }}>
                          {poem.shangxi}
                        </Paragraph>
                      </div>
                    </div>
                  )}
                </div>
              </Card>
            </div>
          </Col>

          {/* 右侧：评论区 */}
          <Col span={10}>
            <div className="comments-section">
              <Card
                title={
                  <div className="comments-header">
                    <Space align="center">
                      <MessageOutlined style={{ color: '#1890ff', fontSize: '16px' }} />
                      <span style={{ fontSize: '16px', fontWeight: 600 }}>评论区</span>
                      <div className="comment-count">
                        <Text type="secondary" style={{ fontSize: '14px' }}>
                          {comments.length > 0 ? `${comments.length} 条评论` : '暂无评论'}
                        </Text>
                      </div>
                    </Space>
                  </div>
                }
                bordered={false}
                style={{ height: '100%' }}
                bodyStyle={{ padding: '16px', display: 'flex', flexDirection: 'column', height: 'calc(100% - 57px)' }}
              >
                {/* 评论列表 */}
                <div className="comments-scroll" style={{ flex: 1, marginBottom: '16px' }}>
                  {commentsLoading ? (
                    <div className="loading-container">
                      <Spin size="large" />
                      <Text type="secondary" style={{ marginTop: '12px', display: 'block' }}>
                        加载评论中...
                      </Text>
                    </div>
                  ) : comments.length === 0 ? (
                    <div className="empty-comments">
                      <Empty
                        image={Empty.PRESENTED_IMAGE_SIMPLE}
                        description={
                          <div>
                            <Text type="secondary" style={{ fontSize: '14px' }}>
                              还没有人评论，快来抢沙发吧！
                            </Text>
                          </div>
                        }
                      />
                    </div>
                  ) : (
                    <div className="comments-list">
                      {/* {{RIPER-5+SMART-6:
                          Action: "Modified"
                          Task_ID: "5ce679c8-0859-4480-82fc-dfdf81d5e348"
                          Timestamp: "2025-08-08T11:09:49+08:00"
                          Authoring_Subagent: "PM-内置顾问团"
                          Principle_Applied: "SOLID-S (单一职责原则)"
                          Quality_Check: "使用扁平化渲染替换递归渲染。"
                      }} */}
                      {/* {{START_MODIFICATIONS}} */}
                      {(() => {
                        // 扁平化评论数据
                        const flatComments = flattenComments(comments);
                        console.log('🔄 扁平化评论数据:', flatComments.length, '条评论');

                        // 渲染扁平化评论列表
                        return flatComments.map((comment, index) => {
                          // 获取回复引用信息
                          const replyInfo = comment.parentId
                            ? getReplyReference(comment, flatComments)
                            : null;

                          // 使用扁平化渲染函数
                          return renderFlatComment(comment, replyInfo, flatComments);
                        });
                      })()}
                      {/* {{END_MODIFICATIONS}} */}
                    </div>
                  )}
                </div>

                {/* 评论输入区域 */}
                <div className="comment-input-section" style={{ borderTop: '1px solid #f0f0f0', paddingTop: '16px' }}>
                  {/* 回复引用显示 */}
                  {replyingTo && (
                    <div className="reply-reference-main" style={{
                      background: '#e6f7ff',
                      padding: '8px 12px',
                      borderRadius: '6px',
                      marginBottom: '8px',
                      fontSize: '12px'
                    }}>
                      <Space>
                        <Text type="secondary">回复</Text>
                        <Text strong style={{ color: '#1890ff' }}>{replyingTo.userInfo?.nickname || '匿名用户'}</Text>
                        <Text type="secondary">:</Text>
                        <Text type="secondary" ellipsis style={{ maxWidth: '200px' }}>
                          {replyingTo.content}
                        </Text>
                        <Button
                          type="text"
                          size="small"
                          icon={<CloseOutlined />}
                          onClick={() => setReplyingTo(null)}
                          style={{ padding: '0 4px' }}
                        />
                      </Space>
                    </div>
                  )}

                  <div className="comment-input-area" style={{ display: 'flex', gap: '8px', alignItems: 'flex-start' }}>
                    <Avatar
                      src={currentUser?.avatar}
                      icon={<UserOutlined />}
                      size={32}
                      style={{
                        backgroundColor: currentUser?.avatar ? 'transparent' : '#1890ff',
                        flexShrink: 0
                      }}
                    />
                    <div style={{ flex: 1 }}>
                      <TextArea
                        value={replyingTo ? replyContent : newComment}
                        onChange={(e) => {
                          if (replyingTo) {
                            setReplyContent(e.target.value);
                          } else {
                            setNewComment(e.target.value);
                          }
                        }}
                        placeholder={replyingTo ? `回复 ${replyingTo.userInfo?.nickname || '匿名用户'}...` : "写下你的评论..."}
                        autoSize={{ minRows: 3, maxRows: 6 }}
                        style={{ marginBottom: '8px' }}
                      />
                      <div style={{ textAlign: 'right' }}>
                        <Space>
                          {replyingTo && (
                            <Button
                              size="small"
                              onClick={() => {
                                setReplyingTo(null);
                                setReplyContent('');
                              }}
                            >
                              取消回复
                            </Button>
                          )}
                          <Button
                            type="primary"
                            size="small"
                            onClick={handleSubmitComment}
                            loading={submittingComment}
                            disabled={!(replyingTo ? replyContent.trim() : newComment.trim())}
                          >
                            {replyingTo ? '发布回复' : '发表评论'}
                          </Button>
                        </Space>
                      </div>
                    </div>
                  </div>
                </div>
              </Card>
            </div>
          </Col>
        </Row>
      ) : (
        <Empty description="诗词不存在" />
      )}
    </Modal>
    </>
  );
};

export default PoemDetailModal;
