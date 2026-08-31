package vn.edu.crs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponseDTO {

    private Long id;
    private Long studentId;
    private Long courseId;
    private String trangThai;
    private LocalDateTime ngayDangKy;
}
