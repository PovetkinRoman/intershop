package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        log.info("Публикуем ответ...");
        return "Добро пожаловать на борт!";
    }
}
