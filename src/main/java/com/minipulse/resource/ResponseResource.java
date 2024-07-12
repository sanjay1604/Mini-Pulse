package com.minipulse.resource;

import com.minipulse.db.DBFactory;
import com.minipulse.db.MiniPulseDB;
import com.minipulse.exception.MiniPulseBadArgumentException;
import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.MultipleChoiceAnswer;
import com.minipulse.model.answer.SingleChoiceAnswer;
import com.minipulse.model.answer.TextAnswer;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import com.minipulse.model.question.SingleChoiceQuestion;
import com.minipulse.model.question.TextQuestion;
import com.minipulse.model.response.Response;

import java.util.UUID;

public class ResponseResource {
    private MiniPulseDB db;

    public ResponseResource() {
        setDb(DBFactory.getDB());
    }
    public MiniPulseDB getDbe   () {
        return db;
    }

    public void setDb(MiniPulseDB db) {
        this.db = db;
    }

    public Response submitResponse(Response response) throws MiniPulseBadArgumentException {

        if(response.getRespondingUser() == null || response.getRespondingUser().isEmpty()){
            throw new MiniPulseBadArgumentException("Invalid user");
        }

        String userName = db.getUser(response.getRespondingUser());
        if (userName == null) {
            throw new MiniPulseBadArgumentException("User doesn't exist");
        }

        if(response.getPollId() == null || response.getPollId().isEmpty()){
            throw new MiniPulseBadArgumentException("Invalid poll ID");
        }
        Poll poll = db.getPoll(response.getPollId());
        if(poll == null){
            throw new MiniPulseBadArgumentException("Invalid poll ID");
        }

        if(poll.getState() != PollState.ACCEPTING){
            throw new MiniPulseBadArgumentException("poll should be in accepting state");
        }

        if(poll.getOwner().equals(response.getRespondingUser())){
            throw new MiniPulseBadArgumentException("poll owner and responding user cannot be same");
        }

        for (Answer answer : response.getAnswers()) {
            if (!answer.getPollId().equals(response.getPollId())) {
                throw new MiniPulseBadArgumentException("Poll id for response doesn't match answer's poll id");
            }
            boolean validQuestion = false;
            for (Question question : poll.getQuestions()) {
                if (question.getQuestionId().equals(answer.getQuestionId())) {
                    if(question.getType().equals(answer.getType())) {
                        validQuestion = true;
                        if(answer.getType().equals("MULTIPLE")){
                            MultipleChoiceAnswer multipleChoiceAnswer = (MultipleChoiceAnswer) answer;
                            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
                            for (Integer choice : multipleChoiceAnswer.getChoices()) {
                                if (!multipleChoiceQuestion.getChoices().containsKey(choice)) {
                                    throw new MiniPulseBadArgumentException("Not a valid choice for this question");
                                }
                            }
                        }else if(answer.getType().equals("SINGULAR")){
                            SingleChoiceAnswer singleChoiceAnswer = (SingleChoiceAnswer) answer;
                            SingleChoiceQuestion singleChoiceQuestion = (SingleChoiceQuestion) question;
                            if (!question.isMandatory() && singleChoiceAnswer.getChoice() == 0) continue;
                            if(!singleChoiceQuestion.getChoices().containsKey(singleChoiceAnswer.getChoice())) {
                                throw new MiniPulseBadArgumentException("Not a valid choice for this question");
                            }
                        }else if(answer.getType().equals("TEXT")) {
                            TextAnswer textAnswer = (TextAnswer) answer;
                            TextQuestion textQuestion = (TextQuestion) question;

                            if (textAnswer.getText().length() > textQuestion.getAnswerLength()) {
                                throw new MiniPulseBadArgumentException("Answer Length must be shorter than " + textQuestion.getAnswerLength());
                            }
                        }
                    }
                    break;
                }
            }
            if (!validQuestion) {
                throw new MiniPulseBadArgumentException("Answer doesn't correspond to a question in this poll");
            }
        }

        for (int outer = 0; outer < response.getAnswers().size(); outer++) {
            for (int inner = outer + 1; inner < response.getAnswers().size(); inner++) {
                if (response.getAnswers().get(outer).getQuestionId().equals(response.getAnswers().get(inner).getQuestionId())) {
                    throw new MiniPulseBadArgumentException("Question is answered multiple times");
                }
            }
        }

        for (Question question : poll.getQuestions()){
            if(question.isMandatory()){
                boolean foundAnswer = false;
                for(Answer answer : response.getAnswers()){
                    if(answer.getQuestionId().equals(question.getQuestionId())){
                        foundAnswer = true;
                        if(answer.getType().equals("TEXT")){
                            TextAnswer textAnswer = (TextAnswer) answer;
                            if(textAnswer.getText() == null || textAnswer.getText().isEmpty()){
                                throw new MiniPulseBadArgumentException("Mandatory question not answered");
                            }
                        }else if(answer.getType().equals("SINGULAR")){
                            SingleChoiceAnswer singleChoiceAnswer = (SingleChoiceAnswer) answer;
                            if(singleChoiceAnswer.getChoice() == 0){
                                throw new MiniPulseBadArgumentException("Mandatory question not answered");
                            }
                        }
                        break;
                      }
                }
                if(!foundAnswer){
                    throw new MiniPulseBadArgumentException("Mandatory question not answered");
                }
            }
        }

        response.setResponseId(UUID.randomUUID().toString());
        for(Answer answer : response.getAnswers()){
            answer.setAnswerId(UUID.randomUUID().toString());
            answer.setResponseId(response.getResponseId());
        }

        db.saveResponse(response);
        return response;
    }


}
