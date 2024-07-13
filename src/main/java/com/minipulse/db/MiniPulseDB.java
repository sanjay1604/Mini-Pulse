package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.response.Response;

import java.util.List;

public interface MiniPulseDB {

    List<Poll> getPollsByUser(String user);

    List<Poll> getAcceptingPolls(String user);
    String getPollIdForUserByTitle(String user, String pollTitle);

    void savePollAndOverwriteQuestions(Poll poll);

    void modifyPollState(String pollId, PollState state);

    Poll getPoll(String pollId);

    void deletePollEntirely(String pollId);

    Poll getPollEntirely(String pollId);

    void newUser(String user, String userName);

    String getUser(String user);

    void saveResponse(Response response);

}
