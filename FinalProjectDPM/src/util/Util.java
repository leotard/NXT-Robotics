package util;

/**
 * A utility class containing various useful static methods.
 *
 * @author Andrei Purcarus
 *
 */
public class Util {
	/**
	 * Converts the given angle in degrees to the range
	 * (minAngle, minAngle + 360.0] if open is true and to
	 * [minAngle, minAngle + 360.0) if open is false.
	 * @param angle The angle to convert in degrees.
	 * @param minAngle The minimum angle for the range in degrees.
	 * @param open If true, minAngle is not included in the range.
	 * @return The angle in degrees converted to the range
	 *		   (minAngle, minAngle + 360.0] if open is true and to
	 * 		   [minAngle, minAngle + 360.0) if open is false.
	 */
	public static double toRange(double angle, double minAngle, boolean open) {
		if (open) {
			while (angle <= minAngle)
				angle += 360.0;
			while (angle > minAngle + 360.0)
				angle -= 360.0;
		} else {
			while (angle < minAngle)
				angle += 360.0;
			while (angle >= minAngle + 360.0)
				angle -= 360.0;
		}
		return angle;
	}
}
