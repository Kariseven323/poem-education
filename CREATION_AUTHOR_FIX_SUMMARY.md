# 创作作者权限问题修复

## 问题描述

用户在"我的创作"页面点击诗词跳转到创作详情页面时，无法看到"发布到社区"按钮和其他作者专属功能。

## 根本原因

在 `CreationServiceImpl.convertToDTO()` 方法中，使用了 `BeanUtils.copyProperties(creation, dto)` 来复制属性，但是：

- `Creation` 实体中的字段名是 `userId`
- `CreationDTO` 中的字段名是 `authorId`

由于字段名不匹配，`BeanUtils.copyProperties` 无法正确复制 `userId` 到 `authorId`，导致前端收到的创作数据中 `authorId` 为 `null`。

## 修复方案

### 后端修复 (`src/main/java/com/poem/education/service/impl/CreationServiceImpl.java`)

在 `convertToDTO` 方法中手动设置 `authorId` 字段：

```java
private CreationDTO convertToDTO(Creation creation) {
    CreationDTO dto = new CreationDTO();
    BeanUtils.copyProperties(creation, dto);
    
    // 手动设置authorId字段（因为Creation中是userId）
    dto.setAuthorId(creation.getUserId());

    // ... 其他转换逻辑
    
    return dto;
}
```

### 前端权限判断逻辑 (`frontend/src/components/CreationDetail.js`)

权限判断逻辑本身是正确的：

```javascript
// 检查是否为作者
const isAuthor = currentUser && currentUser.id === creation.authorId;

// 只有作者可以看到的功能
{isAuthor && (
  <>
    <Tooltip title="编辑">
      <Button icon={<EditOutlined />} onClick={handleEdit} />
    </Tooltip>
    <Tooltip title={creation.isPublic ? "取消发布" : "发布到社区"}>
      <Button 
        type={creation.isPublic ? "default" : "primary"}
        icon={creation.isPublic ? <LockOutlined /> : <GlobalOutlined />}
        onClick={handleTogglePublish}
        loading={publishing}
      >
        {creation.isPublic ? "取消发布" : "发布到社区"}
      </Button>
    </Tooltip>
  </>
)}
```

## 影响范围

这个修复解决了以下问题：

1. **"我的创作"页面跳转问题**：现在用户可以在自己的创作详情页面看到所有作者功能
2. **发布到社区功能**：作者可以正常使用发布/取消发布功能
3. **编辑功能**：作者可以看到编辑按钮
4. **AI评分功能**：作者可以触发AI评分
5. **权限控制**：非作者用户仍然无法看到这些功能，确保安全性

## 测试验证

### 测试步骤

1. 登录用户账号
2. 创建一首新诗词
3. 在"我的创作"页面点击该诗词
4. 验证是否可以看到：
   - "发布到社区"按钮
   - "编辑"按钮
   - "AI智能评分"按钮
5. 点击"发布到社区"，验证功能是否正常工作
6. 验证公开状态显示是否正确

### 预期结果

- 作者可以看到所有专属功能按钮
- 发布功能正常工作
- 公开状态正确显示
- 非作者用户仍然无法看到这些功能

## 技术细节

### 数据流程

1. **创建创作**：`Creation.userId` 设置为当前用户ID
2. **查询创作**：从MongoDB获取 `Creation` 实体
3. **转换DTO**：`convertToDTO` 方法将 `userId` 复制到 `authorId`
4. **前端接收**：前端收到包含正确 `authorId` 的数据
5. **权限判断**：`currentUser.id === creation.authorId` 正确判断

### 字段映射

| 实体层 (Creation) | DTO层 (CreationDTO) | 前端 (JavaScript) |
|------------------|---------------------|-------------------|
| userId (Long)    | authorId (Long)     | authorId (number) |

### 相关API端点

- `GET /api/v1/creations/{id}` - 获取创作详情
- `GET /api/v1/creations/my` - 获取我的创作列表
- `PUT /api/v1/creations/{id}/public` - 切换公开状态

## 后续优化建议

1. **统一字段命名**：考虑将 `Creation.userId` 重命名为 `authorId` 以保持一致性
2. **自动化测试**：添加单元测试验证 DTO 转换的正确性
3. **权限注解**：考虑使用 Spring Security 注解来简化权限控制逻辑
4. **前端类型检查**：使用 TypeScript 来避免类似的类型相关问题
