package com.minipulse.model.answer;

import java.util.List;

public class MultipleChoiceAnswer extends Answer {
        private List<Integer> choices;
        public MultipleChoiceAnswer() {
                setType("MULTIPLE");
        }

        public List<Integer> getChoices() {
                return choices;
        }

        public void setChoices(List<Integer> choices) {
                this.choices = choices;
        }
}
