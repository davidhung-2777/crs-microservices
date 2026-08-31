package vn.edu.crs.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.edu.crs.entity.Student;
import vn.edu.crs.entity.User;
import vn.edu.crs.repository.UserRepository;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role(User.Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("student1").isEmpty()) {
                User student1 = User.builder()
                        .username("student1")
                        .password(passwordEncoder.encode("student123"))
                        .role(User.Role.STUDENT)
                        .build();
                User savedStudent1 = userRepository.save(student1);

                Student studentProfile = Student.builder()
                        .hoTen("Nguyen Van A")
                        .mssv("SV001")
                        .user(savedStudent1)
                        .build();
                // Note: Need to save student separately or use OneToMany
            }
        };
    }
}
