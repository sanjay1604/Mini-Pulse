package com.minipulse.ui.user;

import com.minipulse.db.DummyPollFactory;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.TextQuestion;
import com.minipulse.ui.poll.PollScene;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.*;

public class UserView {

    private final String userName;
    private final GridPane gridPane;
    private final Stage stage;

    public UserView(Stage stage, String userName) {
        this.stage = stage;
        this.userName = userName;
        this.gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(800),new ColumnConstraints(100));
    }

    public Pane render() {
        List<Poll> polls = getListOfPollsOwnedByUserFromDB(userName);
        int row = 0;
        for (Poll poll : polls) {
            Label pollTitleText = new Label(poll.getPollTitle());
            GridPane.setRowIndex(pollTitleText, row);
            GridPane.setColumnIndex(pollTitleText, 0);
            Button editPoll = new Button("Edit");
            editPoll.setOnAction(event -> onEditPoll(poll));
            GridPane.setRowIndex(editPoll, row);
            GridPane.setColumnIndex(editPoll, 1);
            gridPane.getChildren().addAll(pollTitleText, editPoll);
            row++;
        }
        return gridPane;
    }

    private void onEditPoll(Poll poll) {
        Scene scene = PollScene.getScene(stage, poll);
        stage.setScene(scene);
        stage.show();
    }

    private List<Poll> getListOfPollsOwnedByUserFromDB(String userName) {
        return DummyPollFactory.newInstance(userName).getPollsFor(userName);
    }
}
