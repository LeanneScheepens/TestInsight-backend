package nl.testinsight.backend.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class RequestSizeFilter extends OncePerRequestFilter { // Extends OnePerRequestFilter makes sure that this class gets automatically executed

    private static final int MAX_REQUEST_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !(
                request.getMethod().equals("POST")
                        && request.getServletPath().equals("/api/import/testresult")
        );
    }

    @Override
    // This filter is on a lower level that is why Selvet is used
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Check Content-Length if available
        if (request.getContentLengthLong() > MAX_REQUEST_SIZE) {
            rejectRequest(response);
            return;
        }

        // Read at most 10 MiB + 1 byte
        byte[] body = request.getInputStream()
                .readNBytes(MAX_REQUEST_SIZE + 1);

        // Check actual number of bytes
        if (body.length > MAX_REQUEST_SIZE) {
            rejectRequest(response);
            return;
        }

        //because of this filter Spring can see a used stream
        // Make the body available to the controller, so it looks like the original stream that it expects
        HttpServletRequest wrappedRequest =
                new CachedBodyRequest(request, body);

        filterChain.doFilter(wrappedRequest, response);
    }

    private void rejectRequest(
            HttpServletResponse response
    ) throws IOException {
        response.sendError(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "JSON report exceeds the maximum size of 10 MiB."
        );
    }

    private static class CachedBodyRequest
            extends HttpServletRequestWrapper {

        private final byte[] body;

        public CachedBodyRequest(
                HttpServletRequest request,
                byte[] body
        ) {
            super(request);
            this.body = body;
        }

        @Override
        public int getContentLength() {
            return body.length;
        }

        @Override
        public long getContentLengthLong() {
            return body.length;
        }

        @Override
        public ServletInputStream getInputStream() {

            ByteArrayInputStream inputStream =
                    new ByteArrayInputStream(body);

            return new ServletInputStream() {

                @Override
                public int read() {
                    return inputStream.read();
                }

                @Override
                public boolean isFinished() {
                    return inputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener listener) {
                    throw new UnsupportedOperationException(
                            "Non-blocking IO is not supported"
                    );
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            String encoding = getCharacterEncoding();

            return new BufferedReader(
                    new InputStreamReader(
                            getInputStream(),
                            encoding != null
                                    ? java.nio.charset.Charset.forName(encoding)
                                    : StandardCharsets.UTF_8
                    )
            );
        }
    }
}