package com.example.ankush.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankDto {
    private String bankName;
    private String branchName;
    private String accountNo;
    private String ifscCode;

    
}
