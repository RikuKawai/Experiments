package moe.thisis.testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Advent02 {

	public static String[] input = new String[1000];
	public static int totalAmount = 0;
	public static int totalRibbon = 0;
	
	public static void main(String[] args) throws IOException {
		BufferedReader fbr = new BufferedReader(new FileReader("input2.txt"));
		
		for (int i=0; i<input.length; i++) {
			input[i] = fbr.readLine();
		}
		
		fbr.close();
		
		for (int j=0; j<input.length; j++) {
			String[] dimensions = input[j].split("x");
			int l = Integer.parseInt(dimensions[0]);
			int w = Integer.parseInt(dimensions[1]);
			int h = Integer.parseInt(dimensions[2]);
			
			int p1 = (2*l)+(2*w); int p2 = (2*w)+(2*h); int p3 = (2*h)+(2*l);
			
			int sm1 = Math.min(p1, Math.min(p2, p3));
			int volume = l*w*h;
			int totalR = sm1 + volume;
			totalRibbon += totalR;
			
			int side1 = l*w; int side2 = w*h; int side3 = h*l;
			int area = (2*side1)+(2*side2)+(2*side3);
			int smallest = Math.min(side1, Math.min(side2, side3));
			
			int total = area + smallest;
			totalAmount += total;
		}
		
		System.out.println("Total Paper: " + totalAmount);
		System.out.println("Total Ribbon: " + totalRibbon);
	}

}
