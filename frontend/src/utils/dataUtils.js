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

/**
 * 处理换行符转换，将文本中的换行符转换为HTML换行标签
 * @param {string} text - 原始文本内容
 * @returns {string} - 转换后的HTML文本
 */
export const formatTextWithLineBreaks = (text) => {
  if (!text || typeof text !== 'string') {
    return '';
  }

  return text
    .replace(/\\n/g, '<br/>') // 处理转义的换行符
    .replace(/\n/g, '<br/>') // 处理实际的换行符
    .replace(/\\r\\n/g, '<br/>') // 处理Windows换行符
    .replace(/\r\n/g, '<br/>') // 处理实际的Windows换行符
    .replace(/\\r/g, '<br/>') // 处理Mac换行符
    .replace(/\r/g, '<br/>') // 处理实际的Mac换行符
    .replace(/\\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;') // 处理制表符
    .replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;'); // 处理实际的制表符
};

/**
 * 安全的HTML内容处理，提供XSS防护
 * @param {string} htmlContent - 包含HTML标签的内容
 * @param {object} options - 配置选项
 * @param {boolean} options.allowLineBreaks - 是否允许换行标签，默认true
 * @param {boolean} options.allowBasicFormatting - 是否允许基本格式化标签，默认false
 * @returns {string} - 安全处理后的HTML内容
 */
export const sanitizeHtmlContent = (htmlContent, options = {}) => {
  if (!htmlContent || typeof htmlContent !== 'string') {
    return '';
  }

  const {
    allowLineBreaks = true,
    allowBasicFormatting = false
  } = options;

  // 移除危险的HTML标签和属性
  let sanitized = htmlContent
    .replace(/<script[^>]*>.*?<\/script>/gi, '') // 移除script标签
    .replace(/<iframe[^>]*>.*?<\/iframe>/gi, '') // 移除iframe标签
    .replace(/<object[^>]*>.*?<\/object>/gi, '') // 移除object标签
    .replace(/<embed[^>]*>/gi, '') // 移除embed标签
    .replace(/<form[^>]*>.*?<\/form>/gi, '') // 移除form标签
    .replace(/<input[^>]*>/gi, '') // 移除input标签
    .replace(/<textarea[^>]*>.*?<\/textarea>/gi, '') // 移除textarea标签
    .replace(/<select[^>]*>.*?<\/select>/gi, '') // 移除select标签
    .replace(/javascript:/gi, '') // 移除javascript协议
    .replace(/vbscript:/gi, '') // 移除vbscript协议
    .replace(/on\w+\s*=/gi, '') // 移除事件处理器
    .replace(/style\s*=/gi, ''); // 移除style属性

  // 如果不允许换行标签，移除它们
  if (!allowLineBreaks) {
    sanitized = sanitized.replace(/<br\s*\/?>/gi, ' ');
  }

  // 如果不允许基本格式化标签，移除它们
  if (!allowBasicFormatting) {
    sanitized = sanitized
      .replace(/<\/?b>/gi, '')
      .replace(/<\/?strong>/gi, '')
      .replace(/<\/?i>/gi, '')
      .replace(/<\/?em>/gi, '')
      .replace(/<\/?u>/gi, '')
      .replace(/<\/?p>/gi, '')
      .replace(/<\/?div[^>]*>/gi, '');
  }

  return sanitized.trim();
};

/**
 * 解析JSON格式的文本内容并进行格式化处理
 * @param {string} jsonStr - JSON格式的字符串
 * @param {object} options - 配置选项
 * @param {boolean} options.formatLineBreaks - 是否格式化换行符，默认true
 * @param {boolean} options.sanitizeHtml - 是否进行HTML安全处理，默认true
 * @returns {object|string} - 解析后的对象或格式化后的字符串
 */
export const parseAndFormatJsonText = (jsonStr, options = {}) => {
  if (!jsonStr || typeof jsonStr !== 'string') {
    return '';
  }

  const {
    formatLineBreaks = true,
    sanitizeHtml = true
  } = options;

  try {
    const parsedData = JSON.parse(jsonStr);

    if (typeof parsedData === 'object' && parsedData !== null) {
      // 如果是对象，递归处理每个字段
      const processedData = {};

      for (const [key, value] of Object.entries(parsedData)) {
        if (typeof value === 'string') {
          let processedValue = value;

          if (formatLineBreaks) {
            processedValue = formatTextWithLineBreaks(processedValue);
          }

          if (sanitizeHtml) {
            processedValue = sanitizeHtmlContent(processedValue, {
              allowLineBreaks: formatLineBreaks,
              allowBasicFormatting: false
            });
          }

          processedData[key] = processedValue;
        } else {
          processedData[key] = value;
        }
      }

      return processedData;
    } else {
      // 如果是基本类型，直接返回
      return parsedData;
    }
  } catch (error) {
    console.warn('JSON解析失败，返回格式化后的原始字符串:', error.message);

    // JSON解析失败时，作为普通文本处理
    let processedText = jsonStr;

    if (formatLineBreaks) {
      processedText = formatTextWithLineBreaks(processedText);
    }

    if (sanitizeHtml) {
      processedText = sanitizeHtmlContent(processedText, {
        allowLineBreaks: formatLineBreaks,
        allowBasicFormatting: false
      });
    }

    return processedText;
  }
};

/**
 * 扩展的内容标准化函数，支持HTML内容和换行符处理
 * @param {string} content - 原始内容
 * @param {object} options - 配置选项
 * @param {number} options.maxLength - 最大长度限制
 * @param {boolean} options.allowHtml - 是否允许HTML内容，默认false
 * @param {boolean} options.formatLineBreaks - 是否格式化换行符，默认false
 * @returns {string} - 处理后的内容
 */
export const normalizeContentExtended = (content, options = {}) => {
  if (!content || typeof content !== 'string') {
    return '';
  }

  const {
    maxLength = null,
    allowHtml = false,
    formatLineBreaks = false
  } = options;

  let processed = content;

  if (allowHtml) {
    // 如果允许HTML，进行安全处理
    processed = sanitizeHtmlContent(processed, {
      allowLineBreaks: formatLineBreaks,
      allowBasicFormatting: true
    });
  } else {
    // 如果不允许HTML，进行转义
    processed = normalizeContent(processed);

    if (formatLineBreaks) {
      // 在转义后处理换行符
      processed = formatTextWithLineBreaks(processed);
    }
  }

  // 长度限制处理
  if (maxLength && processed.length > maxLength) {
    // 如果包含HTML标签，需要智能截断
    if (allowHtml && processed.includes('<')) {
      // 简单的HTML标签感知截断
      const textOnly = processed.replace(/<[^>]*>/g, '');
      if (textOnly.length > maxLength) {
        const truncated = textOnly.substring(0, maxLength);
        processed = truncated + '...';
      }
    } else {
      processed = processed.substring(0, maxLength) + '...';
    }
  }

  return processed;
};

/**
 * 验证和清理HTML内容的工具函数
 * @param {string} htmlContent - HTML内容
 * @returns {object} - 验证结果和清理后的内容
 */
export const validateAndCleanHtml = (htmlContent) => {
  if (!htmlContent || typeof htmlContent !== 'string') {
    return {
      isValid: false,
      cleaned: '',
      warnings: ['内容为空或不是字符串类型']
    };
  }

  const warnings = [];
  let cleaned = htmlContent;

  // 检查危险内容
  const dangerousPatterns = [
    { pattern: /<script/gi, message: '包含script标签' },
    { pattern: /javascript:/gi, message: '包含javascript协议' },
    { pattern: /on\w+\s*=/gi, message: '包含事件处理器' },
    { pattern: /<iframe/gi, message: '包含iframe标签' },
    { pattern: /<object/gi, message: '包含object标签' },
    { pattern: /<embed/gi, message: '包含embed标签' }
  ];

  dangerousPatterns.forEach(({ pattern, message }) => {
    if (pattern.test(htmlContent)) {
      warnings.push(message);
    }
  });

  // 清理内容
  cleaned = sanitizeHtmlContent(cleaned, {
    allowLineBreaks: true,
    allowBasicFormatting: true
  });

  return {
    isValid: warnings.length === 0,
    cleaned,
    warnings
  };
};

// {{END_MODIFICATIONS}}
