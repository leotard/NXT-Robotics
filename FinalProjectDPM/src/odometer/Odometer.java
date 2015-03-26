package odometer;
import util.Util;
import data.DataCenter;
import drivers.HWConstants;

/**
 * An odometer class which keeps track of the position of the
 * robot. The coordinate system is defined with the angles increasing
 * counterclockwise from the positive x-axis located to the right.
 * 
 * @author Andrei Purcarus
 *
 */
public class Odometer extends Thread {
	/**
	 * Odometer update period, in ms.
	 */
	private static final long ODOMETER_PERIOD = 10;

	/**
	 * Storage for the x, y positions in cm and 
	 * the theta orientation in degrees.
	 */
	private DataCenter dc;
	
	/**
	 * Previous reading of the left wheel tachometer in degrees.
	 */
	private int leftTacho = 0;
	/**
	 * Previous reading of the right wheel tachometer in degrees.
	 */
	private int rightTacho = 0;
	
	/**
	 * Default constructor.
	 * @param dc The storage center in which to store the
	 * 			 x, y positions and the theta orientation.
	 */
	public Odometer(DataCenter dc) {
		this.dc = dc;
	}

	/**
	 * Method called when thread is started. Updates the DataCenter
	 * every period with the new values of x, y and theta calculated
	 * from the wheel tachometer readings.
	 */
	@Override
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();

			//Gets the current positions and orientation of the robot.
			double[] xyt = dc.getXYT();
			double x = xyt[0];
			double y = xyt[1];
			double theta = xyt[2];
			
			//Gets the current tachometer readings in degrees.
			//Executes the commands as an atomic operation.
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);
			int newLeftTacho = HWConstants.DIRECTION * HWConstants.LEFT_MOTOR.getTachoCount();
			int newRightTacho = HWConstants.DIRECTION * HWConstants.RIGHT_MOTOR.getTachoCount();
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

			//Distances traveled by the wheels in cm.
			double leftDistance = Math.toRadians(newLeftTacho - leftTacho) * 
					HWConstants.LEFT_RADIUS;
			double rightDistance = Math.toRadians(newRightTacho - rightTacho) * 
					HWConstants.RIGHT_RADIUS;

			//Stores values for the next iteration.
			leftTacho = newLeftTacho;
			rightTacho = newRightTacho;
			
			//Difference in direction angle of the robot in radians.
			double diff = rightDistance - leftDistance;
			double thetaChange = 0;
			if (diff >= 0)
				thetaChange = Math.toDegrees(diff / HWConstants.CC_WIDTH);
			else
				thetaChange = Math.toDegrees(diff / HWConstants.C_WIDTH);
			
			//Distance between previous location and current location in cm.
			double distanceChange = (leftDistance + rightDistance) / 2;
			
			//Angle counterclockwise from the x-axis through which the robot
			//traveled the distance distanceChange in radians.
			double phi = Math.toRadians(theta + thetaChange / 2);
			
			//Updates values of x, y, and theta.
			x += distanceChange * Math.cos(phi);
			y += distanceChange * Math.sin(phi);
			theta += thetaChange;
			
			//Sets theta to [0, 360).
			theta = Util.toRange(theta, 0.0, false);

			//Check if correction has occurred during the computations.
			//If so, aborts the odometry and starts over.
			double[] new_xyt = dc.getXYT();
			if (new_xyt[0] != xyt[0] || new_xyt[1] != xyt[1] || new_xyt[2] != xyt[2])
				continue;
			
			//Sets the updated values.
			dc.setXYT(x, y, theta);
			
			//This ensures that the odometer only runs once every period.
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}