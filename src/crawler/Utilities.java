package crawler;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * A collection of utility methods for text processing.
 * @author: Marissel Llavore (mllavore 26007253)
 * Updated: Jan 18, 2016
 */
public class Utilities {
	public static HashSet<String> readStopWords(File input) {
		HashSet<String> result = new HashSet<String>();
		Scanner scanner = null;
		
		try{
			scanner = new Scanner(input);
			while (scanner.hasNext())
			{
				String newToken = scanner.next();
				result.add(newToken.toLowerCase()); 
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
		return result;
	}

}