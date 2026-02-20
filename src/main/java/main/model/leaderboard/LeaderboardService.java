package main.model.leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import utilities.LoadSave;

public class LeaderboardService {

    public List<ScoreEntry> loadEntriesForLevel(int levelIndex) {
        return loadTopEntriesForLevel(levelIndex, 5);
    }

    public List<ScoreEntry> loadTopEntriesForLevel(int levelIndex, int limit) {
        List<String> lines = LoadSave.readScoreFile();
        List<ScoreEntry> entries = new ArrayList<>();
        int levelNumber = levelIndex + 1;

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(";");
            if (parts.length < 4) {
                continue;
            }

            try {
                String name = parts[0].trim();
                int level = Integer.parseInt(parts[1].trim());
                int deaths = Integer.parseInt(parts[2].trim());
                String timeStr = parts[3].trim().replace(",", ".");
                double time = Double.parseDouble(timeStr);

                if (level == levelNumber) {
                    entries.add(new ScoreEntry(name, level, deaths, time));
                }
            } catch (NumberFormatException ignored) {
                System.out.println("Failed to parse leaderboard line: " + line);
            }
        }

        entries.sort(Comparator.comparingInt(ScoreEntry::getDeaths).thenComparingDouble(ScoreEntry::getTimeSeconds));

        if (entries.size() > limit) {
            return new ArrayList<>(entries.subList(0, limit));
        }
        return entries;
    }
}
