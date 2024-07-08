package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import javafx.scene.layout.GridPane;

public class MultipleChoiceAnswerView extends AnswerView{
    public MultipleChoiceAnswerView(GridPane questionGridPane, GridPane pollGridPane, Answer answer, boolean isMandatory) {
        super(questionGridPane, pollGridPane, answer);
    }
}
