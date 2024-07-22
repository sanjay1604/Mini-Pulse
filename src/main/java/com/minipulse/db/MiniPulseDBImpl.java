package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.*;
import com.minipulse.model.response.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniPulseDBImpl implements MiniPulseDB {
    private static final Logger LOGGER = Logger.getLogger(MiniPulseDBImpl.class.getName());

    // Retrieve Poll ID based on user and poll title
    public String getPollIdForUserByTitle(String user, String pollTitle) {
        String query = "SELECT poll_id FROM polls WHERE user=? AND title=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, user);
            pst.setString(2, pollTitle);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("poll_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving poll ID", e);
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

        try (Connection con = getConnection();
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
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, state.name());
            pst.setString(2, pollId);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error modifying poll state", e);
        }
    }

    // Get Poll
    public Poll getPoll(String pollId) {
        String query = "SELECT * FROM polls WHERE poll_id=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, pollId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Poll(
                            rs.getString("poll_id"),
                            rs.getString("user"),
                            rs.getString("title"),
                            PollState.valueOf(rs.getString("state")),
                            rs.getString("description")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving poll", e);
        }
        return null;
    }

    // Delete Poll Entirely
    public void deletePollEntirely(String pollId) {
        String deleteQuestionsQuery = "DELETE FROM text_questions WHERE poll_id=?;" +
                                      "DELETE FROM multiple_choice_questions WHERE poll_id=?;" +
                                      "DELETE FROM single_choice_questions WHERE poll_id=?;" +
                                      "DELETE FROM choices WHERE question_id IN (SELECT question_id FROM text_questions WHERE poll_id=? " +
                                      "UNION SELECT question_id FROM multiple_choice_questions WHERE poll_id=? " +
                                      "UNION SELECT question_id FROM single_choice_questions WHERE poll_id=?)";
        String deletePollQuery = "DELETE FROM polls WHERE poll_id=?";
        try (Connection con = getConnection();
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
            LOGGER.log(Level.SEVERE, "Error deleting poll entirely", e);
        }
    }

    // Get Poll Entirely
    public Poll getPollEntirely(String pollId) {
        Poll poll = getPoll(pollId);
        if (poll != null) {
            String questionsQuery = "SELECT * FROM text_questions WHERE poll_id=? " +
                                    "UNION ALL SELECT * FROM multiple_choice_questions WHERE poll_id=? " +
                                    "UNION ALL SELECT * FROM single_choice_questions WHERE poll_id=?";
            try (Connection con = getConnection();
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
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error retrieving poll entirely", e);
            }
        }
        return poll;
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
        String insTextResponseQuery = "INSERT INTO text_responses(response_id, poll_id, question_id, answer) VALUES (?, ?, ?, ?)";
        String insMultipleChoiceResponseQuery = "INSERT INTO multiple_choice_responses(response_id, poll_id, question_id, choice_id) VALUES (?, ?, ?, ?)";
        String insSingleChoiceResponseQuery = "INSERT INTO single_choice_responses(response_id, poll_id, question_id, choice_id) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement insTextRespSt = con.prepareStatement(insTextResponseQuery);
             PreparedStatement insMCRespSt = con.prepareStatement(insMultipleChoiceResponseQuery);
             PreparedStatement insSCRespSt = con.prepareStatement(insSingleChoiceResponseQuery)) {

            // Determine the type of response and insert into the appropriate table
            if (response instanceof TextResponse) {
                TextResponse textResponse = (TextResponse) response;
                insTextRespSt.setString(1, textResponse.getResponseId());
                insTextRespSt.setString(2, textResponse.getPollId());
                insTextRespSt.setString(3, textResponse.getQuestionId());
                insTextRespSt.setString(4, textResponse.getAnswer());
                insTextRespSt.executeUpdate();
            } else if (response instanceof MultipleChoiceResponse) {
                MultipleChoiceResponse mcResponse = (MultipleChoiceResponse) response;
                insMCRespSt.setString(1, mcResponse.getResponseId());
                insMCRespSt.setString(2, mcResponse.getPollId());
                insMCRespSt.setString(3, mcResponse.getQuestionId());
                insMCRespSt.setInt(4, mcResponse.getChoiceId());
                insMCRespSt.executeUpdate();
            } else if (response instanceof SingleChoiceResponse) {
                SingleChoiceResponse scResponse = (SingleChoiceResponse) response;
                insSCRespSt.setString(1, scResponse.getResponseId());
                insSCRespSt.setString(2, scResponse.getPollId());
                insSCRespSt.setString(3, scResponse.getQuestionId());
                insSCRespSt.setInt(4, scResponse.getChoiceId());
                insSCRespSt.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving response", e);
        }
    }

    // Get Response
    public Response getResponse(String responseId) {
        String queryTextResponse = "SELECT * FROM text_responses WHERE response_id=?";
        String queryMCResponse = "SELECT * FROM multiple_choice_responses WHERE response_id=?";
        String querySCResponse = "SELECT * FROM single_choice_responses WHERE response_id=?";

        try (Connection con = getConnection()) {
            // Try to get the response from text responses table
            try (PreparedStatement pst = con.prepareStatement(queryTextResponse)) {
                pst.setString(1, responseId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return new TextResponse(
                                rs.getString("response_id"),
                                rs.getString("poll_id"),
                                rs.getString("question_id"),
                                rs.getString("answer")
                        );
                    }
                }
            }

            // Try to get the response from multiple choice responses table
            try (PreparedStatement pst = con.prepareStatement(queryMCResponse)) {
                pst.setString(1, responseId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return new MultipleChoiceResponse(
                                rs.getString("response_id"),
                                rs.getString("poll_id"),
                                rs.getString("question_id"),
                                rs.getInt("choice_id")
                        );
                    }
                }
            }

            // Try to get the response from single choice responses table
            try (PreparedStatement pst = con.prepareStatement(querySCResponse)) {
                pst.setString(1, responseId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return new SingleChoiceResponse(
                                rs.getString("response_id"),
                                rs.getString("poll_id"),
                                rs.getString("question_id"),
                                rs.getInt("choice_id")
                        );
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving response", e);
        }
        return null;
    }

    // Delete Response
    public void deleteResponse(String responseId) {
        String delTextResponseQuery = "DELETE FROM text_responses WHERE response_id=?";
        String delMCResponseQuery = "DELETE FROM multiple_choice_responses WHERE response_id=?";
        String delSCResponseQuery = "DELETE FROM single_choice_responses WHERE response_id=?";

        try (Connection con = getConnection();
             PreparedStatement delTextRespSt = con.prepareStatement(delTextResponseQuery);
             PreparedStatement delMCRespSt = con.prepareStatement(delMCResponseQuery);
             PreparedStatement delSCRespSt = con.prepareStatement(delSCResponseQuery)) {

            // Try to delete the response from text responses table
            delTextRespSt.setString(1, responseId);
            int rowsAffected = delTextRespSt.executeUpdate();
            if (rowsAffected > 0) return;

            // Try to delete the response from multiple choice responses table
            delMCRespSt.setString(1, responseId);
            rowsAffected = delMCRespSt.executeUpdate();
            if (rowsAffected > 0) return;

            // Try to delete the response from single choice responses table
            delSCRespSt.setString(1, responseId);
            delSCRespSt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting response", e);
        }
    }

    // Establish Database Connection
    private Connection getConnection() throws SQLException {
        // Update with your database URL, username, and password
        return DriverManager.getConnection("jdbc:your_database_url", "your_username", "your_password");
    }
}
