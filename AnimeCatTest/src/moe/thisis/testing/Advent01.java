package moe.thisis.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Advent01 {
	
	public static int basementIndex = 0;

	public static void main(String[] args) throws IOException {
		BufferedReader fbr = new BufferedReader(new FileReader("input1.txt"));
		
		String instructions = fbr.readLine();
		fbr.close();
		
		String[] up = instructions.split("\\(");
		String[] down = instructions.split("\\)");
		
		int floorsUp = up.length; //System.out.println(floorsUp);
		int floorsDown = down.length; //System.out.println(floorsDown);
		
		if (instructions.startsWith("(")) {
			floorsDown -= 1;
		}
		
		int floor = floorsUp - floorsDown;
		
		int curFloor = 0;
		for (int i=0; i<instructions.length(); i++) {
			if (instructions.charAt(i) == '(') {
				curFloor++;
			} else if (instructions.charAt(i) == ')') {
				curFloor--;
			}
			if (curFloor < 0) {
				basementIndex = i+1;
				break;
			}
		}
		
		System.out.println("Resulting Floor: " + floor);
		System.out.println("Basement Entered at: " + basementIndex);
	}

}
