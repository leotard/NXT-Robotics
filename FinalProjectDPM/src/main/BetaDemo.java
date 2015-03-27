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
import util.Util;
import lejos.nxt.Button;
import lejos.nxt.Sound;
import localization.CSLocalizer;
import localization.USLocalizer;

/**
 * The main class. Initializes the threads of execution
 * and starts them. This class is used to execute the beta demo.
 * 
 * @author Andrei Purcarus
 * @author Leotard Niyonkuru
 */
public class BetaDemo {

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
		
		//The points on the map. _tx means turn x after getting there.
		//r is right and l is left.
		final Point p0 = new Point(convert(0), convert(0));
		final Point p1 = new Point(convert(-0.4), convert(2.5));
		final Point p2_tr = new Point(convert(-0.4), convert(5.1));
		final Point p3_tl = new Point(convert(1.1), convert(5.5));
		final Point p4_tr = new Point(convert(1.5), convert(6.1));
		final Point p5 = new Point(convert(4.5), convert(6.4));
		final Point p6_destination = new Point(convert(6), convert(6));

		//The position of the target.
		final Point target = new Point(convert(9), convert(9));
		//The number of shots to fire at each target.
		final int shots = 1;
		
		//Initializes the threads.
		final DataCenter dc = new DataCenter();
		final Odometer odo = new Odometer(dc);
		final OdometerCorrection oc = new OdometerCorrection(dc);
		final Navigation nav = new Navigation(dc);
		final USPoller usFront = new USPoller(90, dc);
		final CSPoller cs = new CSPoller(dc);
		final USLocalizer usl = new USLocalizer(dc, nav);
		final CSLocalizer ll = new CSLocalizer(dc, nav);
		final Launcher launcher = new Launcher(HWConstants.LAUNCHER_MOTOR);

		//Starts the threads.
		odo.start();
		usFront.start();
		//usLeft.start();
		cs.start();

		(new Thread() {
			public void run() {
				usl.doLocalization();
				//Travels to 0 for the light localization.
				nav.travelTo(p0, false);
				//Turns to 45 degrees for the light localization.
				nav.turnTo(45);
				ll.doLocalization();
				//Indicates that the localization is finished.
				Sound.twoBeeps();
				//Gets to destination.
				oc.start();
				nav.travelTo(p1, false);
				nav.travelTo(p2_tr, false);
				nav.turnRight();
				nav.travelTo(p3_tl, false);
				nav.turnLeft();
				nav.travelTo(p4_tr, false);
				nav.turnRight();
				nav.travelTo(p5, false);
				nav.travelTo(p6_destination, false);
				oc.stop();
				//Turns to 45 degrees for the light localization.
				nav.turnTo(45);
				ll.doLocalization(p6_destination);
				
				turnToLaunch(p6_destination, target, nav);
				//Fires shots times.
				launcher.fire(shots);
				
				System.exit(0);
			}
		}).start();
		
		//Wait for another button press to exit.
		Button.waitForAnyPress();
		System.exit(0);
	}
	
	/**
	 * Converts a coordinate in the field to cm.
	 */
	private static double convert(double x) {
		return x * HWConstants.TILE_DISTANCE;
	}
	
	/**
	 * Positions itself to fire at the target using the navigation.
	 * The reference point serves to narrow down the possibilities of 
	 * where to go, and the robot will position itelf on the line joining 
	 * target and reference.
	 */
	private static void turnToLaunch(Point reference, Point target, Navigation nav) {
		double angle = Math.atan2(target.y - reference.y, target.x - reference.x);
		double xx = HWConstants.LAUNCH_DISTANCE * Math.cos(angle);
		double yy = HWConstants.LAUNCH_DISTANCE * Math.sin(angle);
		Point launch_pos = new Point(target.x - xx, target.y - yy);
		nav.travelTo(launch_pos, false);
		nav.turnTo(Util.toRange(Math.toDegrees(angle) - HWConstants.LAUNCH_ANGLE, 0.0, false));
	}
}
