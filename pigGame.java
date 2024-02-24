/* Author: Jason Toft
 * Date: December 10, 2021
 * Description: The Pig dice game is a race to 100 points. Two players alternate turns and have the choice to roll again or bank their points after each roll. They must watch out for their chosen turn-ending roll, which causes them to lose all the points of their current turn.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Assign8PigGameGUI implements ActionListener{
	
	
	JTextField p1Name; // allows player 1 to enter name
	JTextField p2Name; // allows player 2 to enter name
	JTextField rollEnder; // allows player to enter the roll that ends their turn
	JTextField roll; // tells the users which number was rolled
	JTextField pointsThisTurn; // tells the users how many points they have in their current turn
	JTextField bankedPoints; // tells the users how many banked points they have
	JTextField totalPoints; // tells the users how many points they have total (sum of banked points and points in their current turn)
	JTextField gameNarrative; // provides the narrative of the game
	JButton rollAgain = new JButton("ROLL THE DIE!"); // button to roll the die
	JButton bankIt = new JButton("BANK IT!"); // button to bank the player's points
	JButton playAgain = new JButton("Play Again"); // button to play the pig game again
	JButton submit1 = new JButton("Submit"); // button to submit player 1 name
	JButton submit2 = new JButton("Submit"); // button to submit player 2 name
	JButton submitRollEnder = new JButton("Submit"); // button to submit the turn-ending roll
	JButton continueButton = new JButton("Continue"); // the continue button
	
	int p1bank = 0; // player 1 banked score
	int p2bank = 0; // player 2 banked score
	int turnEnder; // the role that will end the player's turn
	int turnScore = 0; // the sum of all rolls in a player's turn
	int currentPlayer = 1; // determines which player's turn it is
	
	boolean p1entered = false; // determines if player 1 has submitted their name
	boolean p2entered = false; // determines if player 2 has submitted their name
	boolean mustContinue = false; // forces the player to click the continue button
	boolean suddenDeath = false; // determines whether the players should be in sudden death mode
	boolean mustPlayAgain = false; // forces the player to click play again or close the window to exit
	

	public Assign8PigGameGUI(){
	
		// creates the frame
		JFrame frame = new JFrame("Pig Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(4,1));
		
		// panel for player names and turn-ending roll
		JPanel playerNamePane = new JPanel();
		playerNamePane.setLayout(new GridLayout(3,3));
		
		p1Name = new JTextField(20);
		p2Name = new JTextField(20);
		rollEnder = new JTextField(1);
		
		// adds all labels and text fields for input
		playerNamePane.add(new JLabel ("Player 1:"));
		playerNamePane.add(p1Name);
		p1Name.addActionListener(this);
		playerNamePane.add(submit1);
		submit1.addActionListener(this);
		
		playerNamePane.add(new JLabel ("Player 2:"));
		playerNamePane.add(p2Name);
		p2Name.addActionListener(this);
		playerNamePane.add(submit2);
		submit2.addActionListener(this);
		
		playerNamePane.add(new JLabel("Turn-Ending Roll:"));
		playerNamePane.add(rollEnder);
		rollEnder.addActionListener(this);
		playerNamePane.add(submitRollEnder);
		submitRollEnder.addActionListener(this);

		frame.add(playerNamePane);
	
		// creates new pane for information on the roll and player's points 
		JPanel rollPane = new JPanel();
		rollPane.setLayout(new GridLayout(2,4));
		
		// adds labels and uneditable text fields 
		roll = new JTextField(1);
		roll.setEditable(false);
		pointsThisTurn = new JTextField(3);
		pointsThisTurn.setEditable(false);
		bankedPoints = new JTextField(3);
		bankedPoints.setEditable(false);
		totalPoints = new JTextField(3);
		totalPoints.setEditable(false);
		
		rollPane.add(new JLabel("Roll:"));
		rollPane.add(new JLabel("Points This Turn:"));
		rollPane.add(new JLabel("Banked Points:"));
		rollPane.add(new JLabel("Total Points:"));
		rollPane.add(roll);
		rollPane.add(pointsThisTurn);
		rollPane.add(bankedPoints);
		rollPane.add(totalPoints);
		
		frame.add(rollPane);
		
		// creates pane for game narrative and roll/bank buttons
		JPanel rollOrBankPane = new JPanel();
        rollOrBankPane.setLayout(new GridLayout(2,2));
        
        gameNarrative = new JTextField(100);
		gameNarrative.setEditable(false);
		gameNarrative.setText("Enter your names and click submit to start.");
		
		// adds game narrative, roll button and bank button
		rollOrBankPane.add(new JLabel("Game Narrative"));
		rollOrBankPane.add(rollAgain);
		rollAgain.addActionListener(this);
		rollOrBankPane.add(gameNarrative);
		rollOrBankPane.add(bankIt);
		bankIt.addActionListener(this);
		
		frame.add(rollOrBankPane);
		
		// creates the pane for the play again and continue buttons
		JPanel playAgainContinuePane = new JPanel();
		playAgainContinuePane.setLayout(new GridLayout(1,2));
		
        playAgainContinuePane.add(playAgain);
		playAgain.addActionListener(this);
		playAgainContinuePane.add(continueButton);
		continueButton.addActionListener(this);

        frame.add(playAgainContinuePane);
        frame.pack();
        frame.setVisible(true);
        
	}
	
	/*
	 * Method Name: dieRoll
	 * Author: Jason Toft
	 * Creation date: November 22, 2021
	 * Modified date: November 22, 2021
	 * Description: Generates the results of a die roll.
	 * Parameters: Lowest possible roll and number of sides on the die. 
	 * Return value: The rolled number 
	 * Data type: Integer
	 * Dependencies: n/a
	 * Throws/Exceptions: n/a
	 */
	public static int dieRoll(int low, int sideCount){
		int number = (int)(Math.random()*(sideCount - low) + low); // random number for die roll
		return number;
	}
	
	/*
	 * Method Name: pigTurn
	 * Author: Jason Toft
	 * Creation date: December 9, 2021
	 * Modified date: December 10, 2021
	 * Description: Executes one turn of the pig game. The die is rolled and added to the turn score until the player banks it or rolls their chosen turn-ending roll, causing the loss of the score from the current turn. It is then determined if the game should go to sudden death.
	 * Parameters: n/a.
	 * Return value: n/a
	 * Data type: n/a
	 * Dependencies: javax.swing.* and java.awt.*
	 * Throws/Exceptions: n/a
	 */
	public void pigTurn() {
		String playerName; // name of the player
		int bankScore; // banked score of the player
		
		// sets appropriate player name
		if (currentPlayer == 1) {
			playerName = p1Name.getText();
			bankScore = p1bank;
		} 
		else {
			playerName = p2Name.getText();
			bankScore = p2bank;
		}
		
		// rolls die
		int numberRolled = dieRoll(1,7);
		roll.setText(String.valueOf(numberRolled));
		
		if (numberRolled == turnEnder) {
			
			// ends turn on roll of turn-ending roll
			totalPoints.setText(String.valueOf(bankScore));
			mustContinue = true;
			gameNarrative.setText("Oh no! " + playerName + " rolled a " + numberRolled + "! No points are added from this turn. Press continue. ");
		} 
		
		else {
			// player wins if they have reached 100 points
			turnScore += numberRolled;
			if (turnScore + bankScore >= 100) {
				if (currentPlayer == 1) {
					gameNarrative.setText(playerName + ", you reached 100 points! Your opponent must now reach 100! Press continue.");
					mustContinue = true;
				}
				
				// Sudden death if both players have reached 100 points
				if (currentPlayer == 2) {
					if (p1bank >= 100) {
						gameNarrative.setText("Both players have reached 100 points! It is sudden death time! Both player's scores are reset to 0 and whoever gets the most points wins! Press continue.");
						p1bank = 0;
						p2bank = 0;
						suddenDeath = true;
						mustContinue = true;
					}
					else {
						// if only one player has reached 100 points, that player wins
						gameNarrative.setText(playerName + ", wins!!!!! Congratulations. Press play again if you so desire.");
						mustPlayAgain = true;
					}
				}
			} 
			else {
				// no player has won so turn continues
				pointsThisTurn.setText(String.valueOf(turnScore));
				totalPoints.setText(String.valueOf(turnScore + bankScore));
				gameNarrative.setText(playerName + ", roll again or bank your points!");
			}
		}
	}
	
	/*
	 * Method Name: actionPerformed
	 * Author: Jason Toft
	 * Creation date: December 8, 2021
	 * Modified date: December 10, 2021
	 * Description: Controls all inputs to the pig game. Player entries of names, turn-ending rolls and sets values upon button presses. It runs sudden death and indicates which player won the game. 
	 * Parameters: n/a.
	 * Return value: n/a
	 * Data type: n/a
	 * Dependencies: javax.swing.* and java.awt.* and java.awt.event.*
	 * Throws/Exceptions: n/a
	 */
	public void actionPerformed(ActionEvent e)
	{
		boolean namesEntered = p1entered && p2entered; // checks to see if both player names have been submitted
	
		if (mustPlayAgain) {
			if (e.getSource() == playAgain) {
				// resets the game if the user selects play again button
				gameNarrative.setText(p1Name.getText() + ", please enter your turn-ending roll and click submit.");
				currentPlayer = 1;
				p1bank = 0;
				p2bank = 0;
				bankedPoints.setText(String.valueOf(0));
				rollEnder.setText("");
				rollEnder.setEditable(true);
				totalPoints.setText("0");
				roll.setText("");
				pointsThisTurn.setText("0");
	
			}
		} 
		else if (mustContinue) {
			if (e.getSource() == continueButton) {
				// no longer must press continue button and information is reset when continue button is pressed
				mustContinue = false;
				roll.setText("");
				pointsThisTurn.setText("0");
				turnScore = 0;
				rollEnder.setEditable(true);
				
				if (currentPlayer == 1) {
					// current player, their points and narrative are adjusted
					currentPlayer = 2;
					bankedPoints.setText(String.valueOf(p2bank));
					totalPoints.setText(String.valueOf(p2bank));
					gameNarrative.setText(p2Name.getText() + ", please enter your turn-ending roll and click submit.");
				}
				else {
					// checks for a win in or out of sudden death
					if (p1bank < 100 && p2bank >= 100 || (suddenDeath && p1bank > p2bank)) {
						gameNarrative.setText(p2Name.getText() + ", wins!!!!! Congratulations. Press play again if you so desire.");
						mustPlayAgain = true;
					} 
					else if (p1bank >= 100 && p2bank < 100 || (suddenDeath && p2bank > p1bank)) {
						gameNarrative.setText(p1Name.getText() + ", wins!!!!! Congratulations. Press play again if you so desire.");
						mustPlayAgain = true;
						
					} 
					else if (suddenDeath && p1bank == p2bank) {
						// both players have same score in sudden death so banked points are set to 0
						gameNarrative.setText("WOAH! Both players have the same score! Sudden death time again! You know the drill. Both players' scores are set to 0. Press continue.");
						mustContinue = true;
						p1bank = 0;
						p2bank = 0;
					}
					// current player, their points and narrative are adjusted
					currentPlayer = 1;
					bankedPoints.setText(String.valueOf(p1bank));
					totalPoints.setText(String.valueOf(p1bank));
					gameNarrative.setText(p1Name.getText() + ", please enter your turn-ending roll and click submit.");
					
					
				}
				
			}
		}
		
		else if (namesEntered) {
			if (e.getSource() == submitRollEnder) {
				// runs the turn and sets the turn-ending roll if the turn-ending roll has been submitted
				rollEnder.setText(rollEnder.getText().trim());
				rollEnder.setEditable(false);	
				turnEnder = Integer.parseInt(rollEnder.getText());
				pigTurn();
				
			} 
			
			else if (e.getSource() == rollAgain) {
				// player rolls again 
				pigTurn();
			}
			
			else if (e.getSource() == bankIt) {
				// player banks points and must continue
				mustContinue = true;
				gameNarrative.setText("Ok! You have decided to bank your points! Press continue.");
				
				// sets banked score to that of the appropriate player
				if (currentPlayer == 1) {
					p1bank += turnScore;
					bankedPoints.setText(String.valueOf(p1bank));
				} 
				
				else {
					p2bank += turnScore;
					bankedPoints.setText(String.valueOf(p2bank));
				}
			}
			
		} 
		
		else {
			
			if (e.getSource() == submit1){
				// sets player 1 name when submit button is pressed and adjusts narrative if both name submit buttons have been pressed
				p1Name.setText(p1Name.getText().trim());
				p1Name.setEditable(false);
				submit1.setVisible(false);
				p1entered = true;
				if (p2entered) {
					gameNarrative.setText(p1Name.getText() + ", please enter your turn-ending roll and click submit.");
					currentPlayer = 1;
					p1bank = 0;
					p2bank = 0;
					bankedPoints.setText(String.valueOf(0));
				}
				
			}
			
			if (e.getSource() == submit2){
				// sets player 2 name when submit button is pressed and adjusts narrative if both name submit buttons have been pressed
				p2Name.setText(p2Name.getText().trim());
				p2Name.setEditable(false);
				submit2.setVisible(false);
				p2entered = true;
				if (p1entered) {
					gameNarrative.setText(p1Name.getText() + ", please enter your turn-ending roll and click submit.");
					currentPlayer = 1;
					p1bank = 0;
					p2bank = 0;
					bankedPoints.setText(String.valueOf(0));
				}
			}
		}
	}
	
	
	
	public static void main(String[] args) {

		new Assign8PigGameGUI(); // runs the constructor
		
	} // end main

} // end class
