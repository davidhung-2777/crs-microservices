package vn.edu.crs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Long id;

    @NotBlank(message = "Tên môn học không được để trống")
    private String tenMonHoc;

    @NotNull(message = "Số tín chỉ không được để null")
    @Min(value = 1, message = "Số tín chỉ phải >= 1")
    private Integer soTinChi;

    @NotNull(message = "Số chỗ tối đa không được để null")
    @Min(value = 1, message = "Số chỗ tối đa phải >= 1")
    private Integer soChoToiDa;

    // soChoConLai không có setter, chỉ read-only
    private Integer soChoConLai;
}
