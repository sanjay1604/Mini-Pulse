package com.minipulse.ui.poll;

import com.minipulse.exception.MiniPulseBadArgumentException;
import com.minipulse.model.poll.Poll;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import com.minipulse.model.question.SingleChoiceQuestion;
import com.minipulse.model.question.TextQuestion;
import com.minipulse.resource.PollResource;
import com.minipulse.ui.question.MultipleChoiceQuestionView;
import com.minipulse.ui.question.QuestionView;
import com.minipulse.ui.question.SingleChoiceQuestionView;
import com.minipulse.ui.question.TextQuestionView;
import com.minipulse.ui.user.UserScene;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PollView {
    private final Poll poll;
    private final GridPane gridPane;
    private int row = 1;

    private PollResource pollResource = new PollResource();
    private ContextMenu contextMenu;

    /** View elements that hold user data for poll */
    private TextField m_PollTitle;
    private TextField m_PollDescription;
    private final Set<QuestionView> questionViews = new HashSet<>();

    private final Stage stage;

    public PollView(Stage stage, Poll poll) {
        this.stage = stage;
        this.poll = poll;
        this.gridPane = new GridPane();
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(200), new ColumnConstraints(800));
    }
    public Pane render() {

        Label pollTitle = new Label("Title");
        pollTitle.setMinHeight(30);

        GridPane.setColumnIndex(pollTitle, 0);
        GridPane.setRowIndex(pollTitle, row);

        TextField pollTitleText = new TextField(poll.getPollTitle());
        m_PollTitle = pollTitleText;
        GridPane.setColumnIndex(pollTitleText, 1);
        GridPane.setRowIndex(pollTitleText, row);
        row++;

        Label pollDescription = new Label("Description");
        pollDescription.setMinHeight(30);
        GridPane.setColumnIndex(pollDescription, 0);
        GridPane.setRowIndex(pollDescription, row);
        TextField pollDescriptionText = new TextField(poll.getPollDescription());
        m_PollDescription = pollDescriptionText;
        GridPane.setColumnIndex(pollDescriptionText, 1);
        GridPane.setRowIndex(pollDescriptionText, row);
        row++;

        if (poll.getQuestions() != null && !poll.getQuestions().isEmpty()) {
            for (Question question : poll.getQuestions()) {
                renderQuestion(row++, question);
            }
        }

        HBox buttonBox = new HBox();

        Button addButton = new Button("Add");

        contextMenu = new ContextMenu();
        MenuItem textQuestion = new MenuItem("Add Text Question");
        textQuestion.setOnAction(event -> onAddQuestion("TEXT"));

        MenuItem multipleChoiceQuestion = new MenuItem("Add Multiple Choice Question");
        multipleChoiceQuestion.setOnAction(event -> onAddQuestion("MULTIPLE"));

        MenuItem singleChoiceQuestion = new MenuItem("Add Single Choice Question");
        singleChoiceQuestion.setOnAction(event -> onAddQuestion("SINGULAR"));

        MenuItem cancelMenu = new MenuItem("Cancel");
        cancelMenu.setOnAction(event -> contextMenu.hide());

        contextMenu.getItems().addAll(textQuestion, multipleChoiceQuestion, singleChoiceQuestion, cancelMenu);

        addButton.setMinHeight(30);
        addButton.setContextMenu(contextMenu);
        addButton.setOnAction(event -> {
            Point2D screenPosition = addButton.localToScreen(0, 0);
            contextMenu.show(addButton, screenPosition.getX(), screenPosition.getY());
        });

        Button saveButton = new Button("Save");
        saveButton.setDefaultButton(true);
        saveButton.setMinHeight(30);
        saveButton.setOnAction(event -> onSubmit());

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);
        cancelButton.setMinHeight(30);
        cancelButton.setOnAction(event -> onCancel());

        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(addButton, saveButton, cancelButton);

        GridPane.setColumnIndex(buttonBox, 1);
        GridPane.setRowIndex(buttonBox, 10000);

        gridPane.getChildren().addAll(pollTitle, pollTitleText, pollDescription, pollDescriptionText, buttonBox);
        return gridPane;
    }

    private void onCancel() {
        switchToUserView();
    }

    private void onSubmit() {
        update();
    }

    private void onAddQuestion(String type) {

        if (type.equals("TEXT")){
            Question question = new TextQuestion();
            renderQuestion(row, question);
        } else if(type.equals("MULTIPLE")){
            Question question = new MultipleChoiceQuestion();
            renderQuestion(row, question);
        } else if(type.equals("SINGULAR")){
            Question question = new SingleChoiceQuestion();
            renderQuestion(row, question);
        }

        row++;
    }
    private void renderQuestion(int row, Question question) {
        QuestionView questionView = null;

        if (question.getType().equals("TEXT")) {
            questionView = new TextQuestionView(gridPane, row, question);
        } else if (question.getType().equals("SINGULAR")) {
            questionView = new SingleChoiceQuestionView(gridPane, row, question);
        } else if (question.getType().equals("MULTIPLE")) {
            questionView = new MultipleChoiceQuestionView(gridPane, row, question);
        }
        questionView.render();
        questionViews.add(questionView);
    }
    public Poll update() {
        Poll pollToSave = new Poll();
        pollToSave.setOwner(poll.getOwner());
        pollToSave.setPollId(poll.getPollId());
        pollToSave.setState(poll.getState());
        pollToSave.setPollTitle(m_PollTitle.getText());
        pollToSave.setPollDescription(m_PollDescription.getText());
        List<Question> questions = new ArrayList<>();
        for (QuestionView questionView : questionViews) {
            if (questionView.isDeleted()) continue;
            questions.add(questionView.update());
        }
        pollToSave.setQuestions(questions);

        Poll updatedPoll = null;
        try {
            if (poll.getPollId() == null) {
                updatedPoll = pollResource.createPoll(pollToSave);
            } else {
                updatedPoll = pollResource.updatePoll(pollToSave);
            }
            switchToUserView();
        } catch (MiniPulseBadArgumentException e) {
            showError(e.getMessage());
        }
        return updatedPoll;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void switchToUserView() {
        Scene scene = UserScene.getScene(stage, poll.getOwner());
        stage.setScene(scene);
        stage.show();
    }
}
