package edu.uci.ics.crawler4j.crawler;

import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.File;
import java.io.FileWriter;

public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "/data/crawl/root";
        int numberOfCrawlers = 7;

        File fetchFile = new File("./fetch_wsj.csv");
        if(fetchFile.exists()) {
            fetchFile.delete();
        }
        fetchFile.createNewFile();

        File visitFile = new File("./visit_wsj.csv");
        if(visitFile.exists()) {
            visitFile.delete();
        }
        visitFile.createNewFile();

        File urlsFile = new File("./urls_wsj.csv");
        if(urlsFile.exists()) {
            urlsFile.delete();
        }
        urlsFile.createNewFile();

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);

        // Instantiate the controller for this crawl.
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // For each crawl, you need to add some seed urls. These are the first
        // URLs that are fetched and then the crawler starts following links
        // which are found in these pages
        controller.addSeed("https://www.wsj.com/");

        // The factory which creates instances of crawlers.
        CrawlController.WebCrawlerFactory<MyCrawler> factory = MyCrawler::new;

        // Start the crawl. This is a blocking operation, meaning that your code
        // will reach the line after this only when crawling is finished.
        controller.start(factory, numberOfCrawlers);
    }
}
