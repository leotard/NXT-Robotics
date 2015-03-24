package drivers;
import data.DataCenter;
import lejos.nxt.Sound;

/**
 * A class that continuously polls the color sensor for data and
 * uses that data to detect grid lines on the floor.
 * 
 * @author Andrei Purcarus
 *
 */
public class CSPoller extends Thread {
	/**
	 * The maximum color value detected that can be taken as a grid line.
	 */
	private static final int MAX_GRID = 45;
	
	/**
	 * The number of values to store to take the
	 * average of.
	 */
	private static final int NUM_VALUES = 5;
	/**
	 * The previous NUM_VALUES values of the color sensor.
	 */
	private int[] data;

	/**
	 * The location to send notifications to.
	 */
	private DataCenter dc;
	
	/**
	 * Default constructor.
	 * @param dc The location to send notifications to. 
	 */
	public CSPoller(DataCenter dc) {
		this.dc = dc;
		this.data = new int[NUM_VALUES];
		HWConstants.CS.setFloodlight(true);
	}

	/**
	 * Method called when thread is started. Corrects the odometer
	 * using the color sensor.
	 */
	@Override
	public void run() {
		init();
		while (true) {
			int csValue = poll();
			dc.setCSValue(csValue);
			int current = getMean();
			//Checks if the current value is less than the previous one.
			if (current < MAX_GRID)  {
				Sound.beep();
				dc.notifyListeners();
			}
		}
	}

	/**
	 * Makes the robot poll to fill the initial data.
	 */
	private void init() {
		for (int i = 0; i < NUM_VALUES; ++i) {
			data[i] = HWConstants.CS.getLightValue();
		}
	}

	/**
	 * Polls the color sensor and records and returns the reading.
	 * @return The color sensor reading.
	 */
	private int poll() {
		int val = HWConstants.CS.getLightValue();
		for (int i = 0; i < NUM_VALUES-1; ++i) {
			data[i] = data[i+1]; 
		}
		data[NUM_VALUES-1] = val;
		return val;
	}
	
	/**
	 * Computes the mean of the last data readings and returns it.
	 * @return The mean of the last data readings.
	 */
	private int getMean() {
		int mean = 0;
		for (int i = 0; i < NUM_VALUES; ++i) {
			mean += data[i];
		}
		mean /= NUM_VALUES;
		return mean;
	}
}