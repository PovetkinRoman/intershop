//package ru.rpovetkin.intershop.web;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/")
//public class HelloController {
//
//    @GetMapping
//    public String redirectToMain() {
//        return "redirect:/main/items";
//    }
//
//    @PostMapping("/test-post")
//    public Mono<String> test() {
//        return Mono.just("POST works!");
//    }
//}
