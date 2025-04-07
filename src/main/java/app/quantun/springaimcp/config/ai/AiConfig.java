package app.quantun.springaimcp.config.ai;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Bean
    ChatClient anthropicChatClient(AnthropicChatModel chatModel)
    {
        return ChatClient.create(chatModel);
    }

    @Bean
    @Primary
    ChatClient geminiAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

}
