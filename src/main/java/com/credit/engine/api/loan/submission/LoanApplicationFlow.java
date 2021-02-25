package com.credit.engine.api.loan.submission;

import com.credit.engine.api.exception.ApiRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.jpa.dsl.Jpa;
import org.springframework.integration.jpa.support.PersistMode;
import org.springframework.messaging.support.HeaderMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@Slf4j
public class LoanApplicationFlow {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    RestTemplate restTemplate;

    @Bean
    public IntegrationFlow loanSubmissionFlow(@Value("${service-url.credit-score}") String url, EntityManager entityManager) {
        return IntegrationFlows.from(Http.inboundGateway("/v1/loan/apply")
                .requestPayloadType(LoanRequest.class)
                .requestMapping(m -> m.methods(HttpMethod.POST))
                .errorChannel("globalErrorChannel.input"))
                .wireTap("loggingFlow.input")
                .log(LoggingHandler.Level.INFO, this.getClass().getName(), m -> "Start - Submitting for Credit Loan")
                .enrichHeaders(h -> h.headerExpression("loanRequest", "payload"))
                .enrichHeaders(h -> h.headerExpression("ssnNumber", "payload.ssnNumber"))
                .<LoanRequest>handle((p, h) ->  loanApplicationRepository.findBySsnNumber(p.getSsnNumber()))
                .<Optional<LoanApplication>>handle((p, h) -> {
                    LoanApplication loan = LoanApplication.builder().build();
                    if(p.isPresent() && p.get().getExpiry().isAfter(LocalDateTime.now())) {
                        throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "id.active", "id is present not allowed to apply");
                    }
                    if(p.isPresent()) {
                        loan = p.get();
                    }
                    return loan;
                })
                .enrichHeaders(h -> h.headerExpression("loanApplication", "payload"))
                .handle(Http.outboundGateway(url, restTemplate)
                        .uriVariable("id", m -> m.getHeaders().get("ssnNumber")).httpMethod(HttpMethod.GET)
                        .expectedResponseType(CreditScore.class))
                .filter(CreditScore.class, p -> p.getScore() > 700,
                e -> e.discardFlow(f -> f.handle(p -> {
                    throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "id.notEligible", "Credit score not matched with the criteria");
                })))
                .<CreditScore>handle((p,h) -> {
                    LoanApplication loan = (LoanApplication) h.get("loanApplication");
                    LoanRequest request = (LoanRequest) h.get("loanRequest");
                    loan = loan.toBuilder()
                            .sanctionedAmount(request.getAnnualIncome() / 2)
                            .ssnNumber(request.getSsnNumber())
                            .updated(LocalDateTime.now())
                            .expiry(LocalDateTime.now().plusSeconds(10))
                            .creditScore(p.getScore())
                            .build();
                    return loan;
                })
                .handle(Jpa.updatingGateway(entityManager)
                        .entityClass(LoanApplication.class)
                        .persistMode(PersistMode.MERGE), e -> e.transactional())
                .log(LoggingHandler.Level.INFO, this.getClass().getName(), m -> "End - Submitting for Credit Loan")
                .<LoanApplication>handle((p,h) -> p)
                .get();


    }

}
