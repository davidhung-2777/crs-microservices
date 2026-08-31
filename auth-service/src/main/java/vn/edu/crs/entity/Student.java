package vn.edu.crs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String hoTen;

    @Column(nullable = false, unique = true, length = 20)
    private String mssv;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
