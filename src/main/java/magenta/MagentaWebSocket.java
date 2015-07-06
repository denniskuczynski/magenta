package magenta;
 
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
 
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

    public void onMongoDbCloudMessage(
        final String method,
        final int status,
        final String pathInfo,
        final Map<String, String[]> queryParams,
        final String requestBody,
        final String responseBody
    ) {
        if (status == 200) { // current impl requires requests complete for the request/response bodies
            if (pathInfo.startsWith("/agents/api/automation/status/v1/")) {
                sendStringToClients(buildStatusJSON(queryParams));
            } else if (pathInfo.startsWith("/agents/api/automation/log/v1/")) {
                sendStringToClients(buildLogsJSON(queryParams, requestBody));
            } else if (pathInfo.startsWith("/agents/api/automation/metrics/v1/")) {
                sendStringToClients(buildMetricsJSON(queryParams, requestBody));
            }
        }
    }

    private void sendStringToClients(final String message) {
        System.out.println("Sending message: "+message);
        for (final Session session : _sessions) {
            session.getRemote().sendString(message, null);
        }
    }
    
    private String buildStatusJSON(final Map<String, String[]> queryParams) {
        StringBuilder sb = new StringBuilder("{");
            sb.append("\"type\": \"status\"");
            sb.append(", \"data\": {");
                sb.append("\"aa\": \"").append(queryParams.get("aa")[0]).append("\"");
                sb.append(",\"ab\": \"").append(queryParams.get("ab")[0]).append("\"");
                sb.append(",\"aos\": \"").append(queryParams.get("aos")[0]).append("\"");
                sb.append(",\"av\": \"").append(queryParams.get("av")[0]).append("\"");
                sb.append(",\"ah\": \"").append(queryParams.get("ah")[0]).append("\"");
                sb.append(",\"ahs\": \"").append(queryParams.get("ahs")[0]).append("\"");
                sb.append(",\"t\": \"").append(System.currentTimeMillis()).append("\"");
            sb.append("}");
        sb.append("}");
        return sb.toString();
    }

    private String buildLogsJSON(
        final Map<String, String[]> queryParams,
        final String requestBody
    ) {
        StringBuilder sb = new StringBuilder("{");
            sb.append("\"type\": \"logs\"");
            sb.append(", \"data\": {");
                sb.append("\"ah\": \"").append(queryParams.get("ah")[0]).append("\"");
                sb.append(",\"logs\": ").append(requestBody);
            sb.append("}");
        sb.append("}");
        return sb.toString();
    }

    private String buildMetricsJSON(
        final Map<String, String[]> queryParams,
        final String requestBody
    ) {
        StringBuilder sb = new StringBuilder("{");
            sb.append("\"type\": \"metrics\"");
            sb.append(", \"data\": {");
                sb.append("\"ah\": \"").append(queryParams.get("ah")[0]).append("\"");
                sb.append(",\"metrics\": ").append(requestBody);
            sb.append("}");
        sb.append("}");
        return sb.toString();
    }
}
