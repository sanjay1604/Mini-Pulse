package com.minipulse.model.question;

import java.util.Map;

public class SingleChoiceQuestion extends Question{

    private Map<Integer,String> choice;

    public SingleChoiceQuestion() {
        setType("SINGULAR");
    }

    public Map<Integer, String> getChoice() {
        return choice;
    }

    public void setChoice(Map<Integer, String> choice) {
        this.choice = choice;
    }
}
