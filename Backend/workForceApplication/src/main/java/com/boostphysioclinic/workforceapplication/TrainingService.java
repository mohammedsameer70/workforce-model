package com.boostphysioclinic.workforceapplication;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.List;

@Service
public class TrainingService {

    @Value("${python.api.url}")
    private String pythonUrl;

    @Value("${python.download.url}")
    private String downloadUrl;

    public String train(
            MultipartFile file,
            List<String> algorithms
    ) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.MULTIPART_FORM_DATA
        );

        ByteArrayResource resource =
                new ByteArrayResource(file.getBytes()) {

                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }

                };

        MultiValueMap<String, Object> body =
                new LinkedMultiValueMap<>();

        body.add("file", resource);

        body.add("algorithms", String.join(",", algorithms));

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        pythonUrl,
                        request,
                        String.class
                );

        return response.getBody();

    }
    public byte[] downloadCleanedDataset() {

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(
                downloadUrl,
                byte[].class
        );
    }

}