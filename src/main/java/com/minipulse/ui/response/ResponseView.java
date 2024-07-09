package com.minipulse.ui.response;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.question.Question;
import com.minipulse.model.response.Response;
import com.minipulse.ui.answer.AnswerView;
import com.minipulse.ui.answer.MultipleChoiceAnswerView;
import com.minipulse.ui.answer.SingleChoiceAnswerView;
import com.minipulse.ui.answer.TextAnswerView;
import com.minipulse.ui.user.UserScene;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private List<AnswerView> answerViews = new ArrayList<>();

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

        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setOnAction(event -> onSubmit());

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(event -> OnCancel());

        responseVBox.getChildren().addAll(saveButton, cancelButton);
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
        if (poll.getResponses() == null) {
            poll.setResponses(new ArrayList<>());
        }
        poll.getResponses().add(response);
        return response;
    }

}
