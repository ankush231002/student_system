package com.example.ankush.dto;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



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

    @JsonIgnore
    private List<MultipartFile> documents;
    private List<String> returnDocumentsPaths;

    private List<BankDto> bankDetails;

    @JsonIgnore
    private List<String> deleteDocumentPaths;
}
