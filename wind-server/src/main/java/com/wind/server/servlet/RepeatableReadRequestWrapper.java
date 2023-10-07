

package com.wind.server.servlet;

import com.wind.common.exception.BaseException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 支持重复读取的 {@link HttpServletRequestWrapper}
 * 注意：请谨慎使用该类，由于会缓存请求内容 {@link #cachedContent}，不要缓存大请求（例如文件上传），导致内存压力过大而导致程序奔溃
 *
 * @author wuxp
 * @see org.springframework.web.util.ContentCachingRequestWrapper
 */
public class RepeatableReadRequestWrapper extends HttpServletRequestWrapper {

    private final ByteArrayOutputStream cachedContent;

    @Nullable
    private final Integer contentCacheLimit;

    @Nullable
    private ServletInputStream inputStream;

    private BodyInputStream bodyInputStream;

    @Nullable
    private BufferedReader reader;

    /**
     * Create a new RepeatableReadRequestWrapper for the given servlet request.
     *
     * @param request the original servlet request
     */
    public RepeatableReadRequestWrapper(HttpServletRequest request) {
        super(request);
        int contentLength = request.getContentLength();
        this.cachedContent = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
        this.contentCacheLimit = null;
    }

    /**
     * Create a new RepeatableReadRequestWrapper for the given servlet request.
     *
     * @param request           the original servlet request
     * @param contentCacheLimit the maximum number of bytes to cache per request
     * @see #handleContentOverflow(int)
     * @since 4.3.6
     */
    public RepeatableReadRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
        super(request);
        this.cachedContent = new ByteArrayOutputStream(contentCacheLimit);
        this.contentCacheLimit = contentCacheLimit;
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new ContentCachingInputStream(getRequest().getInputStream());
            return this.inputStream;
        }
        if (this.bodyInputStream == null) {
            this.bodyInputStream = new BodyInputStream(this.cachedContent.toByteArray());
        }
        // 重置流，用于多次读取
        this.bodyInputStream.reset();
        return this.bodyInputStream;
    }

    @Override
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
        }
        return this.reader;
    }

    @Override
    public String getParameter(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterValues(name);
    }


    private boolean isFormPost() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE) &&
                HttpMethod.POST.matches(getMethod()));
    }

    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = super.getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext(); ) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext(); ) {
                        String value = valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value != null) {
                            this.cachedContent.write('=');
                            this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write('&');
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write('&');
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }


    /**
     * Template method for handling a content overflow: specifically, a request
     * body being read that exceeds the specified content cache limit.
     * <p>The default implementation is empty. Subclasses may override this to
     * throw a payload-too-large exception or the like.
     *
     * @param contentCacheLimit the maximum number of bytes to cache per request
     *                          which has just been exceeded
     * @see #RepeatableReadRequestWrapper(HttpServletRequest, int)
     * @since 4.3.6
     */
    protected void handleContentOverflow(int contentCacheLimit) {
        throw BaseException.common("cache request body overflow，limit = " + contentCacheLimit);
    }


    private class ContentCachingInputStream extends ServletInputStream {

        private final ServletInputStream is;

        private boolean overflow = false;

        public ContentCachingInputStream(ServletInputStream is) {
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            int ch = this.is.read();
            if (ch != -1 && !this.overflow) {
                if (contentCacheLimit != null && cachedContent.size() == contentCacheLimit) {
                    this.overflow = true;
                    handleContentOverflow(contentCacheLimit);
                } else {
                    cachedContent.write(ch);
                }
            }
            return ch;
        }

        @Override
        public int read(byte[] b) throws IOException {
            int count = this.is.read(b);
            writeToCache(b, 0, count);
            return count;
        }

        private void writeToCache(final byte[] b, final int off, int count) {
            if (!this.overflow && count > 0) {
                if (contentCacheLimit != null &&
                        count + cachedContent.size() > contentCacheLimit) {
                    this.overflow = true;
                    cachedContent.write(b, off, contentCacheLimit - cachedContent.size());
                    handleContentOverflow(contentCacheLimit);
                    return;
                }
                cachedContent.write(b, off, count);
            }
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            int count = this.is.read(b, off, len);
            writeToCache(b, off, count);
            return count;
        }

        @Override
        public int readLine(final byte[] b, final int off, final int len) throws IOException {
            int count = this.is.readLine(b, off, len);
            writeToCache(b, off, count);
            return count;
        }

        @Override
        public boolean isFinished() {
            return this.is.isFinished();
        }

        @Override
        public boolean isReady() {
            return this.is.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            this.is.setReadListener(readListener);
        }
    }

    private static class BodyInputStream extends ServletInputStream {

        private final InputStream delegate;

        public BodyInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }

}
