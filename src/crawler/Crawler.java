package crawler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.sql.*;

public class   extends WebCrawler{
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|bmp|js|gif|jpg"
			+ "|png|mp3|mp4|mp2|zip|gz|iso|bigwig|bw|xls|xlsx|ppt|pptx|pdf|exe|swf|bin|eps|tex|wav|avi|mov|jar|dmg|tar|rar|doc|docx|epub|data))$");
	
	private static Connection conn = DBConnection.connect();
	//private static ConcurrentHashMap<String,Integer> seenSubdomains = new ConcurrentHashMap<String, Integer>();
	private static ConcurrentHashMap<String, Integer> seenUrls = new ConcurrentHashMap<String, Integer>();
	

	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://www.ics.uci.edu/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& !referringPage.getWebURL().getURL().toLowerCase().equals(href)
				&& url.getDomain().toLowerCase().equals("uci.edu") 
				&& !url.getSubDomain().toLowerCase().contains("duttgroup.ics")
				&& url.getSubDomain().toLowerCase().contains("ics")
				&& !href.contains("?")
				&& !seenUrls.contains(url); 
		       
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		int docid = page.getWebURL().getDocid();
		String domain = page.getWebURL().getDomain();
		String subDomain = page.getWebURL().getSubDomain();
		System.out.println("crawlerid: " + this.getMyId());
		System.out.println("doc id: " + docid);
		System.out.println("URL: " + url);
		System.out.println("domain: " + domain);
		System.out.println("subDomain: " + subDomain);

		

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();
			logger.debug("Text length: {}", text.length());
			logger.debug("Num of Outgoing Links: {}", links);
			try {
				PreparedStatement data = conn.prepareStatement("INSERT INTO urldb values(0,?,?,?)");
				data.setString(1, url);
				data.setString(2, text);
				data.setInt(3,text.length());
				data.executeUpdate();
				data.closeOnCompletion();
//				if (seenSubdomains.containsKey(subDomain)){
//					int currentValue = (int) seenSubdomains.get(subDomain);
//					seenSubdomains.put(subDomain,currentValue+1);
//				}else {
//					seenSubdomains.put(subDomain, 1);	
//					//System.out.println("	added new key/value pair to subdomains dict: " + subDomain + " " + seenSubdomains.get(subDomain));
//				}
			} catch (SQLException e) {
				//e.printStackTrace();
			}

		}
	}

	/**
	 * This method is for testing purposes only. It does not need to be used
	 * to answer any of the questions in the assignment. However, it must
	 * function as specified so that your crawler can be verified programatically.
	 * 
	 * This methods performs a crawl starting at the specified seed URL. Returns a
	 * collection containing all URLs visited during the crawl.
	 */
	public static Collection<String> crawl(String seedURL) {
		ArrayList<String> result = new ArrayList<>();
		Iterator<String> it = seenUrls.keySet().iterator();

		while(it.hasNext()){
			String key = it.next();
			result.add(key);
		}

		return result;
	}
}