package com.littlebug.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Field("question_seq")
    private Integer questionSeq;

    private String content;
    private  Integer answerId;
//    @Field("question_type")
//    private String questionType;

//    private Integer difficulty;

    private List<Answer> answers = new ArrayList<>();
}