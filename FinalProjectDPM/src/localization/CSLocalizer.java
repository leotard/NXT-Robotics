package localization;
import util.Util;
import data.DataCenter;
import drivers.HWConstants;
import drivers.Navigation;
import interfaces.CSListener;

/**
 * Class to perform localization using the color sensor.
 * 
 * @author Andrei Purcarus
 *
 */
public class CSLocalizer implements CSListener {
	/**
	 * The number of grid lines to detect during localization.
	 */
	private static final int NUM_LINES = 4;
	/**
	 * The maximum number of times to attempt localization before giving up.
	 */
	private static final int MAX_TRIES = 3;
	/**
	 * The minimal delay in ms between consecutive pings.
	 */
	private static final long DELAY = 200;
	/**
	 * The NUM_LINES {x, y, theta} tuples for grid line detection.
	 */
	private double[][] data;
	/**
	 * The number of grid lines detected.
	 */
	private int count;
	/**
	 * The System time in ms at which the last ping occured.
	 */
	private long lastPing;

	/**
	 * The location of the data where localization is applied.
	 */
	private DataCenter dc;

	/**
	 * The navigation system to use to move the robot.
	 */
	private Navigation nav;

	/**
	 * Default constructor.
	 * @param dc The data location to send localization to.
	 * @param nav The navigation to use to move the robot.
	 */
	public CSLocalizer(DataCenter dc, Navigation nav) {
		this.dc = dc;
		this.nav = nav;
		data = new double[NUM_LINES][3];
		count = 0;
		lastPing = System.currentTimeMillis();
	}

	/**
	 * Performs color sensor localization. Robot must be facing
	 * roughly 45 degrees counterclockwise from the +x axis. A CSPoller
	 * must be running using the same DataCenter as this object.
	 */
	public void doLocalization() {
		doLocalization(0, 0);
	}

	/**
	 * Performs color sensor localization around (xGrid, yGrid). Robot must be facing
	 * roughly 45 degrees counterclockwise from the +x axis. A CSPoller
	 * must be running using the same DataCenter as this object. Takes (xGrid, yGrid)
	 * as being the grid line intersection that it is supposed to detect.
	 * The distances are in cm.
	 * @param xGrid The x position of the nearest grid line intersection in cm.
	 * @param yGrid The y position of the nearest grid line intersection in cm.
	 */
	public void doLocalization(double xGrid, double yGrid) {
		//Rotates 360 degrees until it clocks NUM_LINES grid lines in one complete
		//rotation. The localization assumes it starts roughly facing 45 
		//degrees counterclockwise from the +x axis. Therefore it should 
		//detect the grid lines in the order {x, y, x, y} when turning clockwise.
		
		int numberOfTries = 0;
		dc.addListener(this);
		while (count != NUM_LINES) {
			//Turns 360 degrees clockwise.
			nav.turn(-360);
			if (count != NUM_LINES) {
				data = new double[NUM_LINES][3];
				count = 0;
				++numberOfTries;
			}
			if (numberOfTries > MAX_TRIES) {
				//Gives up if it tries to localize too many times.
				dc.removeListener(this);
				count = 0;
				return;
			}
		}
		dc.removeListener(this);
		count = 0;
		
		//Computes the difference between the x and y angles measured.
		//Makes the difference be in the (-180, 180] range.
		double thetaXDiff = data[0][2] - data[2][2];
		double thetaYDiff = data[1][2] - data[3][2];
		thetaXDiff = Util.toRange(thetaXDiff, -180.0, true);
		thetaYDiff = Util.toRange(thetaYDiff, -180.0, true);
		
		//Computes the NUM_LINES offsets from actual values for the NUM_LINES 
		//orientation angles measured previously and averages them.
		double[] actual = new double[NUM_LINES];
		if (thetaXDiff >= 0) {
			actual[0] = 270.0 + thetaXDiff / 2;
			actual[2] = 270.0 - thetaXDiff / 2;
		} else {
			actual[0] = 90.0 + thetaXDiff / 2;
			actual[2] = 90.0 - thetaXDiff / 2;
		}
		if (thetaYDiff >= 0) {
			actual[1] = 180.0 + thetaYDiff / 2;
			actual[3] = 180.0 - thetaYDiff / 2;
		} else {
			actual[1] = thetaYDiff / 2;
			actual[3] = -thetaYDiff / 2;
		}
		for (int i = 0; i < NUM_LINES; ++i) {
			actual[i] = Util.toRange(actual[i], 0.0, false);
		}
		double[] changes = new double[NUM_LINES];
		for (int i = 0; i < NUM_LINES; ++i) {
			changes[i] = actual[i] - data[i][2];
			changes[i] = Util.toRange(changes[i], 0.0, false);
		}
		double sum = 0;
		for (double error : changes)
			sum += error;
		double averageError = sum / NUM_LINES;
		
		//Computes the current position x, y in cm.
		double x, y;
		if (thetaYDiff >= 0.0)
			x = xGrid + HWConstants.CS_DISTANCE * Math.cos(Math.toRadians(thetaYDiff)/2);
		else
			x = xGrid - HWConstants.CS_DISTANCE * Math.cos(Math.toRadians(thetaYDiff)/2);
		if (thetaXDiff >= 0.0)
			y = yGrid + HWConstants.CS_DISTANCE * Math.cos(Math.toRadians(thetaXDiff)/2);
		else
			y = yGrid - HWConstants.CS_DISTANCE * Math.cos(Math.toRadians(thetaXDiff)/2);
		
		//Gets current distance.
		double[] dist = dc.getXYT();
		//Corrects the position.
		double actualAngle = dist[2] + averageError;
		actualAngle = Util.toRange(actualAngle, 0.0, false);
		dc.setXYT(x, y, actualAngle);
	}

	/**
	 * The method to be called to notify the
	 * listener of a grid line detection by
	 * the color sensor.
	 */
	@Override
	public void ping() {
		long currentPing = System.currentTimeMillis();
		if (currentPing - lastPing > DELAY) {
			if (count < NUM_LINES) {
				data[count] = dc.getXYT();
				++count;
			} else {
				count = NUM_LINES + 1;
			}
			lastPing = currentPing;
		}
	}
}
