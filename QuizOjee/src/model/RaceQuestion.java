package model;

import java.io.Serializable;

public class RaceQuestion implements Serializable{

	private long questionId;
	private String question;
	private double rightAnswer;
	private int topicId;
	private String author;
	
	public long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public double getRightAnswer() {
		return rightAnswer;
	}
	public void setRightAnswer(double rightAnswer) {
		this.rightAnswer = rightAnswer;
	}
	public int getTopicId() {
		return topicId;
	}
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public String toString() {
		return "[(RaceQuestion"+questionId+") "+question+"]";
	}
	
	
}
