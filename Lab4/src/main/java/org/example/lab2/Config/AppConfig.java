package org.example.lab2.Config;

import org.example.lab2.Entity.VoteToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;

@Configuration
public class AppConfig {

    @Bean
    @Scope("prototype")
    public VoteToken voteToken() {
        return new VoteToken(UUID.randomUUID().toString(), System.currentTimeMillis());
    }

    @Bean
    public ObjectMapper jackson2ObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
