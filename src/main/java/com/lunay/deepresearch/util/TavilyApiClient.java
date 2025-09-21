package com.lunay.deepresearch.util;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Tavily API 调用
 *
 * @author 苍镜月
 */

@Component
@Slf4j
public class TavilyApiClient {

    private final RestClient restClient;

    public TavilyApiClient(RestClient.Builder restClientBuilder, @Value("${deepresearch.tavliy.apiKey}") String tavilyApiKey) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.tavily.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tavilyApiKey)
                .build();
    }

    /**
     * 对 Tavily API 执行搜索查询。
     *
     * @param request 包含查询参数的 TavilyRequest 对象。
     * @return 包含搜索结果的 TavilyResponse 对象。
     */
    public TavilyResponse search(TavilyRequest request) {

        if (request.getQuery() == null || request.getQuery().isEmpty()) {
            throw new IllegalArgumentException("查询参数是必需的。");
        }
        log.info("收到 TavilyRequest: {}", request);

        // 构建包含所有参数的请求负载，必要时设置默认值
        TavilyRequest requestWithApiKey = TavilyRequest.builder()
                .query(request.getQuery())
                .searchDepth(Objects.nonNull(request.getSearchDepth()) ? request.getSearchDepth() : "basic")
                .topic(Objects.nonNull(request.getTopic()) ? request.getTopic() : "general")
                .days(Objects.nonNull(request.getDays()) ? request.getDays() : 300)
                .maxResults(request.getMaxResults() != 0 ? request.getMaxResults() : 10)
                .includeImages(request.isIncludeImages())
                .includeImageDescriptions(request.isIncludeImageDescriptions())
                .includeAnswer(request.isIncludeAnswer())
                .includeRawContent(request.isIncludeRawContent())
                .includeDomains(Objects.nonNull(request.getIncludeDomains()) ? request.getIncludeDomains() : Collections.emptyList())
                .excludeDomains(Objects.nonNull(request.getExcludeDomains()) ? request.getExcludeDomains() : Collections.emptyList())
                .build();

        log.debug("发送请求到 Tavily API: query={}, searchDepth={}, topic={}, days={}, maxResults={}",
                requestWithApiKey.getQuery(),
                requestWithApiKey.getSearchDepth(),
                requestWithApiKey.getTopic(),
                requestWithApiKey.getDays(),
                requestWithApiKey.getMaxResults());

        try {
            TavilyResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/search").build())
                    .body(requestWithApiKey)
                    .retrieve()
                    .body(TavilyResponse.class);

            log.info("收到来自 Tavily API 的响应，查询为: {}", requestWithApiKey.getQuery());
            return response;
        } catch (RestClientResponseException e) {
            log.error("API 错误: 状态码 {}, 响应体: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("API 错误: " + e.getStatusText(), e);
        } catch (RestClientException e) {
            log.error("RestClient 错误: {}", e.getMessage());
            throw new RuntimeException("RestClient 错误: " + e.getMessage(), e);
        }
    }

    /**
     * Tavily API 的请求对象。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonClassDescription("Tavily API 的请求对象")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TavilyRequest {

        @JsonProperty("query")
        @JsonPropertyDescription("主要的搜索查询。")
        private String query;

        @JsonProperty("api_key")
        @JsonPropertyDescription("用于 Tavily 身份验证的 API 密钥。")
        private String apiKey;

        @JsonProperty("search_depth")
        @JsonPropertyDescription("搜索的深度。接受的值：'basic', 'advanced'。默认为 'basic'。")
        private String searchDepth;

        @JsonProperty("topic")
        @JsonPropertyDescription("搜索的类别。接受的值：'general', 'news'。默认为 'general'。")
        private String topic;

        @JsonProperty("days")
        @JsonPropertyDescription("从当前日期算起，要包含在搜索结果中的天数。默认为 3。仅适用于 'news' 主题。")
        private Integer days;

        @JsonProperty("time_range")
        @JsonPropertyDescription("搜索结果的时间范围。接受的值：'day', 'week', 'month', 'year' 或 'd', 'w', 'm', 'y'。默认为无。")
        private String timeRange;

        @JsonProperty("max_results")
        @JsonPropertyDescription("要返回的最大搜索结果数。默认为 5。")
        private int maxResults;

        @JsonProperty("include_images")
        @JsonPropertyDescription("是否在响应中包含与查询相关的图片列表。默认为 false。")
        private boolean includeImages;

        @JsonProperty("include_image_descriptions")
        @JsonPropertyDescription("当 'include_images' 为 true 时，为每张图片添加描述性文本。默认为 false。")
        private boolean includeImageDescriptions;

        @JsonProperty("include_answer")
        @JsonPropertyDescription("是否包含根据搜索结果生成的查询简短答案。默认为 false。")
        private boolean includeAnswer;

        @JsonProperty("include_raw_content")
        @JsonPropertyDescription("是否包含每个搜索结果的已清理和解析的 HTML 内容。默认为 false。")
        private boolean includeRawContent;

        @JsonProperty("include_domains")
        @JsonPropertyDescription("要特别包含在搜索结果中的域名列表。默认为空列表。")
        private List<String> includeDomains;

        @JsonProperty("exclude_domains")
        @JsonPropertyDescription("要特别从搜索结果中排除的域名列表。默认为空列表。")
        private List<String> excludeDomains;
    }

    /**
     * Tavily API 的响应对象。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonClassDescription("Tavily API 的响应对象")
    public static class TavilyResponse {
        @JsonProperty("query")
        private String query;

        @JsonProperty("follow_up_questions")
        private List<String> followUpQuestions;

        @JsonProperty("answer")
        private String answer;

        @JsonDeserialize(using = ImageDeserializer.class)
        @JsonProperty("images")
        private List<Image> images;

        @JsonProperty("results")
        private List<Result> results;

        @JsonProperty("response_time")
        private float responseTime;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Image {
            @JsonProperty("url")
            private String url;

            @JsonProperty("description")
            private String description;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Result {
            @JsonProperty("title")
            private String title;

            @JsonProperty("url")
            private String url;

            @JsonProperty("content")
            private String content;

            @JsonProperty("raw_content")
            private String rawContent;

            @JsonProperty("score")
            private float score;

            @JsonProperty("published_date")
            private String publishedDate;
        }
    }

    public static class ImageDeserializer extends JsonDeserializer<List<TavilyResponse.Image>> {
        @Override
        public List<TavilyApiClient.TavilyResponse.Image> deserialize(JsonParser jsonParser, DeserializationContext context)
                throws IOException {

            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            List<TavilyResponse.Image> images = new ArrayList<>();

            if (node.isArray()) {
                for (JsonNode element : node) {
                    // 如果元素是字符串，则将其视作 URL
                    if (element.isTextual()) {
                        images.add(new TavilyApiClient.TavilyResponse.Image(element.asText(), null));
                    }
                    // 如果元素是对象，则将其映射为 Image 对象
                    else if (element.isObject()) {
                        String url = element.has("url") ? element.get("url").asText() : null;
                        String description = element.has("description") ? element.get("description").asText() : null;
                        images.add(new TavilyApiClient.TavilyResponse.Image(url, description));
                    }
                }
            }

            return images;
        }
    }
}
