package com.biteup.biteup_payment.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvLoader {

    @jakarta.annotation.PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("STRIPE_SECRET_KEY", dotenv.get("STRIPE_SECRET_KEY"));
        System.setProperty("STRIPE_PUBLIC_KEY", dotenv.get("STRIPE_PUBLIC_KEY"));
    }
}

