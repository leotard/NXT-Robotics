package data;
import java.util.ArrayList;
import interfaces.CSListener;

/**
 * A class to provide a centralized, synchronized location
 * to store the data collected by the various parts of the
 * robot and to enable inter-thread communication.
 * 
 * @author Andrei Purcarus
 *
 */
public class DataCenter {
	/**
	 * Lock for synchronized access to ultrasonic
	 * sensor data.
	 */
	private Object usLock;
	/**
	 * Variable to tell the ultrasonic sensors to
	 * follow the wall or not.
	 */
	private boolean isWallFollowing;
	/**
	 * The number of distances to keep track of.
	 * These correspond to 90 degrees (front) and 180 degrees (left).
	 */
	private static final int NUM_DISTANCES = 2;
	/**
	 * The distances in cm recorded by the ultrasonic sensors.
	 */
	private int[] distances;
	/**
	 * The filtered distances in cm recorded by the ultrasonic sensors.
	 */
	private int[] filteredDistances; 
	
	/**
	 * Lock for synchronized access to odometer data.
	 */
	private Object odometerLock;
	/**
	 * x position of the robot according to the odometer.
	 * Distance given in cm.
	 */
	private double x;
	/**
	 * y position of the robot according to the odometer.
	 * Distance given in cm.
	 */
	private double y;
	/**
	 * Orientation of the robot according to the odometer.
	 * Angle given in degrees.
	 */
	private double theta;

	/**
	 * Lock for synchronized access to the color sensor.
	 */
	private Object csLock;
	/**
	 * The current classes listening to the color sensor
	 * for grid line intersections.
	 */
	private ArrayList<CSListener> csListeners;
	/**
	 * Lock for synchronized access to the color sensor reading.
	 */
	private Object csValueLock;
	/**
	 * The current color sensor reading.
	 */
	private int csValue;
	
	/**
	 * Default constructor.
	 */
	public DataCenter() {
		usLock = new Object();
		isWallFollowing = false;
		distances = new int[NUM_DISTANCES];
		for (int i = 0; i < NUM_DISTANCES; ++i) {
			distances[i] = 255;
		}
		filteredDistances = new int[NUM_DISTANCES];
		for (int i = 0; i < NUM_DISTANCES; ++i) {
			filteredDistances[i] = 255;
		}
		
		odometerLock = new Object();
		x = 0.0;
		y = 0.0;
		theta = 0.0;

		csLock = new Object();
		csListeners = new ArrayList<CSListener>();
		
		csValueLock = new Object();
		csValue = 0;
	}
	
	/**
	 * Sets the robot's wall following behavior
	 * to the value of b. If b is true, sets the
	 * robot to follow the wall, and if b is false,
	 * sets it to stop following the wall.
	 * @param b The value that determines if the
	 * 			robot will wall follow.
	 */
	public void setWallFollowing(boolean b) {
		synchronized (usLock) {
			isWallFollowing = b;
		}
	}
	
	/**
	 * Returns true if the robot is wall following,
	 * and false otherwise.
	 * @return true if the robot is wall following,
	 * 		   and false otherwise.
	 */
	public boolean getWallFollowing() {
		synchronized (usLock) {
			return isWallFollowing;
		}
	}
	
	/**
	 * Sets the distance in cm from the robot to the wall at
	 * an angle in degrees counterclockwise from the right of the robot.
	 * Accepts angles of 90 degrees and 180 degrees.
	 * @param distance The distance to the wall in cm.
	 * @param angle The angle at which the distance
	 * 				is measured in degrees. Accepts angles of 
	 * 				90 degrees and 180 degrees.
	 */
	public void setDistance(int distance, int angle) {
		synchronized (usLock) {
			switch (angle) {
			case 90:
				distances[0] = distance;
				break;
			case 180:
				distances[1] = distance;
				break;
			default:
				throw new RuntimeException(
					"Invalid angle passed to DataCenter::setDistance.");
			}
		}
	}
	
	/**
	 * Sets the filtered distance in cm from the robot to the wall at
	 * an angle in degrees counterclockwise from the right of the robot.
	 * Accepts angles of 90 degrees and 180 degrees.
	 * @param distance The filtered distance to the wall in cm.
	 * @param angle The angle at which the distance
	 * 				is measured in degrees. Accepts angles of 
	 * 				90 degrees and 180 degrees.
	 */
	public void setFilteredDistance(int distance, int angle) {
		synchronized (usLock) {
			switch (angle) {
			case 90:
				filteredDistances[0] = distance;
				break;
			case 180:
				filteredDistances[1] = distance;
				break;
			default:
				throw new RuntimeException(
					"Invalid angle passed to DataCenter::setFilteredDistance.");
			}
		}
	}

