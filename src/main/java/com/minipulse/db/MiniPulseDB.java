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

            public void savePollAndOverwriteQuestions(Poll poll) 
	{
		String delquery="delete from questions where poll_id=?";
		String insPollQuery="replace into polls(poll_id, user, title, state) VALUES(?,?,?,?)";
		String insQuestionQuery="insert into questions (poll_id, question_id,  question_text) VALUES(?,?,?)";
        try
        {
            Connection con = getConnection();
            con.setAutoCommit(false);
            try
            {
                PreparedStatement delst=con.prepareStatement(delquery);
                PreparedStatement insPollst=con.prepareStatement(insPollQuery);
                PreparedStatement insQuesst=con.prepareStatement(insQuestionQuery);
                
                delst.setString(1,poll.getId());
                delst.executeUpdate();
                
                insPollst.setString(1,poll.getId());
                insPollst.setString(2,poll.getUser());
                insPollst.setString(3,poll.getTitle());
                insPollst.setString(4,poll.getState().name());
                insPollst.executeUpdate();
                
                 for (Poll.Question question : poll.getQuestions()) {
                                    insertQuestionst.setString(1, poll.getId());
                                    insertQuestionst.setString(2, question.getId());
                                    insertQuestionst.setString(3, question.getText());
                                    insertQuestionst.addBatch();
                                }
                                insertQuestionst.executeBatch();
                
                                conn.commit();
                    } catch (SQLException e) {
                        conn.rollback();
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
    }

public void modifyPollState(String pollId, PollState state)
{
    String query="update polls set state=? where poll_id=?";
    try
    {
        Connection con =getConnection();
        PreparedStatement st=con.prepareStatement(query);
        st.setString(1,state.name());
        st.setString(2,pollId);
        st.executeUpdate()
    }catch (SQLException e) {
                e.printStackTrace();
            }
}


 public Poll getPoll(String pollId) 
{
    String query="select*from polls where poll_id=?";
    try
    {
        Connection con =getConnection();
        PreparedStatement st=con.prepareStatement(query);
        st.setString(1,pollId);
        ResultSet rs= st.executeQuery();
        if(rs.next())
        {
            String pollId_db=rs.getString("poll_id");
            String user=rs.getString("user");
            String title=rs.getString("title");
            String state=rs.getString("state");
            
            Poll poll = new Poll(pollId_db, user, title, state);
            return poll;
        }
    } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
}

public void deletePollEntirely(String pollId)
{
    String delQuestionsQuery="delete from questions where poll_id=?";
    String delPollQuery="delete from polls where poll_id=?";
    try
    {
        Connection con = getConnection();
        PreparedStatement delQuesst=con.prepareStatement(delQuestionsQuery);
        PreparedStatement delPollst=con.prepareStatement(delPollQuery);
        
        con.setAutoCommit(false);
        
        delQuesst.setString(1,pollId);
        delQuesst.executeUpdate();
        delPollst.setString(1,pollId);
        delPollst.executeUpdate();
        
        con.commit();
    } catch (SQLException e) {
                e.printStackTrace();
            }
}
           Poll getPollEntirely(String pollId)
            {
                           String PollQuery="select* from polls where poll_id=?";
		String QuestionsQuery="select* from questions where poll_id=?";
		try
		{
			Connection con = getConnection();
			PreparedStatement pollst=con.prepareStatement(PollQuery);
			PreparedStatement quesst=con.prepareStatement(QuestionsQuery);	
			pollst.setString(1,pollId);
			ResultSet rs =pollst.executeQuery();
			if(rs.next())
			{
				Poll poll = new Poll( rs.getString("poll_id"), rs.getString("user"), rs.getString("title"), PollState.valueOf(rs.getString("state")));
				
			quest.setString(1,pollId);
			ResultSet qs= quest.executeQuery();
			if(qs.next())
			{
				Question question = new Question( qs.getString("question_id"), qs.getString("poll
_id"), qs.getString("question_text");
				poll.addQuestion(question);
			}
		}
			return poll;
		} 	catch (SQLException e) {
           	 e.printStackTrace();
      		  }
        	return null;
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
