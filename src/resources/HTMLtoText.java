package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTMLtoText {
	static int count = 0;
	public static void htmlToText(String url) {
		try {
			
			Document document = Jsoup.connect(url).userAgent("Chrome").timeout(3000).get();
			String fileText = document.text();
			String filename = document.title();
			File parentDir = new File("TextFile");
	      parentDir.mkdir();
	      FileWriter myWriter = new FileWriter(parentDir+"\\"+count+ ".txt");
	      count = count + 1;
	      myWriter.write(filename +"\n"+ fileText);
	      myWriter.close();
	      
//	      System.out.println("Successfully wrote to the file.");
		    

			} catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	
	}
		
	}
	
	public static void main(String[] args) {
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("urls.txt"));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
				htmlToText(line);
			}
			reader.close();
		} catch (IOException e) {
			//
		}
		
	}

}
