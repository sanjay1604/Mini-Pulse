package com.minipulse.ui.question;


import com.minipulse.model.question.Question;
import com.minipulse.model.question.TextQuestion;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class TextQuestionView extends QuestionView{

    private TextField m_AnswerLength;
    public TextQuestionView(GridPane gridPane, int row, Question question) {
        super(gridPane, row, question);
    }
    public void localRender(){
        Label answerLengthTitle = new Label("Answer Length");
        answerLengthTitle.setMinHeight(30);
        GridPane.setColumnIndex(answerLengthTitle, 0);
        GridPane.setRowIndex(answerLengthTitle, row);

        TextField answerLengthText = new TextField(String.valueOf(((TextQuestion)question).getAnswerLength()));
        m_AnswerLength = answerLengthText;
        GridPane.setColumnIndex(answerLengthText, 1);
        GridPane.setRowIndex(answerLengthText, row);
        this.questionGridPane.getChildren().addAll(answerLengthTitle, answerLengthText);
        row++;
    }

    public Question update(){
        TextQuestion tq = (TextQuestion) question;
        tq.setQuestionTitle(m_QuestionTitle.getText());
        tq.setQuestionDescription(m_QuestionDescription.getText());
        try {
            tq.setAnswerLength(Integer.parseInt(m_AnswerLength.getText()));
        } catch (NumberFormatException e) {
            tq.setAnswerLength(100);
        }
        return tq;
    }
}
