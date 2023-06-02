package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.custom.scripting.nodes.*;

import java.io.IOException;

public class WriterVisitor implements INodeVisitor {
    @Override
    public void visitTextNode(TextNode node) {
        System.out.print(node.toString());
    }

    @Override
    public void visitForLoopNode(ForLoopNode node) throws IOException {
        System.out.print(node.toString());
        for (int i = 0; i < node.numberOfChildren(); i++) {
            node.getChild(i).accept(this);
        }
        System.out.print(node.endTag());
    }

    @Override
    public void visitEchoNode(EchoNode node) {
        System.out.print(node.toString());
    }

    @Override
    public void visitDocumentNode(DocumentNode node) throws IOException {
        for (int i = 0; i < node.numberOfChildren(); i++) {
            node.getChild(i).accept(this);
        }
    }
}
