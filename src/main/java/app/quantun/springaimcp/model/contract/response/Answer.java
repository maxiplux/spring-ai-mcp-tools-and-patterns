package app.quantun.springaimcp.model.contract.response;

import lombok.Data;

@Data
public class Answer {
    private String text;
    private String htmlAnswer;
    private String markDownAnswer;

    private TypeofAnswer answerType;

    //private List<Map<String, Object>>  source;
}
