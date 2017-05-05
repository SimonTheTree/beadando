package model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

public class ForumEntry {

	private int commentId;
	private String text;
	private Timestamp date;
	private String author;
	private int topicId;
	private int refComment = -1;
	
	public ForumEntry() {
		refComment = -1;
	}
	
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date2) {
		this.date = date2;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getTopicId() {
		return topicId;
	}
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	public int getRefComment() {
		return refComment;
	}
	public void setRefComment(int refComment) {
		this.refComment = refComment;
	}
	public void setCurrentTime() {
		this.date = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	public String toString() {
		return "[(ForumEntry"+commentId+") "+text+" "+author+" "+date+" "+topicId+" "+refComment+"]";
	}
	
}
