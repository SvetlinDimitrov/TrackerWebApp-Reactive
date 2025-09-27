package org.nutriGuideBuddy.seed.production;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutriGuideBuddy.seed.production.service.GodUserSeederService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
public class GodUserSeeder implements CommandLineRunner {

    private final GodUserSeederService godUserSeederService;

    @Override
    public void run(String... args) {
        log.info("Seeding GOD user (prod)...");
        godUserSeederService
            .seed()
            .doOnTerminate(() -> log.info("GOD user seeding completed."))
            .onErrorResume(e -> {
                log.error("GOD user seeding failed: {}", e.getMessage(), e);
                return Mono.empty();
            })
            .subscribe();
    }
}
