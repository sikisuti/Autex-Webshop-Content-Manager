package org.autex.util;

import org.autex.model.Product;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Serializer {
    public static final Map<String, String> AVAILABLE_FIELDS;

    static {
        AVAILABLE_FIELDS = new HashMap<>();
        AVAILABLE_FIELDS.put("weight", "súly");
        AVAILABLE_FIELDS.put("category", "kategória");
    }

    public static String serializeProduct(Product product, Set<String> selectedFields) {
        return null;
    }
}
