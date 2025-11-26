package ru.ai.narspiprsja.tools.open;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class OpenNormalizer {

    public String normalize(String text) {
        if (text == null) return "";

        String t = Normalizer.normalize(text, Normalizer.Form.NFC);

        // Удаляем неконтролируемые скрытые символы
        t = t.replaceAll("[\\p{C}&&[^\r\n\t]]+", " ");

        // Длинные тире → дефис
        t = t.replace('\u2014', '-').replace('\u2013', '-');

        // Кавычки
        t = t.replace('«', '"').replace('»', '"')
                .replace('“', '"').replace('”', '"');

        // Сжимаем пробелы
        t = t.replaceAll("\\s+", " ").trim();

        return t;
    }
}
