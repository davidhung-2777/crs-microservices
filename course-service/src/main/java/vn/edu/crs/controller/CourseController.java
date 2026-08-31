package vn.edu.crs.controller;

import vn.edu.crs.dto.CourseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/courses")
public class CourseController {

    /**
     * BUỔI 1: Mock endpoint - chưa nối DB
     * Trả về danh sách 2 môn học cứng để test Postman
     */
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        log.info("GET /courses - Mock endpoint");
        
        List<CourseDTO> mockCourses = Arrays.asList(
            CourseDTO.builder()
                .id(1L)
                .tenMonHoc("Lập Trình Java")
                .soTinChi(3)
                .soChoToiDa(30)
                .soChoConLai(15)
                .build(),
            CourseDTO.builder()
                .id(2L)
                .tenMonHoc("Database Design")
                .soTinChi(2)
                .soChoToiDa(25)
                .soChoConLai(10)
                .build()
        );
        
        return ResponseEntity.ok(mockCourses);
    }
}
