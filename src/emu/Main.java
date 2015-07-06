package emu;

import chip.Chip;

/**
 * Created by francescolaurita on 7/4/15.
 */
public class Main {
    public static void main(String[] args) {
        Chip chip = new Chip(new Chip.IOHandler() {
            @Override
            public void onClearScreen() {

            }

            @Override
            public void onUpdateScreen() {

            }

            @Override
            public void onEmitSound() {

            }
        });
        chip.loadROM("./roms/PONG2");
        while(true){
            chip.run();
        }
    }
}
