package com.minipulse.model.answer;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class MultipleChoiceAnswer extends Answer {
        private List<Integer> choices = new ArrayList<>();
        public MultipleChoiceAnswer() {
                setType("MULTIPLE");
        }

        public List<Integer> getChoices() {
                return choices;
        }

        public void setChoices(List<Integer> choices) {
                this.choices = choices;
        }

        @Override
        public Answer clone() {
                MultipleChoiceAnswer cloneAnswer = new MultipleChoiceAnswer();
                cloneAnswer.setQuestionId(getQuestionId());
                cloneAnswer.setAnswerId(getAnswerId());
                cloneAnswer.setPollId((getPollId()));
                cloneAnswer.setChoices(new ArrayList<>(getChoices()));
                return cloneAnswer;
        }
}
