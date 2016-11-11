package moe.thisis.testing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class wc161j2 {

	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String story = in.readLine();
		story = story.replaceAll("\\bFrankenstein's\\b", "TEMPORARY_VALUE");
		story = story.replaceAll("\\bFrankenstein,\\b", "RANDOM_STRING");
		story = story.replaceAll("\"Frankenstein", "SOME_TEXT");
		story = story.replaceAll("\\bFrankenstein\\b", "Frankenstein's.monster");
		story = story.replaceAll("\\bTEMPORARY_VALUE\\b", "Frankenstein's");
		story = story.replaceAll("\\bRANDOM_STRING\\b", "Frankenstein,");
		story = story.replaceAll("\\bSOME_TEXT\\b", "\"Frankenstein");
		System.out.println(story);
	}

}
