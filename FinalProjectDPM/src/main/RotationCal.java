package main;
import odometer.Odometer;
import data.DataCenter;
import drivers.HWConstants;
import drivers.Navigation;
import lejos.nxt.Button;
import lejos.nxt.LCD;

/**
 * The main class for test 1. Used to calibrate the radii of the wheels
 * and the distance between the wheels.
 * 
 * @author Andrei Purcarus
 *
 */
public class RotationCal {
	/**
	 * Main thread of execution of the robot.
	 */
	public static void main(String [] args) {
		//Wait for a button to start.
		LCD.drawString("Counter | Clockw", 0, 0);
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
			nav.turn(360);
			break;
		case Button.ID_RIGHT:
			nav.turn(-360);
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
