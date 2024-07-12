package com.minipulse.ui.user;

import com.minipulse.resource.UserResource;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class LoginView {

    private final Stage stage;
    private GridPane gridPane;
    private TextField m_UserText;

    private final UserResource userResource = new UserResource();

    public LoginView(Stage stage) {
        this.stage = stage;
        this.gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(300),new ColumnConstraints(300), new ColumnConstraints(300));
    }

    public Pane render() {
        Label userLabel = new Label("User Name");
        GridPane.setColumnIndex(userLabel, 1);
        GridPane.setRowIndex(userLabel, 0);

        TextField userText = new TextField();
        m_UserText = userText;
        GridPane.setColumnIndex(userText, 1);
        GridPane.setRowIndex(userText, 1);

        Button submitButton = new Button("Submit");
        GridPane.setColumnIndex(submitButton, 1);
        GridPane.setRowIndex(submitButton, 2);
        submitButton.setOnAction(event -> onSubmit());

        gridPane.getChildren().addAll(userLabel, userText, submitButton);
        return gridPane;
    }

    private void onSubmit() {
        try {
            final HttpPost httpPost = new HttpPost("http://localhost:8080/minipulse/user/" + m_UserText.getText() + "/" + m_UserText.getText());

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse response = client.execute(httpPost)) {
                    if (response.getStatusLine().getStatusCode() < 300) {
                        switchToUserScreen();
                    } else {
                        showError(response.getStatusLine().getReasonPhrase());
                    }
                }
            }
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void switchToUserScreen() {
        Scene scene = UserScene.getScene(stage, m_UserText.getText());
        stage.setScene(scene);
        stage.show();
    }
}
