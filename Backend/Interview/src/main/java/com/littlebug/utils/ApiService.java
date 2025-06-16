package com.littlebug.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ApiService {

    @Value("${third.party.api.url}")
    private String apiUrl;

    public String callThirdPartyApiWithAudio(MultipartFile audioFile) throws IOException {
        // 创建RestTemplate实例
        RestTemplate restTemplate = new RestTemplate();

        // 设置请求头，指定内容类型为multipart/form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 添加认证信息（如果需要）
        // headers.set("Authorization", "Bearer YOUR_ACCESS_TOKEN");

        // 创建表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // 添加语音文件
        body.add("audio", new org.springframework.core.io.ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename();
            }
        });

        // 添加其他表单参数（如果有）
        // body.add("param1", "value1");

        // 创建HTTP请求实体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送POST请求并获取响应
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 检查响应状态码
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("API request failed with status code: " + response.getStatusCode());
        }
    }
}    