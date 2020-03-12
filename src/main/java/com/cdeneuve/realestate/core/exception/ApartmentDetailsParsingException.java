package com.cdeneuve.realestate.core.exception;

import org.jsoup.nodes.Node;

public class ApartmentDetailsParsingException extends Exception {
    private static final String TEMPLATE = "Error on parsing apartment detail %s, and className=%s";
    private Node node;

    public ApartmentDetailsParsingException(String fieldName, String className, Node node, Throwable cause) {
        super(String.format(TEMPLATE, fieldName, className), cause);
        this.node = node;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + ", Node: " + node.toString();
    }
}
