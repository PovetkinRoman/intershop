package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;

@Controller
@Slf4j
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public Rendering loginPage() {
        log.debug("Showing login page (root path)");
        return Rendering.view("login").build();
    }
}
