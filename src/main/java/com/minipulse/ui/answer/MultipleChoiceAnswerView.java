package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.MultipleChoiceAnswer;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultipleChoiceAnswerView extends AnswerView {

    private List<CheckBox> m_ChoiceBoxes = new ArrayList<>();

    public MultipleChoiceAnswerView(VBox responseVBox, Question question) {
        super(responseVBox, question);
    }

    @Override
    protected void localRender() {
        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
        if (mcq.getChoices() != null) {
            for (Map.Entry<Integer, String> choiceEntry : mcq.getChoices().entrySet()) {
                CheckBox choiceBox = new CheckBox(choiceEntry.getValue());
                choiceBox.setUserData(choiceEntry.getKey());
                m_ChoiceBoxes.add(choiceBox);
                answerVBox.getChildren().add(choiceBox);
            }
        }
    }

    @Override
    public Answer update() {
        MultipleChoiceAnswer answer = new MultipleChoiceAnswer();
        answer.setPollId(question.getPollId());
        answer.setQuestionId(question.getQuestionId());
        List<Integer> selectedChoices = new ArrayList<>();
        for (CheckBox checkBox : m_ChoiceBoxes) {
            if (checkBox.isSelected()) {
                selectedChoices.add((int)checkBox.getUserData());
            }
        }
        answer.setChoices(selectedChoices);
        return answer;
    }
}
