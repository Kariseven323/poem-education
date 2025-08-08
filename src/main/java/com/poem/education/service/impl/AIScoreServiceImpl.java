// {{RIPER-5+SMART-6:
//   Action: "Parallel-Added"
//   Task_ID: "ba11a9d3-6ea1-4b0b-b3d9-86fc30515df8"
//   Timestamp: "2025-08-08T13:45:20+08:00"
//   Authoring_Subagent: "PM-标准协作模式"
//   Principle_Applied: "Service实现最佳实践，异步处理和错误处理"
//   Quality_Check: "编译通过，AI调用和Mock模式完整。"
// }}
// {{START_MODIFICATIONS}}
package com.poem.education.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poem.education.entity.mongodb.Creation;
import com.poem.education.service.AIScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * AI评分服务实现类
 * 负责调用外部AI模型API进行诗词评分
 * 
 * @author poem-education-team
 * @since 2025-08-08
 */
@Service
public class AIScoreServiceImpl implements AIScoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIScoreServiceImpl.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${ai.score.api-url}")
    private String apiUrl;
    
    @Value("${ai.score.api-key}")
    private String apiKey;
    
    @Value("${ai.score.model}")
    private String model;
    
    @Value("${ai.score.timeout}")
    private int timeout;
    
    @Value("${ai.score.mock-enabled}")
    private boolean mockEnabled;
    
    @Value("${ai.score.retry-count}")
    private int retryCount;
    
    @Value("${ai.score.retry-interval}")
    private long retryInterval;

    @Value("${ai.score.max-tokens:1000}")
    private int scoreMaxTokens;

    @Value("${ai.suggest.max-tokens:2048}")
    private int suggestMaxTokens;
    
    private final Random random = new Random();
    
    @Override
    public Creation.AiScore callAIModel(String title, String content, String style) {
        logger.info("开始调用AI模型评分，标题：{}，风格：{}", title, style);
        
        // 如果启用Mock模式，直接返回Mock数据
        if (mockEnabled) {
            logger.info("Mock模式已启用，返回模拟评分数据");
            return generateMockScore(title, content, style);
        }
        
        // 实际AI模型调用逻辑
        return callAIModelWithRetry(title, content, style, retryCount);
    }
    
    @Override
    @Async
    public CompletableFuture<Creation.AiScore> callAIModelAsync(String title, String content, String style) {
        logger.info("开始异步调用AI模型评分，标题：{}，风格：{}", title, style);
        
        try {
            Creation.AiScore result = callAIModel(title, content, style);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            logger.error("异步AI评分调用失败", e);
            return CompletableFuture.failedFuture(e);
        }
    }
    
    @Override
    public Creation.AiScore generateMockScore(String title, String content, String style) {
        logger.debug("生成Mock评分数据，标题：{}，风格：{}", title, style);
        
        // 生成随机评分数据
        int rhythm = 70 + random.nextInt(31);      // 70-100
        int imagery = 65 + random.nextInt(36);     // 65-100
        int emotion = 75 + random.nextInt(26);     // 75-100
        int technique = 60 + random.nextInt(41);   // 60-100
        int innovation = 55 + random.nextInt(46);  // 55-100
        
        // 计算总分（加权平均）
        int totalScore = (int) (rhythm * 0.2 + imagery * 0.25 + emotion * 0.25 + technique * 0.2 + innovation * 0.1);
        
        // 创建多维度评分
        Creation.AiScore.ScoreDimensions dimensions = new Creation.AiScore.ScoreDimensions(
                rhythm, imagery, emotion, technique, innovation
        );
        
        // 生成评分详情
        String details = String.format(
                "AI评分分析：\n" +
                "韵律美感：%d分 - %s\n" +
                "意象表达：%d分 - %s\n" +
                "情感深度：%d分 - %s\n" +
                "技法运用：%d分 - %s\n" +
                "创新独特：%d分 - %s\n" +
                "综合评价：这是一首%s的%s作品，具有较好的艺术价值。",
                rhythm, getScoreComment(rhythm),
                imagery, getScoreComment(imagery),
                emotion, getScoreComment(emotion),
                technique, getScoreComment(technique),
                innovation, getScoreComment(innovation),
                getOverallComment(totalScore), style != null ? style : "诗词"
        );
        
        // 生成Mock思考过程
        String mockThinkingProcess = String.format(
                "这是一首题为《%s》的%s作品，让我从五个维度进行分析：\n\n" +
                "首先从韵律角度看，这首诗的节奏感和音韵搭配%s，给人以%s的听觉体验。\n\n" +
                "意象表达方面，作者运用了%s的意象，营造出%s的画面感。\n\n" +
                "情感深度上，整首诗传达出%s的情感基调，情感表达%s。\n\n" +
                "技法运用方面，可以看出作者在%s等方面有所体现，整体技法%s。\n\n" +
                "创新独特性上，这首诗在%s方面有一定的创新尝试，整体风格%s。\n\n" +
                "综合以上分析，这是一首具有%s特色的作品，值得细细品味。",
                title != null ? title : "未命名",
                style != null ? style : "诗词",
                getRandomComment(new String[]{"较为工整", "富有变化", "朗朗上口", "节奏明快"}),
                getRandomComment(new String[]{"优美", "和谐", "动听", "悦耳"}),
                getRandomComment(new String[]{"生动形象", "富有诗意", "贴切自然", "新颖独特"}),
                getRandomComment(new String[]{"清新淡雅", "深邃悠远", "生动活泼", "意境深远"}),
                getRandomComment(new String[]{"深沉", "欢快", "忧郁", "激昂", "宁静"}),
                getRandomComment(new String[]{"真挚动人", "层次丰富", "细腻入微", "饱满充实"}),
                getRandomComment(new String[]{"修辞手法", "结构安排", "语言运用", "意境营造"}),
                getRandomComment(new String[]{"娴熟", "恰当", "巧妙", "自然"}),
                getRandomComment(new String[]{"表达方式", "意象选择", "情感处理", "语言风格"}),
                getRandomComment(new String[]{"传统而不失新意", "现代感十足", "独具匠心", "别具一格"}),
                getRandomComment(new String[]{"较高艺术价值", "独特魅力", "深厚文化底蕴", "鲜明个性"})
        );

        // 创建AI评分对象
        Creation.AiScore aiScore = new Creation.AiScore();
        aiScore.setTotalScore(totalScore);
        aiScore.setFeedback(details);  // 使用正确的字段名
        aiScore.setThinkingProcess(mockThinkingProcess);  // 设置思考过程
        aiScore.setScoredAt(LocalDateTime.now());
        aiScore.setDimensions(dimensions);
        
        logger.info("Mock评分生成完成，总分：{}", totalScore);
        return aiScore;
    }
    
    @Override
    public boolean isServiceAvailable() {
        if (mockEnabled) {
            return true;
        }
        
        try {
            // 发送健康检查请求
            String healthCheckUrl = apiUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthCheckUrl, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            logger.warn("AI服务健康检查失败", e);
            return false;
        }
    }

    @Override
    public String generateRevisionSuggestions(String title, String content, String style) {
        // 若启用Mock，返回可读的模拟建议
        if (mockEnabled) {
            return "AI修改建议:\n" +
                    "1. 调整节奏: 适当增减句内顿挫，增强韵脚呼应。\n" +
                    "2. 意象统一: 选取更具连贯性的意象群，避免跳脱。\n" +
                    "3. 情感递进: 在第二节增加情绪过渡语，提升层次感。\n" +
                    "4. 技法优化: 尝试对仗或互文，增强语言张力。\n" +
                    "5. 创新表达: 加入一处反转或新奇比喻，形成记忆点。";
        }

        String prompt = String.format(
                "你是专业诗词编辑。\n" +
                "请先在<think>中进行精简思考（不超过200字），然后输出最终建议正文。\n" +
                "正文需分条列出、可执行，覆盖：节奏韵律、意象选择、情感铺陈、修辞技法、创新表达等维度。\n" +
                "正文长度尽量充分（允许2000-4000字），不得包含<think>标签。\n\n" +
                "标题：%s\n风格：%s\n内容：\n%s\n",
                title != null ? title : "未命名",
                style != null ? style : "未指定",
                content != null ? content : ""
        );

        try {
            // 直接调用现有模型接口，返回纯文本建议
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", new Object[]{
                    Map.of("role", "system", "content", "你是一位严谨的诗词编辑，擅长给出具体可执行的修改建议。"),
                    Map.of("role", "user", "content", prompt)
            });
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", suggestMaxTokens);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            String url = apiUrl + "/v1/chat/completions";
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                // 部分兼容 deepseek r1: 输出含<think>包裹的思考
                return choices.get(0).get("message").get("content").asText();
            }
        } catch (Exception e) {
            logger.warn("生成AI修改建议失败，返回默认建议", e);
        }

        return "AI修改建议暂不可用，请稍后再试。";
    }

    @Override
    @Async
    public CompletableFuture<String> generateRevisionSuggestionsAsync(String title, String content, String style) {
        try {
            return CompletableFuture.completedFuture(generateRevisionSuggestions(title, content, style));
        } catch (Exception e) {
            return CompletableFuture.completedFuture("AI修改建议暂不可用，请稍后再试。");
        }
    }
    
    /**
     * 带重试机制的AI模型调用
     */
    private Creation.AiScore callAIModelWithRetry(String title, String content, String style, int remainingRetries) {
        try {
            return performAIModelCall(title, content, style);
        } catch (Exception e) {
            logger.warn("AI模型调用失败，剩余重试次数：{}", remainingRetries, e);
            
            if (remainingRetries > 0) {
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("重试等待被中断", ie);
                }
                return callAIModelWithRetry(title, content, style, remainingRetries - 1);
            } else {
                logger.error("AI模型调用重试次数耗尽，返回默认评分", e);
                return generateDefaultScore(title, content, style);
            }
        }
    }
    
    /**
     * 执行实际的AI模型调用
     */
    private Creation.AiScore performAIModelCall(String title, String content, String style) {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", createMessages(title, content, style));
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", scoreMaxTokens);
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        // 发送请求
        String url = apiUrl + "/v1/chat/completions";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        
        // 解析响应
        return parseAIResponse(response.getBody());
    }
    
    /**
     * 创建AI模型的消息内容
     */
    private Object[] createMessages(String title, String content, String style) {
        String prompt = String.format(
                "请对以下诗词作品进行专业评分，从韵律、意象、情感、技法、创新五个维度给出0-100分的评分，并提供详细分析。\n\n" +
                "标题：%s\n" +
                "风格：%s\n" +
                "内容：\n%s\n\n" +
                "请以JSON格式返回评分结果，格式如下：\n" +
                "{\n" +
                "  \"totalScore\": 85,\n" +
                "  \"dimensions\": {\n" +
                "    \"rhythm\": 80,\n" +
                "    \"imagery\": 85,\n" +
                "    \"emotion\": 90,\n" +
                "    \"technique\": 75,\n" +
                "    \"innovation\": 85\n" +
                "  },\n" +
                "  \"details\": \"详细的评分分析...\"\n" +
                "}",
                title, style != null ? style : "未指定", content
        );
        
        return new Object[]{
                Map.of("role", "system", "content", "你是一位专业的诗词评价专家，具有深厚的文学功底和丰富的评价经验。"),
                Map.of("role", "user", "content", prompt)
        };
    }
    
    /**
     * 解析AI模型响应
     */
    private Creation.AiScore parseAIResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                String content = choices.get(0).get("message").get("content").asText();

                // 智能提取JSON和思考过程
                AIResponseData responseData = extractAIResponseData(content);

                // 解析JSON内容
                JsonNode scoreData = objectMapper.readTree(responseData.jsonContent);

                // 创建评分对象
                Creation.AiScore aiScore = new Creation.AiScore();
                aiScore.setTotalScore(scoreData.get("totalScore").asInt());
                aiScore.setFeedback(scoreData.get("details").asText());
                aiScore.setThinkingProcess(responseData.thinkingProcess);  // 设置思考过程
                aiScore.setScoredAt(LocalDateTime.now());

                // 创建多维度评分
                JsonNode dimensions = scoreData.get("dimensions");
                Creation.AiScore.ScoreDimensions scoreDimensions = new Creation.AiScore.ScoreDimensions(
                        dimensions.get("rhythm").asInt(),
                        dimensions.get("imagery").asInt(),
                        dimensions.get("emotion").asInt(),
                        dimensions.get("technique").asInt(),
                        dimensions.get("innovation").asInt()
                );
                aiScore.setDimensions(scoreDimensions);

                return aiScore;
            }
        } catch (Exception e) {
            logger.error("解析AI响应失败", e);
        }

        // 解析失败时返回默认评分
        return generateDefaultScore("", "", "");
    }
    
    /**
     * 生成默认评分（当AI调用失败时使用）
     */
    private Creation.AiScore generateDefaultScore(String title, String content, String style) {
        logger.info("生成默认评分数据");
        
        Creation.AiScore.ScoreDimensions dimensions = new Creation.AiScore.ScoreDimensions(75, 75, 75, 75, 75);
        
        Creation.AiScore aiScore = new Creation.AiScore();
        aiScore.setTotalScore(75);
        aiScore.setFeedback("AI评分服务暂时不可用，系统给出默认评分。建议稍后重新评分。");  // 使用正确的字段名
        aiScore.setScoredAt(LocalDateTime.now());
        aiScore.setDimensions(dimensions);
        
        return aiScore;
    }
    
    /**
     * 根据分数获取评价
     */
    private String getScoreComment(int score) {
        if (score >= 90) return "优秀";
        if (score >= 80) return "良好";
        if (score >= 70) return "中等";
        if (score >= 60) return "及格";
        return "需要改进";
    }
    
    /**
     * 根据总分获取整体评价
     */
    private String getOverallComment(int totalScore) {
        if (totalScore >= 90) return "优秀";
        if (totalScore >= 80) return "良好";
        if (totalScore >= 70) return "中等水平";
        if (totalScore >= 60) return "基础水平";
        return "初学水平";
    }

    /**
     * 从数组中随机选择一个评价
     */
    private String getRandomComment(String[] comments) {
        if (comments == null || comments.length == 0) {
            return "一般";
        }
        return comments[random.nextInt(comments.length)];
    }

    /**
     * 智能提取AI响应中的JSON内容和思考过程
     * 支持多种AI响应格式：
     * 1. 纯JSON格式
     * 2. <think>思考过程</think> + JSON格式
     * 3. 其他包含JSON的混合格式
     */
    private AIResponseData extractAIResponseData(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("AI响应内容为空");
        }

        String thinkingProcess = null;
        String jsonContent = content.trim();

        // 检查是否包含<think>标签
        if (content.contains("<think>") && content.contains("</think>")) {
            // 提取思考过程
            int thinkStart = content.indexOf("<think>") + 7;
            int thinkEnd = content.indexOf("</think>");
            if (thinkStart < thinkEnd) {
                thinkingProcess = content.substring(thinkStart, thinkEnd).trim();
            }

            // 移除思考过程部分，提取JSON
            jsonContent = content.replaceAll("<think>.*?</think>", "").trim();
        }

        // 尝试提取JSON对象（寻找第一个完整的JSON对象）
        jsonContent = extractJsonObject(jsonContent);

        return new AIResponseData(jsonContent, thinkingProcess);
    }

    /**
     * 从文本中提取JSON对象
     */
    private String extractJsonObject(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("无法从空文本中提取JSON");
        }

        text = text.trim();

        // 如果已经是纯JSON，直接返回
        if ((text.startsWith("{") && text.endsWith("}")) ||
            (text.startsWith("[") && text.endsWith("]"))) {
            return text;
        }

        // 寻找第一个JSON对象
        int jsonStart = text.indexOf("{");
        if (jsonStart == -1) {
            throw new RuntimeException("响应中未找到JSON对象");
        }

        // 寻找匹配的结束括号
        int braceCount = 0;
        int jsonEnd = -1;
        for (int i = jsonStart; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    jsonEnd = i;
                    break;
                }
            }
        }

        if (jsonEnd == -1) {
            throw new RuntimeException("JSON对象格式不完整");
        }

        return text.substring(jsonStart, jsonEnd + 1);
    }

    /**
     * AI响应数据内部类
     */
    private static class AIResponseData {
        final String jsonContent;
        final String thinkingProcess;

        AIResponseData(String jsonContent, String thinkingProcess) {
            this.jsonContent = jsonContent;
            this.thinkingProcess = thinkingProcess;
        }
    }
}
// {{END_MODIFICATIONS}}
