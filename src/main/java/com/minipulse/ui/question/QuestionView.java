package com.minipulse.ui.question;

import com.minipulse.model.question.Question;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public abstract class QuestionView {
    protected final GridPane questionGridPane;
    protected int row;
    protected Question question;
    private final GridPane pollGridPane;

    private boolean isDeleted = false;

    protected TextField m_QuestionTitle;
    protected TextField m_QuestionDescription;
    public QuestionView(GridPane pollGridPane, int questionRow, Question question) {
        this.question = question;
        this.questionGridPane = new GridPane();
        questionGridPane.getColumnConstraints().addAll(new ColumnConstraints(200), new ColumnConstraints(600));
        GridPane.setColumnIndex(questionGridPane, 1);
        GridPane.setRowIndex(questionGridPane, questionRow);

        pollGridPane.getChildren().add(this.questionGridPane);
        this.row = 1;
        this.pollGridPane = pollGridPane;
    }

    protected abstract void localRender();
    public void render(){
        Label questionTitle = new Label("Question Title");
        questionTitle.setMinHeight(30);
        GridPane.setColumnIndex(questionTitle, 0);
        GridPane.setRowIndex(questionTitle, row);
        TextField questionTitleText = new TextField(question.getQuestionTitle());
        m_QuestionTitle = questionTitleText;
        GridPane.setColumnIndex(questionTitleText, 1);
        GridPane.setRowIndex(questionTitleText, row);
        row++;

        Label questionDescription = new Label("Question Description");
        questionDescription.setMinHeight(30);
        GridPane.setColumnIndex(questionDescription, 0);
        GridPane.setRowIndex(questionDescription, row);
        TextField questionDescriptionText = new TextField(question.getQuestionDescription());
        m_QuestionDescription = questionDescriptionText;
        GridPane.setColumnIndex(questionDescriptionText, 1);
        GridPane.setRowIndex(questionDescriptionText, row);
        row++;

        questionGridPane.getChildren().addAll(questionTitle, questionTitleText, questionDescription, questionDescriptionText);
        localRender();

        Button deleteButton = new Button("Delete Question");
        deleteButton.setMinHeight(30);
        deleteButton.setOnAction(actionEvent -> delete());
        GridPane.setColumnIndex(deleteButton, 1);
        GridPane.setRowIndex(deleteButton, 10000);
        questionGridPane.getChildren().add(deleteButton);

    }

    public void delete(){
        pollGridPane.getChildren().remove(questionGridPane);
        isDeleted = true;
    }
    public abstract Question update();

    public boolean isDeleted () {
        return isDeleted;
    }
}
