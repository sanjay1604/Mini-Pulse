package com.minipulse.ui.report.answer;

import com.minipulse.model.answer.Answer;
import com.minipulse.model.answer.MultipleChoiceAnswer;
import com.minipulse.model.question.MultipleChoiceQuestion;
import com.minipulse.model.question.Question;
import com.minipulse.model.response.Response;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportMultipleChoiceAnswerView extends ReportAnswerView {

    public ReportMultipleChoiceAnswerView(VBox parentVBox, Question question, List<Response> responses) {
        super(parentVBox, question, responses);
    }

    @Override
    protected void localRender() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);
        barChart.setTitle(question.getQuestionTitle());
        xAxis.setLabel("Choices");
        yAxis.setLabel("No of responses");
        XYChart.Series<String, Integer> data = new XYChart.Series<>();

        Map<Integer, Integer> countsByChoice = new TreeMap<>();
        MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;

        for (Response response : responses) {
            for (Answer answer : response.getAnswers()) {
                if (answer.getQuestionId().equals(question.getQuestionId())) {
                    MultipleChoiceAnswer mcqAnswer = (MultipleChoiceAnswer) answer;
                    for (int choice : mcqAnswer.getChoices()) {
                        if (!countsByChoice.containsKey(choice)) {
                            countsByChoice.put(choice, 0);
                        }
                        countsByChoice.put(choice, countsByChoice.get(choice) + 1);
                        //countsByChoice.compute(choice, (k, v) ->  v == null ? 1 : v + 1);
                    }
                }
            }
        }

        for (int choice : mcq.getChoices().keySet()) {
            countsByChoice.putIfAbsent(choice, 0);
            data.getData().add(new XYChart.Data<>(mcq.getChoices().get(choice), countsByChoice.get(choice)));
        }
        this.thisQuestionVBox.getChildren().add(barChart);
    }
}
