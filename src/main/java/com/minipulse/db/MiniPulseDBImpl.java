package com.minipulse.db;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.MultipleChoiceAnswer;
import com.minipulse.model.answer.SingleChoiceAnswer;
import com.minipulse.model.answer.TextAnswer;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.*;
import com.minipulse.model.response.*;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniPulseDBImpl implements MiniPulseDB {
    private final Logger LOGGER = Logger.getLogger(MiniPulseDBImpl.class.getName());

    @Override
    public List<Poll> getPollsByUser(String user) {
        return null;
    }

    @Override
    public List<Poll> getAcceptingPolls(String user) {
        return null;
    }

    // Retrieve Poll ID based on user and poll title
    public String getPollIdForUserByTitle(String user, String pollTitle) {
        String query = "SELECT poll_id FROM polls WHERE user=? AND title=?";
        try (Connection con = DriverManager.getConnection("", "", "")) {
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, user);
                pst.setString(2, pollTitle);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("poll_id");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Save Poll and Overwrite Questions
    public void savePollAndOverwriteQuestions(Poll poll) {
        String deleteQuestionsQuery = "DELETE FROM text_questions WHERE poll_id=?;" +
                "DELETE FROM multiple_choice_questions WHERE poll_id=?;" +
                "DELETE FROM single_choice_questions WHERE poll_id=?";
        String upsertPollQuery = "MERGE INTO polls USING (VALUES (?, ?, ?, ?, ?)) AS vals(poll_id, user, title, state, description) " +
                "ON polls.poll_id=vals.poll_id " +
                "WHEN MATCHED THEN UPDATE SET user=vals.user, title=vals.title, state=vals.state, description=vals.description " +
                "WHEN NOT MATCHED THEN INSERT (poll_id, user, title, state, description) VALUES (vals.poll_id, vals.user, vals.title, vals.state, vals.description)";
        String insertTextQuestionQuery = "INSERT INTO text_questions(question_id, poll_id, question_text, question_desc, is_mandatory) VALUES (?, ?, ?, ?, ?)";
        String insertMultipleChoiceQuestionQuery = "INSERT INTO multiple_choice_questions(question_id, poll_id, question_text, question_desc, is_mandatory) VALUES (?, ?, ?, ?, ?)";
        String insertSingleChoiceQuestionQuery = "INSERT INTO single_choice_questions(question_id, poll_id, question_text, question_desc, is_mandatory) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection("","","");
             PreparedStatement delQuesSt = con.prepareStatement(deleteQuestionsQuery);
             PreparedStatement upsertPollSt = con.prepareStatement(upsertPollQuery);
             PreparedStatement insTextQuesSt = con.prepareStatement(insertTextQuestionQuery);
             PreparedStatement insMCQuesSt = con.prepareStatement(insertMultipleChoiceQuestionQuery);
             PreparedStatement insSCQuesSt = con.prepareStatement(insertSingleChoiceQuestionQuery)) {

            // Delete existing questions for the poll
            delQuesSt.setString(1, poll.getPollId());
            delQuesSt.setString(2, poll.getPollId());
            delQuesSt.setString(3, poll.getPollId());
            delQuesSt.executeUpdate();

            // Upsert the poll
            upsertPollSt.setString(1, poll.getPollId());
            upsertPollSt.setString(2, poll.getOwner());
            upsertPollSt.setString(3, poll.getPollTitle());
            upsertPollSt.setString(4, poll.getState().name());
            upsertPollSt.setString(5, poll.getPollDescription());
            upsertPollSt.executeUpdate();

            // Insert questions
            for (Question question : poll.getQuestions()) {
                if (question instanceof TextQuestion) {
                    TextQuestion textQuestion = (TextQuestion) question;
                    insTextQuesSt.setString(1, textQuestion.getQuestionId());
                    insTextQuesSt.setString(2, textQuestion.getPollId());
                    insTextQuesSt.setString(3, textQuestion.getQuestionTitle());
                    insTextQuesSt.setString(4, textQuestion.getQuestionDescription());
                    insTextQuesSt.setBoolean(5, textQuestion.isMandatory());
                    insTextQuesSt.executeUpdate();
                } else if (question instanceof MultipleChoiceQuestion) {
                    MultipleChoiceQuestion mcQuestion = (MultipleChoiceQuestion) question;
                    insMCQuesSt.setString(1, mcQuestion.getQuestionId());
                    insMCQuesSt.setString(2, mcQuestion.getPollId());
                    insMCQuesSt.setString(3, mcQuestion.getQuestionTitle());
                    insMCQuesSt.setString(4, mcQuestion.getQuestionDescription());
                    insMCQuesSt.setBoolean(5, mcQuestion.isMandatory());
                    insMCQuesSt.executeUpdate();
                    // Insert choices
                    saveChoices(con, mcQuestion.getQuestionId(), mcQuestion.getChoices());
                } else if (question instanceof SingleChoiceQuestion) {
                    SingleChoiceQuestion scQuestion = (SingleChoiceQuestion) question;
                    insSCQuesSt.setString(1, scQuestion.getQuestionId());
                    insSCQuesSt.setString(2, scQuestion.getPollId());
                    insSCQuesSt.setString(3, scQuestion.getQuestionTitle());
                    insSCQuesSt.setString(4, scQuestion.getQuestionDescription());
                    insSCQuesSt.setBoolean(5, scQuestion.isMandatory());
                    insSCQuesSt.executeUpdate();
                    // Insert choices
                    saveChoices(con, scQuestion.getQuestionId(), scQuestion.getChoices());
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving poll and overwriting questions", e);
        }
    }

    // Save Choices
    private void saveChoices(Connection con, String questionId, Map<Integer, String> choices) throws SQLException {
        String deleteChoicesQuery = "DELETE FROM choices WHERE question_id=?";
        String insertChoicesQuery = "INSERT INTO choices(question_id, choice_id, choice_text) VALUES (?, ?, ?)";

        try (PreparedStatement delChoicesSt = con.prepareStatement(deleteChoicesQuery);
             PreparedStatement insChoicesSt = con.prepareStatement(insertChoicesQuery)) {

            // Delete existing choices for the question
            delChoicesSt.setString(1, questionId);
            delChoicesSt.executeUpdate();

            // Insert new choices
            for (Map.Entry<Integer, String> entry : choices.entrySet()) {
                insChoicesSt.setString(1, questionId);
                insChoicesSt.setInt(2, entry.getKey());
                insChoicesSt.setString(3, entry.getValue());
                insChoicesSt.executeUpdate();
            }
        }
    }

    // Modify Poll State
    public void modifyPollState(String pollId, PollState state) {
        String query = "UPDATE polls SET state=? WHERE poll_id=?";
        try (Connection con = DriverManager.getConnection("","","");
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, state.name());
            pst.setString(2, pollId);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get Poll
    public Poll getPoll(String pollId) {
        String query = "SELECT * FROM polls WHERE poll_id=?";
        try (Connection con = DriverManager.getConnection("","","");
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, pollId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Poll poll = new Poll();
                    poll.setPollId(rs.getString("poll_id"));
                    poll.setOwner(rs.getString("user"));
                    poll.setPollTitle(rs.getString("title"));
                    poll.setState(PollState.valueOf(rs.getString("state")));
                    poll.setPollDescription(rs.getString("description"));
                    return poll;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Delete Poll Entirely
    public void deletePollEntirely(String pollId) {
        String deleteQuestionsQuery = "DELETE from questions where poll_id=?"+
                "DELETE from text_questions where poll_id=?;" +
                "DELETE from multiple_choice_questions where poll_id=?;" +
                "DELETE from single_choice_questions where poll_id=?;" +
                "DELETE from choices where question_id IN ("+
                "UNION SELECT question_id FROM multiple_choice_questions WHERE poll_id=? " +
                "UNION SELECT question_id FROM single_choice_questions WHERE poll_id=?)";
        String deletePollQuery = "DELETE FROM polls WHERE poll_id=?";
        try (Connection con = DriverManager.getConnection("","","");
             PreparedStatement delQuesSt = con.prepareStatement(deleteQuestionsQuery);
             PreparedStatement delPollSt = con.prepareStatement(deletePollQuery)) {

            // Delete questions and choices associated with the poll
            delQuesSt.setString(1, pollId);
            delQuesSt.setString(2, pollId);
            delQuesSt.setString(3, pollId);
            delQuesSt.setString(4, pollId);
            delQuesSt.setString(5, pollId);
            delQuesSt.setString(6, pollId);
            delQuesSt.executeUpdate();

            // Delete the poll
            delPollSt.setString(1, pollId);
            delPollSt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Get Poll Entirely
    public Poll getPollEntirely(String pollId) {
        Poll poll = getPoll(pollId);
        if (poll != null) {
            String questionsQuery = "SELECT * FROM text_questions WHERE poll_id=? " +
                    "UNION ALL SELECT * FROM multiple_choice_questions WHERE poll_id=? " +
                    "UNION ALL SELECT * FROM single_choice_questions WHERE poll_id=?";
            try (Connection con = DriverManager.getConnection("","","");
                 PreparedStatement pst = con.prepareStatement(questionsQuery)) {
                pst.setString(1, pollId);
                pst.setString(2, pollId);
                pst.setString(3, pollId);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        Question question = null;
                        String questionType = rs.getString("type");
                        switch (questionType) {
                            case "TEXT":
                                question = new TextQuestion();
                                break;
                            case "MULTIPLE":
                                question = new MultipleChoiceQuestion();
                                break;
                            case "SINGULAR":
                                question = new SingleChoiceQuestion();
                                break;
                        }
                        if (question != null) {
                            question.setQuestionId(rs.getString("question_id"));
                            question.setPollId(rs.getString("poll_id"));
                            question.setQuestionTitle(rs.getString("question_text"));
                            question.setQuestionDescription(rs.getString("question_desc"));
                            question.setMandatory(rs.getBoolean("is_mandatory"));

                            // Load choices for multiple and single choice questions
                            if (question instanceof MultipleChoiceQuestion || question instanceof SingleChoiceQuestion) {
                                loadChoices(con, question);
                            }

                            poll.getQuestions().add(question);
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return poll;
    }

    @Override
    public void newUser(String user, String userName) {

    }

    @Override
    public String getUser(String user) {
        return null;
    }

    // Load Choices
    private void loadChoices(Connection con, Question question) throws SQLException {
        String query = "SELECT * FROM choices WHERE question_id=?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, question.getQuestionId());
            try (ResultSet rs = pst.executeQuery()) {
                Map<Integer, String> choices = new HashMap<>();
                while (rs.next()) {
                    choices.put(rs.getInt("choice_id"), rs.getString("choice_text"));
                }
                if (question instanceof MultipleChoiceQuestion) {
                    ((MultipleChoiceQuestion) question).setChoices(choices);
                } else if (question instanceof SingleChoiceQuestion) {
                    ((SingleChoiceQuestion) question).setChoices(choices);
                }
            }
        }
    }

    // Save Response
    public void saveResponse(Response response) {
        String insResponseQuery = "INSERT INTO responses(response_id, poll_id, question_id, answer_type) VALUES (?, ?, ?, ?)";
        String insAnswerQuery = "INSERT INTO answers(answer_id, response_id) VALUES (?, ?)";
        String insTextAnswerQuery = "INSERT INTO text_answers(answer_id, text_answer) VALUES (?, ?)";
        String insMultipleChoiceAnswerQuery = "INSERT INTO multiple_choice_answers(answer_id, choice_id) VALUES (?, ?)";
        String insSingleChoiceAnswerQuery = "INSERT INTO single_choice_answers(answer_id, choice_id) VALUES (?, ?)";

        try (Connection con = DriverManager.getConnection("", "", "");
             PreparedStatement insResponseSt = con.prepareStatement(insResponseQuery);
             PreparedStatement insAnswerSt = con.prepareStatement(insAnswerQuery);
             PreparedStatement insTextAnsSt = con.prepareStatement(insTextAnswerQuery);
             PreparedStatement insMcAnsSt = con.prepareStatement(insMultipleChoiceAnswerQuery);
             PreparedStatement insScAnsSt = con.prepareStatement(insSingleChoiceAnswerQuery)) {

            for (Answer answer : response.getAnswers()) {
                // Insert into common responses table
                insResponseSt.setString(1, answer.getResponseId());
                insResponseSt.setString(2, answer.getPollId());
                insResponseSt.setString(3, answer.getQuestionId());

                if (answer instanceof TextAnswer) {
                    insResponseSt.setString(4, "TEXT");
                    insResponseSt.executeUpdate();

                    // Insert into answers table
                    insAnswerSt.setString(1, answer.getAnswerId());
                    insAnswerSt.setString(2, answer.getResponseId());
                    insAnswerSt.executeUpdate();

                    // Insert into text_answers table
                    insTextAnsSt.setString(1, answer.getAnswerId());
                    insTextAnsSt.setString(2, ((TextAnswer) answer).getText());
                    insTextAnsSt.executeUpdate();

                } else if (answer instanceof MultipleChoiceAnswer) {
                    insResponseSt.setString(4, "MULTIPLE");
                    insResponseSt.executeUpdate();

                    // Insert into answers table
                    insAnswerSt.setString(1, answer.getAnswerId());
                    insAnswerSt.setString(2, answer.getResponseId());
                    insAnswerSt.executeUpdate();

                    // Insert into multiple_choice_answers table
                    insMcAnsSt.setString(1, answer.getAnswerId());
                    insMcAnsSt.setInt(2, ((MultipleChoiceAnswer) answer).getChoices().get(0));
                    insMcAnsSt.executeUpdate();

                } else if (answer instanceof SingleChoiceAnswer) {
                    insResponseSt.setString(4, "SINGLE");
                    insResponseSt.executeUpdate();

                    // Insert into answers table
                    insAnswerSt.setString(1, answer.getAnswerId());
                    insAnswerSt.setString(2, answer.getResponseId());
                    insAnswerSt.executeUpdate();

                    // Insert into single_choice_answers table
                    insScAnsSt.setString(1, answer.getAnswerId());
                    insScAnsSt.setInt(2, ((SingleChoiceAnswer) answer).getChoice());
                    insScAnsSt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Get Response
    public Response getResponse(String responseId) {
        String queryResponse = "SELECT * FROM responses WHERE response_id=?";

        try (Connection con = DriverManager.getConnection("","","");) {
            // Try to get the response from text responses table
            try (PreparedStatement pst = con.prepareStatement(queryResponse)) {
                pst.setString(1, responseId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        Response response= new Response();
                        response.setResponseId(rs.getString("response_id"));
                        response.setPollId(rs.getString("poll_id"));
                        response.setRespondingUser(rs.getString("responding_user"));
                        //TODO: need to get the answers (response.setAnswers should give list)
                        return response;
                    }
                }
            }
            // Try to get the response from multiple choice responses tab
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        // Try to get the response from single choice responses table

        return null;
    }

    // Delete Response
    public void deleteResponse(String responseId) {
        String delAnsquery = "DELETE from answers where response_id=?";
        String delResquery = "DELETE from responses where response_id=?";
        try (Connection con = DriverManager.getConnection("","","");
             PreparedStatement delAnsSt = con.prepareStatement(delAnsquery);
             PreparedStatement delResSt = con.prepareStatement(delResquery)) {

            // Delete answers associated with the response
            delAnsSt.setString(1, responseId);
            delAnsSt.executeUpdate();

            // Delete the response
            delResSt.setString(1, responseId);
            delResSt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting response", e);
        }
    }

}

