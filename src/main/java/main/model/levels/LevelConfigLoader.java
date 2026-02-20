package main.model.levels;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import main.model.entities.entity.SpawnPlatform;

public class LevelConfigLoader {

    public static class LevelConfig {
        public float spawnX;
        public float spawnY;
        public List<SpawnPlatformConfig> spawnPlatforms = new ArrayList<>();
        public List<GroupedTriggerPlatformConfig> groupedTriggerPlatforms = new ArrayList<>();
        public List<SpikeConfig> spikes = new ArrayList<>();
        public List<TriggerSpikeConfig> triggerSpikes = new ArrayList<>();
    }

    public static class SpawnPlatformConfig {
        public float x;
        public float y;
        public float width;
        public float height;
        public float speed;
        public float waitTime;
    }

    public static class GroupedTriggerPlatformConfig {
        public int tileId;
        public float targetOffsetX;
        public float targetOffsetY;
        public float speed;
        public boolean shouldReturn;
        public boolean solid;
        public boolean shouldLoop;
    }

    public static class SpikeConfig {
        public int tileId;
        public int spriteId;
    }

    public static class TriggerSpikeConfig {
        public int tileId;
        public int spriteId;
        public float targetOffsetX;
        public float targetOffsetY;
        public float speed;
        public float triggerDistance;
        public boolean shouldReturn;
        public int id = -1;
        public int collisionWidth = -1;
        public int collisionHeight = -1;
    }

    public static LevelConfig loadConfig(String configFileName) {
        LevelConfig config = new LevelConfig();

        try {
            InputStream is = LevelConfigLoader.class.getResourceAsStream("/" + configFileName);
            if (is == null) {
                System.err.println("Could not find config file: " + configFileName);
                return config;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (line.startsWith("spawnX=")) {
                    config.spawnX = Float.parseFloat(line.substring(7));
                } else if (line.startsWith("spawnY=")) {
                    config.spawnY = Float.parseFloat(line.substring(7));
                } else if (line.startsWith("spawnPlatform=")) {
                    String[] parts = line.substring(14).split(",");
                    SpawnPlatformConfig sp = new SpawnPlatformConfig();
                    sp.x = Float.parseFloat(parts[0].trim());
                    sp.y = Float.parseFloat(parts[1].trim());
                    sp.width = Float.parseFloat(parts[2].trim());
                    sp.height = Float.parseFloat(parts[3].trim());
                    sp.speed = Float.parseFloat(parts[4].trim());
                    sp.waitTime = Float.parseFloat(parts[5].trim());
                    config.spawnPlatforms.add(sp);
                } else if (line.startsWith("groupedTriggerPlatform=")) {
                    String[] parts = line.substring(23).split(",");
                    GroupedTriggerPlatformConfig gtp = new GroupedTriggerPlatformConfig();
                    gtp.tileId = Integer.parseInt(parts[0].trim());
                    gtp.targetOffsetX = Float.parseFloat(parts[1].trim());
                    gtp.targetOffsetY = Float.parseFloat(parts[2].trim());
                    gtp.speed = Float.parseFloat(parts[3].trim());
                    gtp.shouldReturn = Boolean.parseBoolean(parts[4].trim());
                    gtp.solid = Boolean.parseBoolean(parts[5].trim());
                    if (parts.length > 6) {
                        gtp.shouldLoop = Boolean.parseBoolean(parts[6].trim());
                    }
                    config.groupedTriggerPlatforms.add(gtp);
                } else if (line.startsWith("spike=")) {
                    String[] parts = line.substring(6).split(",");
                    SpikeConfig sc = new SpikeConfig();
                    sc.tileId = Integer.parseInt(parts[0].trim());
                    sc.spriteId = Integer.parseInt(parts[1].trim());
                    config.spikes.add(sc);
                } else if (line.startsWith("triggerSpike=")) {
                    String[] parts = line.substring(13).split(",");
                    TriggerSpikeConfig tsc = new TriggerSpikeConfig();
                    tsc.tileId = Integer.parseInt(parts[0].trim());
                    tsc.spriteId = Integer.parseInt(parts[1].trim());
                    tsc.targetOffsetX = Float.parseFloat(parts[2].trim());
                    tsc.targetOffsetY = Float.parseFloat(parts[3].trim());
                    tsc.speed = Float.parseFloat(parts[4].trim());
                    tsc.triggerDistance = Float.parseFloat(parts[5].trim());
                    tsc.shouldReturn = Boolean.parseBoolean(parts[6].trim());
                    if (parts.length > 7) {
                        tsc.id = Integer.parseInt(parts[7].trim());
                    }
                    if (parts.length > 9) {
                        tsc.collisionWidth = Integer.parseInt(parts[8].trim());
                        tsc.collisionHeight = Integer.parseInt(parts[9].trim());
                    }
                    config.triggerSpikes.add(tsc);
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("Error loading config file " + configFileName + ": " + e.getMessage());
            e.printStackTrace();
        }

        return config;
    }

    public static void applyConfig(Level level, LevelConfig config) {
        if (!config.spawnPlatforms.isEmpty()) {
            SpawnPlatformConfig sp = config.spawnPlatforms.get(config.spawnPlatforms.size() - 1);
            level.setSpawnPlatform(new SpawnPlatform(sp.x, sp.y, (int) sp.width, (int) sp.height,
                    (int) sp.speed, sp.waitTime));
        }

        for (GroupedTriggerPlatformConfig gtp : config.groupedTriggerPlatforms) {
            level.createGroupedTriggerPlatformFromTile(gtp.tileId, gtp.targetOffsetX, gtp.targetOffsetY,
                    gtp.speed, gtp.shouldReturn, gtp.solid, gtp.shouldLoop);
        }

        for (SpikeConfig sc : config.spikes) {
            level.createSpikesFromTile(sc.tileId, sc.spriteId);
        }

        for (TriggerSpikeConfig tsc : config.triggerSpikes) {
            level.createTriggerSpikesFromTile(tsc.tileId, tsc.spriteId, tsc.targetOffsetX, tsc.targetOffsetY,
                    tsc.speed, tsc.triggerDistance, tsc.shouldReturn, tsc.id,
                    tsc.collisionWidth, tsc.collisionHeight);
        }
    }
}
