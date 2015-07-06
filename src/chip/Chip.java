package chip;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Francesco Laurita <francesco.laurita@gmail.com> on 7/4/15.
 * CPU spec: https://en.wikipedia.org/wiki/CHIP-8
 */
public class Chip {

    static int MEM_SIZE      = 4096;
    static int STACK_SIZE    = 16;
    static int V_SIZE        = 16;
    static int KEYS_SIZE     = 16;
    static int SCREEN_WIDTH  = 64;
    static int SCREEN_HEIGHT = 32;
    static char INIT_PADDING  = 0x200;

    // Memory allocation
    private char[] memory;
    // Register holder
    private char[] V;
    // Address registers
    private char I;
    // Program counter
    private char pc;

    // Function call stack
    private char[] stack;
    private int stackPointer;

    //Delay timer: Game tick
    private int delay_t;
    // Sound timer
    private int delay_s;

    // I/O
    private byte keys[];
    private byte lcd[];


    private boolean needRedrawing = false;
    private Random rnd;

    private IOHandler ioHandler;

    public Chip(IOHandler handler){

        memory = new char[MEM_SIZE];
        V = new char[V_SIZE];
        I = 0x00;

        stack = new char[STACK_SIZE];
        stackPointer = 0;

        keys = new byte[KEYS_SIZE];
        lcd  = new byte[SCREEN_WIDTH * SCREEN_HEIGHT];

        delay_t = 0;
        delay_s = 0;

        pc = INIT_PADDING;

        ioHandler = handler;

        rnd = new Random();
    }

