package com.minipulse.ui.report;

import com.minipulse.model.poll.Poll;
import com.minipulse.ui.poll.PollView;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ReportScene {
    public static Scene getScene(Stage stage, Poll poll) {
        ReportView reportView = new ReportView(stage, poll);
        Pane pane = reportView.render();
        Scene scene = new Scene(pane, 1000, 800);
        return scene;
    }
}
