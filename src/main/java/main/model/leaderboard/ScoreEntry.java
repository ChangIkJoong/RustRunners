package main.model.leaderboard;

public class ScoreEntry {
    private final String playerName;
    private final int level;
    private final int deaths;
    private final double timeSeconds;

    public ScoreEntry(String playerName, int level, int deaths, double timeSeconds) {
        this.playerName = playerName;
        this.level = level;
        this.deaths = deaths;
        this.timeSeconds = timeSeconds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLevel() {
        return level;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getTimeSeconds() {
        return timeSeconds;
    }
}
