package com.littlebug.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EmotionAnalysisResponse {
    private boolean success;
    private Data data;

    public static class Data {
        private FinalResult finalResult;
        private InterviewAssessment interviewAssessment;


        public static class FinalResult {
            @JsonProperty("recommended_emotion")
            private String recommendedEmotion;
            private double confidence;
        }

        public static class InterviewAssessment {
            @JsonProperty("emotion_type")
            private String emotionType;
            @JsonProperty("emotion_description")
            private String emotionDescription;
            private double score;
            private List<String> recommendations;
        }
    }
}