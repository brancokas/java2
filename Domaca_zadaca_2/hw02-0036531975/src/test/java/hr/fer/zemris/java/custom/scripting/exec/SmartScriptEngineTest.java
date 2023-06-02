package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartScriptEngineTest {
    @Test
    public void script1() throws IOException {
        String documentBody = readFromDisk("./src/test/resources/scripts/osnovni.smscr");
        Map<String,String> parameters = new HashMap<String, String>(); 
        Map<String,String> persistentParameters = new HashMap<String, String>(); 
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        // create engine and execute it 
        new SmartScriptEngine(
        new SmartScriptParser(documentBody).getDocumentNode(), 
                new RequestContext(System.out, parameters, persistentParameters, cookies) ).execute();
        
    }

    @Test
    public void script2() throws IOException {
        String documentBody = readFromDisk("./src/test/resources/scripts/zbrajanje.smscr");
        Map<String,String> parameters = new HashMap<String, String>();
        Map<String,String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        parameters.put("a", "4");
        parameters.put("b", "2");
// create engine and execute it
        new SmartScriptEngine(
        new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies) ).execute();

    }

    @Test
    public void script3() throws IOException {
        String documentBody = readFromDisk("./src/test/resources/scripts/brojPoziva.smscr");
        Map<String,String> parameters = new HashMap<String, String>();
        Map<String,String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
        persistentParameters.put("brojPoziva", "3");
        RequestContext rc = new RequestContext(System.out, parameters, persistentParameters, cookies);
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(), rc ).execute();
        System.out.println("\nVrijednost u mapi: "+rc.getPersistentParameter("brojPoziva"));
    }

    @Test
    public void script4() throws IOException {
        String documentBody = readFromDisk("./src/test/resources/scripts/fibonacci.smscr");
        Map<String,String> parameters = new HashMap<String, String>();
        Map<String,String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
// create engine and execute it
        new SmartScriptEngine(
        new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies) ).execute();
    }

    @Test
    public void script5() throws IOException {
        String documentBody = readFromDisk("./src/test/resources/scripts/fibonaccih.smscr");
        Map<String,String> parameters = new HashMap<String, String>();
        Map<String,String> persistentParameters = new HashMap<String, String>();
        List<RequestContext.RCCookie> cookies = new ArrayList<RequestContext.RCCookie>();
// create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies) ).execute();
    }

    private String readFromDisk(String s) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(s));
        return new String(bytes, StandardCharsets.UTF_8);
    }

}