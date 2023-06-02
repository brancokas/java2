package hr.fer.zemris.java.webserver;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SmartHttpServer {
    private String address;
    private String domainName;
    private int port;
    private int workerThreads;
    private int sessionTimeout;
    private Map<String,String> mimeTypes = new HashMap<String, String>();
    private ServerThread serverThread;
    private ExecutorService threadPool;
    private Path documentRoot;
    private Map<String, IWebWorker> workersMap = new HashMap<>();
    private Map<String, SessionMapEntry> sessions = new HashMap<>();
    private Random sessionRandom = new Random();

    public SmartHttpServer(String configFileName) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(configFileName));

        Properties properties = new Properties();
        properties.load(inputStream);

        String server = "server.";

        address = properties.getProperty(server + "adress");
        domainName = properties.getProperty(server + "domainName");
        port = Integer.parseInt(properties.getProperty(server + "port"));
        workerThreads = Integer.parseInt(properties.getProperty(server + "workerThreads"));
        documentRoot = Paths.get(properties.getProperty(server + "documentRoot"));
        setMimeTypes(properties.getProperty(server + "mimeConfig"));
        sessionTimeout = Integer.parseInt(properties.getProperty("session.timeout"));
        setWorkersMap(properties.getProperty(server + "workers"));

        Thread demonicThread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Set<String> keySet = sessions.keySet();
                for (var key : keySet) {
                    SessionMapEntry entry = sessions.get(key);
                    if (entry.validUntil > System.currentTimeMillis())
                        sessions.remove(key);
                }
            }
        });
        demonicThread.setDaemon(true);
        demonicThread.start();
    }

    private void setWorkersMap(String property) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(property));

        Properties properties = new Properties();
        properties.load(inputStream);

        for (var prop : properties.entrySet()) {
            if (workersMap.containsKey(prop.getKey()))
                throw new RuntimeException("Two workers have the same path.");
            String fqcn = prop.getValue().toString();

            workersMap.put(prop.getKey().toString(), makeIWebWorker(fqcn));
        }
    }

    private IWebWorker makeIWebWorker(String fqcn) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
        Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
         return  (IWebWorker) newObject;
    }

    private void setMimeTypes(String property) throws IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(property));

        Properties properties = new Properties();
        properties.load(inputStream);

        for (var prop : properties.entrySet()) {
            mimeTypes.put(prop.getKey().toString(), prop.getValue().toString());
        }
    }

    protected synchronized void start() {
        serverThread = new ServerThread();
        serverThread.start();
        threadPool = Executors.newFixedThreadPool(workerThreads);

    }
    protected synchronized void stop() {
        serverThread.interrupt();
        threadPool.shutdown();
    }
    protected class ServerThread extends Thread {

        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    Socket client = serverSocket.accept();
                    ClientWorker worker = new ClientWorker(client);
                    threadPool.submit(worker);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private class ClientWorker implements Runnable, IDispatcher {
        private Socket csocket;
        private InputStream istream;
        private OutputStream ostream;
        private String version;
        private String method;
        private String host;
        private Map<String,String> params = new HashMap<String, String>();
        private Map<String,String> tempParams = new HashMap<String, String>();
        private Map<String,String> permPrams = new HashMap<String, String>();
        private List<RequestContext.RCCookie> outputCookies = new ArrayList<RequestContext.RCCookie>();
        private String SID;
        private RequestContext requestContext = null;
        public ClientWorker(Socket csocket) {
            super();
            this.csocket = csocket;
        }

        @Override
        public void run() {
            try {
                istream = new BufferedInputStream(csocket.getInputStream());
                ostream = new BufferedOutputStream(csocket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            List<String> request = readRequest();

            if (request.size() < 1) {
                sendEmptyResponse(ostream, 400, "Bad Request",
                        "plain/text", new byte[0]);
                return;
            }

            String firstLine = request.get(0);

            String[] strings = firstLine.split(" ");

            if (strings.length != 3) {
                sendEmptyResponse(ostream, 400, "Bad Request",
                        "plain/text", new byte[0]);
                return;
            }

            method = strings[0];
            String requestedPath = strings[1];
            version = strings[2];

            if (!method.equals("GET") ||
                !(version.toUpperCase().equals("HTTP/1.0") || version.toUpperCase().equals("HTTP/1.1"))) {
                sendEmptyResponse(ostream, 400, "Bad Request",
                        "plain/text", new byte[0]);
                return;
            }

            setHost(request);


            int i = 0;
            while (i < requestedPath.length() && requestedPath.charAt(i) != '?')
                i++;

            String path = requestedPath.substring(0,i);

            checkSession(request);

            String paramString = i == requestedPath.length() ? null : requestedPath.substring(i+1);

            parseParamString(paramString);

            try {
                internalDispatchRequest(path, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private synchronized void checkSession(List<String> request) {
            boolean foundSid = false;
            String newSid = null;
            String newHost = host == null || host.isEmpty() ? domainName : host;
            for (String req : request) {
                if (req.startsWith("Cookie:")) {
                    String sidCandidate = parseSID(req);

                    if (sidCandidate == null)
                        continue;
                    SessionMapEntry entry = sessions.get(sidCandidate);

                    if (entry == null) continue;
                    if (!newHost.equals(entry.host)) continue;
                    if (entry.validUntil > System.currentTimeMillis()) continue;

                    foundSid = true;
                    entry.validUntil = System.currentTimeMillis() + sessionTimeout;
                    sessions.put(entry.sid, entry);
                    newSid = entry.sid;
                    break;
                }
            }
            //create new session
            if (!foundSid) {
                String sid = createSID();
                SessionMapEntry entry =
                        new SessionMapEntry(sid, newHost, System.currentTimeMillis() + sessionTimeout);
                sessions.put(sid, entry);

                RequestContext.RCCookie cookie =
                        new RequestContext.RCCookie("sid", sid,null, newHost, "/");
                if (requestContext == null)
                    requestContext = new RequestContext(ostream, params, permPrams, tempParams, this, outputCookies);
                requestContext.addRCCookie(cookie);
                newSid = sid;
            }
            permPrams = sessions.get(newSid).map;
        }

        private synchronized String createSID() {
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 20;

            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (sessionRandom.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            return buffer.toString().toUpperCase();
        }

        private synchronized String parseSID(String line) {
            if (line.startsWith("Cookie:"))
                line = line.substring(7).trim();
            List<RequestContext.RCCookie> cookiesList = new ArrayList<>();
            String[] cookies = line.split(";");
            // list[name1="value1" ... namen="valuen"]
            for (String cookie : cookies) {
                String[] strings = cookie.split("=");
                if (strings.length != 2)
                    continue;
                if (strings[0].equals("sid"))
                    return strings[1].substring(1, strings[1].length()-1);
            }
            return null;
        }


        private synchronized void sendSmscr(File givenFile, RequestContext requestContext) {
            try {
                String docNode = new String(Files.readAllBytes(givenFile.toPath()), StandardCharsets.UTF_8);
                new SmartScriptEngine(new SmartScriptParser(docNode).getDocumentNode(),
                        requestContext).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private synchronized void sendPlainFile(File givenFile, RequestContext requestContext) {

            requestContext.setMimeType(getFileExtenstion(givenFile));
            requestContext.setStatusCode(200);
            requestContext.setContentLength(givenFile.length());

            try {
                InputStream fileReader = new BufferedInputStream(new FileInputStream(givenFile));
                while (true) {
                    byte[] bytes = new byte[1024];
                    int res = fileReader.read(bytes);

                    if (res == -1) break;

                    requestContext.write(bytes);
                }

                fileReader.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void parseParamString(String paramString) {
            if (paramString == null) return;
            String[] strings = paramString.split("&");
            for (String s : strings) {
                String[] keyValue = s.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }


        private String getFileExtenstion(File givenFile) {
            String fileExtension = givenFile.getName();

            fileExtension = fileExtension.substring(fileExtension.lastIndexOf('.') + 1);

            fileExtension = mimeTypes.get(fileExtension);

            if (fileExtension == null)
                fileExtension = "application/octet-stream";

            return fileExtension;
        }

        private File checkGivenURL(String path) {
            File givenFile;
            try {
                File rootFile = new File(documentRoot.toUri()).getCanonicalFile();
                Path reqPath = Paths.get(rootFile.getPath(), path);
                givenFile = new File(reqPath.toUri()).getCanonicalFile();
                if (rootFile.toPath().startsWith(givenFile.toPath())) {
                    sendEmptyResponse(ostream, 403, "Forbidden",
                            "plain/text", new byte[0]);
                    return null;
                }
                if (!givenFile.exists() || !givenFile.isFile() || !givenFile.canRead()) {
                    sendEmptyResponse(ostream, 404, "Not Found",
                            "plain/text", new byte[0]);
                    return null;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return givenFile;
        }

        private void setHost(List<String> request) {
            for (String r : request) {
                if (r.startsWith("Host:")) {
                    host = r.substring(5).trim();
                    int i = host.length()-1;
                    while (host.charAt(i) >= '0' && host.charAt(i) <= '9')
                        i--;
                    host = host.substring(0,i);
                    return;
                }
            }
        }

        private void sendEmptyResponse(OutputStream cos, int statusCode, String statusText, String contentType, byte[] data) {
            RequestContext rc = new RequestContext(cos, params, permPrams, outputCookies);
            rc.setStatusCode(statusCode);
            rc.setStatusText(statusText);
            rc.setMimeType(contentType);
            try {
                rc.write(data);
                cos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private Optional<byte[]> readRequest(InputStream is) throws IOException {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int state = 0;
            l:		while(true) {
                int b = is.read();
                if(b==-1) {
                    if(bos.size()!=0) {
                        throw new IOException("Incomplete header received.");
                    }
                    return Optional.empty();
                }
                if(b!=13) {
                    bos.write(b);
                }
                switch(state) {
                    case 0:
                        if(b==13) { state=1; } else if(b==10) state=4;
                        break;
                    case 1:
                        if(b==10) { state=2; } else state=0;
                        break;
                    case 2:
                        if(b==13) { state=3; } else state=0;
                        break;
                    case 3:
                        if(b==10) { break l; } else state=0;
                        break;
                    case 4:
                        if(b==10) { break l; } else state=0;
                        break;
                }
            }
            return Optional.of(bos.toByteArray());
        }


        private List<String> readRequest() {
            Optional<byte[]> bytes;
            try {
                 bytes = readRequest(istream);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (bytes.isEmpty()) throw new RuntimeException("No headere given.");
            String str = new String(bytes.get(), StandardCharsets.ISO_8859_1);
            return new ArrayList<>(Arrays.asList(str.split("\n")));
        }


        private void internalDispatchRequest(String urlPath, boolean directCall) throws Exception {
            if (requestContext == null)
                requestContext = new RequestContext(ostream, params, permPrams,
                        tempParams, this, outputCookies);

            if (urlPath.startsWith("/ext/")) {
                makeIWebWorker("hr.fer.zemris.java.webserver.workers." + urlPath.substring(5)).
                        processRequest(requestContext);
            } else if (workersMap.containsKey(urlPath)) {
                workersMap.get(urlPath).processRequest(requestContext);
            } else {
                if (directCall && (urlPath.equals("/private") || urlPath.startsWith("/private/"))) {
                    sendEmptyResponse(ostream, 404, "Not Found", "plain/text",
                            new byte[0]);
                    return;
                }

                File givenFile = checkGivenURL(urlPath);
                if (givenFile == null) return;

                if (urlPath.endsWith(".smscr")) {
                    sendSmscr(givenFile, requestContext);
                } else {
                    sendPlainFile(givenFile, requestContext);
                }
            }
            try {
                if (ostream != null)
                    ostream.flush();
                csocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void dispatchRequest(String urlPath) throws Exception {
            internalDispatchRequest(urlPath, false);
        }

    }

    private static class SessionMapEntry {
        String sid;
        String host;
        long validUntil;
        Map<String,String> map = new ConcurrentHashMap<>();

        public SessionMapEntry(String sid, String host, long validUntil) {
            this.sid = sid;
            this.host = host;
            this.validUntil = validUntil;
        }
    }
}
