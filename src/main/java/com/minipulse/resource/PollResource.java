package com.minipulse.resource;

import com.minipulse.db.DBFactory;
import com.minipulse.db.MiniPulseDB;
import com.minipulse.exception.MiniPulseBadArgumentException;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.poll.PollState;
import com.minipulse.model.question.Question;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("poll")
public class PollResource  {

    private MiniPulseDB db;

    public PollResource() {
        setDb(DBFactory.getDB());
    }

    public MiniPulseDB getDb() {
        return db;
    }

    public void setDb(MiniPulseDB db) {
        this.db = db;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPoll(Poll poll)  {
        try {
            if (poll.getPollTitle() == null || poll.getPollTitle().isEmpty()) {
                throw new MiniPulseBadArgumentException("Poll Title is missing");
            }

            if (poll.getQuestions() == null || poll.getQuestions().isEmpty()) {
                throw new MiniPulseBadArgumentException("Poll doesn't contain any questions");
            }

            if (poll.getOwner() == null || poll.getOwner().isEmpty()) {
                throw new MiniPulseBadArgumentException("Poll requires an owner");
            }

            String userName = db.getUser(poll.getOwner());
            if (userName == null) {
                throw new MiniPulseBadArgumentException("User doesn't exist");
            }

            String existingPollId = db.getPollIdForUserByTitle(poll.getOwner(), poll.getPollTitle());
            if (existingPollId != null) {
                throw new MiniPulseBadArgumentException("Poll with this Title already exist");
            }

            poll.setPollId(UUID.randomUUID().toString());
            poll.setState(PollState.NEW);

            for (int index = 0; index < poll.getQuestions().size(); index++) {
                Question question = poll.getQuestions().get(index);
                validateQuestion(question, index);
                question.setQuestionId(UUID.randomUUID().toString());
                question.setPollId(poll.getPollId());
            }

            db.savePollAndOverwriteQuestions(poll);

            return Response.ok(poll).build();
        } catch (MiniPulseBadArgumentException e) {
            return Response.status(400, e.getMessage()).build();
        }
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePoll(Poll poll) {
        try {
            if (poll.getPollId() == null || poll.getPollId().isEmpty()){
                throw new MiniPulseBadArgumentException("Poll does not exist");
            }

            if (poll.getState() != PollState.NEW ){
                throw new MiniPulseBadArgumentException("Poll not in new state");
            }

            if (poll.getOwner() == null || poll.getOwner().isEmpty()){
                throw new MiniPulseBadArgumentException("Poll requires an owner");
            }

            //in update poll owner can't be changed
            String userName = db.getUser(poll.getOwner());
            if (userName == null) {
                throw new MiniPulseBadArgumentException("User doesn't exist");
            }

            if (poll.getPollTitle() == null || poll.getPollTitle().isEmpty()){
                throw new MiniPulseBadArgumentException("Poll does not have a title");
            }

            if (poll.getQuestions() == null || poll.getQuestions().isEmpty()){
                throw new MiniPulseBadArgumentException("poll doesn't contain any questions");
            }

            String existingPollId = db.getPollIdForUserByTitle(poll.getOwner(), poll.getPollTitle());
            if (existingPollId != null && !poll.getPollId().equals(existingPollId)){
                throw new MiniPulseBadArgumentException("cant change title to existing title");
            }


            for (int index = 0; index < poll.getQuestions().size(); index++) {
                Question question = poll.getQuestions().get(index);
                validateQuestion(question, index);
                question.setQuestionId(UUID.randomUUID().toString());
                question.setPollId(poll.getPollId());
            }
            db.savePollAndOverwriteQuestions(poll);
            return Response.ok(poll).build();
        } catch (MiniPulseBadArgumentException e) {
            return Response.status(400, e.getMessage()).build();
        }
    }

    @GET
    @Path("byUser/{user}")
    public Response getAllPollsForUserWithoutResponses(@PathParam("user") String userName) {
        return Response.ok(db.getPollsByUser(userName)).build();
    }

    @GET
    @Path("accepting/{user}")
    public Response getAcceptingPollsWithoutResponses(@PathParam("user") String userName) {
        return Response.ok(db.getAcceptingPolls(userName)).build();
    }

    @POST
    @Path("flight/{pollId}")
    public Response flightPoll(@PathParam("pollId") String pollId)  {
        try {
            if(pollId == null || pollId.isEmpty()){
                throw new MiniPulseBadArgumentException("Poll does not exist");
            }

            Poll poll = getPollWithoutResponses(pollId);
            if (poll == null) {
                throw new MiniPulseBadArgumentException("Not a valid poll");
            }

            if (poll.getState() != PollState.NEW ){
                throw new MiniPulseBadArgumentException("Poll not in new state");
            }

            poll.setState(PollState.ACCEPTING);

            db.modifyPollState(pollId, PollState.ACCEPTING);
            return Response.ok().build();
        } catch (MiniPulseBadArgumentException e) {
            return Response.status(400, e.getMessage()).build();
        }
    }

    @GET
    @Path("withoutResponses/{pollId}")
    public Poll getPollWithoutResponses(@PathParam("pollId") String pollId) throws MiniPulseBadArgumentException {
        if(pollId == null || pollId.isEmpty()){
            throw new MiniPulseBadArgumentException("Poll does not exist");
        }
        return db.getPoll(pollId);
    }

    @DELETE
    @Path("{pollId}")
    public Response deletePoll(@PathParam("pollId") String pollId) {
        try {
            if(pollId == null || pollId.isEmpty()){
                throw new MiniPulseBadArgumentException("Poll does not exist");
            }

            Poll poll = getPollWithoutResponses(pollId);

            if(poll.getState() == PollState.ACCEPTING){
                throw new MiniPulseBadArgumentException("Poll should either be in new or closed state");
            }

            db.deletePollEntirely(pollId);
            return Response.ok().build();
        } catch (MiniPulseBadArgumentException e) {
            return Response.status(400, e.getMessage()).build();
        }
    }

    @POST
    @Path("close/{pollId}")
    public Response closePoll(@PathParam("pollId") String pollId){
        try {
            if(pollId == null || pollId.isEmpty()){
                throw new MiniPulseBadArgumentException("poll does not exist");
            }

            Poll poll = getPollWithoutResponses(pollId);
            if(poll.getState() != PollState.ACCEPTING){
                throw new MiniPulseBadArgumentException("Poll should be in accepting state");
            }

            poll.setState(PollState.CLOSED);
            db.modifyPollState(pollId, PollState.CLOSED);
            return Response.ok().build();
        } catch (MiniPulseBadArgumentException e) {
            return Response.status(400, e.getMessage()).build();
        }
    }

    @GET
    @Path("withResponses/{pollId}")
    public Response getFullPollWithResponses(@PathParam("pollId") String pollId) {
        try {
            if(pollId == null || pollId.isEmpty()){
                throw new MiniPulseBadArgumentException("Poll does not exist");
            }
            Poll poll = db.getPollEntirely(pollId);
            if(poll.getState() != PollState.CLOSED){
                throw new MiniPulseBadArgumentException("Poll should be in accepting state");
            }
            return Response.ok(poll).build();
        } catch (MiniPulseBadArgumentException e) {
            return Response.status(400, e.getMessage()).build();
        }
    }

    private void validateQuestion(Question question, int position) throws MiniPulseBadArgumentException {
        if (question.getQuestionTitle() == null || question.getQuestionTitle().isEmpty()) {
            throw new MiniPulseBadArgumentException("Question title is empty for question #" + position);
        }
    }
}
