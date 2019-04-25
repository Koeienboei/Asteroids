/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asteroidsclientsimulator;

import controller.MainFrame;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tom
 */
public class AsteroidsClientSimulator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LinkedList<Integer> amountClientsOverTime = new LinkedList<>();
        File text = new File("input.txt");

        Scanner scnr;
        try {
            scnr = new Scanner(text);
            while (scnr.hasNextLine()) {
                amountClientsOverTime.add(Integer.parseInt(scnr.nextLine()));
            }
        } catch (FileNotFoundException | NumberFormatException ex) {
        }

        MainFrame mainFrame = new MainFrame(amountClientsOverTime);
        Thread mainFrameThread = new Thread(mainFrame);
        mainFrameThread.start();
    }

}
