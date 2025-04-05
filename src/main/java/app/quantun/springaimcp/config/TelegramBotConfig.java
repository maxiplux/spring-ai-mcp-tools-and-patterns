package app.quantun.springaimcp.config;

import app.quantun.springaimcp.service.bot.SecureTelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class TelegramBotConfig {

    @Value("${telegram.bot.upload.dir}")
    private String uploadDir;

    @Value("${telegram.bot.thread.pool.size:10}")
    private int threadPoolSize;

    @Bean
    public TelegramBotsApi telegramBotsApi(SecureTelegramBot secureTelegramBot) throws TelegramApiException {
        log.info("Initializing Telegram Bots API");
        var api = new TelegramBotsApi(DefaultBotSession.class);
        log.info("Registering bot: {}", secureTelegramBot.getBotUsername());
        api.registerBot(secureTelegramBot);
        log.debug("Bot registration completed successfully");
        return api;
    }

    @Bean
    public Executor taskExecutor() throws IOException {
        log.info("Creating thread pool executor with {} threads", threadPoolSize);
        return Executors.newFixedThreadPool(threadPoolSize);
    }

    @PostConstruct
    public void initializeStorage() throws IOException {
        log.info("Initializing file storage directory: {}", uploadDir);
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            log.debug("Upload directory does not exist, creating it");
            Files.createDirectories(uploadPath);
            log.info("Upload directory created successfully");
        } else {
            log.debug("Upload directory already exists");
        }

        // Set proper permissions on the directory
        log.debug("Setting permissions on upload directory");
        uploadPath.toFile().setReadable(true, true);
        uploadPath.toFile().setWritable(true, true);
        uploadPath.toFile().setExecutable(true, true);
        log.info("Upload directory permissions set successfully");
    }
}
