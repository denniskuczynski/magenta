package magenta;

public interface MongoDbCloudMessageListener {
    void addMessage(
        final String method,
        final int status,
        final String url,
        final String queryString,
        final String requestBody,
        final String responseBody
    );
}
