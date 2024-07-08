package com.minipulse.ui.poll;

import com.minipulse.model.poll.Poll;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PollScene {

    public static Scene getScene(Stage stage, Poll poll) {
        PollView pollView = new PollView(stage, poll);
        Pane pane = pollView.render();
        Scene scene = new Scene(pane, 1000, 800);
        return scene;
    }
}
