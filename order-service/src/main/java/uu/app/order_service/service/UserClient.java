package uu.app.order_service.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uu.app.order_service.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserClient {
    private final RestTemplate restTemplate;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserDTO getUser(Long userId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Make the request with headers, expecting a response of type String
        ResponseEntity<UserDTO> response = restTemplate.exchange(
                "http://localhost:8081/users/" + userId,
                HttpMethod.GET,
                entity,
                UserDTO.class
        );
        return response.getBody();
    }
}