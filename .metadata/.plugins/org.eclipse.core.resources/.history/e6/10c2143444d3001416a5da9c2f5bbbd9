package comm;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import data.DataCenter;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 * A class for communicating via Bluetooth to the computer
 * in order to send data for analysis.
 * The data is sent in pairs, with first data a byte being sent to
 * indicate the type of the second data.
 * 
 * @author Andrei Purcarus
 * @author Leotard Niyonkuru
 *
 */
public class BluetoothComm extends Thread {
	private static final byte X = 0x00;
	private static final byte Y = 0x01;
	private static final byte THETA = 0x02;
	private static final byte LEFT_US_DISTANCE = 0x03;
	private static final byte FRONT_US_DISTANCE = 0x04;
	private static final byte CS_VALUE = 0x05;
	private static final byte DONE = 0x06;
	
	/**
	 * The period at which to send data in ms.
	 */
	private static final long COMM_PERIOD = 25;
	
	/**
	 * The input stream used to get data.
	 */
	private DataInputStream dis;
	
	/**
	 * The output stream used to send data.
	 */
	private DataOutputStream dos;
	
	/**
	 * The location to get the data from.
	 */
	private DataCenter dc;
	
	/**
	 * The operating mode of the bluetooth communication.
	 * The first 4 bits encode whether to send each of the
	 * types of data.
	 */
	private byte mode;
	
	/**
	 * Default constructor.
	 * @param mode The operating mode of the communication.
	 * 			   Each of the first 4 bits encodes whether
	 * 			   to send that type of data or not.
	 * 			   bit 0 - x, y, theta
	 * 			   bit 1 - leftUSDistance
	 * 			   bit 2 - frontUSDistance
	 * 			   bit 3 - csValue
	 */
	public BluetoothComm(byte mode, DataCenter dc) {
		BTConnection bt = Bluetooth.waitForConnection();
		dis = bt.openDataInputStream();
		dos = bt.openDataOutputStream();
		this.mode = mode;
		this.dc = dc;
	}
	
	/**
	 * Sends data periodically to the computer.
	 */
	public void run() {
		long start, end;
		while (true) {
			start = System.currentTimeMillis();
			try {
				send();
			} catch (IOException e) {
				e.printStackTrace();
			}
			end = System.currentTimeMillis();
			if (end - start < COMM_PERIOD) {
				try {
					Thread.sleep(COMM_PERIOD - (end - start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Sends all the parameters specified by the mode
	 * to be processed.
	 */
	private void send() throws IOException {
		synchronized (this) {
			if ((mode & 0x1) != 0) {
				double[] xyt = dc.getXYT();
				dos.writeByte(X);
				dos.writeDouble(xyt[0]);
				dos.writeByte(Y);
				dos.writeDouble(xyt[1]);
				dos.writeByte(THETA);
				dos.writeDouble(xyt[2]);
			}
			if ((mode & 0x2) != 0) {
				dos.writeByte(LEFT_US_DISTANCE);
				dos.writeInt(dc.getDistance(180));
			}
			if ((mode & 0x4) != 0) {
				dos.writeByte(FRONT_US_DISTANCE);
				dos.writeInt(dc.getDistance(90));
			}
			if ((mode & 0x8) != 0) {
				dos.writeByte(CS_VALUE);
				dos.writeInt(dc.getCSValue());
			}
			dos.flush();
		}
	}
	
	/**
	 * Returns the data input stream.
	 * @return The data input stream.
	 */
	public DataInputStream getInStream() {
		return dis;
	}
	
	/**
	 * Tells the computer that an instruction has been completed.
	 * @throws IOException
	 */
	public void done() throws IOException {
		synchronized (this) {
			dos.writeByte(DONE);
			dos.flush();
		}
	}
}
