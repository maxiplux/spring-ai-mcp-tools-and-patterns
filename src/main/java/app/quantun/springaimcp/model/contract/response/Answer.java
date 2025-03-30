package app.quantun.springaimcp.model.contract.response;

import lombok.Data;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import jakarta.json.JsonReader;
import jakarta.json.JsonWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Data
public class Answer {
    private String text;

    private List<Map<String, Object>>  source;
}
