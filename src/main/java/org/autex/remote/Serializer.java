package org.autex.remote;

import org.autex.model.Product;

import java.util.HashSet;
import java.util.Set;

public class Serializer {
    public static final Set<String> availableFields;

    static {
        availableFields = new HashSet<>();
        availableFields.add("weight");
    }

    public static String serializeProduct(Product product, Set<String> selectedFields) {
        return null;
    }
}
