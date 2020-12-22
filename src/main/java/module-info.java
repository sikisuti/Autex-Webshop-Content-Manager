module Autex.Webshop.Content.Manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi;
    requires poi.ooxml;
    requires poi.ooxml.schemas;
    requires opencsv;
    requires slf4j.api;
    requires java.sql;

    opens org.autex.controller to javafx.fxml;
    opens org.autex.model to javafx.base;
    exports org.autex;
}