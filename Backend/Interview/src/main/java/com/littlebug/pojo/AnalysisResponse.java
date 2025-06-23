package com.littlebug.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AnalysisResponse {
    private boolean success;
    private String message;

    @JsonProperty("data")
    private ResponseData data;
}

@Data
class ResponseData {
    @JsonProperty("basic_info")
    private BasicInfo basicInfo;

    @JsonProperty("final_result")
    private FinalResult finalResult;

    @JsonProperty("interview_assessment")
    private InterviewAssessment interviewAssessment;

    @JsonProperty("models_results")
    private List<ModelResult> modelsResults;
}

@Data
class BasicInfo {
    private double duration;

    @JsonProperty("models_count")
    private int modelsCount;

    @JsonProperty("sample_rate")
    private int sampleRate;
}

@Data
class FinalResult {
    @JsonProperty("average_confidence")
    private double averageConfidence;

    @JsonProperty("best_model")
    private String bestModel;

    private double confidence;

    @JsonProperty("models_consensus")
    private boolean modelsConsensus;

    @JsonProperty("recommended_emotion")
    private String recommendedEmotion;
}

@Data
class InterviewAssessment {
    @JsonProperty("confidence_level")
    private String confidenceLevel;

    @JsonProperty("detailed_analysis")
    private DetailedAnalysis detailedAnalysis;

    @JsonProperty("emotion_description")
    private String emotionDescription;

    @JsonProperty("emotion_type")
    private String emotionType;

    private List<String> recommendations;
    private double score;
}

@Data
class DetailedAnalysis {
    @JsonProperty("base_score")
    private int baseScore;

    @JsonProperty("confidence_adjustment")
    private double confidenceAdjustment;

    @JsonProperty("confidence_score")
    private double confidenceScore;

    @JsonProperty("detected_emotion")
    private String detectedEmotion;
}

@Data
class ModelResult {
    private double confidence;

    @JsonProperty("emotions_scores")
    private Map<String, Double> emotionsScores;

    @JsonProperty("model_name")
    private String modelName;

    @JsonProperty("predicted_emotion")
    private String predictedEmotion;
}