package com.scanlinearcade.app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Lightweight persistent score storage shared across all games.
 */
public final class HighScoreStore {

    public static final int DEFAULT_MAX_ENTRIES = 8;

    private static final Path SCORE_FILE = Paths.get(
            System.getProperty("user.home"),
            ".scanlinearcade-scores.properties"
    );
        private static final Set<String> SAVED_RUN_KEYS = new HashSet<>();

    private HighScoreStore() {
    }

    public static synchronized List<ScoreEntry> getTopScores(String gameKey, int maxEntries) {
        List<ScoreEntry> scores = readScoresForGame(gameKey);
        sortDescending(scores);
        if (scores.size() > maxEntries) {
            scores = new ArrayList<>(scores.subList(0, maxEntries));
        }
        return scores;
    }

    public static synchronized boolean submitScore(String gameKey, String playerName, int score, int maxEntries) {
        return submitScore(gameKey, playerName, score, maxEntries, null);
    }

    public static synchronized boolean submitScore(String gameKey, String playerName, int score, int maxEntries, String runToken) {
        String runKey = buildRunKey(gameKey, runToken);
        if (runKey != null && SAVED_RUN_KEYS.contains(runKey)) {
            return false;
        }

        String normalizedName = normalizeName(playerName);
        if (normalizedName.isEmpty()) {
            normalizedName = "AAA";
        }

        List<ScoreEntry> scores = readScoresForGame(gameKey);
        scores.add(new ScoreEntry(normalizedName, score));
        sortDescending(scores);

        if (scores.size() > maxEntries) {
            scores = new ArrayList<>(scores.subList(0, maxEntries));
        }

        writeScoresForGame(gameKey, scores);
        if (runKey != null) {
            SAVED_RUN_KEYS.add(runKey);
        }
        return true;
    }

    public static synchronized boolean isRunAlreadySaved(String gameKey, String runToken) {
        String runKey = buildRunKey(gameKey, runToken);
        return runKey != null && SAVED_RUN_KEYS.contains(runKey);
    }

    private static List<ScoreEntry> readScoresForGame(String gameKey) {
        Properties props = loadProperties();
        String raw = props.getProperty(gameKey, "").trim();
        if (raw.isEmpty()) {
            return new ArrayList<>();
        }

        List<ScoreEntry> parsed = new ArrayList<>();
        String[] items = raw.split("\\|");

        for (String item : items) {
            int separatorIndex = item.lastIndexOf(':');
            if (separatorIndex <= 0 || separatorIndex == item.length() - 1) {
                continue;
            }

            String name = normalizeName(decodeName(item.substring(0, separatorIndex)));
            if (name.isEmpty()) {
                name = "AAA";
            }

            try {
                int score = Integer.parseInt(item.substring(separatorIndex + 1).trim());
                parsed.add(new ScoreEntry(name, score));
            } catch (NumberFormatException ignored) {
                // Ignore malformed score rows.
            }
        }

        return parsed;
    }

    private static void writeScoresForGame(String gameKey, List<ScoreEntry> scores) {
        Properties props = loadProperties();
        props.setProperty(gameKey, serialize(scores));
        saveProperties(props);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();

        if (!Files.exists(SCORE_FILE)) {
            return props;
        }

        try (InputStream input = Files.newInputStream(SCORE_FILE)) {
            props.load(input);
        } catch (IOException ignored) {
            // If we cannot read scores, return empty properties.
        }

        return props;
    }

    private static void saveProperties(Properties props) {
        try {
            Files.createDirectories(SCORE_FILE.getParent());
            try (OutputStream out = Files.newOutputStream(SCORE_FILE)) {
                props.store(out, "Scanline Arcade High Scores");
            }
        } catch (IOException ignored) {
            // Avoid crashing the game if score persistence fails.
        }
    }

    private static String serialize(List<ScoreEntry> scores) {
        if (scores.isEmpty()) {
            return "";
        }

        List<String> rows = new ArrayList<>();
        for (ScoreEntry entry : scores) {
            rows.add(encodeName(entry.playerName()) + ":" + entry.score());
        }
        return String.join("|", rows);
    }

    private static void sortDescending(List<ScoreEntry> scores) {
        Collections.sort(scores, Comparator.comparingInt(ScoreEntry::score).reversed());
    }

    private static String normalizeName(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        if (trimmed.length() > 12) {
            trimmed = trimmed.substring(0, 12);
        }
        return trimmed;
    }

    private static String encodeName(String name) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeName(String encodedOrRaw) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encodedOrRaw);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            // Backward compatibility for previously stored raw names.
            return encodedOrRaw;
        }
    }

    private static String buildRunKey(String gameKey, String runToken) {
        if (gameKey == null || gameKey.isBlank() || runToken == null || runToken.isBlank()) {
            return null;
        }
        return gameKey + "|" + runToken;
    }

    public record ScoreEntry(String playerName, int score) {
    }
}
