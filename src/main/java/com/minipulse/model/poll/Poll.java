package com.minipulse.model.poll;

import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import com.minipulse.model.response.Response;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

@XmlRootElement
public class Poll {
    private List<Question> questions;
    private List<Response> responses;
    private String PollId;
    private String owner;
    private PollState state;
    private String pollTitle;
    private String pollDescription;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public String getPollId() {
        return PollId;
    }

    public void setPollId(String pollId) {
        this.PollId = pollId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public PollState getState() {
        return state;
    }

    public void setState(PollState state) {
        this.state = state;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public String getPollDescription() {
        return pollDescription;
    }

    public void setPollDescription(String pollDescription) {
        this.pollDescription = pollDescription;
    }

    public Poll clone() {
        Poll clonePoll = new Poll();
        clonePoll.setState(getState());
        clonePoll.setPollTitle(getPollTitle());
        clonePoll.setPollDescription(getPollDescription());
        clonePoll.setOwner(getOwner());
        clonePoll.setPollId(getPollId());
        for (Question question : getQuestions()) {
            clonePoll.questions.add(question.clone());
        }
        for (Response response : getResponses()) {
            clonePoll.responses.add(response.clone());
        }
        return clonePoll;
    }
}
