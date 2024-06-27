package com.minipulse.resource;

import com.minipulse.db.MiniPulseDB;
import com.minipulse.exception.MiniPulseBadArgumentException;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.Question;
import com.minipulse.model.report.PollReport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.UUID;

public class PollResource  {

    private MiniPulseDB db;

    public MiniPulseDB getDbe   () {
        return db;
    }

    public void setDb(MiniPulseDB db) {
        this.db = db;
    }

    public Poll createPoll(Poll poll) throws MiniPulseBadArgumentException {
        if (poll.getPollTitle() == null || poll.getPollTitle().isEmpty()) {
            throw new MiniPulseBadArgumentException("Poll Title is missing");
        }

        if (poll.getQuestions() == null || poll.getQuestions().isEmpty()) {
            throw new MiniPulseBadArgumentException("Poll doesn't contain any questions");
        }

        if (poll.getOwner() == null || poll.getOwner().isEmpty()) {
            throw new MiniPulseBadArgumentException("Poll requires an owner");
        }

        String userName = db.getUser(poll.getOwner());
        if (userName == null) {
            throw new MiniPulseBadArgumentException("User doesn't exist");
        }

        String existingPollId = db.getPollIdForUserByTitle(poll.getOwner(), poll.getPollTitle());
        if (existingPollId != null) {
            throw new MiniPulseBadArgumentException("Poll with this Title already exist");
        }

        poll.setPollId(UUID.randomUUID().toString());
        poll.setState(PollState.NEW);

        for (int index = 0; index < poll.getQuestions().size(); index++) {
            Question question = poll.getQuestions().get(index);
            validateQuestion(question, index);
            question.setQuestionId(UUID.randomUUID().toString());
            question.setPollId(poll.getPollId());
        }

        db.savePollAndOverwriteQuestions(poll);

        return poll;
    }

    public Poll updatePoll(Poll poll) throws MiniPulseBadArgumentException {
        if (poll.getPollId() == null || poll.getPollId().isEmpty()){
            throw new MiniPulseBadArgumentException("Poll does not exist");
        }

        if (poll.getState() != PollState.NEW ){
            throw new MiniPulseBadArgumentException("Poll not in new state");
        }

        if (poll.getOwner() == null || poll.getOwner().isEmpty()){
            throw new MiniPulseBadArgumentException("Poll requires an owner");
        }

        //in update poll owner can't be changed
        String userName = db.getUser(poll.getOwner());
        if (userName == null) {
            throw new MiniPulseBadArgumentException("User doesn't exist");
        }

        if (poll.getPollTitle() == null || poll.getPollTitle().isEmpty()){
            throw new MiniPulseBadArgumentException("Poll does not have a title");
        }

        if (poll.getQuestions() == null || poll.getQuestions().isEmpty()){
            throw new MiniPulseBadArgumentException("poll doesnt contain any questions");
        }

        String existingPollId = db.getPollIdForUserByTitle(poll.getOwner(), poll.getPollTitle());
        if (existingPollId != null && !poll.getPollId().equals(existingPollId)){
            throw new MiniPulseBadArgumentException("cant change title to existing title");
        }



        for (int index = 0; index < poll.getQuestions().size(); index++) {
            Question question = poll.getQuestions().get(index);
            validateQuestion(question, index);
            question.setQuestionId(UUID.randomUUID().toString());
            question.setPollId(poll.getPollId());
        }
        db.savePollAndOverwriteQuestions(poll);
        return poll;
    }

    public void flightPoll(String pollId) throws MiniPulseBadArgumentException {
        if(pollId == null || pollId.isEmpty()){
            throw new MiniPulseBadArgumentException("Poll does not exist");
        }

        Poll poll = getPoll(pollId);
        if (poll == null) {
            throw new MiniPulseBadArgumentException("Not a valid poll");
        }

        if (poll.getState() != PollState.NEW ){
            throw new MiniPulseBadArgumentException("Poll not in new state");
        }

        poll.setState(PollState.ACCEPTING);

        db.modifyPollState(pollId, PollState.ACCEPTING);
    }

    public Poll getPoll(String pollId) throws MiniPulseBadArgumentException {
        if(pollId == null || pollId.isEmpty()){
            throw new MiniPulseBadArgumentException("Poll does not exist");
        }
        Poll poll = db.getPoll(pollId);
        return poll;
    }

    public void deletePoll(String pollId) throws MiniPulseBadArgumentException {
        if(pollId == null || pollId.isEmpty()){
            throw new MiniPulseBadArgumentException("Poll does not exist");
        }

        Poll poll = getPoll(pollId);

        if(poll.getState() == PollState.ACCEPTING){
            throw new MiniPulseBadArgumentException("Poll should either be in new or closed state");
        }

        db.deletePollEntirely(pollId);
    }

    public void closePoll(String pollId) throws MiniPulseBadArgumentException {
        if(pollId == null || pollId.isEmpty()){
            throw new MiniPulseBadArgumentException("poll does not exist");
        }

        Poll poll = getPoll(pollId);
        if(poll.getState() != PollState.ACCEPTING){
            throw new MiniPulseBadArgumentException("Poll should be in accepting state");
        }

        poll.setState(PollState.CLOSED);
        db.modifyPollState(pollId, PollState.CLOSED);
    }
    
    public PollReport generateReport(String pollId) throws MiniPulseBadArgumentException {
        if(pollId == null || pollId.isEmpty()){
            throw new MiniPulseBadArgumentException("Poll does not exist");
        }
        Poll poll = db.getPollEntirely(pollId);
        if(poll.getState() != PollState.CLOSED){
            throw new MiniPulseBadArgumentException("Poll should be in accepting state");
        }
        //create a report and return
        return null;
    }

    private void validateQuestion(Question question, int position) throws MiniPulseBadArgumentException {
        if (question.getQuestionTitle() == null || question.getQuestionTitle().isEmpty()) {
            throw new MiniPulseBadArgumentException("Question title is empty for question #" + position);
        }
    }

    public void testCaller() {
        try {
            test();
        } catch (MalformedURLException m) {
            System.out.printf("Bad Url");
        } catch (IOException e) {
            System.out.println("Unable to access site");
        }
    }

    public void test() throws MalformedURLException, IOException {

        URLConnection c= null;
        int retries = 0;
        while (retries++ < 3) {
            try {
                c = new URL("http://www.sanjay4084032830498430984320.com").openConnection();
                break;
            } catch (IOException ignore) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
            }
        }
        if (c == null) {
            throw new IOException("site doesn't exist. 3 retries exhausted");
        }
    }
    
}
