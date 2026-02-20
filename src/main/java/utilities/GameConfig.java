package utilities;

public final class GameConfig {

    public static final int TILES_DEFAULT_SIZE = 32;
    public static final float SCALE = 1.0f;
    public static final int TILES_IN_WIDTH = 40;
    public static final int TILES_IN_HEIGHT = 25;
    public static final int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public static final int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    private GameConfig() {
    }
}
