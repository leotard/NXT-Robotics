package odometer;
import data.DataCenter;
import drivers.HWConstants;
import interfaces.CSListener;

/**
 * A class that implements correction on the odometer
 * when grid lines are detected on the floor by a color sensor.
 * 
 * @author Andrei Purcarus
 *
 */
public class OdometerCorrection implements CSListener {
	/**
	 * The bandwidth in cm allowed between the reported 
	 * position and the position of a marker to correct 
	 * the path of the robot.
	 */
	private static final int BANDWIDTH = 5;

	/**
	 * The maximum ratio of x difference and y difference from a grid line
	 * at which to correct both positions.
	 */
	private static final double MAX_RATIO = 2.0;
	
	/**
	 * The location of the data where correction is applied.
	 */
	private DataCenter dc;
	
	/**
	 * Default constructor.
	 * @param dc The location of the data to correct. 
	 */
	public OdometerCorrection(DataCenter dc) {
		this.dc = dc;
	}

	/**
	 * Makes the correction start listening on the color sensor.
	 */
	public void start() {
		dc.addListener(this);
	}

	/**
	 * Makes the correction stop listening on the color sensor.
	 */
	public void stop() {
		dc.removeListener(this);
	}

	/**
	 * The method to be called to notify the
	 * listener of a grid line detection by
	 * the color sensor.
	 */
	@Override
	public void ping() {
		(new Thread() {
			public void run() {
				performCorrection();
			}
		}).start();
	}

	/**
	 * Corrects the odometer.
	 */
	private void performCorrection() {		
		//Get the position of the robot.
		double[] position = dc.getXYT();
	
		//Convert direction angle to radians.
		double thetaRad = Math.toRadians(position[2]);
		
		//Gets the position of the color sensor in cm.
		double x = position[0] + HWConstants.CS_DISTANCE * Math.cos(thetaRad + Math.toRadians(HWConstants.CS_ANGLE));
		double y = position[1] + HWConstants.CS_DISTANCE * Math.sin(thetaRad + Math.toRadians(HWConstants.CS_ANGLE));
		
		//Position of nearest marker in cm.
		double markerX, markerY;
		if (x >= -HWConstants.TILE_DISTANCE/2)
			markerX = HWConstants.TILE_DISTANCE * 
				((int)(x / HWConstants.TILE_DISTANCE + 0.5));
		else
			markerX = HWConstants.TILE_DISTANCE * 
				((int)(x / HWConstants.TILE_DISTANCE + 0.5) - 1);
		if (y >= -HWConstants.TILE_DISTANCE/2)
			markerY = HWConstants.TILE_DISTANCE * 
				((int)(y / HWConstants.TILE_DISTANCE + 0.5));
		else
			markerY = HWConstants.TILE_DISTANCE * 
				((int)(y / HWConstants.TILE_DISTANCE + 0.5) - 1);
		
		//Distance from sensor to the nearest position marker in cm. 
		double distX = Math.abs(x - markerX);
		double distY = Math.abs(y - markerY);
		
		//If distance from sensor to nearest position marker is less than
		//bandwidth, then correct position. Checks if it is much closer to 
		//x than to y and vice versa in the case of intersections.
		if (distX < BANDWIDTH && distY < BANDWIDTH && 
				distX / distY > 1/MAX_RATIO && distX / distY < MAX_RATIO) {
			dc.setX(markerX - HWConstants.CS_DISTANCE * Math.cos(thetaRad + Math.toRadians(HWConstants.CS_ANGLE)));
			dc.setY(markerY - HWConstants.CS_DISTANCE * Math.sin(thetaRad + Math.toRadians(HWConstants.CS_ANGLE)));
		} else if (distX < BANDWIDTH && distX / distY <= 1/MAX_RATIO) {
			dc.setX(markerX - HWConstants.CS_DISTANCE * Math.cos(thetaRad + Math.toRadians(HWConstants.CS_ANGLE)));
		} else if (distY < BANDWIDTH && distX / distY >= MAX_RATIO) {
			dc.setY(markerY - HWConstants.CS_DISTANCE * Math.sin(thetaRad + Math.toRadians(HWConstants.CS_ANGLE)));
		}
	}
}