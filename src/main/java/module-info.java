module Autex.Webshop.Content.Manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi;
    requires poi.ooxml;
    requires poi.ooxml.schemas;
    requires opencsv;
    requires slf4j.api;
    requires java.sql;
    requires java.net.http;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.codec;

    opens org.autex.controller to javafx.fxml;
    opens org.autex.model to javafx.base;
    exports org.autex;
}