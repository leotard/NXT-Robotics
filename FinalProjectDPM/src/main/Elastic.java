package main;
import launcher.Launcher;
import drivers.HWConstants;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * The main class. Initializes the threads of execution
 * and starts them.
 * 
 * @author Andrei Purcarus
 * @author Leotard Niyonkuru
 */
public class Elastic {

	/**
	 * Main thread of execution of the robot. Starts all other threads.
	 */
	public static void main(String [] args) {
		//Wait for a button to start.
		int buttonChoice = Button.waitForAnyPress();
		switch (buttonChoice) {
		case Button.ID_ENTER: case Button.ID_LEFT: case Button.ID_RIGHT:
			break;
		case Button.ID_ESCAPE:
			return;
		default:
			throw new RuntimeException("Impossible button press.");
		}

		final Launcher launcher = new Launcher(HWConstants.LAUNCHER_MOTOR);
		
		(new Thread() {
			public void run() {
				int count = 0;
				while (true) {
					launcher.fire();
					++count;
					LCD.clear();
					LCD.drawInt(count, 8, 0);
				}
			}
		}).start();
		
		//Wait for another button press to exit.
		Button.waitForAnyPress();
		System.exit(0);
	}
}
