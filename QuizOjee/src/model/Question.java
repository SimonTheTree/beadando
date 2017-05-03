package model;

import java.io.Serializable;

/** 
 * <b>Question</b><br>
 *  Egy feleletvalasztos kerdes tarolasara valo. <p>
 *  
 *  <b>Adattagjai:</b><br>
 *   - questionId - int<br>
 *   - question - String<br>
 *   - rightAnswer - String<br>
 *   - answer1 - String<br>
 *   - answer2 - String<br>
 *   - answer3 - String<br>
 *   - topicId - int<br>
 *   - difficulty - int<br>
 *   - author - String<br>
 */
public class Question implements Serializable{

	private long questionId;
	private String question;
	private String rightAnswer;
	private String answer1;
	private String answer2;
	private String answer3;
	private int topicId;
	private String topicName;
	private int difficulty;
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
	public String getRightAnswer() {
		return rightAnswer;
	}
	public void setRightAnswer(String rightAnswer) {
		this.rightAnswer = rightAnswer;
	}
	public String getAnswer1() {
		return answer1;
	}
	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	public String getAnswer2() {
		return answer2;
	}
	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
	public String getAnswer3() {
		return answer3;
	}
	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}
	public int getTopicId() {
		return topicId;
	}
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String toString() {
		return "[(Question"+questionId+") "+question+"]";
	}
	
}
