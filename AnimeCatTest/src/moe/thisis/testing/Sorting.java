package moe.thisis.testing;

import java.util.Arrays;

public class Sorting {

	public static void main(String[] args) {
		int[] intArray = {9, 8, 5, 2, 3, 7, 1, 4};
		for (int number : intArray) {
			System.out.print(number + " ");
		}
		System.out.println();
		Arrays.sort(intArray);
		for (int number : intArray) {
			System.out.print(number + " ");
		}
		System.out.println();
		System.out.println("Generating 2,500,000 random integers between 1 and 1000...");
		int[] bigArray = new int[2500000];
		for (int i : bigArray) {
			bigArray[i] = random(1, 1000);
		}
		System.out.println("Array generated!");
		System.out.println("Sorting array...");
		long startTime = System.nanoTime();
		Arrays.sort(bigArray);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		double time = (double) (duration / 1000000);
		System.out.println("Finished sorting!");
		System.out.println("Sorting took " + time + "ms." + " (" + duration + "ns)" + " (" + (time / 1000) + "s)");
	}
	public static int random(int min, int max) { //generates a random number between specified numbers
		int x = (max - min) + 1;
		int random = (int)(Math.random() * x) + min; 
		return random;
	}

}
