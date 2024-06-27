package com.minipulse.model.answer;

public class SingleChoiceAnswer extends Answer{
    private int choice;
    public SingleChoiceAnswer(){
        setType("SINGLE");
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }
}
