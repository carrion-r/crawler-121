package crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.uwyn.jhighlight.tools.FileUtils;

import edu.uci.ics.crawler4j.url.TLDList;

import java.sql.*;
public class ProcessResults {
	private final static Path subpath = Paths.get("/Users/dinorahcarrion/Documents/frequencyData/Subdomains.txt");
	private final static Path fpath = Paths.get("/Users/dinorahcarrion/Documents/frequencyData/frequencies.txt");
	private final static Path wordspath =  Paths.get("/Users/dinorahcarrion/Documents/frequencyData/CommonWords.txt");
	private final static HashSet<String> stopwords = Utilities.readStopWords(new File("/Users/dinorahcarrion/Documents/workspace/crawler/stopwords.txt"));
	private static HashMap<String, Integer> seenWords = new HashMap<String, Integer>();
	private static Connection conn = DBConnection.connect();
    /*ADD FUNCTIONALITY FOR USER*/
	public static void processDB() {

		
		try{
			
			if(!Files.exists(fpath.getParent()))
				Files.createDirectories(fpath.getParent());

			if(!Files.exists(wordspath.getParent()))
				Files.createDirectories(wordspath.getParent());
		    
			Files.deleteIfExists(wordspath);
			Files.deleteIfExists(fpath);
		    Files.createFile(wordspath);	
		    Files.createFile(fpath);
			
		}catch(IOException io){}

		Statement statement = null ;
		ResultSet result = null;

		try{
			int processed = 0;
			conn.setAutoCommit(false);
			statement = conn.createStatement();
		    statement.setFetchSize(100);
			result = statement.executeQuery("SELECT url,page_data,size FROM urldb WHERE size > 0");
			
			
			while(result.next()){
				   ++processed;
				    System.out.println("processed total" + processed);
					String url = result.getString("url");
					InputStream in = result.getAsciiStream(2);
					List<Frequency> fl = WordProcessing.wordFrequencies(in,stopwords,seenWords);
					Files.write(fpath,new String("URL: "+url+"\n").getBytes(StandardCharsets.US_ASCII), StandardOpenOption.APPEND);
					for(Frequency f: fl){
						Files.write(fpath, f.toString().getBytes(StandardCharsets.US_ASCII), StandardOpenOption.APPEND);
					}	
			}
		} catch (SQLException | IOException e) {
			System.out.println("SQL Exception");

		}finally{
			
			try {
				for(Frequency f: WordProcessing.sortFrequencies(seenWords))
					Files.write(wordspath,f.toString().getBytes(StandardCharsets.US_ASCII), StandardOpenOption.APPEND);
				
				if(statement != null)
					statement.close();
				if(conn != null){
					conn.close();
				}
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}


		}
	}
	
	public static String subDomains(String u){
		URL url;
		String subDomain = "";
		try {
			url = new URL(u);
			String host = url.getHost();
			subDomain = host.substring(0, host.indexOf(".uci"));
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + u);
		}
		return subDomain;
		
	}
	
	public static void subDomainsFreq(){
		Statement stm = null;
		ResultSet rs = null;
		

		try{
			if(!Files.exists(subpath.getParent()))
				Files.createDirectories(subpath.getParent());
			
			if(!Files.exists(subpath))
				Files.createFile(subpath);
			
			stm = conn.createStatement();
			stm = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT subdomain,count(*) AS frequency FROM urldb GROUP BY subdomain ORDER BY subdomain";
			rs = stm.executeQuery(query);
			while(rs.next()){
				
				String r = rs.getString(1) + ", " + Integer.toString(rs.getInt(2))+ "\n";
				System.out.println(r);
				Files.write(subpath,r.getBytes(StandardCharsets.US_ASCII), StandardOpenOption.APPEND);
			}
		}catch(SQLException se){} catch (IOException e) {
			
			e.printStackTrace();
		}finally{stm = null;}
	}
	/*Method used to calculate the biggest page*/
	public static void findBiggest() throws IOException{
		InputStream input = new FileInputStream("/Users/dinorahcarrion/Documents/frequencyData/frequencies.txt");
        int biggest = 0;
        BufferedReader read =  new BufferedReader(new InputStreamReader(input,"US-ASCII"));
        String line;

		while((line = read.readLine())!= null){
			if(line.startsWith("Total Word Count:")){
				String t = line.substring(line.lastIndexOf(":")+1).trim();
				int total =Integer.parseInt(t);
				if(biggest < total)
					biggest = total;
				System.out.println(total);
			}
		}
		System.out.println("Biggest" + biggest);
        read.close();
	}
	public static void main(String[] args) throws Exception{
		///DBConnection.addSubDomains(conn);
		//ProcessResults.subDomainsFreq();
	   // ProcessResults.findBiggest();
		ProcessResults.processDB();
	}
}
