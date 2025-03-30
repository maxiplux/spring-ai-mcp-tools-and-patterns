package app.quantun.springaimcp.controller;

import app.quantun.springaimcp.model.contract.request.Answer;
import app.quantun.springaimcp.model.contract.request.Question;
import app.quantun.springaimcp.service.AgentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Tag(name = "Get Information from Products, and User", description = "API orchestration for getting information from Products, and User")

public class AgentController {
    private final AgentService agentService;
    @PostMapping("/question")
    public Answer getAnswer(Question question) {
        return agentService.getAnswer(question);
    }



}
