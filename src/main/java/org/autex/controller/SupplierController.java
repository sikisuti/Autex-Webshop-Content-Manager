package org.autex.controller;

import org.autex.supplyer.Supplier;

import java.io.Writer;

public abstract class SupplierController {
    Supplier supplier;

    abstract Writer convert();
}
