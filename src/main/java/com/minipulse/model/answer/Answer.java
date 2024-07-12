package com.minipulse.model.answer;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlSeeAlso({TextAnswer.class, MultipleChoiceAnswer.class, SingleChoiceAnswer.class})
public abstract class Answer {

    private String PollId;
    private String responseId;
    private String questionId;
    private String  answerId;
    private String type;

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getPollId() {
        return PollId;
    }

    public void setPollId(String pollId) {
        PollId = pollId;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public abstract Answer clone();
}

