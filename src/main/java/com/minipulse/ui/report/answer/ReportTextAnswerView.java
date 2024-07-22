package com.minipulse.ui.report.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.TextAnswer;
import com.minipulse.model.question.Question;
import com.minipulse.model.response.Response;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

public class ReportTextAnswerView extends ReportAnswerView {

    public ReportTextAnswerView(VBox parentVBox, Question question, List<Response> responses) {
        super(parentVBox, question, responses);
    }

    @Override
    protected void localRender() {
        VBox textResponseHolder = new VBox();
        for (Response response : responses) {
            for (Answer answer : response.getAnswers()) {
                if (answer.getQuestionId().equals(question.getQuestionId())) {
                    TextAnswer textAnswer = (TextAnswer) answer;
                    Label answerLabel = new Label(textAnswer.getText());
                    answerLabel.setMinHeight(35);
                    textResponseHolder.getChildren().add(answerLabel);
                }
            }
        }
        thisQuestionVBox.getChildren().add(textResponseHolder);
    }
}
