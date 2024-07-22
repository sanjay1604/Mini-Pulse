package com.minipulse.model.question;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This is the base question class. Other questions such as TextQuestion
 * MultipleChoiceQuestion etc. extend this class.
 *
 * When a new survey or poll is created one or more questions are attached
 * to the survey or poll.
 */
@XmlTransient
@XmlSeeAlso({TextQuestion.class, MultipleChoiceQuestion.class, SingleChoiceQuestion.class})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = TextQuestion.class, name = "TEXT"),
                @JsonSubTypes.Type(value = MultipleChoiceQuestion.class, name = "MULTIPLE"),
                @JsonSubTypes.Type(value = SingleChoiceQuestion.class, name = "SINGULAR")
        }
)
public abstract class Question {
    private String pollId;
    private String questionId;
    private String questionTitle;
    private String questionDescription;
    private boolean isMandatory;

    private String type;

    /**
     * Gets the Questions' title
     * @return Question Tile
     */
    public String getQuestionTitle() {
        return questionTitle;
    }

    /**
     * Sets the current Questions' title
     */
    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    /**\
     * Gets the Questions' description
     * @return Question description
     */

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract Question clone();
}
