package vn.edu.crs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String tenMonHoc;

    @Column(nullable = false)
    private Integer soTinChi;

    @Column(nullable = false)
    private Integer soChoToiDa;

    @Column(nullable = false)
    private Integer soChoConLai;
}
