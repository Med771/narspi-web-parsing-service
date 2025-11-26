package ru.ai.narspiprsja.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "parse.config")
public class ParserProperty {
    private String date;
    private List<String> parts;

    private String splitter;
    private int maxTokens;
    private int overlapTokens;

    private Map<String, String> selectors = new HashMap<>();
}
