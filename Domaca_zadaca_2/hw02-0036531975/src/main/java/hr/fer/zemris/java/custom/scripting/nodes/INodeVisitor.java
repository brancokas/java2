package hr.fer.zemris.java.custom.scripting.nodes;

import java.io.IOException;

public interface INodeVisitor {
    void visitTextNode(TextNode node) throws IOException;
    void visitForLoopNode(ForLoopNode node) throws IOException;
    void visitEchoNode(EchoNode node) throws IOException;
    void visitDocumentNode(DocumentNode node) throws IOException;
}
