package com.credit.engine.api.loan.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
public class CreditScore {


    private String id;

    private String ssnNumber;

    private Integer score;

}
