package main;
import odometer.Odometer;
import data.DataCenter;
import drivers.CSPoller;
import drivers.Navigation;
import util.Point;
import lejos.nxt.Button;
import localization.CSLocalizer;

/**
 * The main class. Initializes the threads of execution
 * and starts them.
 * 
 * @author Andrei Purcarus
 * @author Leotard Niyonkuru
 */
public class CSL {

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
		final Odometer odo = new Odometer(dc);
		final Navigation nav = new Navigation(dc);
		final CSPoller cs = new CSPoller(dc);
		final CSLocalizer ll = new CSLocalizer(dc, nav);

		//Starts the threads.
		odo.start();
		cs.start();

		(new Thread() {
			public void run() {
				//Turns to 45 degrees for the light localization.
				nav.turnTo(45);
				ll.doLocalization();
				nav.travelTo(new Point(0, 0), false);
				nav.turnTo(90);
				System.exit(0);
			}
		}).start();
		
		//Wait for another button press to exit.
		Button.waitForAnyPress();
		System.exit(0);
	}
}
