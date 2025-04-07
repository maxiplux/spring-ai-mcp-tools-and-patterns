package app.quantun.springaimcp.service.impl;

import app.quantun.springaimcp.model.contract.response.Answer;
import app.quantun.springaimcp.model.contract.request.Question;
import app.quantun.springaimcp.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {


    private final CategoryService categoryService;
    private final ProductService productService;
    private final UserService userService;
    private final AgentUtil agentUtil;

    @Autowired
    @Qualifier("anthropicChatClient")
    private ChatClient anthropicChatClient;

    @Autowired
    @Qualifier("geminiAiChatClient")
    private ChatClient geminiChatClient;


    @Value("classpath:templates/ai/system/store/inventory-and-users.structure.st")
    private Resource systemSummaryBookTemplate;

    @Value("classpath:templates/ai/user/store/inventory-and-users.structure.st")
    private Resource userSummaryBookTemplate;






    @SneakyThrows
    @Override
    public Answer getAnswer(Question question) {
        if (question == null || question.getText() == null)
        {
            log.error("Question or its text cannot be null. {}", question);
            throw new IllegalArgumentException( "Question or its text cannot be null.");
        }
        ObjectMapper objectMapper = new ObjectMapper();

        String questionJson = objectMapper.writeValueAsString(question);

        PromptTemplate systemPromptTemplate = new PromptTemplate(this.systemSummaryBookTemplate);

        PromptTemplate userPromptTemplate = new PromptTemplate(this.userSummaryBookTemplate);

        BeanOutputConverter<Answer> format = new BeanOutputConverter<>(Answer.class);

        Prompt systemPrompt = systemPromptTemplate.create();

        Prompt userPrompt = userPromptTemplate.create(Map.of("question", questionJson, "format", format.getFormat()));

        String aiResponse =
                anthropicChatClient.prompt(systemPrompt)
                        .advisors(new SimpleLoggerAdvisor())
                        .user(userPrompt.getContents())
                        .tools( this.agentUtil)
                        .call().content();

        if (aiResponse == null)
        {
                    log.error("AI response is null for question: {}", questionJson);
                    throw new IllegalStateException("AI response cannot be null.");
        }
        return format.convert(aiResponse);

    }
}
