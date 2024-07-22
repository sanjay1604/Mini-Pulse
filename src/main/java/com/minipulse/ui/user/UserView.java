package com.minipulse.ui.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minipulse.exception.MiniPulseBadArgumentException;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.resource.PollResource;
import com.minipulse.ui.poll.PollScene;
import com.minipulse.ui.report.ReportScene;
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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserView {

    private final String userName;
    private final GridPane gridPane;
    private final Stage stage;

    public UserView(Stage stage, String userName) {
        this.stage = stage;
        this.userName = userName;
        this.gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(500),new ColumnConstraints(250), new ColumnConstraints(500),new ColumnConstraints(250));
    }

    public Pane render() {
        renderOwnedPolls();
        renderAcceptingPolls();
        return gridPane;
    }

    private void renderAcceptingPolls() {
        List<Poll> polls = getAcceptingPollsFromDB(userName);
        int row = 0;
        for (Poll poll : polls) {
            Label pollTitleText = new Label(poll.getPollTitle());
            pollTitleText.setMinHeight(40);
            GridPane.setRowIndex(pollTitleText, row);
            GridPane.setColumnIndex(pollTitleText, 2);
            HBox actionPanel = new HBox();
            Button respondButton = new Button("Respond");
            respondButton.setOnAction(event -> onRespondPoll(poll));
            GridPane.setRowIndex(actionPanel, row);
            GridPane.setColumnIndex(actionPanel, 3);
            actionPanel.getChildren().add(respondButton);
            gridPane.getChildren().addAll(pollTitleText, actionPanel);
            row++;
        }
    }

    private List<Poll> getAcceptingPollsFromDB(String userName) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            final HttpGet httpGet = new HttpGet("http://localhost:8080/minipulse/poll/accepting/" + userName);
            httpGet.setHeader("Accept", "application/json");

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = client.execute(httpGet)) {
                    if (response.getStatusLine().getStatusCode() < 300) {
                        return mapper.readValue(response.getEntity().getContent(), new TypeReference<>(){});
                    }
                    showError(response.getStatusLine().getReasonPhrase());
                }
            }
            return null;
        } catch (Exception e) {
            showError(e.getMessage());
            return null;
        }
    }

    public void renderOwnedPolls() {
        List<Poll> polls = getListOfPollsOwnedByUserFromDB(userName);
        int row = 0;
        for (Poll poll : polls) {
            Label pollTitleText = new Label(poll.getPollTitle());
            pollTitleText.setMinHeight(40);
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
                Button closeButton = new Button("Close");
                closeButton.setOnAction(event -> onClosePoll(poll));
                actionPanel.getChildren().addAll(closeButton);
            } else {
                Button reportButton = new Button("View Report");
                reportButton.setOnAction(event -> onViewReport(poll.getPollId()));
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
    }

    private void onLogout() {
        Scene scene = LoginScene.getScene(stage);
        stage.setScene(scene);
        stage.show();
    }

    private void onViewReport(String pollId) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            final HttpGet httpGet = new HttpGet("http://localhost:8080/minipulse/poll/withResponses/" + pollId);
            httpGet.setHeader("Accept", "application/json");

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = client.execute(httpGet)) {
                    if (response.getStatusLine().getStatusCode() < 300) {
                        Poll fullPollWithResponses = mapper.readValue(response.getEntity().getContent(), Poll.class);
                        showReport(fullPollWithResponses);
                    } else {
                        showError(response.getStatusLine().getReasonPhrase());
                    }
                }
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void showReport(Poll poll) {
        Scene scene = ReportScene.getScene(stage, poll);
        stage.setScene(scene);
        stage.show();
    }

    private void onFlightPoll(Poll poll) {
        try {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/minipulse/poll/flight/" + poll.getPollId());

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = client.execute(httpPost)) {
                    if (response.getStatusLine().getStatusCode() < 300) {
                        refreshScreen();
                    } else {
                        showError(response.getStatusLine().getReasonPhrase());
                    }
                }
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void onClosePoll(Poll poll) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            final HttpPost httpPost = new HttpPost("http://localhost:8080/minipulse/poll/close/" + poll.getPollId());

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = client.execute(httpPost)) {
                    if (response.getStatusLine().getStatusCode() < 300) {
                        refreshScreen();
                    } else {
                        showError(response.getStatusLine().getReasonPhrase());
                    }
                }
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void refreshScreen() {
        Scene scene = UserScene.getScene(stage, userName);
        stage.setScene(scene);
        stage.show();
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            final HttpGet httpGet = new HttpGet("http://localhost:8080/minipulse/poll/byUser/" + userName);
            httpGet.setHeader("Accept", "application/json");

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = client.execute(httpGet)) {
                    if (response.getStatusLine().getStatusCode() < 300) {
                        return mapper.readValue(response.getEntity().getContent(), new TypeReference<>(){});
                    }
                    showError(response.getStatusLine().getReasonPhrase());
                }
            }
            return null;
        } catch (Exception e) {
            showError(e.getMessage());
            return null;
        }
    }
}
