package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.RegisterRequest;
import ru.rpovetkin.intershop.model.User;
import ru.rpovetkin.intershop.repository.UserRepository;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegisterController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Rendering registerPage() {
        log.debug("Showing register page");
        return Rendering.view("register").build();
    }

    @PostMapping
    public Mono<Rendering> register(@RequestBody RegisterRequest request, Model model) {
        return userRepository.findByUsername(request.getUsername())
                .flatMap(existingUser -> {
                    model.addAttribute("error", "Пользователь с именем '" + request.getUsername() + "' уже существует");
                    return Mono.just(Rendering.view("register").modelAttribute("error", 
                            "Пользователь с именем '" + request.getUsername() + "' уже существует").build());
                })
                .switchIfEmpty(
                    userRepository.save(new User(request.getUsername(), passwordEncoder.encode(request.getPassword()), request.getRole()))
                        .then(Mono.just(Rendering.redirectTo("/login").build()))
                );
    }
}
