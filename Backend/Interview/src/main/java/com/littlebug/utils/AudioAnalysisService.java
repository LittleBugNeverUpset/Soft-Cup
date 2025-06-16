package com.littlebug.utils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

@Component
public class AudioAnalysisService {

    private static final String PYTHON_API_URL = "http://localhost:5000/analyze";

    /**
     * 调用Python音频分析接口
     * @param audioFile MP3音频文件
     * @return 分析结果
     * @throws IOException 文件操作异常
     */
    public AudioAnalysisResult analyzeAudio(File audioFile) throws IOException {
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 准备文件资源
        FileSystemResource fileResource = new FileSystemResource(audioFile);

        // 构建请求体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", fileResource);

        // 构建请求实体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PythonApiResponse> response = restTemplate.exchange(
                PYTHON_API_URL,
                HttpMethod.POST,
                requestEntity,
                PythonApiResponse.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            PythonApiResponse apiResponse = response.getBody();
            if (apiResponse.isSuccess()) {
                return apiResponse.getData();
            }
        }

        throw new RuntimeException("音频分析失败: " + response.getStatusCode());
    }

    // 响应数据结构
    public static class PythonApiResponse {
        private boolean success;
        private AudioAnalysisResult data;

        // getters and setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public AudioAnalysisResult getData() {
            return data;
        }

        public void setData(AudioAnalysisResult data) {
            this.data = data;
        }
    }

    // 分析结果数据结构
    public static class AudioAnalysisResult {
        private FinalResult finalResult;
        private InterviewAssessment interviewAssessment;

        // getters and setters
        public FinalResult getFinalResult() {
            return finalResult;
        }

        public void setFinalResult(FinalResult finalResult) {
            this.finalResult = finalResult;
        }

        public InterviewAssessment getInterviewAssessment() {
            return interviewAssessment;
        }

        public void setInterviewAssessment(InterviewAssessment interviewAssessment) {
            this.interviewAssessment = interviewAssessment;
        }
    }

    public static class FinalResult {
        private String recommendedEmotion;
        private double confidence;

        // getters and setters
        public String getRecommendedEmotion() {
            return recommendedEmotion;
        }

        public void setRecommendedEmotion(String recommendedEmotion) {
            this.recommendedEmotion = recommendedEmotion;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
    }

    public static class InterviewAssessment {
        private String emotionType;
        private String emotionDescription;
        private double score;
        private String[] recommendations;

        // getters and setters
        public String getEmotionType() {
            return emotionType;
        }

        public void setEmotionType(String emotionType) {
            this.emotionType = emotionType;
        }

        public String getEmotionDescription() {
            return emotionDescription;
        }

        public void setEmotionDescription(String emotionDescription) {
            this.emotionDescription = emotionDescription;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String[] getRecommendations() {
            return recommendations;
        }

        public void setRecommendations(String[] recommendations) {
            this.recommendations = recommendations;
        }
    }
}