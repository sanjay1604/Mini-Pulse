package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import javafx.scene.layout.GridPane;

public class singleChoiceAnswerView extends AnswerView{
    public singleChoiceAnswerView(GridPane questionGridPane, GridPane pollGridPane, Answer answer, boolean isMandatory) {
        super(questionGridPane, pollGridPane, answer);
    }
}
