package vn.edu.crs.controller;

import vn.edu.crs.dto.RegistrationRequestDTO;
import vn.edu.crs.dto.RegistrationResponseDTO;
import vn.edu.crs.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> register(
            @Valid @RequestBody RegistrationRequestDTO request) {
        log.info("POST /registrations - student: {}, course: {}", request.getStudentId(), request.getCourseId());
        RegistrationResponseDTO response = registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        log.info("DELETE /registrations/{}", id);
        registrationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RegistrationResponseDTO>> getByStudentId(@PathVariable Long studentId) {
        log.info("GET /registrations/student/{}", studentId);
        List<RegistrationResponseDTO> registrations = registrationService.getByStudentId(studentId);
        return ResponseEntity.ok(registrations);
    }
}
