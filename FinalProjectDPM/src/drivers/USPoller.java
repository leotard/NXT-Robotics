package drivers;
import data.DataCenter;
import lejos.nxt.UltrasonicSensor;

/**
 * A thread that polls the ultrasonic sensor for distance
 * values and sends them to be processed.
 * 
 * @author Andrei Purcarus
 *
 */
public class USPoller extends Thread {
	/**
	 * The time in ms needed to wait between a ping() and a getDistance()
	 * call for the sensor to get a proper data. Represents the minimal time
	 * to wait for a new distance to be obtained.
	 */
	public static final int PING_DELAY = 20;
	
	/**
	 * The ultrasonic sensor polled.
	 */
	private final UltrasonicSensor US;
	/**
	 * The angle in degrees at which the ultrasonic sensor is positioned,
	 * with 0 degrees being the right of the robot and angles
	 * increasing counterclockwise.
	 */
	private final int ANGLE;

	/**
	 * The number of values to store to obtain a minimum value
	 * from the ultrasonic sensor.
	 */
	private static final int NUM_VALUES = 5;
	/**
	 * The data used by the sensor to compute the running minimum, in cm.
	 */
	private int[] data;
	
	/**
	 * The data storage location to send data to.
	 */
	private DataCenter dc;

	/**
	 * Default constructor.
	 * @param angle The angle in degrees at which the ultrasonic sensor is positioned,
	 *				with 0 degrees being the right of the robot and angles
	 *				increasing counterclockwise. Valid angles are 0 degrees, 
	 *				90 degrees and 180 degrees.
	 * @param dc The data storage location to send data to.
	 */
	public USPoller(int angle, DataCenter dc) {
		this.dc = dc;
		switch (angle) {
		case 0:
			US = HWConstants.RIGHT_US;
			ANGLE = angle;
			break;
		case 90:
			US = HWConstants.FRONT_US;
			ANGLE = angle;
			break;
		case 180:
			US = HWConstants.LEFT_US;
			ANGLE = angle;
			break;
		default:
			throw new RuntimeException(
				"Invalid angle passed to UltrasonicPoller.");
		}
		data = new int[NUM_VALUES];
	}
	
	/**
	 * Method called when thread is started. Updates the distances
	 * of the robot from the wall at the angle depending
	 * on the feedback received from the DataCenter.
	 */
	@Override
	public void run() {
		init();
		//A check to see if the ultrasonic sensor was turned off
		//for a period of time, which would require reinitialization of the data.
		boolean paused = false;
		while (true) {
			int[] distances;
			switch (ANGLE) {
			case 90:
				distances = pollMin();
				dc.setDistance(distances[0], ANGLE);
				dc.setFilteredDistance(distances[1], ANGLE);
				break;
			case 0: case 180: {
				boolean wallFollowing = dc.getWallFollowing();
				if (wallFollowing) {
					if (paused) {
						paused = false;
						init();
					}
					distances = pollMin();
					dc.setDistance(distances[0], ANGLE);
					dc.setFilteredDistance(distances[1], ANGLE);
				} else {
					US.off();
					paused = true;
				}
				break;
			}
			default:
				throw new RuntimeException(
					"Invalid angle passed to UltrasonicPoller.");
			}
		}
	}
	
	/**
	 * Makes the robot poll to fill the initial data.
	 */
	private void init() {
		for (int i = 0; i < NUM_VALUES; ++i) {
			US.ping();
			try {
				Thread.sleep(PING_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			data[i] = US.getDistance();
		}
	}

	/**
	 * Polls the ultrasonic sensor and returns the polled value and the minimum value
	 * of the previous NUM_VALUES readings and the current one, in cm.
	 * @return The current and minimum value obtained from the ultrasonic sensor, in cm.
	 * 		   The current value is stored in index 0 and the minimum in index 1.
	 */
	private int[] pollMin() {
		US.ping();
		try {
			Thread.sleep(PING_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int val = US.getDistance();
		int min = val;
		for (int i = 0; i < NUM_VALUES; ++i) {
			if (data[i] < min)
				min = data[i];
		}
		for (int i = 0; i < NUM_VALUES-1; ++i) {
			data[i] = data[i+1]; 
		}
		data[NUM_VALUES-1] = val;
		int[] values = {val, min}; 
		return values;
	}
}
