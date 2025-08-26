package ru.rpovetkin.intershop.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.rpovetkin.intershop.model.User;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    
    /**
     * Найти пользователя по имени пользователя
     */
    Mono<User> findByUsername(String username);

}
