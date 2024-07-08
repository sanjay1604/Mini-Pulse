package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import javafx.scene.layout.GridPane;

public class AnswerView {
    protected final GridPane questionGridPane;
    private final GridPane pollGridPane;
    protected Answer answer;


    public AnswerView(GridPane questionGridPane, GridPane pollGridPane, Answer answer) {
        this.questionGridPane = questionGridPane;
        this.pollGridPane = pollGridPane;
        this.answer = answer;
    }



}
