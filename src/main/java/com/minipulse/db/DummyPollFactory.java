package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.TextQuestion;

import java.util.*;

public class DummyPollFactory {

    public static DummyPollFactory INSTANCE;
    private final String userName;
    private List<Poll> polls = new ArrayList<>();

    private DummyPollFactory(String userName) {
        this.userName = userName;
        polls.addAll(getRandomListOfDummyPolls());
    }

    public static synchronized DummyPollFactory newInstance(String userName) {
        if (INSTANCE == null) {
            INSTANCE = new DummyPollFactory(userName);
        }
        return INSTANCE;
    }

    public List<Poll> getPollsFor(String userName) {
        return polls;
    }

    private List<Poll> getRandomListOfDummyPolls() {
        return Arrays.asList(getDummyEditablePoll(), getDummyEditablePoll(), getDummyEditablePoll(), getDummyAcceptingPoll(), getDummyAcceptingPoll());
    }

    private Poll getDummyEditablePoll() {
        Poll poll = new Poll();
        poll.setPollTitle("Poll " + new Random().nextInt(1000));
        poll.setPollDescription("Poll description" + new Random().nextInt(1000));
        poll.setPollId(UUID.randomUUID().toString());
        poll.setState(PollState.NEW);
        poll.setOwner(userName);
        TextQuestion question  = new TextQuestion();
        question.setQuestionTitle("First question");
        question.setQuestionDescription("First question desc");
        question.setAnswerLength(50);
        MultipleChoiceQuestion question1  = new MultipleChoiceQuestion();
        question1.setQuestionTitle("Second question");
        question1.setQuestionDescription("Second question desc");
        question1.setChoices(Map.of(1, "Choice1", 2, "Choice2"));
        poll.setQuestions(Arrays.asList(question, question1));
        return poll;
    }

    private Poll getDummyAcceptingPoll() {
        Poll poll = new Poll();
        poll.setPollTitle("Poll " + new Random().nextInt(1000));
        poll.setPollDescription("Poll description" + new Random().nextInt(1000));
        poll.setPollId(UUID.randomUUID().toString());
        poll.setState(PollState.ACCEPTING);
        poll.setOwner(userName);
        TextQuestion question  = new TextQuestion();
        question.setQuestionTitle("First question");
        question.setQuestionDescription("First question desc");
        question.setAnswerLength(50);
        MultipleChoiceQuestion question1  = new MultipleChoiceQuestion();
        question1.setQuestionTitle("Second question");
        question1.setQuestionDescription("Second question desc");
        question1.setChoices(Map.of(1, "Choice1", 2, "Choice2"));
        poll.setQuestions(Arrays.asList(question, question1));
        return poll;
    }

}
