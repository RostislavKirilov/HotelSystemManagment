package com.tinqinacademy.hotel.api.operations;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class VisitorDetails {

    private LocalDate startDate;
    private LocalDate endDate;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String idCardNo;
    private LocalDate validity;
    private String issueAuthority;
    private LocalDate issueDate;
    private String roomNo;
}
