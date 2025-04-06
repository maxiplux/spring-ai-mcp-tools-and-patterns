package app.quantun.springaimcp.service.bot;

import app.quantun.springaimcp.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class SecureTelegramBot extends TelegramLongPollingBot {
    // Allowed file types (whitelist approach)
    private static final Set<String> ALLOWED_AUDIO_MIME_TYPES = new HashSet<>(
            Arrays.asList("audio/mpeg", "audio/mp4", "audio/ogg", "audio/wav", "audio/x-wav"));

    private static final Set<String> ALLOWED_DOCUMENT_MIME_TYPES = new HashSet<>(
            Arrays.asList("application/pdf", "text/plain", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));

    @Value("${telegram.bot.username}")
    private String botUsername;


    private int maxFileSizeBytes;

    @Autowired
    private AudioProcessingService audioProcessingService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AgentService agentService;

    public SecureTelegramBot(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.file.max-size-mb:10}") int maxFileSizeMb) {
        super(botToken);
        this.botUsername = botUsername;
        this.maxFileSizeBytes = maxFileSizeMb * 1024 * 1024;

        log.info("Initializing Telegram bot with username: {}, max file size: {} MB",
                botUsername, maxFileSizeMb);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Integer messageId = update.getMessage().getMessageId();
        if (update == null) {
            log.warn("Received null update");
            return;
        }

        try {
            if (!update.hasMessage()) {
                log.debug("Update doesn't contain a message");
                return;
            }

            long chatId = update.getMessage().getChatId();

            // Privacy-focused logging - only log minimal user info
            String userIdentifier = securityUtils.getUserIdentifier(update);

            log.info("Received message from user ID: {} (Chat ID: {})",
                    userIdentifier, chatId);

            if (update.getMessage().hasText()) {
                handleTextMessage(update, userIdentifier);
            } else if (update.getMessage().hasAudio()) {
                log.info("Received audio message from user ID: {}", userIdentifier);
                handleAudioMessage(update, userIdentifier);
            } else if (update.getMessage().hasDocument()) {
                log.info("Received document from user ID: {}", userIdentifier);
                handleDocumentMessage(update, userIdentifier);
            } else if (update.getMessage().hasVoice()) {
                log.info("Received voice message from user ID: {}", userIdentifier);
                handleVoiceMessage(update, userIdentifier);
            } else {
                log.info("Received unsupported message type from user ID: {}", userIdentifier);
                sendReply(chatId, messageId, "I received a message but don't know how to process this type.");
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
            try {
                if (update.hasMessage()) {
                    sendReply(update.getMessage().getChatId(), messageId,
                            "Sorry, an error occurred while processing your message.");
                }
            } catch (Exception ex) {
                log.error("Failed to send error message", ex);
            }
        }
    }

    private void handleTextMessage(Update update, String userIdentifier) {
        String messageText = update.getMessage().getText();
        Integer messageId = update.getMessage().getMessageId();
        long chatId = update.getMessage().getChatId();

        // Don't log the actual message content - just metadata
        log.info("Received text message from user: {} (Chat ID: {}), length: {}",
                userIdentifier, chatId, messageText != null ? messageText.length() : 0);

        if (messageText == null) {
            log.warn("Received null text message from user: {}", userIdentifier);

            sendReply(chatId, messageId, "I received an empty message.");
            return;
        }

        if (messageText.startsWith("/start")) {
            log.debug("Processing /start command for user: {}", userIdentifier);
            sendReply(chatId, messageId, "Hello! I'm your secure Spring Boot 3 Telegram bot.");
        } else if (messageText.startsWith("/help")) {
            log.debug("Processing /help command for user: {}", userIdentifier);
            sendReply(chatId, messageId, "Available commands:\n/start - Start the bot\n/help - Show this help message");
        } else {
            log.debug("Processing text message for user: {}", userIdentifier);

            sendReply(chatId, messageId, String.format("MY FRIEND YOU SAID [{%s}]", messageText) );

            /*

            val answer = agentService.getAnswer(Question.builder().text(messageText).build());

            /*switch (answer.getAnswerType()) {
                case HTML -> sendReply(chatId, messageId, answer.getHtmlAnswer());
                case MARKDOWN -> sendReply(chatId, messageId, answer.getMarkDownAnswer());
                default -> log.warn("Unsupported answer type for user: {}", userIdentifier);
            }
            switch (answer.getAnswerType()) {
                case HTML -> sendReply(chatId, messageId, answer.getHtmlAnswer());
                case MARKDOWN -> sendReply(chatId, messageId, answer.getMarkDownAnswer());
                default -> log.warn("Unsupported answer type for user: {}", userIdentifier);
            }
            */


        }
    }


    private void handleAudioMessage(Update update, String userIdentifier) {

        Integer messageId = update.getMessage().getMessageId();
        long chatId = update.getMessage().getChatId();
        Audio audio = update.getMessage().getAudio();

        String fileId = audio.getFileId();
        String fileName = securityUtils.sanitizeFileName(audio.getFileName());
        String mimeType = audio.getMimeType();
        Long fileSize = audio.getFileSize();

        // Log only non-sensitive metadata
        log.info("Processing audio: id={}, name={}, type={}, size={}",
                securityUtils.maskPII(fileId), securityUtils.maskPII(fileName), mimeType, fileSize);

        // Validate file type
        if (!ALLOWED_AUDIO_MIME_TYPES.contains(mimeType)) {
            log.warn("User {} attempted to upload audio with disallowed MIME type: {}",
                    userIdentifier, mimeType);
            sendReply(chatId, messageId, "Sorry, this audio format is not supported.");
            return;
        }

        // Validate file size
        if (fileSize > maxFileSizeBytes) {
            log.warn("User {} attempted to upload oversized audio: {} bytes",
                    userIdentifier, fileSize);
            sendReply(chatId, messageId, "The audio file is too large. Maximum size is 10 MB.");
            return;
        }

        try {

            File audioFile = customDownloadFile(fileId);

            String secureFilePath = fileStorageService.saveFile(audioFile, fileName, getBotToken());

            // Process the audio file
            String result = audioProcessingService.processAudioFile(secureFilePath);
            sendReply(chatId, messageId, result);

        } catch (TelegramApiException e) {
            log.error("Telegram API error while processing audio", e);
            sendReply(chatId, messageId, "Error processing audio: Unable to download the file.");
        } catch (IOException e) {
            log.error("IO error while processing audio", e);
            sendReply(chatId, messageId, "Error processing audio: File processing failed.");
        } catch (SecurityException e) {
            log.error("Security violation while processing audio", e);
            sendReply(chatId, messageId, "Error processing audio: Security check failed.");
        } catch (Exception e) {
            log.error("Unexpected error while processing audio", e);
            sendReply(chatId, messageId, "An unexpected error occurred while processing your audio.");
        }
    }

    private void handleVoiceMessage(Update update, String userIdentifier) {
        long chatId = update.getMessage().getChatId();
        String fileId = update.getMessage().getVoice().getFileId();
        Integer messageId = update.getMessage().getMessageId();
        Integer duration = update.getMessage().getVoice().getDuration();
        String mimeType = update.getMessage().getVoice().getMimeType();
        Long fileSize = update.getMessage().getVoice().getFileSize();

        log.info("Processing voice message: id={}, duration={}s, type={}, size={}",
                securityUtils.maskPII(fileId), duration, mimeType, fileSize);

        // Validate file type (voice messages are usually in OGG format)
        if (!ALLOWED_AUDIO_MIME_TYPES.contains(mimeType)) {
            log.warn("User {} attempted to upload voice with disallowed MIME type: {}",
                    userIdentifier, mimeType);
            sendReply(chatId, messageId, "Sorry, this voice format is not supported.");
            return;
        }

        // Validate file size
        if (fileSize > maxFileSizeBytes) {
            log.warn("User {} attempted to upload oversized voice message: {} bytes",
                    userIdentifier, fileSize);
            sendReply(chatId, messageId, "The voice message is too large. Maximum size is 10 MB.");
            return;
        }

        try {
            File voiceFile = customDownloadFile(fileId);
            String uniqueFileName = "voice_" + securityUtils.generateUniqueId() + ".ogg";

            String secureFilePath = fileStorageService.saveFile(voiceFile, uniqueFileName, getBotToken());

            // Process the voice file
            String result = audioProcessingService.processVoiceFile(secureFilePath);
            sendReply(chatId, messageId, result);

        } catch (Exception e) {
            log.error("Error processing voice message", e);
            sendReply(chatId, messageId, "Error processing voice message: " + e.getMessage());
        }
    }

    private File customDownloadFile(String fileId) throws TelegramApiException {
        log.debug("Downloading file with ID: {}", securityUtils.maskPII(fileId));
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        return execute(getFile);
    }

    private void handleDocumentMessage(Update update, String userIdentifier) {
        long chatId = update.getMessage().getChatId();
        Document document = update.getMessage().getDocument();
        Integer messageId = update.getMessage().getMessageId();


        String fileId = document.getFileId();
        String fileName = securityUtils.sanitizeFileName(document.getFileName());
        String mimeType = document.getMimeType();
        Long fileSize = document.getFileSize();

        log.info("Processing document: id={}, name={}, type={}, size={}",
                securityUtils.maskPII(fileId), securityUtils.maskPII(fileName), mimeType, fileSize);

        // Validate file type
        if (!ALLOWED_DOCUMENT_MIME_TYPES.contains(mimeType)) {
            log.warn("User {} attempted to upload document with disallowed MIME type: {}",
                    userIdentifier, mimeType);
            sendReply(chatId, messageId, "Sorry, this document type is not supported.");
            return;
        }

        // Validate file size
        if (fileSize > maxFileSizeBytes) {
            log.warn("User {} attempted to upload oversized document: {} bytes",
                    userIdentifier, fileSize);
            sendReply(chatId, messageId, "The document is too large. Maximum size is 10 MB.");
            return;
        }

        try {
            File docFile = customDownloadFile(fileId);

            String secureFilePath = fileStorageService.saveFile(docFile, fileName, getBotToken());

            sendReply(chatId, messageId, "Successfully received your document. Processing...");

        } catch (Exception e) {
            log.error("Error processing document", e);
            sendReply(chatId, messageId, "Error processing document: " + e.getMessage());
        }
    }

    /**
     * Sends a reply message to a specific message in a chat
     *
     * @param chatId    The chat ID where to send the reply
     * @param messageId The ID of the message to reply to
     * @param text      The text of the reply message
     */
    public void sendReply(long chatId, Integer messageId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.enableHtml(true);
        //message.enableMarkdown(true);

        // Set the message to reply to
        if (messageId != null) {
            message.setReplyToMessageId(messageId);
        }

        try {
            execute(message);
            log.debug("Reply sent to chat: {}, message: {}", chatId, messageId);
        } catch (TelegramApiException e) {
            log.error("Failed to send reply to chat: {}, message: {}", chatId, messageId, e);
        }
    }


//    public void sendMessage(long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(text);
//
//
//
//        try {
//            execute(message);
//            log.debug("Message sent to chat: {}", chatId);
//        } catch (TelegramApiException e) {
//            log.error("Failed to send message to chat: {}", chatId, e);
//        }
//    }
}
