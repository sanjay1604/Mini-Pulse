module com.minipulse.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.minipulse.ui to javafx.fxml;
    exports com.minipulse.ui;
    exports com.minipulse.ui.user;
    opens com.minipulse.ui.user to javafx.fxml;
}