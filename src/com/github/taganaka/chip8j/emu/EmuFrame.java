package com.github.taganaka.chip8j.emu;

import com.github.taganaka.chip8j.chip.KeyMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

/**
 * Created by francescolaurita on 7/7/15.
 */
public class EmuFrame extends JFrame implements KeyListener {

    private byte[] keys;
    private Map<Integer, Integer> map;
    private Main program;

    public EmuFrame(EmuPanel panel, Main program){
        this.program = program;
        keys = new byte[16];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = 0x0;
        }
        map = new KeyMap().map();

        setPreferredSize(new Dimension(640, 320));

        setPreferredSize(
                new Dimension(
                        640 + getInsets().left + getInsets().right,
                        320 + getInsets().top + getInsets().bottom
                )
        );
        setResizable(false);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("CHIP-8 Emu");
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
                dim.width / 2 - this.getSize().width / 2,
                dim.height / 2 - this.getSize().height/2
        );
        addKeyListener(this);
        toFront();
        setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //System.err.println("TYPED:" + e.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            System.exit(0);
        }
        Integer i = map.get(e.getKeyCode());
        if (i != null) {
            System.out.println(e.getKeyCode());
            keys[i.intValue()] = 0x1;
            program.chip8Instance().setKeys(keys);
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Integer i = map.get(e.getKeyCode());
        if (i != null) {
            System.out.println(e.getKeyCode());
            keys[i.intValue()] = 0x0;
            program.chip8Instance().setKeys(keys);
        }

    }

}
