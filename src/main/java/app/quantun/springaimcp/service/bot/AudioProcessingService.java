package app.quantun.springaimcp.service.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class AudioProcessingService {


    @Value("${telegram.bot.audio.processing.enabled:true}")
    private boolean processingEnabled;

    public String processAudioFile(String filePath) throws IOException {
        log.info("Processing audio file with processing enabled: {}", processingEnabled);

        if (!processingEnabled) {
            log.info("Audio processing is disabled. Skipping processing.");
            return "Audio file received but processing is disabled.";
        }

        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        // Here you would add your audio processing logic
        // For example, speech-to-text or audio analysis

        log.info("Successfully processed audio file of size: {} bytes", fileSize);
        return "Successfully processed audio file (" + fileSize + " bytes)";
    }

    public String processVoiceFile(String filePath) throws IOException {
        log.info("Processing voice file with processing enabled: {}", processingEnabled);

        if (!processingEnabled) {
            log.info("Voice processing is disabled. Skipping processing.");
            return "Voice message received but processing is disabled.";
        }

        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);

        // Add voice processing logic here
        // For example, speech-to-text service

        log.info("Successfully processed voice file of size: {} bytes", fileSize);
        return "Successfully processed voice message (" + fileSize + " bytes)";
    }
}
