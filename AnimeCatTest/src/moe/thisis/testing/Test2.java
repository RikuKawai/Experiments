package moe.thisis.testing;

public class Test2 {

	public static void main(String[] args) {
		int[] array = {1,2,3,4,5};
		for (int i=0; i<array.length; i++) {
			System.out.println(array[i]);
		}
		array = reverseArray(array);
		for (int i=0; i<array.length; i++) {
			System.out.println(array[i]);
		}
	}
	public static int[] reverseArray(int[] x) {
		int[] y = new int[x.length];
		for (int z=0; z<x.length; z++) {
			y[z] = x[(x.length-1)-z];
		}
		return y;
	}
}
