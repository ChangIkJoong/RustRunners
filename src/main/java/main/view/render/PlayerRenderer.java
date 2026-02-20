package main.view.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.model.entities.entity.Player;
import utilities.GameConfig;
import utilities.LoadSave;

public class PlayerRenderer {

    private final BufferedImage[][] animation;
    private final float xDrawOffset = 9.5f * GameConfig.SCALE;
    private final float yDrawOffset = 8.25f * GameConfig.SCALE;

    public PlayerRenderer() {
        animation = new BufferedImage[4][8];
        BufferedImage atlas = LoadSave.getSpriteAtlas(LoadSave.PLAYER_ATLAS);
        if (atlas != null) {
            for (int j = 0; j < animation.length; j++) {
                for (int i = 0; i < animation[j].length; i++) {
                    animation[j][i] = atlas.getSubimage(i * 32, j * 32, 32, 32);
                }
            }
        }
    }

    public void render(Player player, Graphics g) {
        int action = player.getPlayerAction();
        int frame = player.getAniIndex();

        if (action >= 0
                && action < animation.length
                && frame >= 0
                && frame < animation[action].length
                && animation[action][frame] != null) {
            g.drawImage(animation[action][frame],
                    (int) (player.getHitbox().x - xDrawOffset),
                    (int) (player.getHitbox().y - yDrawOffset),
                    player.getWidth(), player.getHeight(), null);
            return;
        }

        g.setColor(Color.GREEN);
        g.fillRect((int) player.getHitbox().x, (int) player.getHitbox().y, player.getWidth(), player.getHeight());
    }
}
