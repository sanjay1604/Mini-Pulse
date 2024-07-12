package com.minipulse.ui.response;

import com.minipulse.exception.MiniPulseBadArgumentException;
import com.minipulse.model.answer.Answer;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.question.Question;
import com.minipulse.model.response.Response;
import com.minipulse.resource.ResponseResource;
import com.minipulse.ui.answer.AnswerView;
import com.minipulse.ui.answer.MultipleChoiceAnswerView;
import com.minipulse.ui.answer.SingleChoiceAnswerView;
import com.minipulse.ui.answer.TextAnswerView;
import com.minipulse.ui.user.UserScene;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ResponseView {
    private final Stage stage;
    private final Poll poll;
    private final VBox responseVBox;
    private final String respondingUser;
    private final List<AnswerView> answerViews = new ArrayList<>();

    private final ResponseResource responseResource = new ResponseResource();

    public ResponseView(Stage stage, Poll poll, String respondingUser) {
        this.stage = stage;
        this.poll = poll;
        this.responseVBox = new VBox();
        this.respondingUser = respondingUser;
        responseVBox.setMinWidth(1000);
    }

    public Pane render() {
        Label pollTitle = new Label(poll.getPollTitle());
        Label pollDescription = new Label(poll.getPollDescription());

        responseVBox.getChildren().addAll(pollTitle, pollDescription);
        if (poll.getQuestions() != null) {
            for (Question question : poll.getQuestions()) {
                renderQuestionForResponse(question);
            }
        }

        Label emptyLine = new Label(" ");

        HBox options = new HBox();
        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(event -> onSubmit());

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(event -> OnCancel());

        options.getChildren().addAll(saveButton, cancelButton);
        responseVBox.getChildren().addAll(emptyLine, options);
        return responseVBox;
    }

    private void onSubmit() {
        update();
        switchToUserView();
    }

    private void OnCancel() {
        switchToUserView();
    }

    private void switchToUserView() {
        Scene scene = UserScene.getScene(stage, respondingUser);
        stage.setScene(scene);
        stage.show();
    }

    private void renderQuestionForResponse(Question question) {
        AnswerView answerView = null;

        if (question.getType().equals("TEXT")) {
            answerView = new TextAnswerView(responseVBox, question);
        } else if (question.getType().equals("SINGULAR")) {
            answerView = new SingleChoiceAnswerView(responseVBox, question);
        } else if (question.getType().equals("MULTIPLE")) {
            answerView = new MultipleChoiceAnswerView(responseVBox, question);
        }
        answerView.render();
        answerViews.add(answerView);
    }

    public Response update() {
        Response response = new Response();
        response.setPollId(poll.getPollId());
        response.setRespondingUser(respondingUser);
        List<Answer> answers = new ArrayList<>();
        for (AnswerView answerView : answerViews) {
            answers.add(answerView.update());
        }
        response.setAnswers(answers);
        try {
            responseResource.submitResponse(response);
        } catch (MiniPulseBadArgumentException e) {
            showError(e.getMessage());
        }
        return response;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
