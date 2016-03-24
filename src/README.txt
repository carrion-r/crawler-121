This project uses maven, mysql, and the mysql-connector-java-5.1.38 jar

We implemented a MySQL database to store the results of our crawler.

https://drive.google.com/a/uci.edu/file/d/0Bx5IdMi7QrRZVTNtdkwzc2kxVkk/view?usp=sharing

Because we had to restart our crawler so many times, we decided to wait until we had finished crawling
to process our data by executing the ProcessResults.java class.
Previously, we had set it up so it would calculate frequencies and common words as it was crawling but we kept loosing our results when the crawler
exited or we had to forcefully stop it. For that reason, ConcurrentHashMap seenUrls in our Crawler.java will only store seen URLs 
that the crawler has visited during that run particular run not. 

In our case, it doesn't affect our data because we emposed an uniqueness constraint on our url column.

Once we had finished with our crawler, we executed DBConnection.addSubDomains(Connection c).
This method queried our database and for each url it would calculate its subdomain and update the subdomain column value.

Then to calculate how many subdomains we crawled thru and their frequencies, we executed ProcessResults.subDomainsFreq().

You will find these two methods commented out.

Additionally, I am hosting the SQL database in AWS.

The methods in ProcessResults.java create files and store outputs. These file directories are based on my file structure, 
so for them to work, you should modify these strings so they match your home directory.

private final static Path subpath = Paths.get("/Users/dinorahcarrion/Documents/frequencyData/Subdomains.txt");
private final static Path fpath = Paths.get("/Users/dinorahcarrion/Documents/frequencyData/frequencies.txt");
private final static Path wordspath =  Paths.get("/Users/dinorahcarrion/Documents/frequencyData/CommonWords.txt");
private final static HashSet<String> stopwords = Utilities.readStopWords(new File("/Users/dinorahcarrion/Documents/workspace/crawler/stopwords.txt"));

The frequencies.txt file contains the frequencies per url.

The CommonWords.txt file in the java folder contains all the words weve found.