package org.autex.exception;

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super("Ismétlődő elem a listában: " + sku);
    }
}
