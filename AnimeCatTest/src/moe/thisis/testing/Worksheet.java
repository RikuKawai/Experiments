package moe.thisis.testing;

public class Worksheet {

	public static void main(String[] args) {
		System.out.println(f1(6));
		System.out.println(f2(6));
		System.out.println(f3(-4));
	}
	public static int f1(int x) {
		if (x > 1) {
			return f1(x-1) + x;
		} else if (x == 1) {
			return x-2;
		}
		return x;
	}
	public static int f2(int x) {
		if (x > 1) {
			return f2(f2(x-2))+1;
		} else if (x == 1) {
			return 2;
		} else if (x == 0) {
			return 1;
		}
		return x;
	}
	public static int f3(int x) {
		if (x < 0) {
			return 2*(f3(x+2))-f3(x+1)+1;
		} else if (x == 0) {
			return 1;
		} else if (x > 0) {
			return 0;
		}
		return x;
	}
	public static int f4(int x, int y) {
		
	}
}
