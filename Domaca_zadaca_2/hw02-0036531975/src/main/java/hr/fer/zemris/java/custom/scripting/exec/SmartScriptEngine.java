package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;
import java.text.DecimalFormat;

public class SmartScriptEngine {
    private DocumentNode documentNode;
    private RequestContext requestContext;
    private ObjectMultistack stack = new ObjectMultistack();

    private INodeVisitor visitor = new INodeVisitor() {
        @Override
        public void visitTextNode(TextNode node) throws IOException {
            requestContext.write(node.toString());
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) throws IOException {
            String variable = node.variable().asText();
            String endExpression = node.endExpression().asText();
            String stepExpression = node.stepExpression() == null ? "1" : node.stepExpression().asText();
            stack.push(variable, new ValueWrapper(node.startExpression().asText()));

            while (true) {
                ValueWrapper curr = stack.peek(variable);

                if (curr.numCompare(endExpression) <= 0) {
                    for (int i = 0; i < node.numberOfChildren(); i++)
                        node.getChild(i).accept(this);
                } else break;

                curr.add(stepExpression);
                stack.pop(variable);
                stack.push(variable, curr);
            }
        }

        @Override
        public void visitEchoNode(EchoNode node) throws IOException {
            ObjectMultistack tmpStack = new ObjectMultistack();
            String keyName = "key", printKey = "printKey";

            Element[] elements = node.elements();
            for (Element element : elements) {
                if (element instanceof ElementString || element instanceof ElementConstantDouble ||
                    element instanceof ElementConstantInteger) {

                    tmpStack.push(keyName, new ValueWrapper(element.asText()));

                } else if (element instanceof ElementVariable) {
                    ElementVariable variable = (ElementVariable) element;

                    Object value = stack.peek(variable.asText()).getValue();

                    tmpStack.push(keyName, new ValueWrapper(value));
                } else if (element instanceof ElementOperator) {
                    String operator = element.asText();

                    Object o2 = tmpStack.pop(keyName).getValue();
                    Object o1 = tmpStack.pop(keyName).getValue();
                    ValueWrapper valueWrapper = new ValueWrapper(o1);

                    if (operator.equals("+")) valueWrapper.add(o2);
                    else if (operator.equals("-")) valueWrapper.subtract(o2);
                    else if (operator.equals("*")) valueWrapper.multiply(o2);
                    else valueWrapper.divide(o2);

                    tmpStack.push(keyName, valueWrapper);

                } else if (element instanceof ElementFunction) {
                    String function = element.asText();
                    if (function.equals("sin")) {
                        String value = tmpStack.pop(keyName).getValue().toString();

                        double sin = Double.parseDouble(value);

                        sin = sin * Math.PI / 180.0;
                        tmpStack.push(keyName, new ValueWrapper(Math.sin(sin)));

                    } else if (function.equals("decfmt")) {
                        Object format = tmpStack.pop(keyName).getValue();

                        Object x = tmpStack.pop(keyName).getValue();
                        String value = new DecimalFormat(format.toString()).format(x);

                        tmpStack.push(keyName, new ValueWrapper(value));
                    } else if (function.equals("dup")) {
                        Object value = tmpStack.pop(keyName).getValue();
                        tmpStack.push(keyName, new ValueWrapper(value));
                        tmpStack.push(keyName, new ValueWrapper(value));
                    } else if (function.equals("swap")) {
                        Object a = tmpStack.pop(keyName).getValue();
                        Object b = tmpStack.pop(keyName).getValue();
                        tmpStack.push(keyName, new ValueWrapper(a));
                        tmpStack.push(keyName, new ValueWrapper(b));
                    } else if (function.equals("setMimeType")) {
                        Object value = tmpStack.pop(keyName).getValue();

                        if (!(value instanceof String))
                            throw new IllegalArgumentException();

                        requestContext.setMimeType((String) value);
                    } else if (function.equals("paramGet")) {
                        Object dv = tmpStack.pop(keyName).getValue();
                        Object name = tmpStack.pop(keyName).getValue();

                        String value = requestContext.getParameter(name.toString());

                        tmpStack.push(keyName, new ValueWrapper(value == null ? dv : value));
                    } else if (function.equals("pparamGet")) {
                        Object dv = tmpStack.pop(keyName).getValue();
                        Object name = tmpStack.pop(keyName).getValue();

                        String value = requestContext.getPersistentParameter(name.toString());

                        tmpStack.push(keyName, new ValueWrapper(value == null ? dv : value));
                    } else if (function.equals("pparamSet")) {
                        Object name = tmpStack.pop(keyName).getValue();
                        Object value = tmpStack.pop(keyName).getValue();

                        requestContext.setPersistentParameter(name.toString(), value.toString());
                    } else if (function.equals("pparamDel")) {
                        Object name = tmpStack.pop(keyName).getValue();

                        requestContext.remoPersistentParameter(name.toString());
                    } else if (function.equals("tparamGet")) {
                        Object dv = tmpStack.pop(keyName).getValue();
                        Object name = tmpStack.pop(keyName).getValue();

                        String value = requestContext.getTemporaryParameter(name.toString());

                        tmpStack.push(keyName, new ValueWrapper(value == null ? dv : value));
                    } else if (function.equals("tparamSet")) {
                        Object name = tmpStack.pop(keyName).getValue();
                        Object value = tmpStack.pop(keyName).getValue();

                        requestContext.setTemporaryParameter(name.toString(), value.toString());
                    } else if (function.equals("tparamDel")) {
                        Object name = tmpStack.pop(keyName).getValue();

                        requestContext.removeTemporaryParameter(name.toString());
                    }
                }
            }

            while (!tmpStack.isEmpty(keyName)) {
                tmpStack.push(printKey, tmpStack.pop(keyName));
            }
            while (!tmpStack.isEmpty(printKey)) {
                Object value = tmpStack.pop(printKey).getValue();
                requestContext.write(value.toString());
            }

        }

        @Override
        public void visitDocumentNode(DocumentNode node) throws IOException {
            for (int i = 0; i < node.numberOfChildren(); i++)
                node.getChild(i).accept(this);
        }
    };

    public SmartScriptEngine(DocumentNode documentNode, RequestContext requestContext) {
        this.documentNode = documentNode;
        this.requestContext = requestContext;
    }

    public void execute() throws IOException {
        documentNode.accept(visitor);
    }
}
