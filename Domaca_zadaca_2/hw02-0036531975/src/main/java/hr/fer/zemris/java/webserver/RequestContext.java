package hr.fer.zemris.java.webserver;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestContext {

    public static class RCCookie {
        private String name, value, domain, path;
        private Integer maxAge;

        public RCCookie(String name, String value, Integer maxAge, String domain, String path) {
            assertNotNull(name);
            assertNotNull(value);
            this.name = name;
            this.path = path;
            this.value = value;
            this.maxAge = maxAge;
            this.domain = domain;
        }

        private void assertNotNull(Object obj) {
            if (obj == null) throw new IllegalArgumentException("Object must not be null.");
        }

        public String getName() {
            return name;
        }

        public String getSessionId() {
            return value;
        }

        public String getDomain() {
            return domain;
        }

        public String getPath() {
            return path;
        }

        public Integer getMaxAge() {
            return maxAge;
        }
    }

    private OutputStream outputStream;
    private Charset charset;
    private String encoding = "UTF-8";
    private int statusCode = 200;
    private String statusText = "OK";
    private String mimeType = "text/html";
    private Long contentLength = null;
    private Map<String, String> parameters;
    private Map<String, String> temporaryParameters;
    private Map<String, String> persistentParameters;
    private List<RCCookie> outputCookies;
    private boolean headerGenerated = false;
    private final String endOfLine = "\r\n";
    private IDispatcher dispatcher;


    public RequestContext(OutputStream outputStream, Map<String, String> parameters,
                          Map<String, String> persistentParameters, List<RCCookie> outputCookies) {
        this(outputStream, parameters, persistentParameters, new HashMap<>(), null, outputCookies);
    }

    public RequestContext(OutputStream outputStream, Map<String, String> parameters,
                          Map<String, String> persistentParameters, Map<String, String> temporaryParameters,
                          IDispatcher dispatcher, List<RCCookie> outputCookies) {

        if (outputStream == null) throw new IllegalArgumentException("Output stream must be given.");

        this.outputStream = outputStream;
        this.parameters = parameters;
        this.persistentParameters = persistentParameters;
        this.outputCookies = outputCookies;
        this.temporaryParameters = temporaryParameters;
        this.dispatcher = dispatcher;

        if (this.parameters == null) this.parameters = new HashMap<>();
        if (this.persistentParameters == null) this.persistentParameters = new HashMap<>();
        if (this.outputCookies == null) this.outputCookies = new LinkedList<>();

    }

    public IDispatcher getDispatcher() {
        return dispatcher;
    }
    public void setEncoding(String encoding) {
        if (headerGenerated) throw new RuntimeException("Header already generated.");
        this.encoding = encoding;
    }

    public void setStatusCode(int statusCode) {
        if (headerGenerated) throw new RuntimeException("Header already generated.");
        this.statusCode = statusCode;
    }

    public void setStatusText(String statusText) {
        if (headerGenerated) throw new RuntimeException("Header already generated.");
        this.statusText = statusText;
    }

    public void setMimeType(String mimeType) {
        if (headerGenerated) throw new RuntimeException("Header already generated.");
        this.mimeType = mimeType;
    }

    public void setContentLength(Long contentLength) {
        if (headerGenerated) throw new RuntimeException("Header already generated.");
        this.contentLength = contentLength;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(parameters.keySet());
    }
    public String getPersistentParameter(String name) {
        return persistentParameters.get(name);
    }
    public Set<String> getPersistentParameterNames() {
        return Collections.unmodifiableSet(persistentParameters.keySet());
    }
    public void setPersistentParameter(String name, String value) {
        persistentParameters.put(name, value);
    }
    public void remoPersistentParameter(String name) {
        persistentParameters.remove(name);
    }
    public String getTemporaryParameter(String name) {
        return temporaryParameters.get(name);
    }
    public Set<String> getTemporaryParameterNames() {
        return Collections.unmodifiableSet(temporaryParameters.keySet());
    }
    public String getSessionID() {
        return "";
    }
    public void setTemporaryParameter(String name, String value) {
        temporaryParameters.put(name, value);
    }
    public void removeTemporaryParameter(String name) {
        temporaryParameters.remove(name);
    }
    public void addRCCookie(RCCookie rcCookie) {
        outputCookies.add(rcCookie);
    }
    public RequestContext write(byte[] data) throws IOException {
        //update charset
        charset = Charset.forName(encoding);
        //make header into output stream

        if (!headerGenerated) {
            writeHeader();
            headerGenerated = true;
        }

        outputStream.write(data);
        return this;
    }

    public RequestContext write(byte[] data, int offset, int len) throws IOException {
        charset = Charset.forName(encoding);

        if (!headerGenerated) {
            writeHeader();
            headerGenerated = true;
        }

        outputStream.write(data, offset, len);
        return this;
    }

    public RequestContext write(String text) throws IOException {
        return write(text.getBytes(Charset.forName(encoding)));
    }

    private void writeHeader() throws IOException {
        String header = String.format("HTTP/1.1 %d %s\r\n" +
                "Content-Type: %s",statusCode, statusText, mimeType);
        if (mimeType.startsWith("text/"))
            header += "; charset=" + encoding;
        header += endOfLine;

        if (contentLength != null)
            header += "Content-Length: " + contentLength + endOfLine;

        header += getCookies();
        header += endOfLine;

        outputStream.write(header.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String getCookies() {
        StringBuilder sb = new StringBuilder();
        for (RCCookie cookie : outputCookies) {
            sb.append(String.format("Set-Cookie: %s=\"%s\"",cookie.name, cookie.value));
            if (cookie.domain != null)
                sb.append("; Domain=" + cookie.domain);
            if (cookie.path != null)
                sb.append("; Path=" + cookie.path);
            if (cookie.domain != null)
                sb.append("; Max-Age=" + cookie.maxAge);
            sb.append("; HttpOnly");
            sb.append(endOfLine);
        }
        return sb.toString();
    }

}
