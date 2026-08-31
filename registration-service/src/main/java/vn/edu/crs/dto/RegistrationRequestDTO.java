package vn.edu.crs.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequestDTO {

    @NotNull(message = "studentId khong duoc de null")
    private Long studentId;

    @NotNull(message = "courseId khong duoc de null")
    private Long courseId;
}
