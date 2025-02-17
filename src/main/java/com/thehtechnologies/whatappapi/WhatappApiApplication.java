package com.thehtechnologies.whatappapi;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@SecurityScheme(
        name = "keycloak",
        type = SecuritySchemeType.OAUTH2,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                password = @OAuthFlow(
                        authorizationUrl = "http://localhost:9090/realms/messaging-api/protocol/openid-connect/auth",
                        tokenUrl = "http://localhost:9090/realms/messaging-api/protocol/openid-connect/token"
                )
        )
)
public class WhatappApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatappApiApplication.class, args);
    }
    static {
        // Charger le fichier .env depuis le dossier local
        Dotenv dotenv = Dotenv.load();

        // Charger les variables dans le système d'environnement
        System.setProperty("DB_username", dotenv.get("DB_username"));
        System.setProperty("DB_password", dotenv.get("DB_password"));
        System.setProperty("DB_name", dotenv.get("DB_name"));
    }
}
