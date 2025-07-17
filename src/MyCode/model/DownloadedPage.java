package src.MyCode.model;

import java.net.URI;

public class DownloadedPage {

    private final URI sourceUrl;
    private final String htmlContent;

    public DownloadedPage(URI sourceUrl, String htmlContent) {
        this.sourceUrl = sourceUrl;
        this.htmlContent = htmlContent;
    }

    public URI getSourceUrl() {
        return sourceUrl;
    }

    public String getHtmlContent() {
        return htmlContent;
    }
    
}
