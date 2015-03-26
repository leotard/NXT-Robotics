package main;
import data.DataCenter;
import drivers.CSPoller;
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
public class ColorSensor {

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

		//Initializes the threads.
		final DataCenter dc = new DataCenter();
		final CSPoller cs = new CSPoller(dc);

		cs.start();
		
		HWConstants.LEFT_MOTOR.flt();
		HWConstants.RIGHT_MOTOR.flt();

		(new Thread() {
			public void run() {
				while (true) {
					LCD.clear();
					LCD.drawInt(dc.getCSValue(), 8, 0);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		//Wait for another button press to exit.
		Button.waitForAnyPress();
		System.exit(0);
	}
}
