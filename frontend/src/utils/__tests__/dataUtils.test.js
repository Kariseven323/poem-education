// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "data-utils-test"
//   Timestamp: "2025-08-07T15:01:34+08:00"
//   Authoring_Subagent: "PM-快速模式"
//   Principle_Applied: "TDD (Test-Driven Development)"
//   Quality_Check: "单元测试覆盖率目标 > 80%。"
// }}
// {{START_MODIFICATIONS}}

import {
  normalizeType,
  normalizeStats,
  normalizeContent,
  validatePoem,
  normalizePoem,
  formatTextWithLineBreaks,
  sanitizeHtmlContent,
  parseAndFormatJsonText,
  normalizeContentExtended,
  validateAndCleanHtml
} from '../dataUtils';

describe('dataUtils', () => {
  describe('normalizeType', () => {
    test('should handle array input correctly', () => {
      expect(normalizeType(['诗', '词'])).toEqual(['诗', '词']);
      expect(normalizeType(['诗'])).toEqual(['诗']);
      expect(normalizeType([])).toEqual([]);
    });

    test('should handle string input correctly', () => {
      expect(normalizeType('诗')).toEqual(['诗']);
      expect(normalizeType('词')).toEqual(['词']);
    });

    test('should handle null/undefined input', () => {
      expect(normalizeType(null)).toEqual([]);
      expect(normalizeType(undefined)).toEqual([]);
      expect(normalizeType('')).toEqual([]);
    });

    test('should filter out invalid array elements', () => {
      expect(normalizeType(['诗', null, '词', undefined, ''])).toEqual(['诗', '词']);
      expect(normalizeType([123, '诗', true])).toEqual(['诗']);
    });

    test('should handle non-string, non-array input', () => {
      expect(normalizeType(123)).toEqual([]);
      expect(normalizeType({})).toEqual([]);
      expect(normalizeType(true)).toEqual([]);
    });
  });

  describe('normalizeStats', () => {
    test('should handle valid stats object', () => {
      const stats = {
        viewCount: 100,
        likeCount: 50,
        favoriteCount: 25,
        commentCount: 10
      };
      expect(normalizeStats(stats)).toEqual(stats);
    });

    test('should handle string numbers', () => {
      const stats = {
        viewCount: '100',
        likeCount: '50',
        favoriteCount: '25',
        commentCount: '10'
      };
      expect(normalizeStats(stats)).toEqual({
        viewCount: 100,
        likeCount: 50,
        favoriteCount: 25,
        commentCount: 10
      });
    });

    test('should handle missing fields', () => {
      expect(normalizeStats({})).toEqual({
        viewCount: 0,
        likeCount: 0,
        favoriteCount: 0,
        commentCount: 0
      });
    });

    test('should handle null/undefined input', () => {
      const expected = {
        viewCount: 0,
        likeCount: 0,
        favoriteCount: 0,
        commentCount: 0
      };
      expect(normalizeStats(null)).toEqual(expected);
      expect(normalizeStats(undefined)).toEqual(expected);
    });

    test('should handle invalid values', () => {
      const stats = {
        viewCount: 'invalid',
        likeCount: null,
        favoriteCount: undefined,
        commentCount: NaN
      };
      expect(normalizeStats(stats)).toEqual({
        viewCount: 0,
        likeCount: 0,
        favoriteCount: 0,
        commentCount: 0
      });
    });
  });

  describe('normalizeContent', () => {
    test('should handle normal content', () => {
      expect(normalizeContent('床前明月光')).toBe('床前明月光');
    });

    test('should escape HTML characters', () => {
      expect(normalizeContent('<script>alert("xss")</script>'))
        .toBe('&lt;script&gt;alert(&quot;xss&quot;)&lt;/script&gt;');
    });

    test('should handle length limit', () => {
      expect(normalizeContent('床前明月光，疑是地上霜', 5)).toBe('床前明月光...');
    });

    test('should handle null/undefined input', () => {
      expect(normalizeContent(null)).toBe('');
      expect(normalizeContent(undefined)).toBe('');
    });

    test('should handle non-string input', () => {
      expect(normalizeContent(123)).toBe('');
      expect(normalizeContent({})).toBe('');
    });
  });

  describe('validatePoem', () => {
    const validPoem = {
      _id: '507f1f77bcf86cd799439011',
      title: '静夜思',
      dynasty: '唐',
      writer: '李白',
      content: '床前明月光，疑是地上霜。举头望明月，低头思故乡。'
    };

    test('should validate correct poem object', () => {
      expect(validatePoem(validPoem)).toBe(true);
    });

    test('should reject poem with missing required fields', () => {
      expect(validatePoem({ ...validPoem, title: undefined })).toBe(false);
      expect(validatePoem({ ...validPoem, _id: '' })).toBe(false);
    });

    test('should reject null/undefined input', () => {
      expect(validatePoem(null)).toBe(false);
      expect(validatePoem(undefined)).toBe(false);
    });

    test('should reject non-object input', () => {
      expect(validatePoem('string')).toBe(false);
      expect(validatePoem(123)).toBe(false);
    });
  });

  describe('normalizePoem', () => {
    const inputPoem = {
      _id: '507f1f77bcf86cd799439011',
      title: '静夜思',
      dynasty: '唐',
      writer: '李白',
      content: '床前明月光，疑是地上霜。举头望明月，低头思故乡。',
      type: '诗',
      stats: { viewCount: '100', likeCount: '50' }
    };

    test('should normalize valid poem object', () => {
      const result = normalizePoem(inputPoem);
      expect(result.type).toEqual(['诗']);
      expect(result.stats.viewCount).toBe(100);
      expect(result.stats.likeCount).toBe(50);
    });

    test('should return null for invalid poem', () => {
      expect(normalizePoem({})).toBe(null);
      expect(normalizePoem(null)).toBe(null);
    });
  });

  // 新增工具函数测试
  describe('formatTextWithLineBreaks', () => {
    test('should convert line breaks correctly', () => {
      expect(formatTextWithLineBreaks('第一行\n第二行')).toBe('第一行<br/>第二行');
      expect(formatTextWithLineBreaks('第一行\\n第二行')).toBe('第一行<br/>第二行');
      expect(formatTextWithLineBreaks('第一行\r\n第二行')).toBe('第一行<br/>第二行');
    });

    test('should handle tab characters', () => {
      expect(formatTextWithLineBreaks('缩进\t内容')).toBe('缩进&nbsp;&nbsp;&nbsp;&nbsp;内容');
    });

    test('should handle empty values', () => {
      expect(formatTextWithLineBreaks('')).toBe('');
      expect(formatTextWithLineBreaks(null)).toBe('');
      expect(formatTextWithLineBreaks(undefined)).toBe('');
    });
  });

  describe('sanitizeHtmlContent', () => {
    test('should remove dangerous HTML tags', () => {
      const dangerousHtml = '<script>alert("xss")</script><p>安全内容</p>';
      const result = sanitizeHtmlContent(dangerousHtml);
      expect(result).not.toContain('<script>');
      expect(result).toContain('<p>安全内容</p>');
    });

    test('should remove event handlers', () => {
      const htmlWithEvents = '<div onclick="alert()">内容</div>';
      const result = sanitizeHtmlContent(htmlWithEvents);
      expect(result).not.toContain('onclick');
    });

    test('should handle line breaks based on options', () => {
      const htmlWithBr = '第一行<br/>第二行';

      const withBreaks = sanitizeHtmlContent(htmlWithBr, { allowLineBreaks: true });
      expect(withBreaks).toContain('<br/>');

      const withoutBreaks = sanitizeHtmlContent(htmlWithBr, { allowLineBreaks: false });
      expect(withoutBreaks).not.toContain('<br/>');
    });
  });

  describe('parseAndFormatJsonText', () => {
    test('should parse JSON and format text correctly', () => {
      const jsonStr = '{"人物生平":"第一段\\n第二段","主要成就":"成就内容"}';
      const result = parseAndFormatJsonText(jsonStr);

      expect(result).toHaveProperty('人物生平');
      expect(result['人物生平']).toContain('<br/>');
    });

    test('should handle invalid JSON', () => {
      const invalidJson = '这不是JSON格式的文本\\n包含换行符';
      const result = parseAndFormatJsonText(invalidJson);

      expect(typeof result).toBe('string');
      expect(result).toContain('<br/>');
    });
  });

  describe('validateAndCleanHtml', () => {
    test('should validate safe HTML', () => {
      const safeHtml = '<p>安全内容</p><br/>';
      const result = validateAndCleanHtml(safeHtml);

      expect(result.isValid).toBe(true);
      expect(result.warnings).toHaveLength(0);
    });

    test('should detect dangerous HTML', () => {
      const dangerousHtml = '<script>alert("xss")</script><p>内容</p>';
      const result = validateAndCleanHtml(dangerousHtml);

      expect(result.isValid).toBe(false);
      expect(result.warnings.length).toBeGreaterThan(0);
      expect(result.cleaned).not.toContain('<script>');
    });
  });
});

// {{END_MODIFICATIONS}}
