package drivers;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

/**
 * A static class for storing all the sensors, motors
 * and constants defining the NXT hardware.
 * 
 * @author Andrei Purcarus
 * 
 */
public class HWConstants {
	/**
	 * The ID of the master NXT brick.
	 */
	public static final String MASTER_ID;
	
	/**
	 * The ID of the slave NXT brick.
	 */
	public static final String SLAVE_ID;
	
	/**
	 * The radius of the left wheel in cm.
	 */
	public static final double LEFT_RADIUS;

	/**
	 * The radius of the left wheel in cm.
	 */
	public static final double RIGHT_RADIUS;

	/**
	 * The distance between the wheels in cm.
	 */
	public static final double WIDTH;

	/**
	 * The motor controlling the left wheel of the robot.
	 */
	public static final NXTRegulatedMotor LEFT_MOTOR;

	/**
	 * The motor controlling the right wheel of the robot.
	 */
	public static final NXTRegulatedMotor RIGHT_MOTOR;
	
	/**
	 * The motor controlling the launching motion of the robot.
	 */
	public static final NXTRegulatedMotor LAUNCHER_MOTOR;

	/**
	 * The direction of the robot's wheel motors. Can
	 * be either 1 if the motors rotating forward make
	 * the robot go forward, or -1 if the wheels rotating
	 * forward make the robot go backward.
	 */
	public static final int DIRECTION;
	
	/**
	 * The sensor port for the left ultrasonic sensor.
	 */
	public static final SensorPort LEFT_US_PORT;

	/**
	 * The sensor port for the front ultrasonic sensor.
	 */
	public static final SensorPort FRONT_US_PORT;

	/**
	 * The sensor port for the color sensor.
	 */
	public static final SensorPort CS_PORT;

	/**
	 * The ultrasonic sensor polling the left of the robot.
	 */
	public static final UltrasonicSensor LEFT_US;

	/**
	 * The ultrasonic sensor polling the front of the robot.
	 */
	public static final UltrasonicSensor FRONT_US;
	
	/**
	 * The distance in cm between the front ultrasonic sensor
	 * and the robot's center of rotation.
	 */
	public static final double US_DISTANCE;

	/**
	 * The color sensor used by the robot to detect grid lines.
	 */
	public static final ColorSensor CS;

	/**
	 * The distance between the center of rotation of the robot and
	 * the color sensor in cm. Negative values indicate that the 
	 * color sensor is placed behind the center of the robot.
	 */
	public static final double CS_DISTANCE;

	/**
	 * The distance in cm between parallel grid lines in the field.
	 */
	public static final double TILE_DISTANCE;

	static {
		MASTER_ID = "NXT";
		SLAVE_ID = "0016531258B4";
		
		LEFT_RADIUS = 2.06;
		RIGHT_RADIUS = 2.06;
		WIDTH = 14;

		LEFT_MOTOR = Motor.A;
		RIGHT_MOTOR = Motor.C;
		LAUNCHER_MOTOR= Motor.B;
		DIRECTION = 1;

		LEFT_US_PORT = SensorPort.S1;
		FRONT_US_PORT = SensorPort.S3;
		CS_PORT = SensorPort.S2;

		LEFT_US = new UltrasonicSensor(LEFT_US_PORT);
		FRONT_US = new UltrasonicSensor(FRONT_US_PORT);
		US_DISTANCE = 7;

		CS = new ColorSensor(CS_PORT);
		CS_DISTANCE = -12.5;
		TILE_DISTANCE = 30.48;
	}
}