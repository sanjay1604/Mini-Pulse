package com.minipulse.ui.user;

import com.minipulse.db.DummyPollFactory;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.ui.poll.PollScene;
import com.minipulse.ui.response.ResponseScene;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

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
            Button actionButton;
            if (poll.getState() == PollState.NEW) {
                actionButton = new Button("Edit");
                actionButton.setOnAction(event -> onEditPoll(poll));
            } else {
                actionButton = new Button("Respond");
                actionButton.setOnAction(event -> onRespondPoll(poll));
            }
            GridPane.setRowIndex(actionButton, row);
            GridPane.setColumnIndex(actionButton, 1);
            gridPane.getChildren().addAll(pollTitleText, actionButton);
            row++;
        }
        Button newPollButton = new Button("New Poll");
        newPollButton.setOnAction(event -> onNewPoll());
        GridPane.setRowIndex(newPollButton, row);
        GridPane.setColumnIndex(newPollButton, 0);
        gridPane.getChildren().add(newPollButton);
        return gridPane;
    }

    private void onNewPoll() {
        Poll poll = new Poll();
        poll.setState(PollState.NEW);
        DummyPollFactory.newInstance(userName).getPollsFor(userName).add(poll);
        onEditPoll(poll);
    }

    private void onEditPoll(Poll poll) {
        Scene scene = PollScene.getScene(stage, poll);
        stage.setScene(scene);
        stage.show();
    }

    private void onRespondPoll(Poll poll) {
        Scene scene = ResponseScene.getScene(stage, poll, userName);
        stage.setScene(scene);
        stage.show();
    }
    private List<Poll> getListOfPollsOwnedByUserFromDB(String userName) {
        return DummyPollFactory.newInstance(userName).getPollsFor(userName);
    }
}
