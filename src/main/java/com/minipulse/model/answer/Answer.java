package com.minipulse.model.answer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlSeeAlso({TextAnswer.class, MultipleChoiceAnswer.class, SingleChoiceAnswer.class})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = TextAnswer.class, name = "TEXT"),
        @JsonSubTypes.Type(value = MultipleChoiceAnswer.class, name = "MULTIPLE"),
        @JsonSubTypes.Type(value = SingleChoiceAnswer.class, name = "SINGULAR")
    }
)
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

    public void setType(String type) {
        this.type = type;
    }

    public abstract Answer clone();
}

