package magenta;

import java.util.Map;

public interface MongoDbCloudMessageListener {
    void onMongoDbCloudMessage(
        final String method,
        final int status,
        final String pathInfo,
        final Map<String, String[]> queryParams,
        final String requestBody,
        final String responseBody
    );
}
