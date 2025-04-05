package app.quantun.springaimcp.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final SecurityUtils securityUtils;
    @Value("${telegram.bot.upload.dir}")
    private String uploadDir;
    @Value("${telegram.bot.file.max-size-mb}")
    private int maxFileSizeMb;

    public String saveFile(File telegramFile, String originalFileName, String botToken)
            throws TelegramApiException, IOException {
        // Generate a unique filename to prevent path traversal attacks
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        }

        String uniqueFileName = securityUtils.generateUniqueId() + fileExtension;
        Path destinationPath = Paths.get(uploadDir, uniqueFileName);

        // Construct the file URL using the Telegram Bot API format
        // Format: https://api.telegram.org/file/bot<token>/<file_path>
        String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + telegramFile.getFilePath();
        log.debug("Saving file to: {}", destinationPath);

        try (InputStream in = new URL(fileUrl).openStream()) {
            Files.copy(in, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Verify file size after download
            long actualSize = Files.size(destinationPath);
            if (actualSize > maxFileSizeMb * 1024 * 1024) {
                log.warn("Downloaded file exceeds size limit: {} bytes", actualSize);
                Files.delete(destinationPath);
                throw new SecurityException("Downloaded file exceeds size limit");
            }

            // Compute file hash for logging
            String fileHash = securityUtils.computeFileHash(destinationPath);
            log.info("File saved successfully: path={}, size={}, hash={}",
                    securityUtils.maskPII(destinationPath.toString()), actualSize, fileHash);

            return destinationPath.toString();
        } catch (IOException e) {
            log.error("Error downloading file from Telegram: {}", e.getMessage());
            throw new IOException("Failed to download file from Telegram", e);
        }
    }
}
