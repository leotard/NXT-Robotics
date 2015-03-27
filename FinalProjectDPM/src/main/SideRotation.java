package main;
import odometer.Odometer;
import data.DataCenter;
import drivers.Navigation;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * The main class. Initializes the threads of execution
 * and starts them.
 * 
 * @author Andrei Purcarus
 * @author Leotard Niyonkuru
 */
public class SideRotation {

	/**
	 * Main thread of execution of the robot. Starts all other threads.
	 */
	public static void main(String [] args) {
		//Wait for a button to start.
		LCD.clear();
		LCD.drawString("right=right", 0, 0);
		LCD.drawString("left=left", 0, 0);
		int buttonChoice;
		do {
			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice == Button.ID_ENTER);

		final DataCenter dc = new DataCenter();
		final Odometer odo = new Odometer(dc);
		final Navigation nav = new Navigation(dc);

		odo.start();

		switch (buttonChoice) {
		case Button.ID_LEFT:
			for (int i = 0; i < 4; ++i)
				nav.turnLeft();
			break;
		case Button.ID_RIGHT:
			for (int i = 0; i < 4; ++i)
				nav.turnRight();
			break;
		case Button.ID_ESCAPE:
			System.exit(0);
		default:
			throw new RuntimeException("Impossible button press.");
		}

		//Wait for another button press to exit.
		Button.waitForAnyPress();
		System.exit(0);
	}
}
