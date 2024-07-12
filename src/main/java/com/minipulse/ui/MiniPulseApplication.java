package com.minipulse.ui;

import com.minipulse.ui.user.LoginScene;
import javafx.application.Application;
import javafx.stage.Stage;

public class MiniPulseApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mini Pulse");
        primaryStage.setScene(LoginScene.getScene(primaryStage));
        primaryStage.show();
    }
}
