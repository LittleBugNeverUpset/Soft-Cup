package com.littlebug.utils;


import com.littlebug.pojo.AnalysisResponse;
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

    public AnalysisResponse callThirdPartyApiWithAudio(MultipartFile audioFile) throws IOException {
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
        ResponseEntity<AnalysisResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                AnalysisResponse.class
        );

        // 处理响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new IOException("API请求失败，状态码: " + response.getStatusCode());
        }
    }
}