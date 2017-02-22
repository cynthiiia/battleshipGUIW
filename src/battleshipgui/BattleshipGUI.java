package battleshipgui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;


/// <summary>
/// The main class containing all data and methods required to generate the game Battleships for player and CPU
/// </summary>

public class BattleshipGUI {

	//declare names for each ship
    private static String[] shipNames = {"Carrier", "Battleship", "Submarine", "Destroyer", "Patrol Boat"};
    private static String ships = "CBSDP", letters = "ABCDEFGHIJ";
    //store string for which player's turn
	private static String[] turn = {"PLAYER", "CPU"};
	//array for each player's 10x10 board
    private static String[][] playerArray = new String[10][10];
    private static String[][] computerArray = new String[10][10];
	//declare hitpoints for each player's ship
    private static int[] cpuHitPoints = {5, 4, 3, 3, 2};
    private static int[] playerHitPoints = {5, 4, 3, 3, 2};
    private static int[] universalHitPoints = {5, 4, 3, 3, 2};
	//counter for each player's score. If either has 17, all opposing ships of that player has been sunked
    private static int plCounter = 0, cpuCounter = 0;
	//new window Battleship Game
    private static JFrame frame = new JFrame("Battleship Game");
	//create panels for images
    private static JPanel rowPanel = new JPanel(), columnPanel = new JPanel(), rowPanel2 = new JPanel();
	//create panels for buttons
    private static JPanel playerPanel = new JPanel(new GridLayout(10, 10)), computerPanel = new JPanel(new GridLayout(10, 10));
	//create main panel for game
    private static JPanel panel = new JPanel(new BorderLayout()), centerPanel = new JPanel(new BorderLayout()), playerMessagePanel = new JPanel(new BorderLayout()), computerMessagePanel = new JPanel(new BorderLayout()), messagePanel = new JPanel(new BorderLayout());
    private static JFileChooser fileChooser = new JFileChooser();
	//create menu with open, restart, exit, while taking input
    private static JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_T), restartItem = new JMenuItem("Restart Game", KeyEvent.VK_U), exitItem = new JMenuItem("Exit", KeyEvent.VK_V);
    private static JButton[][] player = new JButton[10][10];
    private static JButton[][] computer = new JButton[10][10];
	//player message to open PLAYER.txt and CPU.txt
    private static JLabel playerMessage = new JLabel("Please open the PLAYER.txt file!"), computerMessage = new JLabel("Please open the CPU.txt file!"), playerMessage2 = new JLabel(" "), computerMessage2 = new JLabel(" ");
    private static File file = new File("");

	/**
	* This method sets the buttons to true or false depending on set, and sets their colour
	* @param set The boolean operator to set the buttons to
	*/
    public static void enable(boolean set) {
		//for each tile in 10x10 grid
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
	/**
	* This method formats the grid (buttons) and adds it to panel
	* @param player The player setButtons is initializing the game grid for
	* @param panel The 10x10 game grid that is being initialized with clickable buttons
	*/
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
	/** 
	* Format the message at bottom of screen depending on if player or computer's turn, then add it to panel
	* @param message The message to be displayed
	* @param panel The main game panel
	* @param direction The direction on panel message is added to
	*/
    public static void formatMessage(JLabel message, JPanel panel, String direction) {
		//set to blue if player, or red if computer
        Color colour = message.equals(playerMessage) || message.equals(playerMessage2) ? Color.BLUE : Color.RED;
        message.setForeground(colour);
		//add to panel
        panel.add(message, direction);
    }
	
    //Loading the CPU and PLAYER files 
	/**
	* Load the CPU and Player txt files containing initial board state, then stores into 10x10 array for each
	* @param file The filename to be read
	* @param array The array to store initial board state for that player
	* @param button The button where the initial states are stored
	* @param message the JLabel used to store the message "File Loaded" after loading
	*/
    public static void loadFile(String file, String[][] array, JButton[][] button, JLabel message) throws IOException {
        //new bufferedReader
        BufferedReader inputStream = new BufferedReader(new FileReader(file));
		//for each row in textfile, read line
        for (int row = 0; row < array.length; row++) {
            String read = inputStream.readLine();
			//for each column in row, store the value to string and button
            for (int col = 0; col < array[row].length; col++) {
                array[row][col] = Character.toString(read.charAt(col * 2));
				 //load only the computer file as "*" on buttons 
                button[row][col].setText(Arrays.equals(button, player) ? array[row][col] : "*");
            }
        }
        message.setText("File Loaded!");
    }
	/**
	* Check whether button pressed by user or computer on their turn hit or missed 
	* @param array The array storing the ship locations on grid
	* @param x the x coordinate of the button clicked
	* @param y the y coordinate of the button clicked
	* @param player The player whom is being attacked
	* @param health the array containing the current health of all the player's ships
	* @param message2 the jlabel for displaying message to screen
	* @param counter not used
	*/
    public static void checkHit(String[][] array, int x, int y, JButton[][] player, int[] health, JLabel message2, int counter) {
		//set second message to nothing again 
        message2.setText(" "); 
		//if miss, set * to null, to signify missed shot, and set icon to missed shot
        if (array[x][y].equals("*")) {
            player[x][y].setText("");
            player[x][y].setIcon(new ImageIcon(".\\src\\P2\\images\\M.JPG"));
        } else { //else determine which ship was hit and if any sunk
            for (int i = 0; i < 5; i++) {
                if (String.valueOf(ships.charAt(i)).equals(array[x][y])) //if hit, health is reduced by 1
                    health[i] -= 1;
                if (health[i] == 0) { //if health is 0, ship is sunked
                    message2.setText(Arrays.equals(player, computer) ? "You have sunk the computer's " + shipNames[i] + "!" : "The computer has sunk your " + shipNames[i] + "!");
                    health[i] = 5;
                }
            }
			//clear button's text
            player[x][y].setText(""); 
			//set button icon to ship hit 
            player[x][y].setIcon(new ImageIcon(".\\src\\P2\\images\\H.JPG"));
			//update score
            if (Arrays.equals(player, computer))
                plCounter++;
            else
                cpuCounter++;
        }
    }
	/**
	* This method is used to restart the game
	* @param player The 10x10 array containing the player's grid of buttons
	* @param message The JLabel containing the label for displaying a message
	* @param message The 2nd JLabel containing the label for displaying a different message
	* @param hitpoints The array containing the current HP of that player
	* @param counter The counter that keeps track of a player's score
	* @param turn The current player's turn
	*/
    public static void restartGame(JButton[][] player, JLabel message, JLabel message2, int[] hitPoints, int counter, String turn) {
		//for each in 10x10 grid
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                player[x][y].setText(""); //set to unhit
                player[x][y].setIcon(new ImageIcon("")); //set to no image icon
                player[x][y].setEnabled(false); //set button to false
            }
        }
        System.arraycopy(universalHitPoints, 0, hitPoints, 0, 5); //Reset the ship's health points
        plCounter = 0; //initialize player score to 0
        cpuCounter = 0; //initialize computer's score to 0
        message.setText("Please open the " + turn + ".txt file!"); //prompts user to open text file
        message2.setText(" "); //set initial text for message 2 to null
    }

	/**
	* The main executable for game. \n
	* This game allows a player to face of against a CPU in battleship, by taking turns clicking on different grid points,
	* where each grid point is a clickable button. \n
	* A hit or miss will be displayed visually on the button clicked \n
	* The game will end when one player succesfully shoots down all of the enemy player's ships (reach a score of 17)
	*/
    public static void main(String[] args) throws IOException {
        //setup the Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
		//add menu bar 
        menuBar.add(menu);
		//set keyboard shortcuts
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); 
        restartItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		//add openItem, restartItem, exitItem to menu
        menu.add(openItem);
        menu.add(restartItem);
        menu.addSeparator();
        menu.add(exitItem);
		//set menuBar
        frame.setJMenuBar(menuBar);
        //Menu Item actions 
        openItem.addActionListener(new FileOptions());
        openItem.setActionCommand("openButton");
        exitItem.addActionListener(new FileOptions());
        exitItem.setActionCommand("exitButton");
        restartItem.addActionListener(new FileOptions());
        restartItem.setActionCommand("restartButton");
        
        //Added rows and column images to panels
        rowPanel.add(new JLabel(new ImageIcon(".\\src\\P2\\images\\rows.png")));
        columnPanel.add(new JLabel(new ImageIcon(".\\src\\P2\\images\\cols.png")));
        rowPanel2.add(new JLabel(new ImageIcon(".\\src\\P2\\images\\rows.png")));
        //Added panels containing row/col images to main panel
        panel.add(rowPanel, BorderLayout.LINE_START);
        panel.add(columnPanel, BorderLayout.NORTH);
        
        //call setbuttons to intialize all buttons
        setButtons(player, playerPanel);
        setButtons(computer, computerPanel);
		
        //Create borders for gameboard
        computerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 20)));
        playerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        rowPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        rowPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
		
        //Create borders for message panels
        Font font = new Font("Sans_Serif", Font.BOLD, 12);
        playerMessagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Player Messages", TitledBorder.LEFT, TitledBorder.TOP, font, Color.BLUE));
        computerMessagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Computer Messages", TitledBorder.LEFT, TitledBorder.TOP, font, Color.RED));
		
        //format player/computer message 
        formatMessage(playerMessage, playerMessagePanel, BorderLayout.WEST);
        formatMessage(playerMessage2, playerMessagePanel, BorderLayout.SOUTH);
        formatMessage(computerMessage, computerMessagePanel, BorderLayout.WEST);
        formatMessage(computerMessage2, computerMessagePanel, BorderLayout.SOUTH);
        messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
        //Stack Panels together to form game screen
        centerPanel.add(playerPanel, BorderLayout.WEST);
        centerPanel.add(rowPanel2, BorderLayout.CENTER);
        centerPanel.add(computerPanel, BorderLayout.EAST);
        messagePanel.add(playerMessagePanel, BorderLayout.NORTH);
        messagePanel.add(computerMessagePanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(messagePanel, BorderLayout.SOUTH);

		//set oabek abd franes to visible
        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
    }
    /// <summary>
	/// This class handles actionLister events for game grid button clicks
	/// </summary>
    public static class ButtonClick implements ActionListener {
		/**
		* This method extends takes user input, recognize what was clicked then
		* and perform the correct action
		* @param event The buttonclick ActionEvent
		*/
        public void actionPerformed(ActionEvent event) {

            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    if (event.getSource().equals(computer[x][y])) {
						//If place user chooses to attack is already choosen, nothing happens
                        if (computer[x][y].getText().equals("")) { 
                        } else { //otherwise
                            boolean rndm = true;
							//Loops until coordinates generated to attack is valid
                            while (rndm) { 
                                Random rn = new Random();
                                int xx = rn.nextInt(10), yy = rn.nextInt(10);
                                rndm = (player[xx][yy].getText().equals("")) ? true : false;
								//once coordinates valid, computer and player and computer attacks are shown
                                if (rndm == false) { 
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
			//if either player reaches 17 score, then all opposing ships have been shot down and they win
            if (cpuCounter == 17 || plCounter == 17) {
                JOptionPane.showMessageDialog(frame, plCounter == 17 ? "The player has won the game!" : "The computer has won the game!");
                enable(false);
            }
        }
    }
	/// <summary>
	/// This class handles ActionListener for load files, restart game and exit program
	/// </summary>
    public static class FileOptions implements ActionListener {
		/**
		* This method handles the user's selection of load files, restart game, and exit program
		* @param event The action performed by user
		*/
        public void actionPerformed(ActionEvent event) {
			//if open button is clicked
            if (event.getActionCommand().equals("openButton")) {
                if (event.getSource() == openItem) {
                    int returnVal = fileChooser.showOpenDialog(frame);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = fileChooser.getSelectedFile();
                    }
                }
                try { //attempts to open CPU txt file
                    if (file.getName().equals("CPU.txt")) {
                        loadFile(".\\src\\P2\\CPU.TXT", computerArray, computer, computerMessage);
                        enable(true); //calls method to enable the buttons once cpu.txt loaded
						//attemps to open player txt file
                    } else if (file.getName().equals("PLAYER.txt")) { 
                        loadFile(".\\src\\P2\\PLAYER.TXT", playerArray, player, playerMessage);
                    }
                } catch (IOException e) {
                }
            }
			//if restart button was clicked
            if (event.getActionCommand().equals("restartButton")) {
                restartGame(player, playerMessage, playerMessage2, playerHitPoints, plCounter, turn[0]);
                restartGame(computer, computerMessage, computerMessage2, cpuHitPoints, cpuCounter, turn[1]);
            }
			//if exit button was clicked
            if (event.getActionCommand().equals("exitButton")) {
                System.exit(0); //Terminates program
            }
        }
    }
}