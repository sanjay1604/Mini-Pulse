package com.minipulse.ui.user;

import com.minipulse.exception.MiniPulseBadArgumentException;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.resource.PollResource;
import com.minipulse.ui.poll.PollScene;
import com.minipulse.ui.response.ResponseScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class UserView {

    private final String userName;
    private final GridPane gridPane;
    private final Stage stage;

    private final PollResource pollResource = new PollResource();

    public UserView(Stage stage, String userName) {
        this.stage = stage;
        this.userName = userName;
        this.gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(750),new ColumnConstraints(250));
    }

    public Pane render() {
        List<Poll> polls = getListOfPollsOwnedByUserFromDB(userName);
        int row = 0;
        for (Poll poll : polls) {
            Label pollTitleText = new Label(poll.getPollTitle());
            pollTitleText.setMinHeight(45);
            GridPane.setRowIndex(pollTitleText, row);
            GridPane.setColumnIndex(pollTitleText, 0);
            HBox actionPanel = new HBox();
            if (poll.getState() == PollState.NEW) {
                Button editButton = new Button("Edit");
                editButton.setOnAction(event -> onEditPoll(poll));
                Button flightButton = new Button("Flight");
                flightButton.setOnAction(event -> onFlightPoll(poll));
                actionPanel.getChildren().addAll(editButton, flightButton);
            } else if (poll.getState() == PollState.ACCEPTING) {
                Button respondButton = new Button("Respond");
                respondButton.setOnAction(event -> onRespondPoll(poll));
                Button closeButton = new Button("Close");
                closeButton.setOnAction(event -> onClosePoll(poll));
                actionPanel.getChildren().addAll(respondButton, closeButton);
            } else {
                Button reportButton = new Button("View Report");
                reportButton.setOnAction(event -> onViewReport(poll));
                actionPanel.getChildren().addAll(reportButton);
            }
            GridPane.setRowIndex(actionPanel, row);
            GridPane.setColumnIndex(actionPanel, 1);
            gridPane.getChildren().addAll(pollTitleText, actionPanel);
            row++;
        }
        Label emptyLine = new Label(" ");
        GridPane.setRowIndex(emptyLine, row++);
        GridPane.setColumnIndex(emptyLine, 0);

        HBox options = new HBox();
        options.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setRowIndex(options, row);
        GridPane.setColumnIndex(options, 0);

        Button newPollButton = new Button("New Poll");
        newPollButton.setOnAction(event -> onNewPoll());
        Button logoutButton = new Button("Logout (" + userName + ")");
        logoutButton.setOnAction(event -> onLogout());

        options.getChildren().addAll(newPollButton, logoutButton);
        gridPane.getChildren().addAll(emptyLine, options);
        return gridPane;
    }

    private void onLogout() {
        Scene scene = LoginScene.getScene(stage);
        stage.setScene(scene);
        stage.show();
    }

    private void onViewReport(Poll poll) {

    }

    private void onFlightPoll(Poll poll) {
        try {
            pollResource.flightPoll(poll.getPollId());
            Scene scene = UserScene.getScene(stage, userName);
            stage.setScene(scene);
            stage.show();
        } catch (MiniPulseBadArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void onClosePoll(Poll poll) {
        try {
            pollResource.closePoll(poll.getPollId());
            Scene scene = UserScene.getScene(stage, userName);
            stage.setScene(scene);
            stage.show();
        } catch (MiniPulseBadArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void onNewPoll() {
        Poll poll = new Poll();
        poll.setOwner(userName);
        poll.setState(PollState.NEW);
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private List<Poll> getListOfPollsOwnedByUserFromDB(String userName) {
        return pollResource.getAllPollsForUser(userName);
    }
}
