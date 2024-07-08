package com.minipulse.ui.question;

import com.minipulse.model.question.Question;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class SingleChoiceQuestionView extends QuestionView{
    protected final GridPane choiceGridPane;
    public SingleChoiceQuestionView(GridPane gridPane, int row, Question question) {

        super(gridPane, row, question);
        this.choiceGridPane = new GridPane();

        questionGridPane.getChildren().add(this.choiceGridPane);
        choiceGridPane.getColumnConstraints().addAll(new ColumnConstraints(100), new ColumnConstraints(400));
    }

    public void localRender(){
    }

    public void update(){

    }

    public void addChoice(String choiceText, Integer choiceKey){

    }

    public void deleteChoice(Integer choiceKey){

    }
}
