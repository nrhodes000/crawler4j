package edu.uci.ics.crawler4j.crawler;

import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Set;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|mp3|mp4|zip|gz|woff2))$");

    private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png))$");

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "https://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) throws IOException {
        String href = url.getURL().toLowerCase();
        boolean shouldVisitBool = (!FILTERS.matcher(href).matches() || imgPatterns.matcher(href).matches()) && href.startsWith("https://www.wsj.com/");
        boolean doesContainTargetUrl = url.getURL().contains("www.wsj.com");
        String okOrNotOk = doesContainTargetUrl ? "OK" : "N_OK";
        FileWriter fw = new FileWriter("urls_wsj.csv", true);
        Object[] params = new Object[]{url.getURL(), okOrNotOk};
        String msg = MessageFormat.format("{0},{1}\n", params);
        fw.write(msg);
        fw.flush();
        fw.close();
        return shouldVisitBool;
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) throws IOException {
        String url = page.getWebURL().getURL();
//        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

//            System.out.println("Text length: " + text.length());
//            System.out.println("Html length: " + html.length());
//            System.out.println("Number of outgoing links: " + links.size());

            String fileSize = formatIntToString(page.getContentData().length);
            String fileType = page.getContentType().replace("; charset=utf-8", "");
            Object[] params = new Object[]{url, fileSize, links.size(), fileType};
            String msg = MessageFormat.format("{0},{1},{2},{3}\n", params);

            FileWriter fw = new FileWriter("./visit_wsj.csv", true);

            fw.write(msg);
            fw.flush();
            fw.close();
        }
        else if (page.getParseData() instanceof BinaryParseData) {
            String fileSize = formatIntToString(page.getContentData().length);
            String fileType = page.getContentType().replace("; charset=utf-8", "");
            Object[] params = new Object[]{url, fileSize, 0, fileType};
            String msg = MessageFormat.format("{0},{1},{2},{3}\n", params);

            FileWriter fw = new FileWriter("./visit_wsj.csv", true);

            fw.write(msg);
            fw.flush();
            fw.close();
        }
    }

    private String formatIntToString(int fileSize) {
        int mbSize = fileSize / 1000;
        String mbSizeString = Integer.toString(mbSize);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < mbSizeString.length(); i++) {
            if(mbSizeString.charAt(i) != ',') {
                sb.append(mbSizeString.charAt(i));
            }
        }
        sb.append("KB");
        return sb.toString();
    }
}
