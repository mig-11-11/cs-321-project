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
 * 
 * <p>Intent: Scores are stored in a properties file located in the user's home directory.
 * Each game is identified by a unique key, and scores are maintained as a
 * sorted list of {@link ScoreEntry} objects. The class also prevents duplicate
 * submissions for the same game run using optional run tokens.
 */
public final class HighScoreStore {

    public static final int DEFAULT_MAX_ENTRIES = 20;

    private static final Path SCORE_FILE = Paths.get(
            System.getProperty("user.home"),
            ".scanlinearcade-scores.properties"
    );
        private static final Set<String> SAVED_RUN_KEYS = new HashSet<>();

    private HighScoreStore() {
    }

    /**
     * Retrieves the top scores for a given game.
     * 
     * @param gameKey unique identifier for the game
     * @param maxEntries maximum number of scores to return
     * @return a list of top {@link ScoreEntry} objects sorted in descending order
     */
    public static synchronized List<ScoreEntry> getTopScores(String gameKey, int maxEntries) {
        List<ScoreEntry> scores = readScoresForGame(gameKey);
        sortDescending(scores);
        if (scores.size() > maxEntries) {
            scores = new ArrayList<>(scores.subList(0, maxEntries));
        }
        return scores;
    }

    /**
     * Submits a score for a game.
     * 
     * @param gameKey unique identifier for the game
     * @param playerName name of player submitting their score
     * @param score player's score being submitted
     * @param maxEntries maximum number of scores submitted
     * @return true if score was successfully stored
     */
    public static synchronized boolean submitScore(String gameKey, String playerName, int score, int maxEntries) {
        return submitScore(gameKey, playerName, score, maxEntries, null);
    }

    /**
     * Submits a score for a game with a run token.
     * 
     * @param gameKey unique identifier for the game
     * @param playerName name of player submitting their score
     * @param score player's score being submitted
     * @param maxEntries maximum number of scores submitted
     * @param runToken unique identifier for a specific game run
     * @return true if score was successfully stored
     */
    public static synchronized boolean submitScore(String gameKey, String playerName, int score, int maxEntries, String runToken) {
        String runKey = buildRunKey(gameKey, runToken);
        if (runKey != null && SAVED_RUN_KEYS.contains(runKey)) {
            return false;
        }

        String normalizedName = normalizeName(playerName);
        if (normalizedName.isEmpty()) {
            return false;
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

    /**
     * Checks if game is already saved for a specific run.
     * 
     * @param gameKey unique identifier for the game
     * @param runToken unique identifier for a specific game run
     * @return true if game run is already saved
     */
    public static synchronized boolean isRunAlreadySaved(String gameKey, String runToken) {
        String runKey = buildRunKey(gameKey, runToken);
        return runKey != null && SAVED_RUN_KEYS.contains(runKey);
    }

    /**
     * Reads and parses stored scores for a given game from persistent storage.
     * 
     * @param gameKey unique identifier for the game
     * @return a list of parsed objects
     */
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

    /**
     * Writes the scores to persistent storage.
     * 
     * @param gameKey unique identifier for the game
     * @param scores list of scores to write
     */
    private static void writeScoresForGame(String gameKey, List<ScoreEntry> scores) {
        Properties props = loadProperties();
        props.setProperty(gameKey, serialize(scores));
        saveProperties(props);
    }

    /**
     * Loads the properties file containing all stored scores.
     * 
     * @return a object containing stored data
     */
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

    /**
     * Saves the given properties to the score file.
     * 
     * @param props properties object containing score data
     */
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

    /**
     * Serializes a list of score entries into a string format for storage.
     * 
     * @param scores list of scores to serialize
     * @return a formatted string of the scores
     */
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

    /**
     * Sort the scores in descending order.
     * 
     * @param scores list of scores to sort
     */
    private static void sortDescending(List<ScoreEntry> scores) {
        Collections.sort(scores, Comparator.comparingInt(ScoreEntry::score).reversed());
    }

    /**
     * Normalizes a player name, given by value.
     * 
     * @param value raw player name
     * @return normalized player name
     */
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

    /**
     * Encodes a player name using Base64 for safe storage.
     * 
     * @param name player name to encode
     * @return encoded string representation
     */
    private static String encodeName(String name) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(name.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a player name from storage.
     * 
     * @param encodedOrRaw encoded or raw string
     * @return decoded name
     */
    private static String decodeName(String encodedOrRaw) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encodedOrRaw);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ignored) {
            // Backward compatibility for previously stored raw names.
            return encodedOrRaw;
        }
    }

    /**
     * Builds a unique key for a game run using game key and run token.
     * 
     * @param gameKey unique identifier for each game
     * @param runToken unique identifier for a specific game run
     * @return combined run key
     */
    private static String buildRunKey(String gameKey, String runToken) {
        if (gameKey == null || gameKey.isBlank() || runToken == null || runToken.isBlank()) {
            return null;
        }
        return gameKey + "|" + runToken;
    }

    public record ScoreEntry(String playerName, int score) {
    }
}
