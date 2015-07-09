package com.github.taganaka.chip8j.emu;

import javax.swing.*;
import java.awt.*;

/**
 * Created by francescolaurita on 7/7/15.
 */
public class EmuPanel extends JPanel {

    public byte[] lcd;

    @Override
    public void paint(Graphics g) {
        for (int i = 0; i < lcd.length; i++){
            if (lcd[i] == 0)
                g.setColor(Color.BLACK);
            else
                g.setColor(Color.GREEN);

            int x = (i % 64);
            int y = (int)Math.floor(i / 64);

            g.fillRect(x * 10, y * 10, 10, 10);
        }
    }
}
