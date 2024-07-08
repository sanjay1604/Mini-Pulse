module com.minipulse.ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.minipulse.ui to javafx.fxml;
    exports com.minipulse.ui;
}