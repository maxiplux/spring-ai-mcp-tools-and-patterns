package app.quantun.springaimcp.service;

import app.quantun.springaimcp.model.contract.request.Answer;
import app.quantun.springaimcp.model.contract.request.Question;

public interface AgentService {


    Answer getAnswer(Question question);
}
