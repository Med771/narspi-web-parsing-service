package ru.ai.narspiprsja.tools.open;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OpenSentenceProcessor {

    public List<String> mergeBrokenSentences(List<String> sentences) {
        List<String> result = new ArrayList<>();

        for (String cur : sentences) {
            if (cur == null || cur.trim().isEmpty()) continue;

            if (result.isEmpty()) {
                result.add(cur.trim());
                continue;
            }

            String prev = result.get(result.size() - 1);

            if (shouldMerge(prev, cur)) {
                result.set(result.size() - 1, (prev + " " + cur).trim());
            } else {
                result.add(cur.trim());
            }
        }

        return result;
    }

    private boolean shouldMerge(String prev, String cur) {
        prev = prev.trim();
        cur = cur.trim();

        if (prev.isEmpty() || cur.isEmpty()) return false;

        char last = lastNonSpace(prev);
        char first = firstNonSpace(cur);

        // Если заканчивается на запятую, двоеточие, тире — склеиваем
        if (last == ',' || last == ';' || last == ':' || last == '-' || last == '—') {
            return true;
        }

        // Если начинается с малой буквы — склейка
        if (Character.isLowerCase(first)) {
            return true;
        }

        // Если начинается с тире — это "— сказал он"
        if (first == '-' || first == '—') {
            return true;
        }

        // Если слишком короткое
        return cur.length() <= 3;
    }

    private char lastNonSpace(String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) return c;
        }
        return '\0';
    }

    private char firstNonSpace(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) return c;
        }
        return '\0';
    }

    public List<String> mergeShortChunks(List<String> chunks, int minSize) {
        List<String> merged = new ArrayList<>();

        for (String ch : chunks) {
            if (ch == null || ch.trim().isEmpty()) continue;

            if (!merged.isEmpty() && ch.length() < minSize) {
                int last = merged.size() - 1;
                merged.set(last, merged.get(last) + " " + ch);
            } else {
                merged.add(ch.trim());
            }
        }

        return merged;
    }
}
