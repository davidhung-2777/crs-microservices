package vn.edu.crs.service;

import vn.edu.crs.client.CourseClient;
import vn.edu.crs.dto.RegistrationRequestDTO;
import vn.edu.crs.dto.RegistrationResponseDTO;
import vn.edu.crs.entity.Registration;
import vn.edu.crs.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final CourseClient courseClient;

    @Transactional
    public RegistrationResponseDTO register(RegistrationRequestDTO request) {
        log.info("Registering student {} for course {}", request.getStudentId(), request.getCourseId());

        // Check if already registered
        if (registrationRepository.existsByStudentIdAndCourseIdAndTrangThai(
                request.getStudentId(), request.getCourseId(), Registration.TrangThai.DA_DANG_KY)) {
            throw new IllegalArgumentException("Sinh vien da dang ky mon hoc nay roi");
        }

        // Call course service to reserve seat
        courseClient.reserveSeat(request.getCourseId());

        // Save registration
        Registration registration = Registration.builder()
                .studentId(request.getStudentId())
                .courseId(request.getCourseId())
                .trangThai(Registration.TrangThai.DA_DANG_KY)
                .ngayDangKy(LocalDateTime.now())
                .build();

        Registration saved = registrationRepository.save(registration);
        log.info("Registration created with id: {}", saved.getId());

        return convertToDTO(saved);
    }

    public void cancel(Long registrationId) {
        log.info("Canceling registration {}", registrationId);

        Registration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new NoSuchElementException("Khong tim thay dang ky voi ID: " + registrationId));

        if (registration.getTrangThai() == Registration.TrangThai.DA_HUY) {
            throw new IllegalArgumentException("Dang ky nay da bi huy roi");
        }

        // Call course service to release seat
        courseClient.releaseSeat(registration.getCourseId());

        // Update status
        registration.setTrangThai(Registration.TrangThai.DA_HUY);
        registrationRepository.save(registration);
        log.info("Registration {} cancelled", registrationId);
    }

    public List<RegistrationResponseDTO> getByStudentId(Long studentId) {
        log.info("Getting registrations for student {}", studentId);
        List<Registration> registrations = registrationRepository.findByStudentId(studentId);
        return registrations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private RegistrationResponseDTO convertToDTO(Registration registration) {
        return RegistrationResponseDTO.builder()
                .id(registration.getId())
                .studentId(registration.getStudentId())
                .courseId(registration.getCourseId())
                .trangThai(registration.getTrangThai().name())
                .ngayDangKy(registration.getNgayDangKy())
                .build();
    }
}
