package vn.edu.crs.service;

import vn.edu.crs.dto.CourseDTO;
import vn.edu.crs.entity.Course;
import vn.edu.crs.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<CourseDTO> search(String keyword, Pageable pageable) {
        log.info("Searching courses with keyword: {}", keyword);
        Page<Course> courses;
        if (keyword == null || keyword.isBlank()) {
            courses = courseRepository.findAll(pageable);
        } else {
            courses = courseRepository.findByTenMonHocContainingIgnoreCase(keyword, pageable);
        }
        return courses.map(this::convertToDTO);
    }

    public CourseDTO getById(Long id) {
        log.info("Getting course by id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc voi ID: " + id));
        return convertToDTO(course);
    }

    public CourseDTO create(CourseDTO dto) {
        log.info("Creating course: {}", dto.getTenMonHoc());
        
        if (courseRepository.existsByTenMonHocIgnoreCase(dto.getTenMonHoc())) {
            throw new IllegalArgumentException("Mon hoc '" + dto.getTenMonHoc() + "' da ton tai");
        }

        Course course = Course.builder()
                .tenMonHoc(dto.getTenMonHoc())
                .soTinChi(dto.getSoTinChi())
                .soChoToiDa(dto.getSoChoToiDa())
                .soChoConLai(dto.getSoChoToiDa())
                .build();

        Course saved = courseRepository.save(course);
        log.info("Course created with id: {}", saved.getId());
        return convertToDTO(saved);
    }

    public CourseDTO update(Long id, CourseDTO dto) {
        log.info("Updating course id: {}", id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc voi ID: " + id));

        if (!course.getTenMonHoc().equalsIgnoreCase(dto.getTenMonHoc()) &&
            courseRepository.existsByTenMonHocIgnoreCase(dto.getTenMonHoc())) {
            throw new IllegalArgumentException("Mon hoc '" + dto.getTenMonHoc() + "' da ton tai");
        }

        course.setTenMonHoc(dto.getTenMonHoc());
        course.setSoTinChi(dto.getSoTinChi());
        course.setSoChoToiDa(dto.getSoChoToiDa());

        Course updated = courseRepository.save(course);
        return convertToDTO(updated);
    }

    public void delete(Long id) {
        log.info("Deleting course id: {}", id);
        if (!courseRepository.existsById(id)) {
            throw new NoSuchElementException("Khong tim thay mon hoc voi ID: " + id);
        }
        courseRepository.deleteById(id);
    }

    @Transactional
    public void reserveSeat(Long courseId) {
        log.info("Reserving seat for course id: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc voi ID: " + courseId));

        if (course.getSoChoConLai() <= 0) {
            throw new IllegalStateException("Het cho trong mon hoc '" + course.getTenMonHoc() + "'");
        }

        course.setSoChoConLai(course.getSoChoConLai() - 1);
        courseRepository.save(course);
        log.info("Seat reserved. Remaining seats: {}", course.getSoChoConLai());
    }

    @Transactional
    public void releaseSeat(Long courseId) {
        log.info("Releasing seat for course id: {}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay mon hoc voi ID: " + courseId));

        if (course.getSoChoConLai() >= course.getSoChoToiDa()) {
            log.warn("Cannot release seat, already at max capacity");
            return;
        }

        course.setSoChoConLai(course.getSoChoConLai() + 1);
        courseRepository.save(course);
        log.info("Seat released. Remaining seats: {}", course.getSoChoConLai());
    }

    private CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .tenMonHoc(course.getTenMonHoc())
                .soTinChi(course.getSoTinChi())
                .soChoToiDa(course.getSoChoToiDa())
                .soChoConLai(course.getSoChoConLai())
                .build();
    }
}
