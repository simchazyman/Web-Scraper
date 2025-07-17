package src.MyCode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import src.MyCode.core.Crawler;
import src.MyCode.core.Scraper;
import src.MyCode.core.UI;
import src.MyCode.model.DownloadedPage;
import src.MyCode.model.Result;

/**
 * Main thread.
 */
public class Main {


    public static void main(String[] args) {

        URI seedURL = null;
        try {
            // Put the seed URL here.
            seedURL = new URI("https://www.example.com");

        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        
        /**
         * Atomic integer to keep track of when the threads are active.
         */
        AtomicInteger activeScrapers = new AtomicInteger(0);


        /**
         * Boolean to track if the crawler thread is sleeping.
         */
        AtomicBoolean isCrawlerSleeping = new AtomicBoolean(false);


        /**
         * Atomic boolean to coordinate quitting the threads.
         */
        AtomicBoolean shouldQuit = new AtomicBoolean(false);


        
        
        


        /**
         * Queue of urls to be processed
         */
        BlockingQueue<URI> urlsQueue = new LinkedBlockingQueue<>();



        /**
         * List of the html pages to be scraped.
         */
        BlockingQueue<DownloadedPage> htmlsQueue = new LinkedBlockingQueue<>();


        /**
         * Set to stop duplicate urls.
         */
        Set<URI> foundUrls = Collections.newSetFromMap(new ConcurrentHashMap<>());


        // Add the first url to the queue.
        try {
            urlsQueue.put(seedURL);
            foundUrls.add(seedURL);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        /**
         * Arraylist of found results.
         */
        List<Result> sharedresultList = Collections.synchronizedList(new ArrayList<>());




        



        // Initialize the threads.

        // First make the Runnables.
        Runnable crawlerRunnable = new Crawler(urlsQueue, htmlsQueue, activeScrapers, isCrawlerSleeping, shouldQuit);
        Runnable firstScraper = new Scraper(seedURL, urlsQueue, htmlsQueue, sharedresultList,
        foundUrls, activeScrapers, shouldQuit);
        Runnable secondScraper = new Scraper(seedURL, urlsQueue, htmlsQueue, sharedresultList,
        foundUrls, activeScrapers, shouldQuit);
        Runnable uiRunnable = new UI(activeScrapers, shouldQuit, urlsQueue, sharedresultList, htmlsQueue, foundUrls, isCrawlerSleeping);


        // Now the Threads.
        Thread crawlerThread = new Thread(crawlerRunnable, "Crawler");    
        Thread firstScraperThread = new Thread(firstScraper, "Scraper1");    
        Thread secondScraperThread = new Thread(secondScraper, "Scraper2");    
        Thread UIThread = new Thread(uiRunnable, "UI");


        // Start the threads.
        UIThread.start();
        crawlerThread.start();
        firstScraperThread.start();
        secondScraperThread.start();
        


        try {
            UIThread.join();
        } catch (InterruptedException e) {
            System.err.println("Main thread interruped while waiting for UI.");
            return;
        }

        System.out.println("Shutdown signal received. Waiting for worker threads to finish.");



        // Waiting for the rest of the threads.
        try {
            crawlerThread.join();
            firstScraperThread.join();
            secondScraperThread.join();

        } catch (InterruptedException e) {
            System.err.println("Main thread interrupted while waiting for workers.");
        }


        System.out.println("All threads finished. Program exiting.");


    }


};
