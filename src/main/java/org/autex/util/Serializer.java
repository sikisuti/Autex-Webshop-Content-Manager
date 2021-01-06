package org.autex.util;

import org.autex.model.Product;

import java.util.HashSet;
import java.util.Set;

public class Serializer {
    public static final Set<String> AVAILABLE_FIELDS;

    private Serializer() {}

    static {
        AVAILABLE_FIELDS = new HashSet<>();
        AVAILABLE_FIELDS.add("weight");
        AVAILABLE_FIELDS.add("category");
    }

    public static String serializeProduct(Product product, Set<String> selectedFields) {
        return null;
    }
}
