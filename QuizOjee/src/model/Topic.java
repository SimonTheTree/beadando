package model;

import java.util.Map;

import view.Labels;

public class Topic {

	private int topicId;
	private String name;
	private Map<Integer,Integer> numberOfQuestionsByDifficulty;
	private int numberOfRaceQuestions;
	
	public int getTopicId() {
		return topicId;
	}
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<Integer, Integer> getNumberOfQuestionsByDifficulty() {
		return numberOfQuestionsByDifficulty;
	}
	public void setNumberOfQuestionsByDifficulty(Map<Integer, Integer> numberOfQuestionsByDifficulty) {
		this.numberOfQuestionsByDifficulty = numberOfQuestionsByDifficulty;
	}
	public int getNumberOfRaceQuestions() {
		return numberOfRaceQuestions;
	}
	public void setNumberOfRaceQuestions(int numberOfRaceQuestions) {
		this.numberOfRaceQuestions = numberOfRaceQuestions;
	}
	public int getNumberOfQuestions(int minDiff, int maxDiff) {
		int re = 0;
		for(int i : numberOfQuestionsByDifficulty.keySet()) {
			if(i >= minDiff && i <= maxDiff) re+=numberOfQuestionsByDifficulty.get(i); 
		}
		return re;
	}
	public String toString() {
		return "[(Topic"+topicId+") "+name+" "+numberOfRaceQuestions+" "+numberOfQuestionsByDifficulty+"]";
	}
	public String[] convertToStringArray() {
		if(numberOfQuestionsByDifficulty == null) {
			String[] re = {topicId+"", name}; 
			return re;
		}
		return null;
	}
	public String[] sequence() {
		if(numberOfQuestionsByDifficulty == null) {
			String[] re = {Labels.M_TOPIC_ID, Labels.M_TOPIC_NAME}; 
			return re;
		}
		return null;
	}
}
