package com.example.ankush.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="students")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String studentId;

    private String studentName;
    private String studentClass;
    private String fatherName;
    private LocalDate dob;
    private String gender;
    private String address;
    private String nationality;
    private String phoneNo;
    private String aadharNo;
    private String imagePath;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Bank bankDetails;
}
