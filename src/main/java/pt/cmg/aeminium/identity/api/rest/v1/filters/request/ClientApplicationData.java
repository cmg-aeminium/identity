package pt.cmg.aeminium.knowledge.api.rest.filters.request;

public class ClientApplicationData {

    private String name;
    private String receivedVersion;

    public ClientApplicationData() {
        this.name = "N/A";
        this.receivedVersion = "0.0";
    }

    public ClientApplicationData(String appName, String appVersion) {
        this.name = appName;
        this.receivedVersion = appVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceivedVersion() {
        return receivedVersion;
    }

    public void setReceivedVersion(String receivedVersion) {
        this.receivedVersion = receivedVersion;
    }

}
