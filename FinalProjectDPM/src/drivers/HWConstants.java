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
	 * The radius of the left wheel in cm.
	 */
	public static final double LEFT_RADIUS;

	/**
	 * The radius of the left wheel in cm.
	 */
	public static final double RIGHT_RADIUS;

	/**
	 * The distance between the robot's wheels when turning counter-clockwise.
	 */
	public static final double CC_WIDTH;
	
	/**
	 * The distance between the robot's wheels when turning clockwise.
	 */
	public static final double C_WIDTH;
	
	/**
	 * The distance between the robot's wheels when turning on the left wheel.
	 * This means that the left wheel is stationary and the right wheel does 
	 * the turning.
	 */
	public static final double L_WIDTH;
	
	/**
	 * The distance between the robot's wheels when turning on the right wheel.
	 * This means that the right wheel is stationary and the left wheel does 
	 * the turning.
	 */
	public static final double R_WIDTH;

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
	 * The distance fired at directly in front of the robot's center.
	 * Negative distances indicate a backwards launcher.
	 */
	public static final double LAUNCH_FRONT_DISTANCE;
	
	/**
	 * The distance fired at perpendicular to the robot's center.
	 * Positive values indicate a deviation to the right.
	 */
	public static final double LAUNCH_SIDE_DISTANCE;

	/**
	 * Distance of the ball fired.
	 */
	public static final double LAUNCH_DISTANCE;
	
	/**
	 * Angle relative to orientation of robot that the launcher fires at.
	 */
	public static final double LAUNCH_ANGLE;
	
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
	 * The sensor port for the right ultrasonic sensor.
	 */
	public static final SensorPort RIGHT_US_PORT;
	
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
	 * The ultrasonic sensor polling the right of the robot.
	 */
	public static final UltrasonicSensor RIGHT_US;
	
	/**
	 * The distance in cm between the left ultrasonic sensor and
	 * the central axis of symmetry of the NXT brick.
	 */
	public static final double LEFT_US_DISTANCE;
	
	/**
	 * The distance in cm between the front ultrasonic sensor
	 * and the robot's center of rotation.
	 */
	public static final double FRONT_US_DISTANCE;

	/**
	 * The distance in cm between the right ultrasonic sensor and
	 * the central axis of symmetry of the NXT brick.
	 */
	public static final double RIGHT_US_DISTANCE;
	
	/**
	 * The color sensor used by the robot to detect grid lines.
	 */
	public static final ColorSensor CS;

	/**
	 * The distance between the center of rotation of the robot and
	 * the color sensor in cm, aligned with the robot's orientation. 
	 * Negative values indicate that the color sensor is placed behind 
	 * the center of the robot.
	 */
	public static final double FRONT_CS_DISTANCE;

	/**
	 * The distance between the center of rotation of the robot and
	 * the color sensor in cm, perpendicular to the robot's orientation. 
	 * Negative values indicate that the color sensor is placed in the negative 
	 * x axis of the robots' frame.
	 */
	public static final double SIDE_CS_DISTANCE;
	
	/**
	 * Distance from the robot's center of rotation to the color sensor, in cm.
	 */
	public static final double CS_DISTANCE;
	
	/**
	 * Angle in degrees at which the CS is oriented relative to the robot's orientation.
	 * This is taken as if the positive orientation of the robot is the x-axis.
	 */
	public static final double CS_ANGLE;
	
	/**
	 * The distance in cm between parallel grid lines in the field.
	 */
	public static final double TILE_DISTANCE;

	static {
		LEFT_RADIUS = 2.076; //TODO
		RIGHT_RADIUS = 2.085 + 0.01;  //TODO
		CC_WIDTH = 16.692 + 0.335; //TODO
		C_WIDTH = 16.689 + 0.325; //TODO
		L_WIDTH = 16.98 + 0.5; //TODO
		R_WIDTH = 17.05 + 0.5; //TODO

		LEFT_MOTOR = Motor.A;
		RIGHT_MOTOR = Motor.C;
		LAUNCHER_MOTOR= Motor.B;
		
		LAUNCH_FRONT_DISTANCE = 117;
		LAUNCH_SIDE_DISTANCE = -4;
		LAUNCH_DISTANCE = Math.sqrt(LAUNCH_FRONT_DISTANCE * LAUNCH_FRONT_DISTANCE +
				LAUNCH_SIDE_DISTANCE * LAUNCH_SIDE_DISTANCE);
		LAUNCH_ANGLE = Math.toDegrees(Math.atan2(-LAUNCH_SIDE_DISTANCE, LAUNCH_FRONT_DISTANCE));
		DIRECTION = 1;

		LEFT_US_PORT = SensorPort.S1;
		FRONT_US_PORT = SensorPort.S3;
		RIGHT_US_PORT = SensorPort.S4;
		CS_PORT = SensorPort.S2;

		LEFT_US = new UltrasonicSensor(LEFT_US_PORT);
		FRONT_US = new UltrasonicSensor(FRONT_US_PORT);
		RIGHT_US = new UltrasonicSensor(RIGHT_US_PORT);
		LEFT_US_DISTANCE = 7.5;
		FRONT_US_DISTANCE = 4;
		RIGHT_US_DISTANCE = 10.7;

		CS = new ColorSensor(CS_PORT);
		FRONT_CS_DISTANCE = -11.5;
		SIDE_CS_DISTANCE = -0.3;
		CS_DISTANCE = Math.sqrt(FRONT_CS_DISTANCE * FRONT_CS_DISTANCE +
				SIDE_CS_DISTANCE * SIDE_CS_DISTANCE);
		CS_ANGLE = Math.toDegrees(Math.atan2(-SIDE_CS_DISTANCE, FRONT_CS_DISTANCE));
		TILE_DISTANCE = 30.48;
	}
}