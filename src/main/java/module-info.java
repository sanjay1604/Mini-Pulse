module com.minipulse.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    requires java.xml.bind;

    opens com.minipulse.ui to javafx.fxml;
    opens com.minipulse.ui.user to javafx.fxml;

    opens com.minipulse.model.poll to java.xml.bind;

    exports com.minipulse.model.answer;
    exports com.minipulse.model.poll;
    exports com.minipulse.model.question;
    exports com.minipulse.model.report;
    exports com.minipulse.model.response;
    exports com.minipulse.model.user;

    exports com.minipulse.ui;
    exports com.minipulse.ui.user;
}