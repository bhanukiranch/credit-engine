package com.credit.engine.api.loan.submission;

import lombok.*;

import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
@ToString(includeFieldNames = true)
public class LoanRequest {

    @Size(max = 9)
    private String ssnNumber;
    @Size(max = 8)
    private Long loanAmount;
    @Size(max = 8)
    private Long annualIncome;

}
