package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.TextAnswer;
import com.minipulse.ui.question.QuestionView;
import javafx.scene.layout.GridPane;

public class TextAnswerView extends AnswerView{
    protected final GridPane textAnswerPane;
    private int row;

    public TextAnswerView(GridPane questionGridPane, GridPane pollGridPane, Answer answer) {
        super(questionGridPane, pollGridPane, answer);
        this.textAnswerPane = new GridPane();

        GridPane.setColumnIndex(textAnswerPane, 1);
        GridPane.setRowIndex(textAnswerPane, row);
        row++;

        textAnswerPane.getChildren().add(questionGridPane);
    }

    public void renderAnswerField(int row){

    }
}
