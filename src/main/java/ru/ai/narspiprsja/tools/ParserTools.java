package ru.ai.narspiprsja.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.ai.narspiprsja.config.ParserConfig;
import ru.ai.narspiprsja.model.Page;
import ru.ai.narspiprsja.model.Url;
import ru.ai.narspiprsja.property.ParserProperty;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserTools {

    private final ParserConfig parserConfig;
    private final ParserProperty parserProperty;
    private final OpenTools openTools;

    // ========== FETCH PAGE WITH RETRY ===========================================================

    private Optional<Document> fetchPageWithRetry(String url) {
        int attempts = parserConfig.getRetryCount();     // ← добавь в ParserConfig
        long delay = parserConfig.getRetryDelay();       // ← добавь в ParserConfig

        for (int i = 1; i <= attempts; i++) {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent(parserConfig.getUserAgent())
                        .referrer(parserConfig.getReferrer())
                        .header("Accept-Language", parserConfig.getAcceptLanguage())
                        .ignoreHttpErrors(true)
                        .timeout(parserConfig.getTimeout())
                        .get();

                return Optional.of(doc);

            } catch (IOException e) {
                log.warn("Попытка {} / {} — ошибка загрузки {}: {}",
                        i, attempts, url, e.getMessage());
                sleep(delay);
            }
        }

        log.error("Не удалось загрузить {} после {} попыток", url, attempts);
        return Optional.empty();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // ========== URL BUILDER ===========================================================

    private String buildNewsUrl(String part, int page) {
        return parserConfig.getBaseUrl().formatted(part)
                + parserConfig.getNewsPath().formatted(
                parserProperty.getDate(),
                page
        );
    }

    // ========== PARSE DATE ===========================================================

    private Optional<LocalDateTime> parseDate(String text) {
        try {
            DateTimeFormatter fmt = ParserConfig.FORMATTER;
            return Optional.of(LocalDateTime.parse(text, fmt));
        } catch (Exception e) {
            log.warn("Не удалось распарсить дату '{}'", text);
            return Optional.empty();
        }
    }

    // ========== EXTRACT NEWS LINKS FROM ONE PAGE ==============================================

    private Url extractOneNews(Element item, String part) {

        // Жёстко заданные селекторы, как у тебя раньше
        Element linkTag = item.selectFirst("a.news-list_title");
        Element dateTag = item.selectFirst("div.news-list_date span");

        if (linkTag == null || dateTag == null) {
            return null;
        }

        String href = linkTag.attr("href");
        String fullUrl = parserConfig.getBaseUrl().formatted(part) + href;

        Optional<LocalDateTime> dateOpt = parseDate(dateTag.text());
        return dateOpt.map(localDateTime -> new Url(fullUrl, localDateTime)).orElse(null);

    }

    private List<Url> extractNewsLinks(Document doc, String part) {

        Element newsList = doc.selectFirst("div.news_list");
        if (newsList == null) {
            log.info("Не найден блок div.news_list");
            return List.of();
        }

        Elements items = newsList.select("div.item_news");

        List<Url> results = new ArrayList<>();
        for (Element item : items) {
            Url news = extractOneNews(item, part);
            if (news != null) {
                results.add(news);
            }
        }

        return results;
    }

    // ========== PARSE NEWS PAGES ===========================================================

    public List<Url> parseNewsPage(String part) {
        List<Url> allNews = new ArrayList<>();
        int page = 1;

        while (true) {
            String url = buildNewsUrl(part, page);
            log.info("Парсим страницу: {}", url);

            Optional<Document> docOpt = fetchPageWithRetry(url);
            if (docOpt.isEmpty()) {
                log.warn("HTML отсутствует — пропускаем страницу {}", page);
                break;
            }

            List<Url> links = extractNewsLinks(docOpt.get(), part);
            if (links.isEmpty()) {
                log.info("Страница {} пустая — останавливаем", page);
                break;
            }

            allNews.addAll(links);
            page++;

            sleep(parserConfig.getSleep());
        }

        return allNews;
    }

    // ========== PARSE ARTICLE ===========================================================

    public Optional<Page> parseArticlePage(long postId, String url) {

        Optional<Document> docOpt = fetchPageWithRetry(url);
        if (docOpt.isEmpty()) return Optional.empty();

        // Жёсткий селектор
        Element textBlock = docOpt.get().selectFirst("div.news_text");
        if (textBlock == null) {
            log.warn("Не найден div.news_text у {}", url);
            return Optional.empty();
        }

        List<String> badChunks = openTools.detectSentences(textBlock.text());
        List<String> chunks = openTools.mergeShortChunks(
                badChunks,
                parserProperty.getOverlapTokens()
        );

        return Optional.of(new Page(
                postId,
                url,
                String.join(parserProperty.getSplitter(), chunks)
        ));
    }
}
