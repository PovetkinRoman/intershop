package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ImageController {

    @GetMapping("/image/{filename:.+}")
    public Mono<ResponseEntity<Resource>> serveImage(@PathVariable String filename) {
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new ClassPathResource("static/" + filename)));
    }
}
