package crawler;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;



public class CrawlerController {
	 // private static final Logger logger = LoggerFactory.getLogger(CrawlerController.class);
	   

	 
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/Users/dinorahcarrion/Documents/datacrawl";
        int numberOfCrawlers = 7;
        

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder+"/crawler2");
        config.setUserAgentString("UCI Inf141-CS121 crawler 26007253 34161706 14593176 35657319");
        config.setPolitenessDelay(2000);
        config.setMaxPagesToFetch(15000);
        config.setResumableCrawling(true);
        config.setMaxDepthOfCrawling(-1);
        config.setConnectionTimeout(2000);
        config.setOnlineTldListUpdate(true);
        config.setFollowRedirects(true);
        config.setIncludeBinaryContentInCrawling(false);
        
        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
         controller.addSeed("http://www.informatics.ics.uci.edu/");
         controller.addSeed("http://www.cs.uci.edu/");
         controller.addSeed("http://sli.ics.uci.edu/");
         controller.addSeed("http://www.ics.uci.edu/");
        
        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        long start = System.currentTimeMillis();
        controller.start(Crawler.class, numberOfCrawlers);       
        controller.shutdown();
        controller.waitUntilFinish();
        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        System.out.print("Execution time is " + formatter.format((end - start) / 1000d) + " seconds");
        
        /* So after the 7 crawlers have finished crawling, I try to get a list of seen URLS
         * using the crawl function that we're asked to create for testing purposes. The only problem
         * with this is that there isn't actually a crawler instance to work with here so I can't call
         * this crawler function on anything. That's why as of right now, I've overloaded the onBeforeExit()
         * function in Crawler in order to print out seen URLs and seen subdomains. But as we are again working
         * with several crawlers, this ends up printing out the same set of urls and subdomains since all
         * crawlers are asked to print these things out when they finish
         */
         Crawler.crawl("http://www.ics.uci.edu/");
        
    }
    
    
}
