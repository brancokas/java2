package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TreeWriter {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("./src/main/resources/examples/doc1.txt");
        String docBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        SmartScriptParser parser = new SmartScriptParser(docBody);
        WriterVisitor visitor = new WriterVisitor();
        parser.getDocumentNode().accept(visitor);
    }
}
