package magenta;
 
import org.eclipse.jetty.proxy.ProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;

public class MongoDbCloudProxyServlet extends ProxyServlet {

    private String _baseUrl;

    public void init() throws ServletException {
        super.init();
        _baseUrl = getServletConfig().getInitParameter("mmsBaseUrl");
    }

    @Override
    protected String rewriteTarget(final HttpServletRequest request) {
        final String newURL = _baseUrl+request.getPathInfo();
        return newURL;
    }
}
