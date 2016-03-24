package crawler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WordProcessing {
	public static final Pattern IGNORE = Pattern.compile("[a-zA-Z]+");
	
	WordProcessing() {}
	

	public static List<Frequency> wordFrequencies(InputStream in, HashSet<String> stopwords, HashMap<String,Integer> wcommon){
		HashMap<String,Integer> fmap = new HashMap<String,Integer>();
		int totalWords = 0;
		try{
			BufferedReader bf = new BufferedReader(new InputStreamReader(in,"US-ASCII"));
			String line;

			while((line = bf.readLine().toLowerCase())!= null){
				Matcher matcher = IGNORE.matcher(line.trim());
				while(matcher.find()){
					String w = matcher.group();
					if(!stopwords.contains(w)){
						totalWords++;
						Integer reps = (Integer)wcommon.get(w);
						reps = (reps == null)? 1: reps+1;
						wcommon.put(w, reps);
						Integer fnum = (Integer)fmap.get(w);
						fnum = (fnum == null)? 1: fnum+1;
						fmap.put(w,fnum);
					}						
				}
			}

		}catch(Exception e){}
		List<Frequency> results = new ArrayList<Frequency>();

		if(totalWords != 0)
			results = sortFrequencies(fmap);

		results.add(new Frequency("Total Word Count", totalWords));
		return results;		
	}
	
	
	public static List<Frequency> sortFrequencies(HashMap<String,Integer> m){
		List<Frequency> results = new ArrayList<Frequency>();
		for(Map.Entry<String, Integer> fq: m.entrySet())
			results.add(new Frequency(fq.getKey(), fq.getValue()));
		Collections.sort(results, new FrequencyComparator());
		return results;
		
	}
}


//Source: https://stackoverflow.com/questions/3408976/sort-array-first-by-length-then-alphabetically-in-java


class FrequencyComparator implements Comparator<Frequency> {
	@Override
	public int compare(Frequency a, Frequency b) {
		int freqComparison;
		// first compare by frequency; decreasing order
		freqComparison = Integer.compare(b.getFrequency(),a.getFrequency());
		// if frequency count is same, compare alphabetically
		if (freqComparison == 0) { freqComparison = a.getText().compareTo(b.getText()); }
		return freqComparison;
	}
}
