//package com.littlebug.pojo;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//import java.util.Date;
//import java.util.Map;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class TimelineEvent {
//    @Field("event_type")
//    private String eventType;
//
//    private Date timestamp;
//
//    // 使用具体类型替代Object
//    private Map<String, Object> metadata;
//
//    private String content;
//}