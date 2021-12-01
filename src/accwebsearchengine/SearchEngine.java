package accwebsearchengine;

import java.io.*;
import java.util.*;

import textprocessing.*;

import static java.util.stream.Collectors.toMap;

public class SearchEngine {
	
	// links to file locations
	public final static String hashMapLocation = "C:\\Users\\vanam\\Downloads\\MynewProject\\MynewProject\\hashmap\\";
	public final static String stopWordsLocation = "C:\\Users\\vanam\\Downloads\\MynewProject\\MynewProject\\src\\accwebsearchengine\\stop-words.txt";
	public final static String urlLocation = "C:\\Users\\vanam\\Downloads\\MynewProject\\MynewProject\\urls.txt";
	public final static String textFileLocation = "C:\\Users\\vanam\\Downloads\\MynewProject\\MynewProject\\TextFile\\";
	
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

		// System.out.println(inputStr);

		StringTokenizer st = new StringTokenizer(inputStr, " ");
		String[] keyWords = new String[st.countTokens()];

		while (st.hasMoreTokens()) {
			keyWords[i] = st.nextToken();
			i++;
		}
		return keyWords;
	}

	/**
	 * This methods indexes the url and inserts them into hashmap
	 * 
	 * @return urlIndex - hashmap containing the indexed urls
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
		return UrlIndex;
	}

	/**
	 * This method creates the TST for each file
	 * 
	 * @param finalPath - the path of the text file for which TST is to be created
	 * @return tst - the trie for the particular file
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

		return tst;
	}

	/**
	 * This method calculates the number of occurances of search word in the trie
	 * and inserts the file path and the frequency in hash map
	 * 
	 * @param keyWords - tokenized input query
	 * @return freqList - the hashmap containing the file name and frequency in key, value pairs
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
		return freqList;
	}

	/**
	 * This method sorts the  hashmap 
	 * 
	 * @param freqList - the hashmap containing the file name and frequency
	 * @return sortedHashMap - the sorted hashmap 
	 */
	public static HashMap<Integer, Integer> sortHashMap(HashMap<Integer, Integer> freqList) {
		HashMap<Integer, Integer> sortedHashMap = freqList.entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(toMap(k -> k.getKey(), k -> k.getValue(), (k1, k2) -> k2, LinkedHashMap::new));

		return sortedHashMap;
	}

	/**
	 * This method saves the sorted hash file to memory
	 * 
	 * @param freqList - sorted hash map  
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
	 * This method retrieves the sorted hash map from memory
	 * 
	 * @param keyWords - tokenized input
	 * @return - the hashmap retrieved from memory
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

		return freqList;

	}

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		System.out.println("Enter your search: ");
		String mySearch = sc.nextLine();
		sc.close();

		String[] keyWords = SearchEngine.getKeywords(mySearch);
		Sort.mergeSort(keyWords);

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

		if (fileExist == true) {

			freqList = SearchEngine.retreiveHashMap(keyWords);

			System.out.println("\nTop Ten Search Results for \"" + mySearch + "\" are:\n");

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

		} else if (fileExist == false) {

			freqList = SearchEngine.getFreqList(keyWords);
			freqList = SearchEngine.sortHashMap(freqList);

			SearchEngine.storeHashMap(freqList, keyWords);

			System.out.println("\nTop Ten Search Results for \"" + mySearch + "\" are:\n");
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

		}

	}

}
