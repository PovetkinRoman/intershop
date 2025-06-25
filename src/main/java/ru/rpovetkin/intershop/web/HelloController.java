package ru.rpovetkin.intershop.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HelloController {

    @GetMapping
    public String redirectToMain() {
        return "redirect:/main/items";
    }
}
