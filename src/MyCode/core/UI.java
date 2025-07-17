package src.MyCode.core;

import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import src.MyCode.model.DownloadedPage;
import src.MyCode.model.Result;
import src.MyCode.model.ResultType;

/**
 * Finally, you will have one thread for the UI. This thread can be the main thread, after is has
 * started the other threads. This thread should allow the user to check the progress of the crawler
 * and view the most current results. I will leave the exact details up to you, but the basic idea is
 * that the user has to be able to view URLs, emails, etc. and also know how many pages have been
 * collected, how many more are in the queue, etc
 */
public class UI implements Runnable {
    


    // All the passed values that are shared with the other threads.
    AtomicInteger activeScapers;
    AtomicBoolean isCrawlerSleeping;
    BlockingQueue<URI> urlsToDownload;
    BlockingQueue<DownloadedPage> pagesToScrape;
    Set<URI> foundURLs;
    List<Result> foundResults;
    



    AtomicBoolean shouldQuit;



    public UI(AtomicInteger activeScrapers, AtomicBoolean shouldQuit, BlockingQueue<URI> urlsToDownload, List<Result> foundResults,
    BlockingQueue<DownloadedPage> pagesToScrape, Set<URI> foundURLs, AtomicBoolean isCrawlerSleeping) {
        this.activeScapers = activeScrapers;
        this.shouldQuit = shouldQuit;
        this.urlsToDownload = urlsToDownload;
        this.foundResults = foundResults;
        this.pagesToScrape = pagesToScrape;
        this.foundURLs = foundURLs;
        this.isCrawlerSleeping = isCrawlerSleeping;
    }




    @Override
    public void run() {

        Scanner kb = new Scanner(System.in);
        while (!shouldQuit.get() && !shouldAutoQuit()) {

            showInfo();
            
            ResultType userChoice = displayMenu(kb);

            if (userChoice != null) {
                printResults(userChoice);
            }
        }


        shouldQuit.set(true);
        System.out.println("UI Thread exiting.");
        kb.close();

        
    }


    private boolean shouldAutoQuit() {
        return urlsToDownload.isEmpty() && pagesToScrape.isEmpty()
        && activeScapers.get() == 0 && !isCrawlerSleeping.get();
    }

    /**
     * Method to print all the results of the given type.
     */
    private void printResults(ResultType resultType) {
        
        List<Result> filtered = foundResults.stream().filter(r -> r.getType() == resultType)
        .collect(Collectors.toList());

        System.out.println("Found " + filtered.size() + " results of type " + resultType);

        for (Result r : filtered) {
            System.out.println(r.toString()); // Or format however you like
        }

    }


    /**
     * Method to show basic info.
     */
    private void showInfo() {
        System.out.println("As of last check:");

        // Show total urls found.
        System.out.print(foundURLs.size());
        System.out.println(" urls found in total.");

        // Show how many need to be downloaded.
        System.out.print(urlsToDownload.size());
        System.out.println(" URLs to download.");

        // Show how many need to be scraped.
        System.out.print(pagesToScrape.size());
        System.out.println(" pages to be scraped.");

        // Show which threads are working.
        System.out.print(activeScapers.get());
        System.out.println(" scraper thread(s) working right now.");
        
        // Show if the crawler is sleeping or not.
        if (isCrawlerSleeping.get())
            System.out.println("Crawler thread is between downloading pages and is sleeping");
        else
            System.out.println("Crawler Thread is working.");

        // Show amount of results.
        System.out.println(foundResults.size() + " total results found.");

    }

    /**
     * Method to show a menu to the user.
     * @return either null or the enum value of the kind of result to display.
     */
    private ResultType displayMenu(Scanner kb) {


        int usersChoice;


        // Show menu title.
        System.out.println("Pick an option:");

        // Show first option, to refresh the data and do nothing else.
        System.out.println("0) Refresh");


        // Show options based on enums.
        for (ResultType type : ResultType.values()) {
            int option = type.ordinal() + 1;
            System.out.print(option + ") ");
            System.out.println(type.getLabel());


        }
        // Show that any other number will make the program quit.
        System.out.println("Enter any other number to stop the program.");



        System.out.print("\nEnter number of menu option: ");
        usersChoice = kb.nextInt();
        // Clear the buffer.
        kb.nextLine();
                  


        // If the user pressed refresh.
        if (usersChoice == 0)
            return null;

        if (usersChoice > ResultType.values().length) {
            shouldQuit.set(true);
            return null;
        } 

        return ResultType.values()[usersChoice - 1];
    }
};
