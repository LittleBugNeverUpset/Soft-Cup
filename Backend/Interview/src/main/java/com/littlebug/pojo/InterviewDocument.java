package com.littlebug.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "interviews")
public class InterviewDocument {
    @Id
    private String id;

    @Field("interview_id")
    private Long interviewId; // 对应MySQL的interview.id

    @Field("student_id")
    private int studentId;

    private String positionType;
    private String status;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    private List<TimelineEvent> timeline = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();

    @Field("current_question")
    private Integer currentQuestion;
}