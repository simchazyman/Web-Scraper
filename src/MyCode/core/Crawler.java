package src.MyCode.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import src.MyCode.model.DownloadedPage;



/**
 * You will have one thread that crawls the site in a BFS manner, starting from www.touro.edu. It 
 * will download a webpage using the next (internal) URL in a queue dedicated to holding the URLs 
 * to crawl, and then put the html in another queue. 
 * 
 * You may not download ANY page that is not in the touro.edu domain.
 * 
 * You will have ONE thread that is dedicated to downloading pages from within the touro.edu
 * domain, and it will always pause between downloading pages. It will remember the time
 * elapsed from the moment it starts downloading a webpage and moment the download is
 * complete, and then it will pause either twice as long as the amount of time required by the
 * previous download, or ten seconds, whichever is greater. 
 */
public class Crawler implements Runnable {
    

    /**
     * Queue of urls to download.
     */
    BlockingQueue<URI> toDownload;


    /**
     * Queue to store the html strings that came from the downloaded URLs.
     */
    BlockingQueue<DownloadedPage> results;


    /**
     * Counter of how many scrapers are still workers.
     */
    AtomicInteger activeScrapers;


    /**
     * Shared boolean to track if the crawler is in middle of sleeping.
     */
    AtomicBoolean isSleeping;


    /**
     * Track if the thread should quit.
     */
    AtomicBoolean shouldQuit;





    public Crawler(BlockingQueue<URI> toDownload, BlockingQueue<DownloadedPage> results,
    AtomicInteger activeScrapers, AtomicBoolean isSleeping, AtomicBoolean shouldQuit) {
        this.toDownload = toDownload;
        this.results = results;
        this.activeScrapers = activeScrapers;
        this.isSleeping = isSleeping;
        this.shouldQuit = shouldQuit;

    }

    
    @Override
    public void run() {

        URI current;

        /*
        * Loop until a condition showing that there are no more urls to be processed.
        * The condition is that both queues are empty because then there is no more place for another URL to be found.
        * The only time there would still be something to process is when a Scraper thread is in middle of processing
        * something that will find more inner urls.
        * This boolean is dependent on two different factors that need to be evaluated together.
        * So it must be down in a synchronized block, not in the while condition.
        */


        while (!shouldQuit.get() && (!toDownload.isEmpty() || activeScrapers.get() > 0)) {





            // Sleep 10 seconds before any iteration.
            isSleeping.set(true);

            sleep10secs();
            isSleeping.set(false);



            try {
                current = toDownload.take();
            } catch (InterruptedException e) {
                
                /*
                This is where the thread might be when it is stopped.
                The other possibility is that the while condition will be false.
                The condition of the while loop is what will determine if the thread should continue,
                so I am making it go back to there.
                */
                continue;
            }
            

            long timeElapsed;
            // take the current url, and process it with the method provided, saving the time elapsed.
            try {
                timeElapsed = downloadURL(current);
            } catch (InterruptedException e) {
                /*
                 * This url needs to be processed. It will be added to the queue to be done later.
                 */
                try {
                    toDownload.put(current);
                    continue;
                } catch (InterruptedException e1) {
                    System.err.println("In the process of scraping " + current.toString() +
                    " there was an interruption, and then another interruption while handling the first one.\n" +
                    "this url will be skipped and not be processed as this is a complex issue.");
                    e1.printStackTrace();
                    continue;


                    // Maybe I will add in more to deal with the interruption later.
                }

            } catch (FileNotFoundException e) {

                System.err.println("The url " + current.toString() + " was not found so will be skipped.");
                continue;
  
            } catch (IOException e) {
                System.err.println("There was an IO error while processing " + current.toString() + 
                ". This url will be skipped.");
                //e.printStackTrace();
                continue;
            }


            isSleeping.set(true);
            sleepBeyond10secs(timeElapsed);
            isSleeping.set(false);
            
            
        }


        System.out.println("Crawler is exiting.");
    }




    /**
     * Downloads the URL and saves the String to the queue.
     * @param url url to be downloaded.
     * @return Time the download took in milliseconds.
     * @throws InterruptedException 
     */

    private long downloadURL(URI url) throws InterruptedException, IOException {


        long startTime = System.currentTimeMillis();

        StringBuilder content = new StringBuilder();


        URL URLFromURI = url.toURL();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(URLFromURI.openStream()))) {
            String line;
            while((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
        }

        String htmlContent = content.toString();

        results.put(new DownloadedPage(url, htmlContent));

        long timeElapsed = System.currentTimeMillis() - startTime;


        // Return the time in milliseconds. A conversion is needed.
        return timeElapsed;

    }



    /**
     * Helper method to make the thread sleep for a correct amount of time.
     * The amount is the total amount*2 minus 10 seconds, because the 10 seconds
     * will be waited at the next iteration of the loop no matter what.
     * @param totalMilliseconds
     */
    private void sleepBeyond10secs(long totalMilliseconds) {

        long timeToSleep = totalMilliseconds * 2 - 10000;
        // If the time is less than 10 seconds, stop the method.
        if (timeToSleep <= 0)
            return;

        long startTime = System.currentTimeMillis();
        
        long endTime = startTime + timeToSleep;
        long remaining;

        // Update the remaining variable and make it determine if the correct amount of time to sleep has passed.
        while ((remaining = endTime - System.currentTimeMillis()) > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                // The sleep got interrupted, and now the thread will be put to sleep again.
            }
        }

    }


    /**
     * Method to make the thread sleep for 10 seconds even with interruptions.
     */
    private void sleep10secs() {
        long endTime = System.currentTimeMillis() + 10000;
        long remaining;

        while ((remaining = endTime - System.currentTimeMillis()) > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                // The sleep got interrupted, and now the thread will be put to sleep again.
            }
        }
    }
}
