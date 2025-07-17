package src.MyCode.model;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Object to show a search result.
 */
public class Result {


    /**
     * The info that was found.
     */
    String info;


    /**
     * Kind of info that got found as an enum.
     */
    ResultType resultType;


    /**
     * The page this result was found on.
     */
    URI sourcePage;


    public Result(URI sourcePage, String info, ResultType resultType) {
        this.sourcePage = sourcePage;
        this.info = info;
        this.resultType = resultType;
    }


    @Override
    public String toString() {
        return "From: " + sourcePage.toString() + '\t' + resultType.getLabel() + ":\t " + info;
    }


     /**
      * Method to check if a result in an inner url.
      * It will return the URL in a usable form if it is.
      * If not, null will be returned.
      * @param baseURL The site that is being checked. Needed for reference.
      * @return The inner url in usable form.
      */
    public URI getInnerURL(URI baseURI) {

        if (this.resultType != ResultType.URL)
            return null;

        try {

            URI resultURI = new URI(null, info.trim(), null);

            URI resolved = baseURI.resolve(resultURI);



            if (baseURI.getHost().equalsIgnoreCase(resolved.getHost())) {
                return resolved;
            }

        } catch (URISyntaxException e) {
            System.err.println("There was an issue checking if " + this.info +
            " is an inner url that needs to be scraped.");
            //e.printStackTrace();
        }

        return null;
    
    }


    public ResultType getType() {
        return resultType;
    }
}