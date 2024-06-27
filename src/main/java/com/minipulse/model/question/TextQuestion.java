package com.minipulse.model.question;

/**
 * This is the subclass TextQuestion. This class contains questions that accept response type as texts.
 *
 * When a new survey is created and a text-response question is needed, this class would be useful.
 */
public class TextQuestion extends Question {
     private int answerLength;

     public TextQuestion() {
          setType("TEXT");
     }

     /**
      * Gets the length of the answer.
      * @return answer's length.
      */
     public int getAnswerLength() {
          return answerLength;
     }

     /**
      * Sets the answer length.
      *
      */
     public void setAnswerLength(int answerLength) {
          this.answerLength = answerLength;
     }

}