    public void run(){
        /**
         * Fetch opCode
         * Each opCode is 2-bytes long so we merge 2 item from mem array
         */
        final char opcode = (char)((memory[pc] << 8) | memory[pc + 1]);

        /**
         *
         0NNN	Calls RCA 1802 program at address NNN.
         00E0	Clears the screen.
         00EE	Returns from a subroutine.
         1NNN	Jumps to address NNN.
         2NNN	Calls subroutine at NNN.
         3XNN	Skips the next instruction if VX equals NN.
         4XNN	Skips the next instruction if VX doesn't equal NN.
         5XY0	Skips the next instruction if VX equals VY.
         6XNN	Sets VX to NN.
         7XNN	Adds NN to VX.
         8XY0	Sets VX to the value of VY.
         8XY1	Sets VX to VX or VY.
         8XY2	Sets VX to VX and VY.
         8XY3	Sets VX to VX xor VY.
         8XY4	Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
         8XY5	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
         8XY6	Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
         8XY7	Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
         8XYE	Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift.
         9XY0	Skips the next instruction if VX doesn't equal VY.
         ANNN	Sets I to the address NNN.
         BNNN	Jumps to the address NNN plus V0.
         CXNN	Sets VX to a random number, masked by NN.
         DXYN	Sprites stored in memory at location in index register (I), maximum 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero. All drawing is XOR drawing (i.e. it toggles the screen pixels)
         EX9E	Skips the next instruction if the key stored in VX is pressed.
         EXA1	Skips the next instruction if the key stored in VX isn't pressed.
         FX07	Sets VX to the value of the delay timer.
         FX0A	A key press is awaited, and then stored in VX.
         FX15	Sets the delay timer to VX.
         FX18	Sets the sound timer to VX.
         FX1E	Adds VX to I.
         FX29	Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font.
         FX33	Stores the Binary-coded decimal representation of VX, with the most significant of three digits at the address in I, the middle digit at I plus 1, and the least significant digit at I plus 2. (In other words, take the decimal representation of VX, place the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.)
         FX55	Stores V0 to VX in memory starting at address I.
         FX65	Fills V0 to VX with values from memory starting at address I.
         *
         */

        switch (opcode & 0xF000){ // opCommand is in in the first nibble
            case 0x0000:
                switch (opcode & 0x000F){
                    case 0x0000: { //00E0	Clears the screen.
                        needRedrawing = true;
                        clearScreen();
                        ioHandler.onClearScreen();
                        pc += 2;
                        break;
                    }
                    case 0x000E: { //00EE	Returns from a subroutine.
                        pc = stack[stackPointer];
                        pc += 2;
                        break;
                    }
                }
                break;

            case 0x1000: { //1NN Jumps to address NNN
                pc = (char) (opcode & 0x0FFF);
                break;
            }
            case 0x2000: { //2NNN	Calls subroutine at NNN.
                stack[stackPointer++] = pc;
                pc = (char) (opcode & 0x0FFF);
                break;
            }
            case 0x3000: { //3XNN Skips the next instruction if VX equals NN
                char x = (char) ((opcode & 0x0F00) >> 8);
                char nn = (char) (opcode & 0x00FF);
                if (V[x] == nn)
                    pc += 4;
                else
                    pc += 2;
                break;
            }
            case 0x4000: { //4XNN Skips the next instruction if VX doesn't equal NN.
                char x  = (char)((opcode & 0x0F00) >> 8);
                char nn = (char)(opcode & 0x00FF);
                if (V[x] != nn)
                    pc += 4;
                else
                    pc += 2;
                break;
            }
            case 0x5000: { //5XY0	Skips the next instruction if VX equals VY.
                char x = (char)((opcode & 0x0F00) >> 8);
                char y = (char)((opcode & 0x00F0) >> 4);
                if (V[x] == V[y])
                    pc += 4;
                else
                    pc += 2;
                break;

            }
            case  0x6000: { //6XNN	Sets VX to NN
                char x = (char)((opcode & 0x0F00) >> 8);
                char nn = (char)(opcode & 0x00FF);
                V[x] = nn;
                pc += 2;
            }
            case 0x7000: { //7XNN Adds NN to VX.
                char x = (char)((opcode & 0x0F00) >> 8);
                char nn = (char)(opcode & 0x00FF);
                V[x] += nn;
                pc += 2;
            }

            case 0x8000: // Other data in the last nibble
                switch (opcode & 0x000F){
                    case 0x0000: { // 8XY0	Sets VX to the value of VY.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        char y = (char)((opcode & 0x00F0) >> 4);
                        V[x] = V[y];
                        pc += 2;
                        break;
                    }
                    case 0x0001: { //8XY1	Sets VX to VX or VY.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        char y = (char)((opcode & 0x00F0) >> 4);
                        V[x] = (char)(V[x] | V[y]);
                        pc += 2;
                        break;
                    }
                    case 0x0002: { //8XY2	Sets VX to VX and VY.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        char y = (char)((opcode & 0x00F0) >> 4);
                        V[x] = (char)(V[x] & V[y]);
                        pc += 2;
                        break;
                    }
                    case 0x0003: { //8XY3	Sets VX to VX xor VY.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        char y = (char)((opcode & 0x00F0) >> 4);
                        V[x] = (char)(V[x] ^ V[y]);
                        pc += 2;
                        break;
                    }
                    case 0x0004: { //8XY4	Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        char y = (char)((opcode & 0x00F0) >> 4);
                        if (V[y] > (0xFF - V[x])) // Overflow
                            V[0xF] = 0x1; //Carry
                        else
                            V[0xF] = 0x0;
                        V[x] += V[y];
                        pc += 2;
                        break;
                    }
                    case 0x0005: { //8XY5	VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        char y = (char)((opcode & 0x00F0) >> 4);
                        if (V[y] > V[x]) // Underflow
                            V[0xF] = 0x0; // borrow
                        else
                            V[0xF] = 0x1;
                        V[x] -= V[y];
                        pc += 2;
                        break;
                    }
                    case 0x0006: { //8XY6	Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        V[0xF] = (char)(V[x] & 0x1);
                        V[x] = (char)(V[x] >> 1);
                        pc += 2;
                        break;
                    }
                    case 0x0007: { //8XY7	Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        char y = (char)((opcode & 0x00F0) >> 4);
                        if (V[x] > V[y]) // Underflow
                            V[0xF] = 0x0; // borrow
                        else
                            V[0xF] = 0x1;
                        V[x] = (char)(V[y] - V[x]);
                        pc += 2;
                        break;
                    }
                    case 0x000E: { //8XYE	Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift.
                        char x = (char)((opcode & 0x0F00) >> 8);
                        V[0xF] = (char)(V[x] & 0x1);
                        V[x] = (char)(V[x] << 1);
                        pc += 2;
                        break;
                    }
                    default:
                        System.err.println("Unsupported opcode: " + Integer.toHexString(opcode));
                }
            case 0x9000: { //9XY0	Skips the next instruction if VX doesn't equal VY.
                char x = (char)((opcode & 0x0F00) >> 8);
                char y = (char)((opcode & 0x00F0) >> 4);
                if (V[x] == V[y])
                    pc += 2;
                else
                    pc += 4;
                break;
            }
            case 0xA000: { //ANNN	Sets I to the address NNN.
                I = (char)(opcode & 0x0FFF);
                pc += 2;
                break;
            }
            case 0xB000: { //BNNN	Jumps to the address NNN plus V0.
                pc = (char)(V[0x0] + (opcode & 0x0FFF));
                pc += 2;
                break;
            }
            case 0xC000: { // CXNN	Sets VX to a random number, masked by NN.
                char x = (char)((opcode & 0x0F00) >> 8);
                char nn = (char)(opcode & 0x00FF);
                V[x] = (char)(rnd.nextInt(0xFF) & nn);
                pc += 2;
                break;
            }
            case 0xD000: { // DXYN Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels.
                           // Sprites stored in memory at location in index register (I).
                           // Maximum 8bits wide.
                           // Wraps around the screen.
                           // If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero.
                           // All drawing is XOR drawing (i.e. it toggles the screen pixels)
                char x      = (char)((opcode & 0x0F00) >> 8);
                char y      = (char)((opcode & 0x00F0) >> 4);
                char height = (char)(opcode & 0x000F);

                V[0xF] = 0;
                for (int yline = 0; yline < height; yline++){
                    char p = memory[I + yline];
                    for(int xline = 0; xline < 8; xline++) {

                    }
                }
            }

            default:
                System.err.println("Unsupported opcode: " + Integer.toHexString(opcode));
        }
        // Decode opCode
        // Execute opCode

        if (needRedrawing){
            needRedrawing = false;
            ioHandler.onUpdateScreen();
        }


    }

    public void loadROM(String fileName){
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(fileName));
            for(int off = 0; dis.available() > 0 ;){
                memory[INIT_PADDING + off] = (char)(dis.readByte() & 0xFF);
                off ++;
            }
        } catch (IOException ex){
            ex.printStackTrace();
            System.exit(1);
        }finally {
            if (dis != null){
                try {
                    dis.close();
                }catch (Exception ex){}
            }
        }

    }

    private void printlnOpcode(char c){
        System.out.println("OPCODE: " + Integer.toHexString(c));
    }

    private void clearScreen(){
        for (int i = 0; i < lcd.length; i++){
            lcd[i] = 0x0;
        }
    }

    public interface IOHandler {
        void onClearScreen();
        void onUpdateScreen();
        void onEmitSound();
    }
}
