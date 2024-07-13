package com.minipulse.ui.report.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.SingleChoiceAnswer;
import com.minipulse.model.question.Question;
import com.minipulse.model.question.SingleChoiceQuestion;
import com.minipulse.model.response.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportSingleChoiceAnswerView extends ReportAnswerView {

    public ReportSingleChoiceAnswerView(VBox parentVBox, Question question, List<Response> responses) {
        super(parentVBox, question, responses);
    }

    @Override
    protected void localRender() {

        Map<Integer, Integer> choiceData = new TreeMap<>();
        SingleChoiceQuestion scq = (SingleChoiceQuestion) question;

        for (Response response : responses) {
            for (Answer answer : response.getAnswers()) {
                if (answer.getQuestionId().equals(question.getQuestionId())) {
                    SingleChoiceAnswer scqAnswer = (SingleChoiceAnswer) answer;
                    if (!choiceData.containsKey(scqAnswer.getChoice())) {
                        choiceData.put(scqAnswer.getChoice(), 0);
                    }
                    choiceData.put(scqAnswer.getChoice(), choiceData.get(scqAnswer.getChoice()) + 1);
                }
            }
        }

        List<PieChart.Data> pieData = new ArrayList<>();
        for (Map.Entry<Integer, String> choiceInQuestion : scq.getChoices().entrySet()) {
            choiceData.putIfAbsent(choiceInQuestion.getKey(), 0);
            pieData.add(new PieChart.Data(choiceInQuestion.getValue(), choiceData.get(choiceInQuestion.getKey())));
        }
        ObservableList<PieChart.Data> data = FXCollections.observableList(pieData);
        PieChart pieChart = new PieChart(data);
        pieChart.setTitle(question.getQuestionTitle());
        this.thisQuestionVBox.getChildren().add(pieChart);
    }
}
