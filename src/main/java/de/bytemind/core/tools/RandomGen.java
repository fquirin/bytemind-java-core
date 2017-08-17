package de.bytemind.core.tools;

import java.util.Random;

/**
 * Some random generator methods.
 * 
 * @author Florian Quirin
 *
 */
public class RandomGen {
	
	/**
	 * Get a random number between start (inclusive) and end. End must be >= start.
	 * @param start - start with this number (inclusive)
	 * @param end - end with this number (exclusive)
	 * @return random integer in range
	 */
	public static int randomInt(int start, int end){
		if (end < start){
			end = start+1;
		}
		int res = new Random().nextInt(end-start) + start;
		return res;
	}
	/**
	 * Get a random value of 10, 100 or 1000 with a higher probability of hitting 10 (4) and 100 (2) than 1000 (1).
	 * @return random integer either 10, 100 or 1000
	 */
	public static int random_10_100_1000(){
		int[] sel = {10,10,10,10,100,100,1000};
		int n = new Random().nextInt(sel.length);
		return sel[n];
	}

}
