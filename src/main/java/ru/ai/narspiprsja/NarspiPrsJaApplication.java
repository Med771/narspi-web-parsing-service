package ru.ai.narspiprsja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.ai.narspiprsja.config.ParserConfig;

@EnableConfigurationProperties(ParserConfig.class)
@SpringBootApplication(exclude = {
        WebMvcAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class,
        ValidationAutoConfiguration.class
})
@EnableScheduling
public class NarspiPrsJaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NarspiPrsJaApplication.class, args);
    }

}
