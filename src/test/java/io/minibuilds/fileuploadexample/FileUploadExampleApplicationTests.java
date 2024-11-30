package io.minibuilds.fileuploadexample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileUploadExampleApplicationTests {

    private static final ParameterizedTypeReference<Map<String, String>> MAP_TYPE =
            new ParameterizedTypeReference<>() {
            };

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void when_a_file_is_uploaded_the_response_should_contain_the_contents_of_the_file() {
        // Given a multipart request containing a small file
        ByteArrayResource file = createFile("hello world");
        RequestEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(file);

        // When the request is made
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                requestEntity, MAP_TYPE
        );

        // Then the response status code is ok
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // And the response contains the file contents
        assertThat(response.getBody()).containsEntry("contents", "hello world");
    }

    @Test
    void when_a_file_is_uploaded_that_is_too_large_the_response_should_be_payload_too_large() {
        // Given a multipart request containing a large file (>1MB)
        ByteArrayResource file = createFile("a".repeat(1_100_000));
        RequestEntity<MultiValueMap<String, Object>> requestEntity = createMultipartRequest(file);

        // When the request is made
        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                requestEntity, MAP_TYPE
        );

        // Then the response status code is payload too large
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    private ByteArrayResource createFile(String contents) {
        return new ByteArrayResource(contents.getBytes()) {
            @Override
            public String getFilename() {
                return "file.txt";
            }
        };
    }

    private RequestEntity<MultiValueMap<String, Object>> createMultipartRequest(
            ByteArrayResource file
    ) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        return RequestEntity.post("/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body);
    }
}
