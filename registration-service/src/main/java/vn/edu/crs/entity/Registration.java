package vn.edu.crs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrangThai trangThai;

    @Column(nullable = false)
    private LocalDateTime ngayDangKy;

    public enum TrangThai {
        DA_DANG_KY, DA_HUY
    }
}
