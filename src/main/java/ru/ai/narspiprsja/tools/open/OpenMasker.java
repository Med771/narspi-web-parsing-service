package ru.ai.narspiprsja.tools.open;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ai.narspiprsja.config.OpenConfig;

@Component
@RequiredArgsConstructor
public class OpenMasker {

    private final OpenConfig config;

    public String mask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return config.mask(text);
    }

    public String unmask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return config.unmask(text);
    }
}
