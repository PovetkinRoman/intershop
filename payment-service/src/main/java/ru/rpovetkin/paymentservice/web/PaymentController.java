package ru.rpovetkin.paymentservice.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rpovetkin.paymentservice.conf.PaymentProperties;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;
import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/payment")
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentProperties paymentProperties;
    private BigDecimal amountOnBalance;

    @PostConstruct
    public void init() {
        amountOnBalance = paymentProperties.getInitialBalance();
    }

    @GetMapping
    public Mono<ResponseEntity<Boolean>> checkBalance(@RequestParam BigDecimal amountForPay) {
        log.info("Check balance input, amountForPay: {} / amountOnBalance: {}", amountForPay, amountOnBalance);
        Boolean isPay = amountOnBalance.compareTo(amountForPay) >= 0;
        return Mono.just(ResponseEntity.ok(isPay));
    }

    @PostMapping
    public Mono<ResponseEntity<Boolean>> payCart(@RequestParam BigDecimal amountForPay) {
        log.info("Pay input, amountForPay: {} / amountOnBalance: {}", amountForPay, amountOnBalance);
        if (amountOnBalance.compareTo(amountForPay) >= 0) {
            amountOnBalance = amountOnBalance.subtract(amountForPay);
            log.info("Оплата прошла, новый баланс: {}", amountOnBalance);
            return Mono.just(ResponseEntity.ok(true));
        } else {
            log.info("Недостаточно средств для оплаты. Баланс: {}", amountOnBalance);
            return Mono.just(ResponseEntity.ok(false));
        }
    }
}
