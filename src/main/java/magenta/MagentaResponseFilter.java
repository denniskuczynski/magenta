package magenta;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ReadListener;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class MagentaResponseFilter implements Filter {
    public FilterConfig _filterConfig;
    public MongoDbCloudMessages _messages;

    @Override
    public void init(final FilterConfig filterConfig) {
        _filterConfig = filterConfig;
        _messages = MongoDbCloudMessages.getInstance();
    } 
    
    @Override
    public void doFilter(
        final ServletRequest request,
        final ServletResponse response,
        final FilterChain chain
    ) throws IOException, ServletException {
        final ServletInputStreamCopier is = new ServletInputStreamCopier(request.getInputStream());
        final ServletOutputStreamCopier os = new ServletOutputStreamCopier(response.getOutputStream());
        chain.doFilter(
            new HttpServletRequestWrapper((HttpServletRequest)request) {
                @Override
                public ServletInputStream getInputStream() {
                    return is;
                }
            }, 
            new HttpServletResponseWrapper((HttpServletResponse)response) {
                @Override
                public ServletOutputStream getOutputStream() {
                    return os;
                }
            }
        );

        if (request.isAsyncStarted()) {
            final AsyncContext asyncContext = request.getAsyncContext();
            asyncContext.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    _messages.addMessage(
                        ((HttpServletRequest)request).getMethod(),
                        ((HttpServletResponse)response).getStatus(),
                        ((HttpServletRequest)request).getRequestURL().toString(),
                        ((HttpServletRequest)request).getQueryString(),
                        is.getCopy(),
                        os.getCopy()
                    );
                }
                @Override
                public void onTimeout(AsyncEvent event) throws IOException {
                }
                @Override
                public void onError(AsyncEvent event) throws IOException {
                }
                @Override
                public void onStartAsync(AsyncEvent event) throws IOException {
                }
            });
        }
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    public class ServletInputStreamCopier extends ServletInputStream {
        private ServletInputStream _inputStream;
        private ByteArrayOutputStream _copy;

        public ServletInputStreamCopier(final ServletInputStream inputStream) {
            _inputStream = inputStream;
            _copy = new ByteArrayOutputStream(1024);
        }

        @Override
        public int read() throws IOException {
            int read = _inputStream.read();
            if (read != -1) {
                _copy.write(read);
            }
            return read;
        }

        @Override
        public int readLine(byte[] b, int off, int len) throws IOException {
            int read = _inputStream.readLine(b, off, len);
            if (read != -1) {
                _copy.write(b, off, read);
            }
            return read;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }

        public String getCopy() {
            return new String(_copy.toByteArray());
        }
    }

    public class ServletOutputStreamCopier extends ServletOutputStream {
        private ServletOutputStream _outputStream;
        private ByteArrayOutputStream _copy;

        public ServletOutputStreamCopier(final ServletOutputStream outputStream) {
            _outputStream = outputStream;
            _copy = new ByteArrayOutputStream(1024);
        }

        @Override
        public void write(int b) throws IOException {
            _outputStream.write(b);
            _copy.write(b);
        }

        @Override
        public boolean isReady() {
          return true;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
        }

        public String getCopy() {
            return new String(_copy.toByteArray());
        }
    }
}
