package app.quantun.springaimcp.model.contract.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Question {
    private String text;
}
