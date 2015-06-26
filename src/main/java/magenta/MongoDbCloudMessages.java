package magenta;

import java.util.List;
import java.util.ArrayList;

public class MongoDbCloudMessages {
    private static MongoDbCloudMessages _singleton;

    private List<MongoDbCloudMessageListener> _listeners;

    private MongoDbCloudMessages() {
        _listeners = new ArrayList<>();
    }

    public static MongoDbCloudMessages getInstance() {
        if (_singleton == null) {
            _singleton = new MongoDbCloudMessages();
        }
        return _singleton;
    }

    public void addListener(final MongoDbCloudMessageListener listener) {
        _listeners.add(listener);
    }

    public void addMessage(
        final String method,
        final int status,
        final String url,
        final String queryString,
        final String requestBody,
        final String responseBody
    ) {
        for (final MongoDbCloudMessageListener listener : _listeners) {
            listener.addMessage(
                method,
                status,
                url,
                queryString,
                requestBody,
                responseBody
            );
        }

    }
}
