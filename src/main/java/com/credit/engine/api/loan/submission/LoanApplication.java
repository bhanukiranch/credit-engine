package com.credit.engine.api.loan.submission;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="LOAN_APPLICATION")
public class LoanApplication {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    @Column(name = "ID", length = 50)
    @JsonIgnore
    private String id;

    @Column(name = "REFERENCE_NUMBER", length = 50)
    private String referenceNumber;

    @Column(name = "SSN_NUMBER", length = 9)
    @JsonIgnore
    private String ssnNumber;

    @Column(name = "SANCTIONED_AMOUNT")
    private Long sanctionedAmount;

    @Column(name = "CREDIT_SCORE")
    @JsonIgnore
    private int creditScore;

    @Column(name = "CREATED_DT", nullable = false)
    @JsonIgnore
    private LocalDateTime created;

    @Column(name = "UPDATED_DT", nullable = false)
    @JsonIgnore
    private LocalDateTime updated;

    @Column(name = "EXPIRY_DT", nullable = false)
    @JsonIgnore
    private LocalDateTime expiry;

    @PrePersist
    protected void onCreate() {
        updated = created = LocalDateTime.now();
        Long identifier = ThreadLocalRandom.current().nextLong(100000000, 999999999);
        StringBuilder refNumber =
                new StringBuilder(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        refNumber.append(identifier);
        referenceNumber = refNumber.toString();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = LocalDateTime.now();
    }

}
