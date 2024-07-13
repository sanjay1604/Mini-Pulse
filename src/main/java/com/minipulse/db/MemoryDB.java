package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryDB implements MiniPulseDB {

    private final Map<String, Poll> polls = new HashMap<>();
    private final Map<String, String> users = new HashMap<>();

    @Override
    public List<Poll> getPollsByUser(String user) {
        List<Poll> outList = new ArrayList<>();
        for (Poll poll : polls.values()) {
            if (poll.getOwner().equals(user)) {
                outList.add(poll);
            }
        }
        return outList;
    }

    @Override
    public List<Poll> getAcceptingPolls(String user) {
        List<Poll> outList = new ArrayList<>();
        for (Poll poll : polls.values()) {
            if (poll.getState() == PollState.ACCEPTING && !poll.getOwner().equals(user)) {
                outList.add(poll);
            }
        }
        return outList;
    }

    @Override
    public String getPollIdForUserByTitle(String user, String pollTitle) {
        for (Poll poll : polls.values()) {
            if (poll.getOwner().equals(user) && poll.getPollTitle().equals(pollTitle)) {
                return poll.getPollId();
            }
        }
        return null;
    }

    @Override
    public void savePollAndOverwriteQuestions(Poll poll) {
        polls.put(poll.getPollId(), poll);
    }

    @Override
    public void modifyPollState(String pollId, PollState state) {
        polls.get(pollId).setState(state);
    }

    @Override
    public Poll getPoll(String pollId) {
        return polls.get(pollId);
    }

    @Override
    public void deletePollEntirely(String pollId) {
        polls.remove(pollId);
    }

    @Override
    public Poll getPollEntirely(String pollId) {
        return polls.get(pollId);
    }

    @Override
    public void newUser(String user, String userName) {
        users.put(user, userName);
    }

    @Override
    public String getUser(String user) {
        return users.get(user);
    }

    @Override
    public void saveResponse(Response response) {
        if (polls.get(response.getPollId()).getResponses() == null) {
            polls.get(response.getPollId()).setResponses(new ArrayList<>());
        }
        polls.get(response.getPollId()).getResponses().add(response);
    }
}
