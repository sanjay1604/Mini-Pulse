package com.minipulse.ui.report;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.question.Question;
import com.minipulse.ui.report.answer.ReportAnswerView;
import com.minipulse.ui.report.answer.ReportMultipleChoiceAnswerView;
import com.minipulse.ui.report.answer.ReportSingleChoiceAnswerView;
import com.minipulse.ui.report.answer.ReportTextAnswerView;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReportView {
    private final Poll poll;
    private final Stage stage;
    private final VBox responseVBox;

    public ReportView(Stage stage, Poll poll) {
        this.stage = stage;
        this.poll = poll;
        this.responseVBox = new VBox();
        responseVBox.setMinWidth(1000);
    }

    public Pane render() {
        Label pollTitle = new Label(poll.getPollTitle());
        Label pollDescription = new Label(poll.getPollDescription());
        Label emptyLine = new Label(" ");

        responseVBox.getChildren().addAll(pollTitle, pollDescription, emptyLine);
        if (poll.getQuestions() != null) {
            for (Question question : poll.getQuestions()) {
                renderAnswerSummaryForPoll(question);
            }
        }
        return responseVBox;
    }

    private void renderAnswerSummaryForPoll(Question question) {
        ReportAnswerView reportAnswerView = null;
        if (question.getType().equals("TEXT")) {
            reportAnswerView = new ReportTextAnswerView(responseVBox, question, poll.getResponses());
        } else if (question.getType().equals("MULTIPLE")) {
            reportAnswerView = new ReportMultipleChoiceAnswerView(responseVBox, question, poll.getResponses());
        } else if (question.getType().equals("SINGULAR")) {
            reportAnswerView = new ReportSingleChoiceAnswerView(responseVBox, question, poll.getResponses());
        }
        Pane answerVBox = reportAnswerView.render();
        responseVBox.getChildren().add(answerVBox);
    }
}
