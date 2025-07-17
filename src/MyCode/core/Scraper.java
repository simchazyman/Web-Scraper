package src.MyCode.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

import src.MyCode.model.DownloadedPage;
import src.MyCode.model.Result;
import src.MyCode.model.ResultType;



/**
 * You will have 2 threads that will do the actual scraping. They will take the next html String off of
 * the queue and extract all of the useful information, including internal URLs which, in addition to
 * being tracked for informational purposes, need to be added to the crawler's queue. Remember
 * to lock carefully, there is a lot of shared memory here.
 */
public class Scraper implements Runnable {


    // Here are the shared variables.
    URI seedURL;
    BlockingQueue<URI> newURLs;
    BlockingQueue<DownloadedPage> htmlToScrape;
    Set<URI> foundUrls;
    List<Result> allFoundResults;
    AtomicInteger activeScrapers;
    AtomicBoolean shouldQuit;






    public Scraper(URI seedURL, BlockingQueue<URI> newURLs, BlockingQueue<DownloadedPage> htmlToScrape,
    List<Result> allFoundResults, Set<URI> foundUrls, AtomicInteger activeScrapers, AtomicBoolean shouldQuit) {

        this.seedURL = seedURL;
        this.newURLs = newURLs;
        this.htmlToScrape = htmlToScrape;
        this.allFoundResults = allFoundResults;
        this.foundUrls = foundUrls;
        this.activeScrapers = activeScrapers;
        this.shouldQuit = shouldQuit;
    }


    @Override
    public void run() {

        activeScrapers.incrementAndGet(); // this scraper is now active

    

        // Better to make the loop stop without a break command.
        while (!shouldQuit.get()) {
            

            DownloadedPage currentPage = null;

            
            try {

                // Get the next page to scrape.
                currentPage = htmlToScrape.take();
                

                


            } catch (InterruptedException e) {

            // This place will only be reached if there was an interrupt when waiting for an hmtl file
            // to scrape. Nothing will be missed if the program just goes back to the beginning of the loop.
                continue;
            }



            // Scrape and store the results in the designated data structure.
            List<Result> currentFoundResults = scrape(currentPage);


            // Add the results to the shared data structure.
            allFoundResults.addAll(currentFoundResults);

    
            // Update the list of urls to be crawled.
            updateUrlQueue(currentFoundResults);

                


        }
    
        activeScrapers.decrementAndGet(); // mark this scraper as done
        System.out.println(Thread.currentThread().getName() + " exiting.");

    }



    



    /**
     * Searches a page for results that match the patterns stored in the enum.
     * @param html The page to search.
     * @return A List storing all the found results.
     */
    private List<Result> scrape(DownloadedPage html) {


        List<Result> foundResults = new ArrayList<>();

        for (ResultType resultType : ResultType.values()) {


            Matcher matcher = resultType.matcher(html.getHtmlContent());


            while (matcher.find()) {
                Result result = new Result(html.getSourceUrl(), matcher.group(1), resultType);
                foundResults.add(result);
            }

        }


        return foundResults;

    }


    /**
     * Method to update the list of urls to be processed.
     * @param listToCheck
     */
    private void updateUrlQueue(List<Result> listToCheck) {

        for (Result oneResult : listToCheck) {

            URI innerURL = oneResult.getInnerURL(seedURL);
            if (innerURL != null && foundUrls.add(innerURL))
                newURLs.add(innerURL);
        }
    }

    
}


