package intre.it.javaoperator.model;

public class WebSiteSpec {

    private String webSiteName;
    private String url;
    private String shortDescription;
    private int replicas;

    public String getWebSiteName() {
        return webSiteName;
    }

    public void setWebSiteName(String WebSite) {
        this.webSiteName = WebSite;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}
