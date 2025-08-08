// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "ddbbc9a7-94f7-4df6-a617-0d1e2f1c7a90"
//   Timestamp: "2025-08-08T14:15:00+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "React表单组件设计最佳实践，Ant Design Form规范"
//   Quality_Check: "编译通过，表单验证正常。"
// }}
// {{START_MODIFICATIONS}}
import React, { useState } from 'react';
import { 
  Form, 
  Input, 
  Select, 
  Button, 
  Card, 
  message, 
  Typography, 
  Space,
  Row,
  Col,
  Divider
} from 'antd';
import { 
  EditOutlined, 
  SendOutlined, 
  ReloadOutlined,
  ThunderboltOutlined,
  BookOutlined
} from '@ant-design/icons';
import { creationAPI } from '../utils/api';
import { useNavigate } from 'react-router-dom';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;

/**
 * 诗词创作表单组件
 * 提供诗词创作输入界面，支持表单验证、提交处理和AI评分触发
 */
const PoemCreation = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [aiScoring, setAiScoring] = useState(false);
  const [createdId, setCreatedId] = useState(null);
  const navigate = useNavigate();

  // 诗词风格选项
  const styleOptions = [
    { value: '律诗', label: '律诗', description: '格律严谨，对仗工整' },
    { value: '绝句', label: '绝句', description: '四句成篇，意境深远' },
    { value: '词', label: '词', description: '长短句式，音韵优美' },
    { value: '散文', label: '散文', description: '自由表达，情感真挚' },
    { value: '现代诗', label: '现代诗', description: '形式自由，意象新颖' },
    { value: '其他', label: '其他', description: '不拘一格，创新表达' }
  ];

  // 表单提交处理
  const handleSubmit = async (values) => {
    setLoading(true);
    try {
      const response = await creationAPI.create(values);
      if (response.code === 200) {
        message.success('创作提交成功！');
        setCreatedId(response.data.id);
        
        // 询问是否立即进行AI评分
        const shouldScore = await new Promise((resolve) => {
          message.info({
            content: (
              <div>
                <div>创作已保存！是否立即进行AI评分？</div>
                <div style={{ marginTop: 8 }}>
                  <Button 
                    size="small" 
                    type="primary" 
                    onClick={() => resolve(true)}
                    style={{ marginRight: 8 }}
                  >
                    立即评分
                  </Button>
                  <Button 
                    size="small" 
                    onClick={() => resolve(false)}
                  >
                    稍后再说
                  </Button>
                </div>
              </div>
            ),
            duration: 0,
            key: 'ai-score-prompt'
          });
        });

        if (shouldScore) {
          handleAIScore(response.data.id);
        } else {
          message.destroy('ai-score-prompt');
        }
        
      } else {
        message.error(response.message || '创作提交失败');
      }
    } catch (error) {
      console.error('Creation submit error:', error);
      message.error(error.response?.data?.message || '创作提交失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  // AI评分处理
  const handleAIScore = async (id) => {
    const targetId = id || createdId;
    if (!targetId) {
      message.error('请先提交创作');
      return;
    }

    setAiScoring(true);
    message.destroy('ai-score-prompt');
    
    try {
      const response = await creationAPI.requestScore(targetId);
      if (response.code === 200) {
        message.success('AI评分请求已提交，正在分析中...');
        
        // 3秒后跳转到详情页面查看结果
        setTimeout(() => {
          navigate(`/creations/${targetId}`);
        }, 3000);
        
      } else {
        message.error(response.message || 'AI评分请求失败');
      }
    } catch (error) {
      console.error('AI score request error:', error);
      message.error(error.response?.data?.message || 'AI评分请求失败');
    } finally {
      setAiScoring(false);
    }
  };

  // 重置表单
  const handleReset = () => {
    form.resetFields();
    setCreatedId(null);
    message.info('表单已重置');
  };

  // 查看创作详情
  const handleViewCreation = () => {
    if (createdId) {
      navigate(`/creations/${createdId}`);
    }
  };

  return (
    <div style={{ padding: '24px', maxWidth: '800px', margin: '0 auto' }}>
      <Card>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <Title level={2}>
            <BookOutlined style={{ marginRight: 8, color: '#1890ff' }} />
            诗词创作
          </Title>
          <Text type="secondary">
            挥毫泼墨，抒发心中诗意。创作完成后可使用AI智能评分功能。
          </Text>
        </div>

        <Form
          form={form}
          name="poemCreation"
          onFinish={handleSubmit}
          layout="vertical"
          size="large"
          autoComplete="off"
        >
          <Row gutter={16}>
            <Col span={16}>
              <Form.Item
                name="title"
                label="作品标题"
                rules={[
                  { required: true, message: '请输入作品标题' },
                  { min: 1, max: 100, message: '标题长度必须在1-100字符之间' }
                ]}
              >
                <Input
                  placeholder="请输入您的作品标题"
                  prefix={<EditOutlined />}
                  showCount
                  maxLength={100}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="style"
                label="创作风格"
                rules={[
                  { required: false, message: '请选择创作风格' }
                ]}
              >
                <Select
                  placeholder="选择创作风格"
                  allowClear
                  optionLabelProp="label"
                >
                  {styleOptions.map(option => (
                    <Option 
                      key={option.value} 
                      value={option.value} 
                      label={option.label}
                    >
                      <div>
                        <div style={{ fontWeight: 'bold' }}>{option.label}</div>
                        <div style={{ fontSize: '12px', color: '#999' }}>
                          {option.description}
                        </div>
                      </div>
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="content"
            label="作品内容"
            rules={[
              { required: true, message: '请输入作品内容' },
              { min: 1, max: 5000, message: '内容长度必须在1-5000字符之间' }
            ]}
          >
            <TextArea
              placeholder="请输入您的诗词作品内容&#10;&#10;示例：&#10;春眠不觉晓，&#10;处处闻啼鸟。&#10;夜来风雨声，&#10;花落知多少。"
              rows={8}
              showCount
              maxLength={5000}
              style={{ 
                fontFamily: 'KaiTi, 楷体, serif',
                fontSize: '16px',
                lineHeight: '1.8'
              }}
            />
          </Form.Item>

          <Form.Item
            name="description"
            label="创作说明"
            rules={[
              { max: 1000, message: '描述长度不能超过1000字符' }
            ]}
          >
            <TextArea
              placeholder="请简要描述您的创作背景、灵感来源或想要表达的情感（可选）"
              rows={3}
              showCount
              maxLength={1000}
            />
          </Form.Item>

          <Divider />

          <Form.Item style={{ marginBottom: 0 }}>
            <Space size="middle">
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                icon={<SendOutlined />}
                size="large"
              >
                {loading ? '提交中...' : '提交创作'}
              </Button>

              {createdId && (
                <Button
                  type="default"
                  onClick={handleAIScore}
                  loading={aiScoring}
                  icon={<ThunderboltOutlined />}
                  size="large"
                >
                  {aiScoring ? 'AI评分中...' : 'AI智能评分'}
                </Button>
              )}

              <Button
                onClick={handleReset}
                icon={<ReloadOutlined />}
                size="large"
              >
                重置表单
              </Button>

              {createdId && (
                <Button
                  type="link"
                  onClick={handleViewCreation}
                  size="large"
                >
                  查看作品详情
                </Button>
              )}
            </Space>
          </Form.Item>
        </Form>

        {/* 创作提示 */}
        <Card 
          size="small" 
          style={{ 
            marginTop: 24, 
            background: '#f6ffed', 
            border: '1px solid #b7eb8f' 
          }}
        >
          <Title level={5} style={{ color: '#52c41a', marginBottom: 8 }}>
            💡 创作小贴士
          </Title>
          <ul style={{ margin: 0, paddingLeft: 20, color: '#666' }}>
            <li>选择合适的创作风格有助于AI更准确地评分</li>
            <li>内容建议分行输入，便于阅读和欣赏</li>
            <li>创作说明可以帮助读者更好地理解您的作品</li>
            <li>提交后可立即使用AI评分功能获得专业点评</li>
          </ul>
        </Card>
      </Card>
    </div>
  );
};

export default PoemCreation;
// {{END_MODIFICATIONS}}
