package com.minipulse.model.question;


/**
 * This is the subclass MultipleChoiceQuestion . This class contains questions that accept response type as option value.
 *
 * When a new survey is created and a text-response question is needed, this class would be useful.
 *
 * points to note:
 * 1. Audience can only choose the amount of choices the creator has decided.
 * 2. The text options correspond to their respective hash values.
 */

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.TreeMap;

@XmlRootElement
public class MultipleChoiceQuestion extends Question {
    private Map<Integer, String> choices = new TreeMap<>();

    public MultipleChoiceQuestion() {
        setType("MULTIPLE");
    }

    /**
     * Gets the choices.
     * @return choices.
     */
    public Map<Integer, String> getChoices() {
        return choices;
    }
    /**d
     * Setting the choices.
     */
    public void setChoices(Map<Integer, String> choices) {
        this.choices = choices;
    }

    @Override
    public Question clone() {
        MultipleChoiceQuestion cloneQuestion = new MultipleChoiceQuestion();
        cloneQuestion.setQuestionId(getQuestionId());
        cloneQuestion.setMandatory(isMandatory());
        cloneQuestion.setQuestionTitle(getQuestionTitle());
        cloneQuestion.setQuestionDescription(getQuestionDescription());
        cloneQuestion.setPollId(getPollId());
        cloneQuestion.setChoices(new TreeMap<>(choices));
        return cloneQuestion;
    }
}
