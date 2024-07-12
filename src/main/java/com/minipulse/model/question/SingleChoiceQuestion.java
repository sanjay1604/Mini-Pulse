package com.minipulse.model.question;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@XmlRootElement
public class SingleChoiceQuestion extends Question{

    private Map<Integer,String> choices= new TreeMap<>();

    public SingleChoiceQuestion() {
        setType("SINGULAR");
    }

    public Map<Integer, String> getChoices() {
        return choices;
    }

    public void setChoices(Map<Integer, String> choices) {
        this.choices = choices;
    }

    @Override
    public Question clone() {
        SingleChoiceQuestion cloneQuestion = new SingleChoiceQuestion();
        cloneQuestion.setQuestionId(getQuestionId());
        cloneQuestion.setMandatory(isMandatory());
        cloneQuestion.setQuestionTitle(getQuestionTitle());
        cloneQuestion.setQuestionDescription(getQuestionDescription());
        cloneQuestion.setPollId(getPollId());
        cloneQuestion.setChoices(new HashMap<>(choices));
        return cloneQuestion;
    }
}
