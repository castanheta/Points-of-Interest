package com.example.pointsofinterest.util;

import com.example.pointsofinterest.model.ApiToken;
import com.example.pointsofinterest.repository.ApiTokenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class TokenGenerator implements CommandLineRunner {

    private final ApiTokenRepository tokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public TokenGenerator(ApiTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void run(String... args) {
        String generateTokenFlag = null;
        Integer expirationDays = null;
        String tokenIdToRevoke = null;
        boolean listTokens = false;

        // Parse command line arguments
        for (String arg : args) {
            if (arg.startsWith("--generate-token=")) {
                generateTokenFlag = arg.substring("--generate-token=".length());
            } else if (arg.startsWith("--expiration-days=")) {
                try {
                    expirationDays = Integer.parseInt(arg.substring("--expiration-days=".length()));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid expiration days format. Must be a number.");
                    return;
                }
            } else if (arg.equals("--list-tokens")) {
                listTokens = true;
            } else if (arg.startsWith("--revoke-token=")) {
                tokenIdToRevoke = arg.substring("--revoke-token=".length());
            }
        }

        // Execute the appropriate command
        if (generateTokenFlag != null) {
            generateToken(generateTokenFlag, expirationDays != null ? expirationDays : 0);
        } else if (listTokens) {
            listTokens();
        } else if (tokenIdToRevoke != null) {
            revokeToken(tokenIdToRevoke);
        }
    }

    private void generateToken(String description, int days) {
        // Generate random token
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        ApiToken apiToken = new ApiToken();
        apiToken.setToken(token);
        apiToken.setDescription(description);
        if (days > 0) {
            apiToken.setExpiresAt(LocalDateTime.now().plusDays(days));
        }
        
        tokenRepository.save(apiToken);
        
        System.out.println("Token generated: " + token);
        if (days > 0) {
            System.out.println("Expires at: " + apiToken.getExpiresAt());
        } else {
            System.out.println("No expiration");
        }
    }
    
    private void listTokens() {
        System.out.println("Active API Tokens:");
        System.out.println("-----------------------------------");
        tokenRepository.findAll().forEach(token -> {
            System.out.println("ID: " + token.getId());
            System.out.println("Token: " + token.getToken());
            System.out.println("Description: " + token.getDescription());
            System.out.println("Active: " + token.isActive());
            if (token.getExpiresAt() != null) {
                System.out.println("Expires: " + token.getExpiresAt());
            } else {
                System.out.println("No expiration");
            }
            System.out.println("-----------------------------------");
        });
    }
    
    private void revokeToken(String idStr) {
        try {
            Long id = Long.parseLong(idStr);
            tokenRepository.findById(id).ifPresent(token -> {
                token.setActive(false);
                tokenRepository.save(token);
                System.out.println("Token revoked successfully.");
            });
        } catch (NumberFormatException e) {
            System.err.println("Invalid token ID format. Must be a number.");
        }
    }
    
    // Public method that can be called programmatically if needed
    public String createApiToken(String description, int expirationDays) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        ApiToken apiToken = new ApiToken();
        apiToken.setToken(token);
        apiToken.setDescription(description);
        if (expirationDays > 0) {
            apiToken.setExpiresAt(LocalDateTime.now().plusDays(expirationDays));
        }
        
        tokenRepository.save(apiToken);
        return token;
    }
}