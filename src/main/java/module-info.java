module Autex.Webshop.Content.Manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi;
    requires poi.ooxml;
    requires opencsv;
    requires slf4j.api;
    requires java.sql;

    opens org.autex.controller to javafx.fxml;
    exports org.autex;
}