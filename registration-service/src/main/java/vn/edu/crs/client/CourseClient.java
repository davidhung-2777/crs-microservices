package vn.edu.crs.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseClient {

    private final RestTemplate restTemplate;
    private static final String COURSE_SERVICE_URL = "http://localhost:8082";

    public void reserveSeat(Long courseId) {
        String url = COURSE_SERVICE_URL + "/internal/courses/" + courseId + "/reserve-seat";
        try {
            log.info("Calling reserveSeat for courseId: {}", courseId);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PATCH, null, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("reserveSeat successful for courseId: {}", courseId);
            } else {
                throw new IllegalStateException("Failed to reserve seat");
            }
        } catch (Exception ex) {
            log.error("Error calling reserveSeat: {}", ex.getMessage());
            throw new IllegalStateException("Khong the dat cho mon hoc, vui long thu lai");
        }
    }

    public void releaseSeat(Long courseId) {
        String url = COURSE_SERVICE_URL + "/internal/courses/" + courseId + "/release-seat";
        try {
            log.info("Calling releaseSeat for courseId: {}", courseId);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.PATCH, null, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("releaseSeat successful for courseId: {}", courseId);
            } else {
                throw new IllegalStateException("Failed to release seat");
            }
        } catch (Exception ex) {
            log.error("Error calling releaseSeat: {}", ex.getMessage());
            throw new IllegalStateException("Khong the huy cho mon hoc, vui long thu lai");
        }
    }
}
