package ru.ai.narspiprsja.tools;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.ai.narspiprsja.tools.open.OpenMasker;
import ru.ai.narspiprsja.tools.open.OpenNormalizer;
import ru.ai.narspiprsja.tools.open.OpenSentenceProcessor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenTools {

    private final OpenNormalizer normalizer;
    private final OpenMasker masker;
    private final OpenSentenceProcessor processor;

    private SentenceDetectorME detector;

    @PostConstruct
    public void init() throws Exception {
        try (InputStream model = new ClassPathResource(
                "models/opennlp-ru-ud-gsd-sentence-1.3-2.5.4.bin").getInputStream()) {
            detector = new SentenceDetectorME(new SentenceModel(model));
        }
    }

    public List<String> detectSentences(String rawText) {
        // 1) нормализация
        String clean = normalizer.normalize(rawText);

        // 2) маскирование
        String masked = masker.mask(clean);

        // 3) детектор предложений
        Span[] spans = detector.sentPosDetect(masked);

        List<String> parts = new ArrayList<>();
        for (Span s : spans) {
            String sent = masked.substring(s.getStart(), s.getEnd()).trim();
            parts.add(masker.unmask(sent));
        }

        // 4) умное слияние ошибок детектора
        return processor.mergeBrokenSentences(parts);
    }

    public List<String> mergeShortChunks(List<String> chunks, int minSize) {
        return processor.mergeShortChunks(chunks, minSize);
    }
}
