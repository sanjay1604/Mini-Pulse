package com.minipulse.model.question;

import java.util.Map;

public class SingleChoiceQuestion extends Question{

    private Map<Integer,String> choices;

    public SingleChoiceQuestion() {
        setType("SINGULAR");
    }

    public Map<Integer, String> getChoices() {
        return choices;
    }

    public void setChoices(Map<Integer, String> choices) {
        this.choices = choices;
    }
}
