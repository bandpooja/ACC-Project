package accwebsearchengine;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import Spell_check.SpellChecker1;
import textprocessing.*;

import static java.util.stream.Collectors.toMap;

public class SearchEngine {
	
	public final static String hashMapLocation = "hashmap\\";
	public final static String stopWordsLocation = "stop-words.txt";
	public final static String urlLocation = "urls.txt";
	public final static String textFileLocation = "TextFile\\";
	
	
	
	public static String[] getKeywords(String inputStr) {
		int i = 0;
		In in = new In(stopWordsLocation);
		inputStr = inputStr.toLowerCase();

		while (!in.isEmpty()) {

			String text = in.readLine();
			text = text.toLowerCase();
			text = "\\b" + text + "\\b";
			inputStr = inputStr.replaceAll(text, "");
		}


		StringTokenizer st = new StringTokenizer(inputStr, " ");
		String[] keyWords = new String[st.countTokens()];

		while (st.hasMoreTokens()) {
			keyWords[i] = st.nextToken();
			i++;
		}
		return keyWords;
	}

	/**
	 * We do the URL indexing by using the URLs from the file and insertion into a hashmap is done
	 * 
	 * @return - a hashmap having the indexed URLs
	 */
	public static HashMap<Integer, String> indexURLS() {
		int i = 0;
		HashMap<Integer, String> UrlIndex = new HashMap<Integer, String>();
		In in = new In(urlLocation);

		while (!in.isEmpty()) {

			String text = in.readLine();
			UrlIndex.put(i, text);
			i++;
		}
		// piyush
		System.out.println("URLS Indexed -> " + UrlIndex + "\n");
		return UrlIndex;
	}

	/**
	 * This method is responsible for creating TST of each text file
	 * 
	 * @param finalPath
	 * @return
	 */
	public static TST<Integer> getTST(String finalPath) {
		int j = 0;
		TST<Integer> tst = new TST<Integer>();
		In in = new In(finalPath);
 
		while (!in.isEmpty()) {
			String text = in.readLine();
			if (j == 0) {

				j = 1;
				continue;

			} else if (j == 1) {
				j = 0;

				StringTokenizer st = new StringTokenizer(text, " ");
				while (st.hasMoreTokens()) {

					String word = st.nextToken();
					word = word.toLowerCase();
					// System.out.println(word);

					if (tst.contains(word)) {

						tst.put(word, tst.get(word) + 1);
						// System.out.println("true");

					} else {

						tst.put(word, 1);
					}
				}
			}
		}

//        for (String key : tst.keys()) {
//            System.out.println(key + " " + tst.get(key));
//        }

		return tst;
	}

	/**
	 * This method is responsible to find the the occurrence of the keywords in each
	 * text file and get the count
	 * 
	 * @param keyWords
	 * @return
	 */
	public static HashMap<Integer, Integer> getFreqList(String[] keyWords) {

		// Map each text file to its corresponding number into an arraylist
		ArrayList<String> textList = new ArrayList<>();
		HashMap<Integer, Integer> freqList = new HashMap<Integer, Integer>();

		File folder = new File(textFileLocation);
		File[] files = folder.listFiles();

		for (File file : files) {

			String myURL = file.getName();
			// myURL = myURL.substring(0, (myURL.length()-4));
			textList.add(myURL);

		}

		for (int i = 0; i < textList.size(); i++) {

			String filePath = textFileLocation;
			String fileName = textList.get(i);
			String finalPath = filePath + fileName;

//			 System.out.println(fileName);

			String tempFileIndex = fileName.substring(0, (fileName.length() - 4));
			int fileIndex = Integer.parseInt(tempFileIndex);
//			System.out.println("index: " + fileIndex);

			TST<Integer> tst = new TST<Integer>();
			tst = SearchEngine.getTST(finalPath);
			// System.out.println(tst);

//	        for (String key : tst.keys()) {
//	        	System.out.println(key + " " + tst.get(key));
//	        }

			int counter = 0;

			for (String str : keyWords) {
				if (tst.contains(str)) {

					int count = tst.get(str);
					// System.out.println(str+" "+count);
					counter = counter + count;
				}
			}

			freqList.put(fileIndex, counter);
		}

		// System.out.println(freqList);
		return freqList;
	}

	/**
	 * It is used to sort the hashmap having frequency list and return the sorted hashmap
	 * 
	 * @param freqList
	 * @return
	 */
	public static HashMap<Integer, Integer> sortHashMap(HashMap<Integer, Integer> freqList) {
		// piyush
		HashMap<Integer, Integer> sortedFreqList = freqList.entrySet().stream()
		        .sorted(Comparator.comparingInt(e -> -e.getValue()))
		        .collect(Collectors.toMap(
		                Map.Entry::getKey,
		                Map.Entry::getValue,
		                (a, b) -> { throw new AssertionError(); },
		                LinkedHashMap::new
		        ));
		System.out.println("Unsorted freq list (key, value=frequency) ->" + freqList);
		System.out.println("sorted freq list (key, value=frequency) -> " + sortedFreqList);
		return sortedFreqList;
	}

