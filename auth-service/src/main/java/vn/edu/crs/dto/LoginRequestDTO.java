package vn.edu.crs.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "Username khong duoc de trong")
    private String username;

    @NotBlank(message = "Password khong duoc de trong")
    private String password;
}
