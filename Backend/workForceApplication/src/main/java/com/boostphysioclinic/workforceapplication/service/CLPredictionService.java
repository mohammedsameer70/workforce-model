package com.boostphysioclinic.workforceapplication.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class CLPredictionService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String predict(MultipartFile file) throws IOException {

        HttpEntity<MultiValueMap<String, Object>> request = createRequest(file);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:5233/predict",
                request,
                String.class
        );

        return response.getBody();
    }

    private HttpEntity<MultiValueMap<String, Object>> createRequest(MultipartFile file) throws IOException {

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(body, headers);
    }
}