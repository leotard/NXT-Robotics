package launcher;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

/**
 * A class to control a ping-pong ball launcher.
 * The launcher moves from a storage state, to an
 * active state, to an armed state, to an armed and loaded
 * state, to a post-launch state, and finally back to a
 * storage state.
 * 
 * @author Andrei Purcarus
 * @author Leotard Niyonkuru
 *
 */
public class Launcher {
	/**
	 * The high speed used to move the launcher and loader in deg/s.
	 * It is used when no resistance is expected.
	 */
	private final static int HIGH = 300;
	/**
	 * The low speed used to move the launcher and loader in deg/s.
	 * It is used when resistance is expected.
	 */
	private final static int LOW = 100;
	
	/**
	 * The motor controlling the launcher.
	 */
	private final NXTRegulatedMotor LAUNCHER;
	
	/**
	 * Default constructor.
	 * @param launcher The motor controlling the launcher.
	 */
	public Launcher(NXTRegulatedMotor launcher) {
		LAUNCHER = launcher;
	}
	
	/**
	 * Fires a ball from the launcher.
	 */
	public void fire() {
		activate();
		arm();
		launch();
		store();
	}
	
	/**
	 * Fires n balls from the launcher sequentially. 
	 * @param n The number of balls to fire.
	 */
	public void fire(int n) {
		//Fires the first ball.
		if (n > 0) {
			activate();
			arm();
			launch();
		}
		//Fires the rest of the balls.
		for (int i = 1; i < n; ++i) {
			activateFromLaunch();
			arm();
			launch();
		}
		//Stores the launcher.
		store();
	}
	
	/**
	 * Activates the launcher from a storage state to an
	 * active state.
	 */
	private void activate() {
		LAUNCHER.setSpeed(HIGH);
		LAUNCHER.rotate(-540);
	}
	
	/**
	 * Activates the launcher from a post-launch state
	 * to an active state.
	 */
	private void activateFromLaunch() {
		LAUNCHER.setSpeed(HIGH);
		LAUNCHER.rotate(-450);
	}
	
	/**
	 * Arms the launcher from an active state.
	 */
	private void arm() {
		LAUNCHER.setSpeed(HIGH);
		LAUNCHER.rotate(260);
		LAUNCHER.setSpeed(LOW);
		LAUNCHER.rotate(100);
	}
	
	/**
	 * Launches a loaded ball from an armed state.
	 */
	private void launch() {
		Sound.twoBeeps();
		LAUNCHER.setSpeed(LOW);
		LAUNCHER.rotate(90);
	}
	
	/**
	 * Sets the launcher to storage state after
	 * having been fired.
	 */
	private void store() {
		LAUNCHER.setSpeed(HIGH);
		LAUNCHER.rotate(90);
	}
}
