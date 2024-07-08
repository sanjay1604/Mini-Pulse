package com.minipulse.ui.user;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginScene {

    public static Scene getScene(Stage stage) {
        LoginView loginView = new LoginView(stage);
        Pane pane = loginView.render();
        Scene scene = new Scene(pane, 900, 400);
        return scene;
    }
}
