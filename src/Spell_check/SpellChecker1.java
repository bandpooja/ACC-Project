package Spell_check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SpellChecker1 {

	private static ArrayList words;
	private static int c = 0;
	static String store[] = new String[100];

	static String wantToContinue = "yes";
	static String word;
	String suggestion;
	
	static String wordlist[];
	static String final_query;

	public SpellChecker1() {
		words = new ArrayList();	
	} // main
	// SpellChecker
	
	
	public static String Correct_String() throws IOException
	{
		 BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		
		//to take input from the user
		while (wantToContinue.equalsIgnoreCase("yes")) {
			// ask the user for a word to spell check.
			System.out.print("Please enter your search. >> ");
			
			word = input.readLine().toLowerCase();
			wordlist = word.split(" ");
			StringBuilder sb = new StringBuilder("");
			int j = 0;

			// get the closest suggestion for the sentence entered.
			SpellChecker1.findClosestMatch(wordlist);

			for (int i = 0; i < wordlist.length; i++) {
				if (!wordlist[i].equals(store[i])) {
					j++;
				}
				sb.append(store[i] + " ");

			}

				if (j == 0) {
					final_query = word;
					wantToContinue = "Done";
				}

//				if (wantToContinue.equalsIgnoreCase("Done"))
//					final_query = word;
				if (j > 0) {
					System.out.print("Did you mean??  " + sb.toString() + " YES/NO >>");

				}
				
				if (!wantToContinue.equalsIgnoreCase("Done"))
					wantToContinue = input.readLine();

				if (wantToContinue.equalsIgnoreCase("NO")) {
					System.out.print("Could you please spell it again ? ");
					System.out.println();
					wantToContinue = "YES";
				} else {

					final_query = sb.toString();
					wantToContinue = "NO";

				}
			}

		return final_query;
		
	}

	public static void loadDictionary(String fileName) {
		BufferedReader reader;
		FileReader freader;
		String holder;

		try {
			// create a new file reader to open the given input.
			freader = new FileReader(fileName);
			reader = new BufferedReader(freader);

			holder = reader.readLine();

			// continue until the end of the file.
			while (holder != null) {
				// add the read-in value to the array of words.
				String[] key;
				key = holder.split(" ");
				for (int i = 0; i < key.length; i++) {
					words.add(key[i].toLowerCase());
				}

				holder = reader.readLine();
			} // while
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: That file cannot be found.");
		} catch (IOException e) {
			System.out.println("ERROR: The file may be locked. It cannot be read from properly.");
		} // catch
	} // loadDictionary

	// find minimum operation required for word correction
	private static int findTheMinimum(int a, int b, int c) {
		int minimum = a;

		// find the minimum of the three values a, b, and c.
		if (b < minimum) {
			minimum = b;
		} 

		if (c < minimum) {
			minimum = c;
		} 

		return minimum;
	} 

	private static int findDifference(String inputWord, String testWord) {
		int n, m;
		char word1Holder, word2Holder;
		int above, left, diagonal, cost;

		n = inputWord.length();
		m = testWord.length();

		// if one of the words is empty (a length of zero), the levenshtein distance
		// is simply the length of the input word.
		if (n == 0) {
			return m;
		} // if

		if (m == 0) {
			return n;
		} // if

		// create a new matrix with n columns and m rows.
		int[][] testMatrix = new int[n + 1][m + 1];

		// instantiate the columns with the numbers 0..n
		for (int x = 0; x <= n; x++) {
			testMatrix[x][0] = x;
		} // for

		// instatiate the columns with the numbers 0..m
		for (int x = 0; x <= m; x++) {
			testMatrix[0][x] = x;
		} // for

		// go through the entire matrix. get the character at i column of the input word and j  row for test word
		
		for (int i = 1; i <= n; i++) {
			word1Holder = inputWord.charAt(i - 1);

			for (int j = 1; j <= m; j++) {
				word2Holder = testWord.charAt(j - 1);

				// find the values of the cell directly above and to the left of the current
				// cell.
				// add one to each of these variables.
				above = testMatrix[i - 1][j] + 1;
				left = testMatrix[i][j - 1] + 1;

				// if the two characters are equal, their cost is zero. find the value of the
				// diagonal.
				if (word1Holder == word2Holder) {
					diagonal = testMatrix[i - 1][j - 1];
				} else {
					// if they are not, the cost is 1. find the value of the diagonal + 1.
					diagonal = testMatrix[i - 1][j - 1] + 1;
				} // else

				// find the minimum of the three values (above, left, and diagonal.
				testMatrix[i][j] = findTheMinimum(above, left, diagonal);
			} // for
		} // for

		// return the value of the cell to the bottom right of the matrix. this is the
		// levenshtein distance.
		return testMatrix[n][m];
	} // findDifference

	public static void findClosestMatch(String[] inputWord) {
		int smallestWord = -1, smallestDistance = 100, holder;
		String input;

		// go through each word to find the word with the least
		// Levenshtein distance.
		// if the current word in the list has a shorter
		// Levenshtein distance than the last minimum, set it to the minimum.
		for (int i = 0; i < inputWord.length; i++) {
			for (int x = 0; x < words.size(); x++) {
				holder = findDifference(inputWord[i], words.get(x).toString());

				if (holder < smallestDistance) {
					smallestDistance = holder;
					smallestWord = x;
				} // if
			} // for

			// if the minimum distance is 0, the word is spelled correctly
			if (smallestDistance == 0) {
				// System.out.println("SPELLED CORRECTLY");
				store[i] = inputWord[i];
				c++;
			} else {
				// else, return the closest matching word.
				store[i] = words.get(smallestWord).toString();
				// System.out.print("Did you mean " + words.get(smallestWord).toString() + " for
				// input " + inputWord[i]+ "? YES/NO >> ");

			}
			smallestWord = -1;
			smallestDistance = 100;

		}

		if (c == inputWord.length) {
			System.out.println("Spelled correctly");

		}
		c = 0;
		
	} // findClosestMatch

	public void addToDictionary(String word) {
		// add a word to the list of words.
		words.add(word);
	} // addToDictionary

	public static void main(String args[]) throws FileNotFoundException {


		SpellChecker1 check = new SpellChecker1();
		
}
}
