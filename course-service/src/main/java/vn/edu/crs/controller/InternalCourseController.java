package vn.edu.crs.controller;

import vn.edu.crs.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/internal/courses")
@RequiredArgsConstructor
public class InternalCourseController {

    private final CourseService courseService;

    @PatchMapping("/{id}/reserve-seat")
    public ResponseEntity<Map<String, String>> reserveSeat(@PathVariable Long id) {
        log.info("PATCH /internal/courses/{}/reserve-seat", id);
        courseService.reserveSeat(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Dat cho thanh cong");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/release-seat")
    public ResponseEntity<Map<String, String>> releaseSeat(@PathVariable Long id) {
        log.info("PATCH /internal/courses/{}/release-seat", id);
        courseService.releaseSeat(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Huy cho thanh cong");
        return ResponseEntity.ok(response);
    }
}
