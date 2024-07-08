package com.minipulse.ui.question;

import com.minipulse.model.question.Question;
import com.minipulse.model.question.SingleChoiceQuestion;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class SingleChoiceQuestionView extends MultipleChoiceQuestionView {
    public SingleChoiceQuestionView(GridPane gridPane, int row, Question question) {
        super(gridPane, row, question);
    }
    @Override
    protected Collection<String> getChoices() {
        SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
        if (scq.getChoices() == null) {
            return Collections.emptyList();
        }
        return scq.getChoices().values();
    }

    @Override
    public Question update() {
        SingleChoiceQuestion mcq = (SingleChoiceQuestion) question;
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
