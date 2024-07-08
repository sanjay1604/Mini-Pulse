package com.minipulse.ui.question;

import com.minipulse.model.question.Question;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.TextQuestion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class MultipleChoiceQuestionView extends QuestionView{

    protected final GridPane choiceGridPane;
    private HBox buttonbox;
    private String choicesText;
    protected Question question;
    public MultipleChoiceQuestionView(GridPane gridPane, int row, Question question) {
        super(gridPane, row, question);
        this.choiceGridPane = new GridPane();

        questionGridPane.getChildren().add(this.choiceGridPane);
        choiceGridPane.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(400));
    }



    public void localRender(){
        Button addButton = new Button("Add Choice");
        Button deleteButton =  new Button("Delete Choice");
        addButton.setOnAction(actionEvent -> addChoice(choicesText, question));
        deleteButton.setOnAction(actionEvent -> deleteChoice(question));



    }



    public void update(){

    }

    public void addChoice(String choiceText, MultipleChoiceQuestion question){
        TextField choicesText = new TextField(String.valueOf(((MultipleChoiceQuestion)question).getChoices()));

        GridPane.setColumnIndex(choicesText, 1);
        GridPane.setRowIndex(choicesText, row);
        row++;  

        this.questionGridPane.getChildren().addAll(choicesText);

    }
    public void deleteChoice(Question question){
        this.choiceGridPane.getChildren().remove(row);
    }
}
