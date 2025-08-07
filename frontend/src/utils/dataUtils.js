// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "data-utils-creation"
//   Timestamp: "2025-08-07T15:01:34+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "DRY (Don't Repeat Yourself)"
//   Quality_Check: "数据类型安全处理，防止运行时错误。"
// }}
// {{START_MODIFICATIONS}}

/**
 * 数据处理工具函数
 * 用于处理前后端数据结构不一致的问题
 */

/**
 * 安全地处理poem.type字段，确保兼容数组和字符串两种格式
 * @param {string|string[]|null|undefined} type - 诗词类型数据
 * @returns {string[]} - 始终返回字符串数组
 */
export const normalizeType = (type) => {
  if (!type) {
    return [];
  }
  
  if (Array.isArray(type)) {
    return type.filter(t => t && typeof t === 'string');
  }
  
  if (typeof type === 'string') {
    return [type];
  }
  
  return [];
};

/**
 * 安全地处理统计数据，确保返回数字类型
 * @param {object} stats - 统计数据对象
 * @returns {object} - 标准化的统计数据
 */
export const normalizeStats = (stats) => {
  if (!stats || typeof stats !== 'object') {
    return {
      viewCount: 0,
      likeCount: 0,
      favoriteCount: 0,
      commentCount: 0
    };
  }
  
  return {
    viewCount: parseInt(stats.viewCount) || 0,
    likeCount: parseInt(stats.likeCount) || 0,
    favoriteCount: parseInt(stats.favoriteCount) || 0,
    commentCount: parseInt(stats.commentCount) || 0
  };
};

/**
 * 安全地处理诗词内容，防止XSS攻击
 * @param {string} content - 诗词内容
 * @param {number} maxLength - 最大长度限制
 * @returns {string} - 处理后的内容
 */
export const normalizeContent = (content, maxLength = null) => {
  if (!content || typeof content !== 'string') {
    return '';
  }
  
  // 基本的HTML转义
  const escaped = content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#x27;');
  
  if (maxLength && escaped.length > maxLength) {
    return escaped.substring(0, maxLength) + '...';
  }
  
  return escaped;
};

/**
 * 验证诗词对象的完整性
 * @param {object} poem - 诗词对象
 * @returns {boolean} - 是否为有效的诗词对象
 */
export const validatePoem = (poem) => {
  if (!poem || typeof poem !== 'object') {
    return false;
  }
  
  // 必需字段检查
  const requiredFields = ['_id', 'title', 'dynasty', 'writer', 'content'];
  return requiredFields.every(field => poem[field] && typeof poem[field] === 'string');
};

/**
 * 标准化诗词对象，确保所有字段都符合预期格式
 * @param {object} poem - 原始诗词对象
 * @returns {object} - 标准化后的诗词对象
 */
export const normalizePoem = (poem) => {
  if (!validatePoem(poem)) {
    console.warn('Invalid poem object:', poem);
    return null;
  }
  
  return {
    ...poem,
    type: normalizeType(poem.type),
    stats: normalizeStats(poem.stats),
    content: normalizeContent(poem.content),
    title: normalizeContent(poem.title),
    remark: normalizeContent(poem.remark),
    shangxi: normalizeContent(poem.shangxi),
    translation: normalizeContent(poem.translation)
  };
};

// {{END_MODIFICATIONS}}
