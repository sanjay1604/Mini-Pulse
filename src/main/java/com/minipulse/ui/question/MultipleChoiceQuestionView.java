package com.minipulse.ui.question;

import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.*;

public class MultipleChoiceQuestionView extends QuestionView{

    protected final GridPane choiceGridPane;
    private int choiceRow = 0;

    protected Set<TextField> m_Choices = new HashSet<>();
    public MultipleChoiceQuestionView(GridPane gridPane, int row, Question question) {
        super(gridPane, row, question);
        this.choiceGridPane = new GridPane();

        GridPane.setColumnIndex(choiceGridPane, 1);
        GridPane.setRowIndex(choiceGridPane, row);
        super.row++;
        questionGridPane.getChildren().add(this.choiceGridPane);
        choiceGridPane.getColumnConstraints().addAll(new ColumnConstraints(400), new ColumnConstraints(200));
    }

    public void renderChoice(String choice, int choiceRow){
        TextField choicesText = new TextField(choice);
        m_Choices.add(choicesText);
        GridPane.setColumnIndex(choicesText, 0);
        GridPane.setRowIndex(choicesText, choiceRow);
        Button deleteButton =  new Button("X");
        GridPane.setColumnIndex(deleteButton, 1);
        GridPane.setRowIndex(deleteButton, choiceRow);
        deleteButton.setOnAction(actionEvent -> onDeleteChoice(choicesText, deleteButton));
        choiceGridPane.getChildren().addAll(choicesText, deleteButton);
    }

    private void onDeleteChoice(TextField choicesText, Button deleteButton) {
        choiceGridPane.getChildren().removeAll(choicesText, deleteButton);
        m_Choices.remove(choicesText);
    }
    public void localRender(){
        Button addButton = new Button("+");
        GridPane.setColumnIndex(addButton, 1);
        GridPane.setRowIndex(addButton, 10000);
        addButton.setAlignment(Pos.CENTER_RIGHT);
        addButton.setOnAction(actionEvent -> onAddChoice());
        choiceGridPane.getChildren().add(addButton);

        Collection<String> choices = getChoices();
        for(String choice : choices) {
            renderChoice(choice, choiceRow);
            choiceRow++;
        }
    }

    protected Collection<String> getChoices() {
        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
        if (mcq.getChoices() == null) {
            return Collections.emptyList();
        }
        return mcq.getChoices().values();
    }
    public void onAddChoice(){
        renderChoice("", choiceRow);
        choiceRow++;
    }

    @Override
    public Question update() {
        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
        mcq.setQuestionTitle(m_QuestionTitle.getText());
        mcq.setQuestionDescription(m_QuestionDescription.getText());
        Map<Integer, String> choices = new HashMap<>();
        int key = 1;
        for (TextField choiceText : m_Choices) {
            choices.put(key, choiceText.getText());
            key++;
        }
        mcq.setChoices(choices);
        return mcq;
    }
}
