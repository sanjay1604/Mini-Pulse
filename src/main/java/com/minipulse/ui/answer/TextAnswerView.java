package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.TextAnswer;
import com.minipulse.model.question.Question;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TextAnswerView extends AnswerView{

    private TextField m_Answer;

    public TextAnswerView(VBox responseVBox, Question question) {
        super(responseVBox, question);
    }

    @Override
    protected void localRender() {
        TextField answerText = new TextField();
        m_Answer = answerText;
        answerVBox.getChildren().addAll(answerText);
    }

    @Override
    public Answer update() {
        TextAnswer answer = new TextAnswer();
        answer.setPollId(question.getPollId());
        answer.setQuestionId(question.getQuestionId());
        answer.setText(m_Answer.getText());
        return answer;
    }
}
