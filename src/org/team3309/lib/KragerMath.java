package org.team3309.lib;

/**
 * Class containing static methods of various mathmatical methods
 * 
 * @author TheMkrage
 * 
 */
public class KragerMath {

	/**
	 * Sin of degrees
	 * 
	 * @param a
	 *            degrees
	 * @return
	 */
	public static double sinDeg(double a) {
		System.out.println("ANGLE: " + a + " after " + Math.sin(a * (180 / Math.PI)) + " deg " + Math.sin(a));
		return Math.sin(a * (Math.PI / 180));
	}

	/**
	 * Cos of degrees
	 * 
	 * @param a
	 *            degrees
	 * @return
	 */
	public static double cosDeg(double a) {
		return Math.cos(a *  (Math.PI / 180));
	}

	/**
	 * Tan of Degrees
	 * 
	 * @param a
	 *            degrees
	 * @return
	 */
	public static double tanDeg(double a) {
		return Math.tan(a *  (Math.PI / 180));
	}

	public static double threshold(double input) {
		if (Math.abs(input) < .1) {
			return 0;
		}
		return input;
	}

	public static double sign(double error) {
		if (error > 0)
			return 1;
		else if (error < 0)
			return -1;
		return 0;
	}
}
