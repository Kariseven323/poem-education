// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "creation-edit-route-fix"
//   Timestamp: "2025-08-08T00:00:00Z"
//   Authoring_Subagent: "react-frontend-expert"
//   Principle_Applied: "KISS, DRY"
//   Quality_Check: "编译通过"
// }}
// {{START_MODIFICATIONS}}
import React, { useEffect, useState } from 'react';
import { Form, Input, Select, Button, Card, message, Typography, Space, Row, Col } from 'antd';
import { EditOutlined, SaveOutlined, ReloadOutlined, ArrowLeftOutlined, BookOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import { creationAPI } from '../utils/api';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;

const styleOptions = [
  { value: '律诗', label: '律诗', description: '格律严谨，对仗工整' },
  { value: '绝句', label: '绝句', description: '四句成篇，意境深远' },
  { value: '词', label: '词', description: '长短句式，音韵优美' },
  { value: '散文', label: '散文', description: '自由表达，情感真挚' },
  { value: '现代诗', label: '现代诗', description: '形式自由，意象新颖' },
  { value: '其他', label: '其他', description: '不拘一格，创新表达' }
];

function CreationEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      if (!id) {
        message.error('缺少作品ID');
        navigate('/creations');
        return;
      }
      setInitialLoading(true);
      try {
        const resp = await creationAPI.getById(id);
        if (resp.code === 200 && resp.data) {
          form.setFieldsValue({
            title: resp.data.title,
            style: resp.data.style || undefined,
            content: resp.data.content,
            description: resp.data.description || ''
          });
        } else {
          message.error(resp.message || '获取作品详情失败');
        }
      } catch (e) {
        console.error(e);
        message.error('加载作品失败，请稍后重试');
      } finally {
        setInitialLoading(false);
      }
    };
    load();
  }, [id]);

  const handleSubmit = async (values) => {
    if (!id) return;
    setLoading(true);
    try {
      const resp = await creationAPI.update(id, values);
      if (resp.code === 200) {
        message.success('保存成功');
        navigate(`/creations/${id}`);
      } else {
        message.error(resp.message || '保存失败');
      }
    } catch (e) {
      console.error(e);
      message.error('保存失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    form.resetFields();
  };

  return (
    <div style={{ padding: '24px', maxWidth: '900px', margin: '0 auto' }}>
      <Card loading={initialLoading}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <Title level={2} style={{ margin: 0 }}>
            <BookOutlined style={{ marginRight: 8, color: '#1890ff' }} />
            编辑作品
          </Title>
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(`/creations/${id}`)}>返回详情</Button>
          </Space>
        </div>

        <Form form={form} layout="vertical" onFinish={handleSubmit} size="large">
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
                <Input placeholder="请输入您的作品标题" prefix={<EditOutlined />} showCount maxLength={100} />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="style" label="创作风格">
                <Select placeholder="选择创作风格" allowClear optionLabelProp="label">
                  {styleOptions.map(option => (
                    <Option key={option.value} value={option.value} label={option.label}>
                      <div>
                        <div style={{ fontWeight: 'bold' }}>{option.label}</div>
                        <div style={{ fontSize: '12px', color: '#999' }}>{option.description}</div>
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
            <TextArea rows={8} showCount maxLength={5000} style={{ fontFamily: 'KaiTi, 楷体, serif', fontSize: '16px', lineHeight: '1.8' }} />
          </Form.Item>

          <Form.Item name="description" label="创作说明" rules={[{ max: 1000, message: '描述长度不能超过1000字符' }]}>
            <TextArea rows={3} showCount maxLength={1000} placeholder="可选" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SaveOutlined />} loading={loading}>保存</Button>
              <Button onClick={handleReset} icon={<ReloadOutlined />}>重置</Button>
              <Button onClick={() => navigate(`/creations/${id}`)} icon={<ArrowLeftOutlined />}>返回</Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}

export default CreationEdit;
// {{END_MODIFICATIONS}}


