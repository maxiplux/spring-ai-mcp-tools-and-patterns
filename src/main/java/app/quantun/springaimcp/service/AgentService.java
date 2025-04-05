package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.contract.request.Question;
import app.quantun.springaimcp.model.contract.response.Answer;

public interface AgentService {


    Answer getAnswer(Question question);
}
