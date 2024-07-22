package com.minipulse.model.answer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TextAnswer extends Answer {

    private String text;

    public TextAnswer(){
        setType("TEXT");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Answer clone() {
        TextAnswer cloneAnswer = new TextAnswer();
        cloneAnswer.setQuestionId(getQuestionId());
        cloneAnswer.setAnswerId(getAnswerId());
        cloneAnswer.setPollId((getPollId()));
        cloneAnswer.setText(getText());
        return cloneAnswer;

    }
}
