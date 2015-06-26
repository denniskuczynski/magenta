package magenta;
 
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
 
public class MagentaWebSocketServlet extends WebSocketServlet {
 
    @Override
    public void configure(final WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(120000); // close if idle for 2 minutes
        factory.register(MagentaWebSocket.class);
    }
}
