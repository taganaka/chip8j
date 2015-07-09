package com.github.taganaka.chip8j.chip;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by francescolaurita on 7/7/15.
 */
public class KeyMap {
    Map map;
    public KeyMap(){
        /**
         * Keypad                 Keyboard
         +-+-+-+-+                +-+-+-+-+
         |1|2|3|C|                |1|2|3|4|
         +-+-+-+-+                +-+-+-+-+
         |4|5|6|D|                |Q|W|E|R|
         +-+-+-+-+       =>       +-+-+-+-+
         |7|8|9|E|                |A|S|D|F|
         +-+-+-+-+                +-+-+-+-+
         |A|0|B|F|                |Z|X|C|V|
         +-+-+-+-+                +-+-+-+-+
         */
        map = new HashMap<Integer, Integer>();
        map.put((int)'1', 0x1);
        map.put((int)'2', 0x2);
        map.put((int)'3', 0x3);
        map.put((int)'4', 0xC);
        map.put((int)'Q', 0x4);
        map.put((int)'W', 0x5);
        map.put((int)'E', 0x6);
        map.put((int)'R', 0xD);
        map.put((int)'A', 0x7);
        map.put((int)'S', 0x8);
        map.put((int)'D', 0x9);
        map.put((int)'F', 0xE);
        map.put((int)'Z', 0xA);
        map.put((int)'X', 0x0);
        map.put((int)'C', 0xB);
        map.put((int)'V', 0xF);
    }

    public Map<Integer, Integer> map(){
        return map;
    }
}
