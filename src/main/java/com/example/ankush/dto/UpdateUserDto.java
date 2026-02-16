package com.example.ankush.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    private String studentName;
    private String studentClass;
    private String fatherName;
    private LocalDate dob;
    private String gender;
    private String nationality;
    private String phoneNo;
    private String address;
    private String aadharNo;
    @JsonIgnore
    private MultipartFile image;
    private String returnImagePath;

    private BankDto bankDetails;

}
