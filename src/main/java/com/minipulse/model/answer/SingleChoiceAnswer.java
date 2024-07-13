package com.minipulse.model.answer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SingleChoiceAnswer extends Answer{
    private int choice;
    public SingleChoiceAnswer(){
        setType("SINGULAR");
    }

    public int getChoice() {
        return choice;
    }

    public void setChoice(int choice) {
        this.choice = choice;
    }

    @Override
    public Answer clone() {
        SingleChoiceAnswer cloneAnswer = new SingleChoiceAnswer();
        cloneAnswer.setQuestionId(getQuestionId());
        cloneAnswer.setAnswerId(getAnswerId());
        cloneAnswer.setPollId((getPollId()));
        cloneAnswer.setChoice(getChoice());
        return cloneAnswer;
    }
}
