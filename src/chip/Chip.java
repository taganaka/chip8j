package chip;

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

    public Chip(){

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
    }

    public void run(){
        /**
         * Fetch opCode
         * Each opCode is 2-bytes long so we merge 2 item from mem array
         */
        final char opcode = (char)((memory[pc] << 8) | memory[pc + 1]);

        switch (opcode & 0xF000){ // opcommand is in in the first nibble
            case 0x8000: // Other data in the last nibble
                switch (opcode & 0x000F){

                    default:
                        System.err.println("Unsupported opcode: " + Integer.toHexString(opcode));
                }

                break;

            default:
                System.err.println("Unsupported opcode: " + Integer.toHexString(opcode));
        }
        // Decode opCode
        // Execute opCode
    }

    private void printlnOpcode(char c){
        System.out.println("OPCODE: " + Integer.toHexString(c));
    }
}
