package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.response.Response;

import java.sql.*;


public interface MiniPulseDB {

    String getPollIdForUserByTitle(String user, String pollTitle);

    void savePollAndOverwriteQuestions(Poll poll);

    void modifyPollState(String pollId, PollState state);

    Poll getPoll(String pollId);

    void deletePollEntirely(String pollId);

    Poll getPollEntirely(String pollId);

    void newUsASer(String user, String userName);

    String getUser(String user);

    void saveResponse(Response response);

}
            
        }
