package com.minipulse.ui.poll;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import com.minipulse.model.question.SingleChoiceQuestion;
import com.minipulse.model.question.TextQuestion;
import com.minipulse.ui.question.MultipleChoiceQuestionView;
import com.minipulse.ui.question.QuestionView;
import com.minipulse.ui.question.SingleChoiceQuestionView;
import com.minipulse.ui.question.TextQuestionView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class PollView {

    private final Poll poll;
    private final GridPane gridPane;

    private int row = 1;

    private HBox buttonBox;

    private ContextMenu contextMenu;

    public PollView(Poll poll, GridPane gridPane) {
        this.poll = poll;
        this.gridPane = gridPane;
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(200), new ColumnConstraints(800));
    }

    public void addQuestion(String type){

    }

    public void deleteQuestion(Integer questionNumber){

    }

    public void render() {

        Label pollTitle = new Label("Title");
        pollTitle.setMinHeight(30);

        GridPane.setColumnIndex(pollTitle, 0);
        GridPane.setRowIndex(pollTitle, row);

        TextField pollTitleText = new TextField(poll.getPollTitle());
        GridPane.setColumnIndex(pollTitleText, 1);
        GridPane.setRowIndex(pollTitleText, row);
        row++;

        Label pollDescription = new Label("Description");
        pollDescription.setMinHeight(30);
        GridPane.setColumnIndex(pollDescription, 0);
        GridPane.setRowIndex(pollDescription, row);
        TextField pollDescriptionText = new TextField(poll.getPollDescription());
        GridPane.setColumnIndex(pollDescriptionText, 1);
        GridPane.setRowIndex(pollDescriptionText, row);
        row++;

        if (poll.getQuestions() != null && !poll.getQuestions().isEmpty()) {
            for (Question question : poll.getQuestions()) {
                renderQuestion(row++, question);
            }
        }

        buttonBox = new HBox();



        Button addButton = new Button("Add");

        contextMenu = new ContextMenu();
        MenuItem textQuestion = new MenuItem("Add Text Question");
        textQuestion.setOnAction(event -> onAddQuestion("TEXT"));

        MenuItem multipleChoiceQuestion = new MenuItem("Add Multiple Choice Question");
        multipleChoiceQuestion.setOnAction(event -> onAddQuestion("MULTIPLE"));

        MenuItem singleChoiceQuestion = new MenuItem("Add Single Choice Question");
        singleChoiceQuestion.setOnAction(event -> onAddQuestion("SINGULAR"));

        MenuItem cancelMenu = new MenuItem("Cancel");
        cancelMenu.setOnAction(event -> contextMenu.hide());

        contextMenu.getItems().addAll(textQuestion, multipleChoiceQuestion, singleChoiceQuestion, cancelMenu);

        addButton.setMinHeight(30);
        addButton.setContextMenu(contextMenu);
        addButton.setOnAction(event -> contextMenu.show(addButton, addButton.getTranslateX(), addButton.getTranslateY()));

        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setMinHeight(30);

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setMinHeight(30);

        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(addButton, saveButton, cancelButton);

        GridPane.setColumnIndex(buttonBox, 1);
        GridPane.setRowIndex(buttonBox, row);

        gridPane.getChildren().addAll(pollTitle, pollTitleText, pollDescription, pollDescriptionText, buttonBox);
    }



    private void onAddQuestion(String type) {

        GridPane.setRowIndex(buttonBox, row+1);
        if(type.equals("TEXT")){
            Question question = new TextQuestion();
            renderQuestion(row, question);
        }else if(type.equals("MULTIPLE")){
            Question question = new MultipleChoiceQuestion();
            renderQuestion(row, question);
        }else if(type.equals("SINGULAR")){
            Question question = new SingleChoiceQuestion();
            renderQuestion(row, question);
        }

        row++;
    }


    private void renderQuestion(int row, Question question) {
        QuestionView questionView = null;

        if (question.getType().equals("TEXT")) {
            questionView = new TextQuestionView(gridPane, row, question);
        } else if (question.getType().equals("SINGULAR")) {
            questionView = new SingleChoiceQuestionView(gridPane, row, question);
        } else if (question.getType().equals("MULTIPLE")) {
            questionView = new MultipleChoiceQuestionView(gridPane, row, question);
        }
        questionView.render();
    }

    public void update(){

    }


}
