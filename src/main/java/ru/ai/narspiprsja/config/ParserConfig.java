package ru.ai.narspiprsja.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
@ConfigurationProperties(prefix = "parser")
@Data
public class ParserConfig {

    private String baseUrl;
    private String newsPath;
    private String userAgent;
    private String referrer;
    private String acceptLanguage;
    private int timeout;
    private int sleep;

    private int retryCount = 3;
    private long retryDelay = 500;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm | dd.MM.yyyy");
}
