package com.github.taganaka.chip8j.emu;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by francescolaurita on 7/7/15.
 */
public class RomChooserFrame extends JFrame{

    public RomChooserFrame(){
        setPreferredSize(new Dimension(0, 0));
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("./ROMS"));
        setResizable(false);
        setLayout(new BorderLayout());
        add(fileChooser, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height/2);

        toFront();
        int retval = fileChooser.showOpenDialog(this);
        if (retval == JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            final Main main = new Main(file.getAbsolutePath());
            main.start();
        }
        setVisible(false);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RomChooserFrame();
            }
        });

    }
}
