package com.minipulse.ui.response;

import com.minipulse.model.poll.Poll;
import com.minipulse.ui.poll.PollView;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ResponseScene {
    public static Scene getScene(Stage stage, Poll poll, String respondingUser) {
        ResponseView responseView = new ResponseView(stage, poll, respondingUser);
        Pane pane = responseView.render();
        Scene scene = new Scene(pane, 1000, 800);
        return scene;
    }
}
