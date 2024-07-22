package com.minipulse.model.response;

import com.minipulse.model.answer.Answer;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Response {
    private String respondingUser;
    private String pollId;
    private String responseId;
    private List<Answer> answers;

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getRespondingUser() {
        return respondingUser;
    }

    public void setRespondingUser(String respondingUser) {
        this.respondingUser = respondingUser;
    }

    public String getPollId() {
        return pollId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public Response clone() {
        Response cloneResponse = new Response();
        cloneResponse.setRespondingUser(getRespondingUser());
        cloneResponse.setResponseId(getResponseId());
        cloneResponse.setPollId(getPollId());
        for (Answer answer : getAnswers()) {
            cloneResponse.answers.add(answer.clone());
        }
        return cloneResponse;
    }
}
