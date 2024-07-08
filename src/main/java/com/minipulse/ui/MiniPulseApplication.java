package com.minipulse.ui;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.question.Question;
import com.minipulse.model.question.TextQuestion;
import com.minipulse.ui.poll.PollView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Collections;

public class MiniPulseApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane gridPane = new GridPane();
        Poll poll = new Poll();
        TextQuestion question  = new TextQuestion();
        question.setQuestionTitle("First question");
        question.setQuestionDescription("First question desc");
        question.setAnswerLength(50);
        poll.setQuestions(Collections.singletonList(question));
        PollView pollView = new PollView(poll, gridPane);

        Scene scene = new Scene(gridPane, 1000, 800);
        pollView.render();

        primaryStage.setTitle("Mini Pulse");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