	/**
	 * The hashmap for frequncy list for Page ranking is stored.
	 * 
	 * @param freqList
	 * @param keyWords
	 */
	public static void storeHashMap(HashMap<Integer, Integer> freqList, String[] keyWords) {

		Sort.mergeSort(keyWords);
		String fileName = "";
		
		for (String str : keyWords) {

			fileName = fileName + str + "_";
		}

		fileName = fileName + ".dat";

//		 System.out.println(fileName);

		String filePath = hashMapLocation;
		String finalPath = filePath + fileName;

		try {

			FileOutputStream fileOut = new FileOutputStream(finalPath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(freqList);
			out.close();
			fileOut.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * This method is used to get the hashmap used for page ranking is retreived which uses the frequency list of the searched keywords
	 * 
	 * @param keyWords
	 * @return
	 */
	public static HashMap<Integer, Integer> retreiveHashMap(String[] keyWords) {

		Sort.mergeSort(keyWords);

		String fileName = "";

		for (String str : keyWords) {

			fileName = fileName + str + "_";
		}

		fileName = fileName + ".dat";
		String filePath = hashMapLocation;
		String finalPath = filePath + fileName;

		HashMap<Integer, Integer> freqList = new HashMap<Integer, Integer>();
		freqList = null;

		try {

			FileInputStream fileIn = new FileInputStream(finalPath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			freqList = (HashMap<Integer, Integer>) in.readObject();
			in.close();
			fileIn.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		// displaying the sorted hashmap displaying the frequency
		System.out.println("Sorted frequency list (key, value=frequency) -> " + freqList);
		return freqList;

	}
//	

	/**
	 * Main method which is used to do the complete flow of the process
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		SpellChecker1 c= new SpellChecker1();

		File dir = new File("TextFile");
		File[] fileslist = dir.listFiles();
		for (File file : fileslist) {
			if (file.isFile()) {
				c.loadDictionary("TextFile/" + file.getName());
				
			}
		}

		
		
		String[] keyWords = SearchEngine.getKeywords(c.Correct_String());
		System.out.println(c.Correct_String());
		Sort.mergeSort(keyWords);
		// generating file name with .dat extension
		String fileName = "";
		for (String str : keyWords) {

			fileName = fileName + str + "_";
		}

		fileName = fileName + ".dat";

		boolean fileExist = false;

		File folder = new File(hashMapLocation);
		File[] files = folder.listFiles();

		for (File file : files) {

			String myFileName = file.getName();
			if (myFileName.compareTo(fileName) == 0) {
				fileExist = true;
				break;
			}

		}

		HashMap<Integer, String> urlIndex = new HashMap<Integer, String>();
		urlIndex = SearchEngine.indexURLS();
		HashMap<Integer, Integer> freqList = new HashMap<Integer, Integer>();
		
		
		long start1=0, finish1=0,total1=0,start2=0,finish2=0,total2=0;
		// if the cache file is already present
		if (fileExist == true) {
			
			start1=System.currentTimeMillis();
			
			freqList = SearchEngine.retreiveHashMap(keyWords);

			System.out.println("\nTop Ten Search Results for \"" + c.Correct_String() + "\" are:\n");

			int j = 0;
			for (HashMap.Entry<Integer, Integer> entry : freqList.entrySet()) {

				if (j < 10) {

					// System.out.println(entry.getKey() + " = " + entry.getValue());
					int urlKey = entry.getKey();
					System.out.println(urlIndex.get(urlKey));
					j++;

				} else {
					break;
				}
			}
			// calculating and displaying the time taken
			finish1=System.currentTimeMillis();
			total1 = finish1 - start1;
			System.out.println("\nTime taken for search WITH already present cache file (" + fileName + ") = " + total1 + " ms");
		}
		// if the cache file is not present
		else if (fileExist == false) {
			
			start2=System.currentTimeMillis();
			
			freqList = SearchEngine.getFreqList(keyWords);
			freqList = SearchEngine.sortHashMap(freqList);

			SearchEngine.storeHashMap(freqList, keyWords);

			System.out.println("\nTop Ten Search Results for \"" + c.Correct_String() + "\" are:\n");
			int j = 0;

			for (HashMap.Entry<Integer, Integer> entry : freqList.entrySet()) {

				if (j < 10) {

					// System.out.println(entry.getKey() + " = " + entry.getValue());
					int urlKey = entry.getKey();
					System.out.println(urlIndex.get(urlKey));
					j++;

				} else {

					break;
				}
			}
			// calculating and displaying the time taken
			finish2 = System.currentTimeMillis();
			total2 = finish2 - start2;
			System.out.println("\nTime taken for search WITHOUT cache file = " + total2 + " ms");
			
			// Steps for caching implemented
			/*
			String is input by the user and spell check is done
			The Stop words (words which are avoided for the search so that we can focus on the important words) are removed from the string
			the string is tokenized
			it is then sorted
			fileNAME is generated for the search
			checking if the file is already present or not
			if the file exists
				- urls are indexed using hashmap and assigned a key
				- hashmap used for page ranking is retreived which uses the frequency list of the searched keywords
				- using the sorted list (hashmap), the top results for the search are displayed
			if the file is NOT present
				- urls are indexed using hashmap and assigned a key
				- the frequency list of the keywords searched is obtained and stored in Hashmap
				- the Hashmap is sorted and the file created is stored
				- using the sorted list (hashmap), the top results for the search are displayed
			Time of the caching process is calculated.
			*/
		}

	}

}
