package com.minipulse.ui.user;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class UserScene {
    public static Scene getScene(Stage stage, String userName) {
        UserView userView = new UserView(stage, userName);
        Pane pane = userView.render();
        Scene scene = new Scene(pane, 1000, 400);
        return scene;
    }
}
