package vn.edu.crs.controller;

import vn.edu.crs.dto.LoginRequestDTO;
import vn.edu.crs.dto.LoginResponseDTO;
import vn.edu.crs.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("POST /auth/login - {}", request.getUsername());
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
