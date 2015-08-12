package com.github.taganaka.chip8j.emu;

import com.github.taganaka.chip8j.chip.Chip;

import javax.swing.*;


/**
 * Created by francescolaurita on 7/4/15.
 */
public class Main extends Thread implements Chip.IOHandler{
    private Chip chip8;
    private EmuPanel panel;
    private EmuFrame frame;

    public Main(String rom){
        chip8 = new Chip(this);
        chip8.loadROM(rom);
        panel = new EmuPanel();
        frame = new EmuFrame(panel, this);
        panel.lcd = new byte[0];

    }
    public void run(){
        while (true)
            chip8.run();
    }
    public static void main(String[] args) {
        if (args.length > 0) {
            final Main main = new Main(args[0]);
            main.start();
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new RomChooserFrame();
                }
            });
        }


    }

    public Chip chip8Instance(){
        return chip8;
    }

    @Override
    public void onClearScreen() {

    }

    @Override
    public void onUpdateScreen(byte[] lcd) {
        panel.lcd = lcd;
        frame.repaint();
    }

    @Override
    public void onEmitSound() {
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

}
