package com.example.ankush.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="bank_details")
public class Bank {

    @Id
    private Long id;

    private String bankName;
    private String branchName;
    private String accountNo;
    private String ifscCode;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

}
