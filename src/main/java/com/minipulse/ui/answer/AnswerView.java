package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.question.Question;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public abstract class AnswerView {
    protected final VBox responseVBox;
    protected final VBox answerVBox;
    protected Question question;
    public AnswerView(VBox responseVBox, Question question) {
        this.responseVBox = responseVBox;
        this.answerVBox = new VBox();
        responseVBox.getChildren().add(answerVBox);
        this.question = question;
    }

    public void render() {
        Label emptyLine = new Label(" ");

        Label questionTitle = new Label(question.getQuestionTitle());
        Label questionDescription = new Label(question.getQuestionDescription());
        answerVBox.getChildren().addAll(emptyLine, questionTitle, questionDescription);
        localRender();
    }

    protected abstract void localRender();

    public abstract Answer update();
}
