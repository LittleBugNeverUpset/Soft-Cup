package com.littlebug.utils;


import com.littlebug.pojo.VoiceEmotionAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiService {

    //语音情感识别
    @Value("${third.party.api.videoToVoiceUrl}")
    private String videoToVoiceApiUrl;
    public VoiceEmotionAnalysisResponse callThirdPartyApiWithAudio(MultipartFile audioFile) throws IOException {
        // 创建RestTemplate实例（保持原有方式）
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("audio", new org.springframework.core.io.ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename();
            }
        });

        // 创建请求实体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求并直接映射到AnalysisResponse
        ResponseEntity<VoiceEmotionAnalysisResponse> response = restTemplate.exchange(
                videoToVoiceApiUrl,
                HttpMethod.POST,
                requestEntity,
                VoiceEmotionAnalysisResponse.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("API请求失败，状态码: " + response.getStatusCode());
        }
    }

    //上传岗位需求和求职者简历
    @Value("${third.party.api.uploadFileUrl}")
    private String uploadFileUrl;
    public String uploadPdfAndTxt(MultipartFile pdfFile, MultipartFile txtFile) throws IOException {
        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 添加PDF文件（使用resume_file作为字段名）
        body.add("resume_file", new org.springframework.core.io.ByteArrayResource(pdfFile.getBytes()) {
            @Override
            public String getFilename() {
                return pdfFile.getOriginalFilename();
            }
        });

        // 添加TXT文件（使用job_file作为字段名）
        body.add("job_file", new org.springframework.core.io.ByteArrayResource(txtFile.getBytes()) {
            @Override
            public String getFilename() {
                return txtFile.getOriginalFilename();
            }
        });

        // 创建请求实体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求并接收响应
        ResponseEntity<String> response = restTemplate.exchange(
                uploadFileUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("文件上传API请求失败，状态码: " + response.getStatusCode());
        }
    }

    //简历岗位匹配度分析
    @Value("${third.party.api.matchingAnalyzeUrl}")
    private String matrchingAnalizeUrl;
    public String callMatchingAnalysisApi() throws IOException {
        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头（如果需要）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 根据API需求调整

        // 创建空请求体（无参数）
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // 发送GET请求（假设是无参数的GET请求）
        ResponseEntity<String> response = restTemplate.exchange(
                matrchingAnalizeUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("匹配分析API请求失败，状态码: " + response.getStatusCode());
        }
    }

    //获取初始问题路径传参
    @Value("${third.party.api.getInitQuestionUrl}")
    private String getInitQuestionUrl;
    public String getInitQuestions(String Session_id) throws IOException {
        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建带查询参数的URL
        String url = getInitQuestionUrl + "/" + Session_id;

        // 创建请求实体
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // 发送POST请求
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("获取初始问题API请求失败，状态码: " + response.getStatusCode());
        }
    }

    //获取下一个问题
    @Value("${third.party.api.getNextQuestionUrl}")
    private String getNextQuestionUrl;
    public String getNextQuestion(String Sesstion_id, String message) throws IOException {
        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求URL（路径参数）
        String url = getNextQuestionUrl + "/" +  Sesstion_id;

        // 创建请求体（包含message）
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", message);

        // 创建请求实体（包含请求体和请求头）
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 发送POST请求（假设是POST方法）
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("获取下一个问题API请求失败，状态码: " + response.getStatusCode());
        }
    }
}