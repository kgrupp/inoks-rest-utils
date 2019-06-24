package de.kgrupp.rest.utils.model;

/**
 * @author Konstantin
 */
public class RestLoginData {

    private final String restUrl;
    private final Authorization authorization;

    public RestLoginData(String restUrl, Authorization authorization) {
        this.restUrl = restUrl;
        this.authorization = authorization;
    }

    public String getRestUrl() {
        return restUrl;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    @Override
    public String toString() {
        return "RestLoginData{" +
                "restUrl='" + restUrl + '\'' +
                ", authorization=" + authorization +
                '}';
    }
}
