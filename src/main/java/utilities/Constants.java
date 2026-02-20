package utilities;

public class Constants {
    public static class PlayerConstants {
        public static final int IDLE_RIGHT = 0;
        public static final int IDLE_LEFT = 2;
        public static final int RUNNING_RIGHT = 1;
        public static final int RUNNING_LEFT = 3;
        public static final int JUMPING_RIGHT = 0;
        public static final int JUMPING_LEFT = 2;
        public static final int HIT = 6;

        public static int getSpriteAmount(int playerAction) {
            return switch (playerAction) {
            case RUNNING_RIGHT, RUNNING_LEFT -> 6;
            case IDLE_LEFT, IDLE_RIGHT -> 7;
            case HIT -> 2;
            default -> 1;
            };
        }
    }
}
