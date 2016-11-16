package moe.thisis.testing;

import java.util.Arrays;

public class Sorting {

	public static long totalTime;
	public static void main(String[] args) {
		int scale;
		int min;
		int max;
		int iterations;
		if (args.length > 0) {
			scale = Integer.parseInt(args[0]);
		} else {
			System.out.println("Scale not specified, defaulting to 5 million.");
			scale = 5000000;
		}
		if (args.length > 2) {
			min = Integer.parseInt(args[1]);
			max = Integer.parseInt(args[2]);
		} else {
			System.out.println("Range not specified, defaulting to between 1 and 1000");
			min = 1; max = 1000;
		}
		if (args.length > 3) {
			iterations = Integer.parseInt(args[3]);
		} else {
			iterations = 1;
		}
		if (iterations>1){System.out.println("Running " + iterations + " iterations of " + scale + " integers between " + min + " and " + max + "...");}
		for (int x = 0; x<iterations; x++) {
			if (iterations==1){System.out.println("Generating " + scale + " random integers between " + min + " and " + max + "...");}
			int[] bigArray = new int[scale];
			for (int i : bigArray) {
				bigArray[i] = random(min, max);
			}
			if (iterations==1){System.out.println("Array generated!");System.out.println("Sorting array...");}
			long startTime = System.nanoTime();
			Arrays.sort(bigArray);
			long endTime = System.nanoTime();
			long duration = (endTime - startTime);
			if (iterations>1){totalTime+=duration;}
			double time = (double) (duration / 1000000);
			if (iterations==1){System.out.println("Finished sorting!");}
			if (iterations==1){System.out.println("Sorting took " + time + "ms." + " (" + duration + "ns)" + " (" + (time / 1000) + "s)");}
		}
		if (iterations>1){
			long averageTime = totalTime / iterations;
			double time = (double) (averageTime / 1000000);
			System.out.println(iterations + " Iterations averaged " + time + "ms." + " (" + averageTime + "ns)" + " (" + (time / 1000) + "s)");
		}
	}
	public static int random(int min, int max) { //generates a random number between specified numbers
		int x = (max - min) + 1;
		int random = (int)(Math.random() * x) + min; 
		return random;
	}

}
