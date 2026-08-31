package vn.edu.crs.service;

import vn.edu.crs.dto.LoginRequestDTO;
import vn.edu.crs.dto.LoginResponseDTO;
import vn.edu.crs.entity.User;
import vn.edu.crs.repository.UserRepository;
import vn.edu.crs.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Username hoac password khong dung"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Username hoac password khong dung");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        log.info("User {} logged in successfully", user.getUsername());
        
        return LoginResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
