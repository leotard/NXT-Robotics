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
 * @author Andrei Purcarus
 * @author Leotard Niyonkuru
 */
public class Master {
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

		//The destination.
		final Point destination = new Point(2*HWConstants.TILE_DISTANCE, 
				2*HWConstants.TILE_DISTANCE);
		//The number of shots to fire at each target.
		final int shots = 3;
		
		//Initializes the threads.
		final DataCenter dc = new DataCenter();
		final Odometer odo = new Odometer(dc);
		final OdometerCorrection oc = new OdometerCorrection(dc);
		final Navigation nav = new Navigation(dc);
		final USPoller usLeft = new USPoller(180, dc);
		final USPoller usFront = new USPoller(90, dc);
		final CSPoller cs = new CSPoller(dc);
		final USLocalizer usl = new USLocalizer(dc, nav);
		final CSLocalizer ll = new CSLocalizer(dc, nav);
		final Launcher launcher = new Launcher(HWConstants.LAUNCHER_MOTOR);
		

		//Starts the threads.
		odo.start();
		usFront.start();
		usLeft.start();
		cs.start();

		(new Thread() {
			public void run() {
				
				usl.doLocalization();
				//Pauses to allow other threads to update.
				pause();
				//Travels to 0 for the light localization.
				nav.travelTo(0, 0, false);
				//Turns to 45 degrees for the light localization.
				nav.turnTo(45);
				ll.doLocalization();
				//Pauses to allow other threads to update.
				pause();
				
				//Gets to destination.
				oc.start();
				nav.travelTo(destination.x, destination.y, true);
				oc.stop();
				//Turns to 45 degrees for the light localization.
				nav.turnTo(45);
				ll.doLocalization(destination.x, 
						destination.y);
				//Pauses to allow other threads to update.
				pause();
				
				//Tells the slave NXT to fire shots times.
				for (int i = 0; i < shots; ++i) {
					boolean confirmation = launcher.isActive();
					launcher.fire();
					if (confirmation != false) {
						Sound.twoBeeps();
						throw new RuntimeException("Invalid command return.");
					}
				}
				
				//Returns to origin.
				oc.start();
				nav.travelTo(0, 0, true);
				oc.stop();
				//Turns to 45 degrees for the light localization.
				nav.turnTo(45);
				ll.doLocalization();
				//Pauses to allow other threads to update.
				pause();
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
