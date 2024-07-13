package com.minipulse.ui.report.answer;

import com.minipulse.model.question.Question;
import com.minipulse.model.response.Response;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public abstract class ReportAnswerView {

    private final VBox parentVBox;
    protected final Question question;

    protected final VBox thisQuestionVBox;
    protected final List<Response> responses;

    public ReportAnswerView(VBox parentVBox, Question question, List<Response> responses) {
        this.parentVBox = parentVBox;
        this.question = question;
        this.responses = responses;
        this.thisQuestionVBox = new VBox();
    }

    public Pane render() {
        Label emptyLine = new Label(" ");
        thisQuestionVBox.getChildren().add(emptyLine);

        Label questionTitle = new Label(question.getQuestionTitle());
        thisQuestionVBox.getChildren().add(questionTitle);

        Label questionDescription = new Label(question.getQuestionDescription());
        thisQuestionVBox.getChildren().add(questionDescription);

        Label emptyLine2 = new Label(" ");
        thisQuestionVBox.getChildren().add(emptyLine2);

        localRender();

        return thisQuestionVBox;
    }

    protected abstract void localRender();

}
