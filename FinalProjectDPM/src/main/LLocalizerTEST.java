package main;
import launcher.Launcher;
import odometer.Odometer;
import odometer.OdometerCorrection;
import data.DataCenter;
import drivers.CSPoller;
import drivers.HWConstants;
import drivers.Navigation;
import drivers.USPoller;
import util.Point;
import lejos.nxt.Button;
import lejos.nxt.Sound;
import localization.CSLocalizer;
import localization.USLocalizer;

/**
 * The main class. Initializes the threads of execution
 * and starts them.
 * 
 * @author Leotard Niyonkuru
 * 
 */
public class LLocalizerTEST {
	/**
	 * Time to wait during a pause in ms.
	 */
	private static final long TIMEOUT = 100;

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

		//The test destination.

		final Point destination = new Point(0, 
				0);
		
		
		//Initializes the threads.
		final DataCenter dc = new DataCenter();
		final Odometer odo = new Odometer(dc);
		final OdometerCorrection oc = new OdometerCorrection(dc);
		final Navigation nav = new Navigation(dc);
		final CSPoller cs = new CSPoller(dc);
		final CSLocalizer ll = new CSLocalizer(dc, nav);

		

		//Starts the threads.
		odo.start();
		cs.start();

		(new Thread() {
			public void run() {
				
				
				
				ll.doLocalization(destination.x, destination.y);
			
				nav.travelTo(0, 0, false);
				nav.turnTo(90.0);
				
				//Tells the slave to exit.
				
				System.exit(0);
			}
		}).start();
		
		//Wait for another button press to exit.
		Button.waitForAnyPress();
		System.exit(0);
	}
	
	/**
	 * Pauses the current thread for a period of TIMEOUT ms.
	 */
	private static void pause() {
		try {
			Thread.sleep(TIMEOUT);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
