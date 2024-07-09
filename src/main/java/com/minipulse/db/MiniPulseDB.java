package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.response.Response;

import java.sql.*;

public class MiniPulseDBImpl implements MiniPulseDB {
    private static final String URL = "jdbc:mysql://localhost:3306/your_database";
    private static final String USER = "username";
    private static final String PASSWORD = "password";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


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
    public class MiniPulse implements MiniPulseDB
        {
            String getPollIdForUserByTitle(String user, String pollTitle)
                {
                    String query="select  poll_id from polls where user=? and title=?";
                        try
                            {
                                Connection con = getConnection();
                                PreparedStatement st=con.prepareStatement(query);
                                st.setString(1,user);
                                st.setString(2,pollTitle);
                                ResultSet rs=st.executeQuery();
                                if(rs.next())
                                    {
                                        return rs.getString("poll id");
                                    }
                            } catch (SQLException e)
                                {
                                e.printStackTrace();
                            }
                        return null;
                }

            void savePollAndOverwriteQuestions(Poll poll);
            {
                
            }

             void modifyPollState(String pollId, PollState state)
            {
                
            }

            Poll getPoll(String pollId)
            {
                
            }

            void deletePollEntirely(String pollId)
            {
                
            }

            Poll getPollEntirely(String pollId)
            {
                
            }

            void newUser(String user, String userName)
            {
                
            }
            String getUser(String user)
            {
                
            }

            void saveResponse(Response response)
            {
                
            }


            
            
        }
