package resources;

import java.util.HashMap;
import java.util.StringTokenizer;

import textprocessing.In;
import textprocessing.Out;
import textprocessing.StdOut;
import textprocessing.TST;

public class searchEngine {
	
	static final String URL_FILE = "urls.txt";

	public static HashMap<Integer,String> indexURLS()
	{	
		int i = 0;
		HashMap<Integer,String> UrlIndex = new HashMap<Integer,String>();	
		In in = new In("urls.txt");
		
        while (!in.isEmpty()) 
        {
        	
        	String text = in.readLine();
        	UrlIndex.put(i,text);
        	i++;	    	
        }
		return UrlIndex;
	}
	
	
	public static TST<Integer> getTST(String finalPath) 
	{	
		int j = 0;
		TST<Integer> tst = new TST<Integer>();	
		In in = new In(finalPath);
		
        while (!in.isEmpty()) 
        {
        	String text = in.readLine();
	        if (j == 0) {
	            	 
	        	j = 1;
	            continue;
	            	 
	        } else if (j == 1) {  
	        	j = 0; 
	        	
	        	StringTokenizer st  = new StringTokenizer(text, " ");
	        	while (st.hasMoreTokens()) { 
	    			
	    			String word = st.nextToken();
	    			word = word.toLowerCase();
	    			System.out.println(word);
	    			
	        		if(tst.contains(word)) {
	        			
	        			tst.put(word, tst.get(word)+1);
	        			System.out.println("true");
	        			
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
	
	public static void main(String[] args) {
		
		
		
		HashMap<Integer,String> urlIndex = new HashMap<Integer, String>();
		urlIndex = indexURLS();
		StdOut.println(urlIndex);
		
		

		
		
	}

}