	/**
	 * Returns the distance in cm from the robot to
	 * the wall at the specified angle in degrees.
	 * Accepts angles of 90 degrees and 180 degrees.
	 * @param angle The angle from the robot
	 * 				to the wall in degrees. Accepts 
	 * 				90 degrees and 180 degrees as values.
	 * @return The distance in cm to the wall at the
	 * 		   specified angle.
	 */
	public int getDistance(int angle) {
		synchronized (usLock) {
			switch (angle) {
			case 90:
				return distances[0];
			case 180:
				return distances[1];
			default:
				throw new RuntimeException(
					"Invalid angle passed to DataCenter::getDistance.");
			}
		}
	}
	
	/**
	 * Returns the filtered distance in cm from the robot to
	 * the wall at the specified angle in degrees.
	 * Accepts angles of 90 degrees and 180 degrees.
	 * @param angle The angle from the robot
	 * 				to the wall in degrees. Accepts 
	 * 				90 degrees and 180 degrees as values.
	 * @return The filtered distance in cm to the wall at the
	 * 		   specified angle.
	 */
	public int getFilteredDistance(int angle) {
		synchronized (usLock) {
			switch (angle) {
			case 90:
				return filteredDistances[0];
			case 180:
				return filteredDistances[1];
			default:
				throw new RuntimeException(
					"Invalid angle passed to DataCenter::getFilteredDistance.");
			}
		}
	}
	
	/**
	 * Sets the values of the x and y positions in cm
	 * and the theta orientation in degrees of the robot.
	 * @param x The value of x to set, in cm.
	 * @param y The value of y to set, in cm.
	 * @param theta The value of theta to set, in degrees.
	 */
	public void setXYT(double x, double y, double theta) {
		synchronized (odometerLock) {
			this.x = x;
			this.y = y;
			this.theta = theta;
		}
	}
	
	/**
	 * Returns the x, y positions in cm and the
	 * theta orientation in degrees of the robot in
	 * an array of length 3.
	 * @return The x, y positions in cm and the
	 * 		   theta orientation in degrees of the robot.  
	 * 		   x is stored in position 0, y in position 1, 
	 * 		   and theta in position 2.
	 */
	public double[] getXYT() {
		synchronized (odometerLock) {
			double[] xyt = {x, y, theta};
			return xyt;
		}
	}
	
	/**
	 * Sets the value of the x position of the
	 * robot in cm.
	 * @param x The value to set, in cm.
	 */
	public void setX(double x) {
		synchronized (odometerLock) {
			this.x = x;
		}
	}
	
	/**
	 * Returns the value of the x position of
	 * the robot, in cm.
	 * @return The value of the x position of
	 * 		   the robot, in cm.
	 */
	public double getX() {
		synchronized (odometerLock) {
			return x;
		}
	}
	
	/**
	 * Sets the value of the y position of the
	 * robot in cm.
	 * @param y The value to set, in cm.
	 */
	public void setY(double y) {
		synchronized (odometerLock) {
			this.y = y;
		}
	}
	
	/**
	 * Returns the value of the y position of
	 * the robot, in cm.
	 * @return The value of the y position of
	 * 		   the robot, in cm.
	 */
	public double getY() {
		synchronized (odometerLock) {
			return y;
		}
	}
	
	/**
	 * Sets the value of the orientation of the
	 * robot in degrees.
	 * @param theta The value to set, in degrees.
	 */
	public void setTheta(double theta) {
		synchronized (odometerLock) {
			this.theta = theta;
		}
	}
	
	/**
	 * Returns the value of the orientation of
	 * the robot, in degrees.
	 * @return The value of the orientation of
	 * 		   the robot, in degrees.
	 */
	public double getTheta() {
		synchronized (odometerLock) {
			return theta;
		}
	}

	/**
	 * Adds the listener if it does not currently exists.
	 * @param csListener The listener to add.
	 */
	public void addListener(CSListener csListener) {
		synchronized (csLock) {
			for (CSListener csl : csListeners) {
				if (csl == csListener)
					throw new RuntimeException(
							"Listener already exists in DataCenter.");
			}
			csListeners.add(csListener);
		}
	}

	/**
	 * Remove the listener if it is currently listening.
	 * @param csListener The listener to remove.
	 */
	public void removeListener(CSListener csListener) {
		synchronized (csLock) {
			boolean found = csListeners.remove(csListener);
			if (!found) {
				throw new RuntimeException(
						"Invalid CSListener to remove in DataCenter.");
			}
		}
	}

	/**
	 * Notifies the listeners that the color sensor has detected
	 * a grid line.
	 */
	public void notifyListeners() {
		synchronized (csLock) {
			(new Thread() {
				public void run() {
					synchronized (csLock) {
						for (CSListener csl : csListeners) {
							csl.ping();
						}
					}
				}
			}).start();
		}
	}
	
	/**
	 * Sets the color sensor reading value.
	 * @param csValue The value to set.
	 */
	public void setCSValue(int csValue) {
		synchronized (csValueLock) {
			this.csValue = csValue;
		}
	}
	
	/**
	 * Returns the color sensor reading value.
	 * @return The color sensor reading value.
	 */
	public int getCSValue() {
		synchronized (csValueLock) {
			return csValue;
		}
	}
}
