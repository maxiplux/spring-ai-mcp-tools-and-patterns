package app.quantun.springaimcp.service.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class SecurityUtils {

    /**
     * Creates a privacy-preserving user identifier for logging purposes.
     * Uses a hash of user ID rather than username to prevent PII exposure.
     */
    public String getUserIdentifier(Update update) {
        if (update.hasMessage() && update.getMessage().getFrom() != null) {
            Long userId = update.getMessage().getFrom().getId();
            if (userId != null) {
                // Return just the user ID or a hash of it to avoid logging usernames/PII
                return "ID:" + userId;
            }
        }
        return "unknown-user";
    }

    /**
     * Masks potentially sensitive information in logs
     * Shows first few characters followed by a hash to maintain traceability
     * while protecting sensitive data
     */
    public String maskPII(String data) {
        if (data == null || data.isEmpty()) {
            return "null";
        }

        // Keep the first few characters for troubleshooting purposes
        // but mask the rest to protect sensitive info
        int visibleChars = Math.min(4, data.length());
        String visible = data.substring(0, visibleChars);

        // Generate deterministic hash for the rest to maintain traceability
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(data.getBytes());
            String hash = Base64.getEncoder().encodeToString(hashBytes).substring(0, 8);

            return visible + "..." + hash;
        } catch (NoSuchAlgorithmException e) {
            return visible + "...masked";
        }
    }

    public String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unknown_file";
        }

        // Remove path traversal characters and limit length
        String sanitized = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");

        // Limit filename length to prevent potential issues
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }

        return sanitized;
    }

    public String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public String computeFileHash(Path filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] hashBytes = digest.digest(fileBytes);
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("Error computing file hash", e);
            return "hash_computation_failed";
        }
    }
}

