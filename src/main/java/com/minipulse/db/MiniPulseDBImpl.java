package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import com.minipulse.model.question.SingleChoiceQuestion;
import com.minipulse.model.question.TextQuestion;
import com.minipulse.model.response.Response;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniPulseDBImpl implements MiniPulseDB
{
     private static final Logger LOGGER = Logger.getLogger(MiniPulseDBImpl.class.getName());
     

 public String getPollIdForUserByTitle(String user, String pollTitle) {
    try {
        String query = "select poll_id from polls where user=? and title=?";
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        
        try (Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, user);
            pst.setString(2, pollTitle);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("poll_id");
                }
            }
        }   catch (SQLException ex) {
            Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }   catch (ClassNotFoundException ex) {
            Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
         return null;
}

      
       public void savePollAndOverwriteQuestions(Poll poll)
        {
               try{
                   String delquery="delete from questions where poll_id=?";
                   String insPollQuery="replace into polls(poll_id,user,title,state)"+"values(?,?,?,?)";
                   String insQuestionQuery="insert into questions(poll_id,question_id,question_text)"+"values(?,?,?)";
                   Class.forName("org.apache.derby.jdbc.ClientDriver");
                   try(Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                           PreparedStatement delst =con.prepareStatement(delquery);
                           PreparedStatement insPollst =con.prepareStatement(insPollQuery);
                           PreparedStatement insQuesst =con.prepareStatement(insQuestionQuery)){
                       
                       delst.setString(1, poll.getPollId());
                       delst.executeUpdate();
                       
                       insPollst.setString(1,poll.getPollId());
                       insPollst.setString(2,poll.getOwner());
                       insPollst.setString(3,poll.getPollTitle());
                       insPollst.setString(4,poll.getState().name());
                       insPollst.executeUpdate();
                       
                       for(Question question: poll.getQuestions())
                       {
                           insQuesst.setString(1,poll.getPollId());
                           insQuesst.setString(2,question.getQuestionId());
                           insQuesst.setString(3,question.getText());
                           insQuesst.addBatch();
                       }
                       insQuesst.executeBatch();
                       
                   }catch (SQLException e) {
                       e.printStackTrace();
                   }
                   
               }catch (ClassNotFoundException ex) {
                        Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
        
        public void modifyPollState(String pollId,PollState state)
        {
               try{
                   String query="update polls set state=? where poll_id=?";
                   Class.forName("org.apache.derby.jdbc.ClientDriver");
                   try(Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                           PreparedStatement pst=con.prepareStatement(query)){
                       pst.setString(1, state.name());
                       pst.setString(2,pollId);
                       pst.executeUpdate();
                       
                   }catch (SQLException e) {
                       e.printStackTrace();
                   }
               }catch (ClassNotFoundException ex) {
                Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
       public Poll getPoll(String pollId) 
       {
               try{
                   String query="select* from polls where poll_id=?";
                   Class.forName("org.apache.derby.jdbc.ClientDriver");
                   try(Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                           PreparedStatement pst=con.prepareStatement(query)){
                       pst.setString(1, pollId);
                       try(ResultSet rs= pst.executeQuery()){
                           if(rs.next())
                           {
                               String pollId_db=rs.getString("poll_id");
                               String user=rs.getString("username");
                               String title=rs.getString("title");
                               String state=rs.getString("state");
                               
                               Poll poll=new Poll(pollId_db, user, title, state);
                               return poll;
                           }
                       }
                       
                   }catch (SQLException e) {
                       e.printStackTrace();
                   }
                   
                   return null;
                   
               }catch (ClassNotFoundException ex) {
                Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
         return null;
           
       }
       
       public void deletePollEntirely(String pollId)
       {
                try{
                    String delQuestionsQuery="delete from questions where poll_id=?";
                    String delPollQuery="delete from polls where poll_id=?";
                    Class.forName("org.apache.derby.jdbc.ClientDriver");
                    try(Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                            PreparedStatement delQuesst = con.prepareStatement(delQuestionsQuery);
                            PreparedStatement delPollst = con.prepareStatement(delPollQuery)){
                        delQuesst.setString(1, pollId);
                        delQuesst.executeUpdate();
                        delPollst.setString(1, pollId);
                        delPollst.executeUpdate();
                        
                    }catch (SQLException e) {
                        e.printStackTrace();
                    }
                }catch (ClassNotFoundException ex) {
                Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
       
       public Poll getPollEntirely(String pollId) 
{
    Poll poll = null;
    try {
        String PollQuery = "select * from polls where poll_id = ?";
        String QuestionsQuery = "select * from questions where poll_id = ?";
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        try (Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
             PreparedStatement pollst = con.prepareStatement(PollQuery);
             PreparedStatement quesst = con.prepareStatement(QuestionsQuery)) {
            
            pollst.setString(1, pollId);
            try (ResultSet rs = pollst.executeQuery()) {
                if (rs.next()) {
                    poll = new Poll(rs.getString("poll_id"), rs.getString("username"), rs.getString("title"), rs.getString("state"));
                    
                    quesst.setString(1, pollId);
                    try (ResultSet qs = quesst.executeQuery()) {
                        while (qs.next()) {
                            String questionType = qs.getString("type");
                            Question question;
                            
                            switch (questionType) {
                                case "TEXT":
                                    question = new TextQuestion(
                                        qs.getString("question_id"),
                                        qs.getString("poll_id"),
                                        qs.getString("question_text")
                                    );
                                    ((TextQuestion) question).setAnswerLength(qs.getInt("answer_length"));
                                    break;
                                case "MULTIPLE":
                                    question = new MultipleChoiceQuestion(
                                        qs.getString("question_id"),
                                        qs.getString("poll_id"),
                                        qs.getString("question_text")
                                    );
                                    Map<Integer, String> choices = getChoicesForQuestion(con, qs.getString("question_id"));
                                    ((MultipleChoiceQuestion) question).setChoices(choices);
                                    break;
                                case "SINGULAR":
                                    question = new SingleChoiceQuestion(
                                        qs.getString("question_id"),
                                        qs.getString("poll_id"),
                                        qs.getString("question_text")
                                    );
                                    choices = getChoicesForQuestion(con, qs.getString("question_id"));
                                    ((SingleChoiceQuestion) question).setChoices(choices);
                                    break;
                                default:
                                    throw new IllegalArgumentException("Unknown question type: " + questionType);
                            }
                            
                            question.setQuestionDescription(qs.getString("question_desc"));
                            poll.addQuestion(question);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } catch (ClassNotFoundException ex) {
        Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    return poll;
}

        private Map<Integer, String> getChoicesForQuestion(Connection con, String questionId) throws SQLException {
            Map<Integer, String> choices = new HashMap<>();
            String choicesQuery = "SELECT * FROM choices WHERE question_id = ?";
            try (PreparedStatement choicesSt = con.prepareStatement(choicesQuery)) {
                choicesSt.setString(1, questionId);
                try (ResultSet choicesRs = choicesSt.executeQuery()) {
                    while (choicesRs.next()) {
                        choices.put(choicesRs.getInt("choice_id"), choicesRs.getString("choice_text"));
                    }
                }
            }
            return choices;
        }

       
       void newUser(String user, String userName)
       {
           String query = "insert into users(user_id,userName)"+" values(?,?)";
           try
           {
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                Statement st=con.createStatement();
                PreparedStatement pst=con.prepareStatement(query);
                pst.setString(1, user);
                pst.setString(2, userName);
                pst.executeUpdate();
           }catch (SQLException e) {
                 e.printStackTrace();
           }
           catch (ClassNotFoundException e) {
                e.printStackTrace();
            }           

       }
       @Override
       public String getUser(String user) 
       {
               try{
                   String query="select userName from users where user_id=?";
                   Class.forName("org.apache.derby.jdbc.ClientDriver");
                   try( Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                           PreparedStatement pst=con.prepareStatement(query)){
                       pst.setString(1,user);
                       try( ResultSet rs=pst.executeQuery()){
                           if(rs.next())
                           {
                               return rs.getString("userName");
                           }
                       }
                   }catch (SQLException e) {
                       e.printStackTrace();
                   }
                   
                   return null;
               }catch (ClassNotFoundException ex) {
                 Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
           }       
         return null;
       }
       
     @Override
       public void saveResponse(Response response)  {
        try {
            String query = "INSERT INTO responses (response_id, poll_id, user_id, answer) VALUES (?, ?, ?, ?)";
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            try (Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
                    PreparedStatement pst = con.prepareStatement(query)) {
                
                pst.setString(1, response.getResponseId());
                pst.setString(2, response.getPollId());
                pst.setString(3, response.getUser());
                Array answersArray = con.createArrayOf("VARCHAR", response.getAnswers().toArray());
                pst.setArray(4, answersArray);
                
                pst.executeUpdate();
                
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "SQL error occurred while saving response", ex);
            }
        } catch (ClassNotFoundException ex) {
             Logger.getLogger(MiniPulseDBImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void newUsASer(String user, String userName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

   
    
     
