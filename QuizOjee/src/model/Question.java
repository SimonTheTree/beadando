package model;

/** 
 *  Egy feleletvalasztos kerdes tarolasara valo. <br>
 *  Adattagjai: <br>
 *   - questionId - int<br>
 *   - question - String<br>
 *   - rightAnswer - String<br>
 *   - answer1 - String<br>
 *   - answer2 - String<br>
 *   - answer3 - String<br>
 *   - topicId - int<br>
 *   - difficulty - int<br>
 *   - userName - String<br>
 */
public class Question {

	private long questionId;
	private String question;
	private String rightAnswer;
	private String answer1;
	private String answer2;
	private String answer3;
	private int topicId;
	private int difficulty;
	private String userName;
	
	
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String toString() {
		return "[(Question"+questionId+") "+question+"]";
	}
	
}
