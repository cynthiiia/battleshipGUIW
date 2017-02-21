package battleshipgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class BattleshipGUI {

    private static String[] shipNames = {"Carrier", "Battleship", "Submarine", "Destroyer", "Patrol Boat"};
    private static String ships = "CBSDP", letters = "ABCDEFGHIJ";
    private static String[] turn = {"PLAYER", "CPU"};
    private static String[][] playerArray = new String[10][10];
    private static String[][] computerArray = new String[10][10];
    private static int[] cpuHitPoints = {5, 4, 3, 3, 2};
    private static int[] playerHitPoints = {5, 4, 3, 3, 2};
    private static int[] universalHitPoints = {5, 4, 3, 3, 2};
    private static int plCounter = 0, cpuCounter = 0;
    private static JFrame frame = new JFrame("Battleship Game");
    private static JPanel rowPanel = new JPanel(), columnPanel = new JPanel(), rowPanel2 = new JPanel(); //Panels for images
    private static JPanel playerPanel = new JPanel(new GridLayout(10, 10)), computerPanel = new JPanel(new GridLayout(10, 10)); //Panels for buttons
    private static JPanel /*main panel*/ panel = new JPanel(new BorderLayout()), centerPanel = new JPanel(new BorderLayout()), playerMessagePanel = new JPanel(new BorderLayout()), computerMessagePanel = new JPanel(new BorderLayout()), messagePanel = new JPanel(new BorderLayout());
    private static JFileChooser fileChooser = new JFileChooser();
    private static JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_T), restartItem = new JMenuItem("Restart Game", KeyEvent.VK_U), exitItem = new JMenuItem("Exit", KeyEvent.VK_V);
    private static JButton[][] player = new JButton[10][10];
    private static JButton[][] computer = new JButton[10][10];
    private static JLabel playerMessage = new JLabel("Please open the PLAYER.txt file!"), computerMessage = new JLabel("Please open the CPU.txt file!"), playerMessage2 = new JLabel(" "), computerMessage2 = new JLabel(" ");
    private static File file = new File("");

    //Method to set buttons enabled and the colour
    public static void enable(boolean set) {

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                player[x][y].setEnabled(set);
                player[x][y].setForeground(Color.BLUE);
                computer[x][y].setEnabled(set);
                computer[x][y].setForeground(Color.RED);
            }
        }
    }
    //Format buttons and add to panel
    public static void setButtons(JButton[][] player, JPanel panel) {

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                player[x][y] = new JButton();
                panel.add(player[x][y]);
                player[x][y].setPreferredSize(new Dimension(50, 50));
                player[x][y].setActionCommand("");
                player[x][y].addActionListener(new ButtonClick());
                player[x][y].setEnabled(false);
            }
        }
    }
    //Format the messages at the bottom and add to panel 
    public static void formatMessage(JLabel message, JPanel panel, String direction) {

        Color colour = message.equals(playerMessage) || message.equals(playerMessage2) ? Color.BLUE : Color.RED; //If it is a player's message, set to blue, if it's computer, then set to red
        message.setForeground(colour);
        panel.add(message, direction);
    }
    //Loading the CPU and PLAYER files 
    public static void loadFile(String file, String[][] array, JButton[][] button, JLabel message) throws IOException {
        
        BufferedReader inputStream = new BufferedReader(new FileReader(file));
        for (int row = 0; row < array.length; row++) {
            String read = inputStream.readLine();
            for (int col = 0; col < array[row].length; col++) {
                array[row][col] = Character.toString(read.charAt(col * 2));
                button[row][col].setText(Arrays.equals(button, player) ? array[row][col] : "*"); //load only the computer file as "*" on buttons 
            }
        }
        message.setText("File Loaded!");
    }
    //Check whether button pressed by user or computer is a hit or miss
    public static void checkHit(String[][] array, int x, int y, JButton[][] player, int[] health, JLabel message2, int counter) {

        message2.setText(" "); //set second message to nothing again 
        if (array[x][y].equals("*")) {
            player[x][y].setText("");
            player[x][y].setIcon(new ImageIcon(".\\src\\P2\\images\\M.JPG"));
        } else {
            //Determining which ship was hit and if any sunk
            for (int i = 0; i < 5; i++) {
                if (String.valueOf(ships.charAt(i)).equals(array[x][y]))
                    health[i] -= 1;
                if (health[i] == 0) {
                    message2.setText(Arrays.equals(player, computer) ? "You have sunk the computer's " + shipNames[i] + "!" : "The computer has sunk your " + shipNames[i] + "!");
                    health[i] = 5;
                }
            }
            player[x][y].setText(""); //clear button's text
            player[x][y].setIcon(new ImageIcon(".\\src\\P2\\images\\H.JPG"));
            if (Arrays.equals(player, computer))
                plCounter++;
            else
                cpuCounter++;
        }
    }

    public static void restartGame(JButton[][] player, JLabel message, JLabel message2, int[] hitPoints, int counter, String turn) {

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                player[x][y].setText("");
                player[x][y].setIcon(new ImageIcon(""));
                player[x][y].setEnabled(false);
            }
        }
        System.arraycopy(universalHitPoints, 0, hitPoints, 0, 5); //Restart the ship's health points
        plCounter = 0;
        cpuCounter = 0;
        message.setText("Please open the " + turn + ".txt file!");
        message2.setText(" ");
    }

    public static void main(String[] args) throws IOException {

        //Creating the Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); //for keyboard shortcuts
        restartItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        menu.add(openItem);
        menu.add(restartItem);
        menu.addSeparator();
        menu.add(exitItem);
        frame.setJMenuBar(menuBar);
        //Menu Item actions 
        openItem.addActionListener(new FileOptions());
        openItem.setActionCommand("openButton");
        exitItem.addActionListener(new FileOptions());
        exitItem.setActionCommand("exitButton");
        restartItem.addActionListener(new FileOptions());
        restartItem.setActionCommand("restartButton");
        
        //Added rows and col images to panels
        rowPanel.add(new JLabel(new ImageIcon(".\\src\\P2\\images\\rows.png")));
        columnPanel.add(new JLabel(new ImageIcon(".\\src\\P2\\images\\cols.png")));
        rowPanel2.add(new JLabel(new ImageIcon(".\\src\\P2\\images\\rows.png")));
        //Adding panels containing row/col images to main panel
        panel.add(rowPanel, BorderLayout.LINE_START);
        panel.add(columnPanel, BorderLayout.NORTH);
        
        //Setting the buttons
        setButtons(player, playerPanel);
        setButtons(computer, computerPanel);
        //Creating borders for gameboard
        computerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 20)));
        playerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        rowPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        rowPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
        //Creating borders for message panels
        Font font = new Font("Sans_Serif", Font.BOLD, 12);
        playerMessagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Player Messages", TitledBorder.LEFT, TitledBorder.TOP, font, Color.BLUE));
        computerMessagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Computer Messages", TitledBorder.LEFT, TitledBorder.TOP, font, Color.RED));
        //Formatting player/computer message 
        formatMessage(playerMessage, playerMessagePanel, BorderLayout.WEST);
        formatMessage(playerMessage2, playerMessagePanel, BorderLayout.SOUTH);
        formatMessage(computerMessage, computerMessagePanel, BorderLayout.WEST);
        formatMessage(computerMessage2, computerMessagePanel, BorderLayout.SOUTH);
        messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //Stacking Panels together
        centerPanel.add(playerPanel, BorderLayout.WEST);
        centerPanel.add(rowPanel2, BorderLayout.CENTER);
        centerPanel.add(computerPanel, BorderLayout.EAST);
        messagePanel.add(playerMessagePanel, BorderLayout.NORTH);
        messagePanel.add(computerMessagePanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(messagePanel, BorderLayout.SOUTH);

        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }
    //Action Listener for when player clicks on a button
    public static class ButtonClick implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    if (event.getSource().equals(computer[x][y])) {
                        if (computer[x][y].getText().equals("")) { //If place user chooses to attack is already choosen, nothing happens
                        } else {
                            boolean rndm = true;
                            while (rndm) { //Loops until coordinates generated to attack is valid
                                Random rn = new Random();
                                int xx = rn.nextInt(10), yy = rn.nextInt(10);
                                rndm = (player[xx][yy].getText().equals("")) ? true : false;
                                if (rndm == false) { //once coordinates valid, computer and player and computer attacks are shown
                                    playerMessage.setText(computerArray[x][y].equals("*") ? "You have missed sir!" : "Direct hit, nice shot sir!");
                                    checkHit(computerArray, x, y, computer, cpuHitPoints, playerMessage2, plCounter);
                                    computerMessage.setText(player[xx][yy].getText().equals("*") ? "The computer has attacked " + letters.charAt(xx) + yy + " and missed!" : "The computer has attacked " + letters.charAt(xx) + yy + " and hit your " + shipNames[ships.indexOf(player[xx][yy].getText())] + "!");
                                    checkHit(playerArray, xx, yy, player, playerHitPoints, computerMessage2, cpuCounter);
                                }
                            }
                        }
                    }
                }
            }
            if (cpuCounter == 17 || plCounter == 17) {
                JOptionPane.showMessageDialog(frame, plCounter == 17 ? "The player has won the game!" : "The computer has won the game!"); //Winner statement
                enable(false);
            }
        }
    }
    //Action Listener for the menu items; Load files, Restart Game, and exit program
    public static class FileOptions implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            if (event.getActionCommand().equals("openButton")) {
                if (event.getSource() == openItem) {
                    int returnVal = fileChooser.showOpenDialog(frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = fileChooser.getSelectedFile();
                    }
                }
                try {
                    if (file.getName().equals("CPU.txt")) {
                        loadFile(".\\src\\P2\\CPU.TXT", computerArray, computer, computerMessage);
                        enable(true); //calls method to enable the buttons once cpu.txt loaded
                    } else if (file.getName().equals("PLAYER.txt")) {
                        loadFile(".\\src\\P2\\PLAYER.TXT", playerArray, player, playerMessage);
                    }
                } catch (IOException e) {
                }
            }
            if (event.getActionCommand().equals("restartButton")) {
                restartGame(player, playerMessage, playerMessage2, playerHitPoints, plCounter, turn[0]);
                restartGame(computer, computerMessage, computerMessage2, cpuHitPoints, cpuCounter, turn[1]);
            }
            if (event.getActionCommand().equals("exitButton")) {
                System.exit(0); //Terminates program
            }
        }
    }
}