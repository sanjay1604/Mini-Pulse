module com.minipulse.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    requires java.xml.bind;
    requires jersey.media.json.jackson;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires jakarta.activation;
    requires java.sql;

    requires java.ws.rs;

    requires grizzly.http.server;
    requires grizzly.framework;
    requires jersey.container.grizzly2.http;
    requires jersey.server;
    requires jersey.common;

    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    requires osgi.resource.locator;

    opens com.minipulse.ui to javafx.fxml;
    opens com.minipulse.ui.user to javafx.fxml;

    opens com.minipulse.model.poll to java.xml.bind;

    exports com.minipulse.model.answer;
    exports com.minipulse.model.poll;
    exports com.minipulse.model.question;
    exports com.minipulse.model.response;
    exports com.minipulse.model.user;

    exports com.minipulse.resource;

    exports com.minipulse.ui;
    exports com.minipulse.db;
    exports com.minipulse.exception;
    exports com.minipulse.ui.user;
}