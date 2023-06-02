package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.scripting.demo.WriterVisitor;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartScriptEngineDemo {
    public static void main(String[] args) throws IOException {

        Path path = Paths.get("./src/main/resources/scripts/script1.txt");
        String docBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        Map<String,String> parameters = new HashMap<String, String>();
        Map<String,String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();

        new SmartScriptEngine(new SmartScriptParser(docBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)).execute();
    }
}
