package com.littlebug.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;
import com.littlebug.pojo.VoiceEmotionAnalysisResponse;
@Document(collection = "answers")
@Data
public class Answer {
    @Id
    private String id;
    // 必需字段
    private String text;  // 回答文本内容
    // 分析结果相关字段
    private VoiceEmotionAnalysisResponse voiceEmotionAnalysisResponse;
    private Analysis analysis;  // 大小模型分析结果(干对象)

    // 建议添加的常用字段
    private Date createTime;    // 创建时间
    private String questionId; // 关联的问题ID


    // 嵌套的分析结果类
    public static class Analysis {

    }

    public Answer(String id, String text, VoiceEmotionAnalysisResponse voiceEmotionAnalysisResponse, Date createTime, String questionId) {
        this.id = id;
        this.text = text;
        this.voiceEmotionAnalysisResponse = voiceEmotionAnalysisResponse;
        this.createTime = createTime;
        this.questionId = questionId;
    }
}