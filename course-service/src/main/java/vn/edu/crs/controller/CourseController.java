package vn.edu.crs.controller;

import vn.edu.crs.dto.CourseDTO;
import vn.edu.crs.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Page<CourseDTO>> searchCourses(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        log.info("GET /courses - keyword: {}, page: {}", keyword, pageable.getPageNumber());
        Page<CourseDTO> courses = courseService.search(keyword, pageable);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getById(@PathVariable Long id) {
        log.info("GET /courses/{}", id);
        CourseDTO course = courseService.getById(id);
        return ResponseEntity.ok(course);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO dto) {
        log.info("POST /courses - {}", dto.getTenMonHoc());
        CourseDTO created = courseService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO dto) {
        log.info("PUT /courses/{}", id);
        CourseDTO updated = courseService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        log.info("DELETE /courses/{}", id);
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
