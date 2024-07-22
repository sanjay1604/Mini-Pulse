package com.minipulse.db;

import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.*;
import com.minipulse.model.response.Response;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniPulseDBImpl implements MiniPulseDB {
    private static final Logger LOGGER = Logger.getLogger(MiniPulseDBImpl.class.getName());

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
            LOGGER.log(Level.SEVERE, null, e);
        }
        return null;
    }

    public void savePollAndOverwriteQuestions(Poll poll) {
        String deleteQuestionsQuery = "DELETE FROM questions WHERE poll_id=?";
        String upsertPollQuery = "MERGE INTO polls USING (VALUES (?, ?, ?, ?, ?)) AS vals(poll_id, user, title, state, description) " +
                "ON polls.poll_id=vals.poll_id " +
                "WHEN MATCHED THEN UPDATE SET user=vals.user, title=vals.title, state=vals.state, description=vals.description " +
                "WHEN NOT MATCHED THEN INSERT (poll_id, user, title, state, description) VALUES (vals.poll_id, vals.user, vals.title, vals.state, vals.description)";
        String insertQuestionQuery = "INSERT INTO questions(poll_id, question_id, question_text, question_desc, is_mandatory, type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement delQuesSt = con.prepareStatement(deleteQuestionsQuery);
             PreparedStatement upsertPollSt = con.prepareStatement(upsertPollQuery);
             PreparedStatement insQuesSt = con.prepareStatement(insertQuestionQuery)) {

            // Delete existing questions for the poll
            delQuesSt.setString(1, poll.getPollId());
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
                insQuesSt.setString(1, poll.getPollId());
                insQuesSt.setString(2, question.getQuestionId());
                insQuesSt.setString(3, question.getQuestionTitle());
                insQuesSt.setString(4, question.getQuestionDescription());
                insQuesSt.setBoolean(5, question.isMandatory());
                insQuesSt.setString(6, question.getType());
                insQuesSt.executeUpdate();

                // Insert choices for multiple and single choice questions
                if (question instanceof MultipleChoiceQuestion) {
                    saveChoices(con, question.getQuestionId(), ((MultipleChoiceQuestion) question).getChoices());
                } else if (question instanceof SingleChoiceQuestion) {
                    saveChoices(con, question.getQuestionId(), ((SingleChoiceQuestion) question).getChoices());
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

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

    public void modifyPollState(String pollId, PollState state) {
        String query = "UPDATE polls SET state=? WHERE poll_id=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, state.name());
            pst.setString(2, pollId);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

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
            LOGGER.log(Level.SEVERE, null, e);
        }
        return null;
    }

    public void deletePollEntirely(String pollId) {
        String deleteQuestionsQuery = "DELETE FROM questions WHERE poll_id=?";
        String deletePollQuery = "DELETE FROM polls WHERE poll_id=?";
        try (Connection con = getConnection();
             PreparedStatement delQuesSt = con.prepareStatement(deleteQuestionsQuery);
             PreparedStatement delPollSt = con.prepareStatement(deletePollQuery)) {

            // Delete questions associated with the poll
            delQuesSt.setString(1, pollId);
            delQuesSt.executeUpdate();

            // Delete the poll
            delPollSt.setString(1, pollId);
            delPollSt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public Poll getPollEntirely(String pollId) {
        Poll poll = getPoll(pollId);
        if (poll != null) {
            String questionsQuery = "SELECT * FROM questions WHERE poll_id=?";
            try (Connection con = getConnection();
                 PreparedStatement pst = con.prepareStatement(questionsQuery)) {
                pst.setString(1, pollId);
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
                LOGGER.log(Level.SEVERE, null, e);
            }
        }
        return poll;
    }

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

    public void saveResponse(Response response) {
        String insertResponseQuery = "INSERT INTO responses(response_id, poll_id, answer) VALUES (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement insResp = con.prepareStatement(insertResponseQuery)) {

            insResp.setString(1, response.getResponseId());
            insResp.setString(2, response.getPollId());
            insResp.setString(4, response.getAnswer());
            insResp.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public Response getResponse(String responseId) {
        String query = "SELECT * FROM responses WHERE response_id=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, responseId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Response(
                            rs.getString("response_id"),
                            rs.getString("poll_id"),
                            rs.getString("answer")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        return null;
    }

    public void deleteResponse(String responseId) {
        String query = "DELETE FROM responses WHERE response_id=?";
        try (Connection con = getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, responseId);
            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    private Connection getConnection() throws SQLException {
        // Replace with your database connection logic
        return DriverManager.getConnection("jdbc:derby://localhost:1527/MiniPulse", "sanjay", "sruthi");
    }
}
