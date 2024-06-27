package com.minipulse.model.answer;

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
}
