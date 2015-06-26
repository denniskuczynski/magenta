package magenta;
 
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.Set;
import java.util.HashSet;
 
@WebSocket
public class MagentaWebSocket implements MongoDbCloudMessageListener {
    private Set<Session> _sessions;

    public MagentaWebSocket() {
        _sessions = new HashSet<>();
        MongoDbCloudMessages.getInstance().addListener(this);
    }

    @OnWebSocketConnect
    public void onConnect(final Session session) {
        System.out.println("onConnect");
        _sessions.add(session);
    }

    @OnWebSocketMessage
    public void onMessage(final Session session, final String message) {
        System.out.println("onMessage: "+message);
        if (session.isOpen()) {
            //System.out.printf("Echoing back message [%s]%n", message);
            //
        }
    }

    @OnWebSocketClose
    public void onClose(final Session session, final int closeCode, final String closeReason) {
        System.out.println("onClose: "+closeReason);
        _sessions.remove(session);
    }

    @OnWebSocketError
    public void onError(final Session session, final Throwable error) {
        System.out.println("onError: "+error);
        error.printStackTrace();
    }

    public void addMessage(
        final String method,
        final int status,
        final String url,
        final String queryString,
        final String requestBody,
        final String responseBody
    ) {
        final String message = url+"/"+method+"/"+status+"/"+queryString+"/"+requestBody;
        if (status == 200 && requestBody != null) {
            System.out.println("Recieved message: "+message);
            for (final Session session : _sessions) {
                session.getRemote().sendString(message, null);
            }
        }
    }
}
