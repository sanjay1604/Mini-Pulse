package com.minipulse.ui.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.SingleChoiceAnswer;
import com.minipulse.model.question.Question;
import com.minipulse.model.question.SingleChoiceQuestion;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SingleChoiceAnswerView extends AnswerView{

    private final List<RadioButton> m_RadioButtons = new ArrayList<>();

    public SingleChoiceAnswerView(VBox responseVBox, Question question) {
        super(responseVBox, question);
    }

    @Override
    protected void localRender() {
        SingleChoiceQuestion scq = (SingleChoiceQuestion) question;
        ToggleGroup group = new ToggleGroup();
        if (scq.getChoices() != null) {
            for (Map.Entry<Integer, String> choiceEntry : scq.getChoices().entrySet()) {
                RadioButton radioButton = new RadioButton(choiceEntry.getValue());
                radioButton.setToggleGroup(group);
                radioButton.setUserData(choiceEntry.getKey());
                m_RadioButtons.add(radioButton);
                answerVBox.getChildren().add(radioButton);
            }
        }
    }

    @Override
    public Answer update() {
        SingleChoiceAnswer answer = new SingleChoiceAnswer();
        answer.setPollId(question.getPollId());
        answer.setQuestionId(question.getQuestionId());
        for (RadioButton optionButton : m_RadioButtons) {
            if (optionButton.isSelected()) {
                answer.setChoice((int)optionButton.getUserData());
                break;
            }
        }
        return answer;
    }}
