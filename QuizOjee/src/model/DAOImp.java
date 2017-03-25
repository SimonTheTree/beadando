package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DAOImp implements DAO {

	private static final String DATABASE_LINK = "localhost:1521:kabinet";
	private static final String SQL_GET_QUESTION = 
			"SELECT * FROM NORMAL_QUESTIONS "
			+ "WHERE difficulty = ? and topic_id = ?";
	private static final String SQL_MAX_DIFFICULTY = "SELECT MAX(difficulty) FROM NORMAL_QUESTIONS";//tudom hogy ronda, de nem ment preparedStatement-tel
	private static final String SQL_MAX_TOPIC_ID = "SELECT MAX(topic_id) FROM NORMAL_QUESTIONS";
	
	public DAOImp() {
		  /*SSHSocketFactory fact = new SSHSocketFactory(sshHost, sshPort, new SSHPasswordAuthenticator(sshUser, sshPassword));

		  sock = fact.createSocket(host, port);*/
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<Question> getQuestions(int diff, int topic) {
		
		List<Question> questions = new ArrayList<Question>();
		
		System.out.print("Load questions from db: ");
		
		try(
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@"+DATABASE_LINK,"h664800","jelszo");
				PreparedStatement pst = conn.prepareStatement(SQL_GET_QUESTION);
			) {
			
			/*
			Session session=jsch.getSession(user, host, 22);
			session.setPassword();
			session.setPortForwardingL(String bind_address,
			                              int lport,
			                              String host,
			                              int rport,
			                              ServerSocketFactory ssf,
			                              int connectTimeout);
			session.isConnected();*/
			
			int index = 1;
			pst.setInt(index++, diff);
			pst.setInt(index++, topic);
			
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				Question q = new Question();
				q.setQuestionId(rs.getInt("question_id"));
				q.setQuestion(rs.getString("question"));
				q.setRightAnswer(rs.getString("right_answer"));
				q.setAnswer1(rs.getString("answer1"));
				q.setAnswer2(rs.getString("answer2"));
				q.setTopicId(rs.getInt("topic_id"));
				q.setDifficulty(rs.getInt("difficulty"));
				q.setUserName(rs.getString("user_name"));
				questions.add(q);
			}
			
			//Ha nincs kerdes ebben a kategoriaban, az hibas parameternek szamit
			if(questions.size() == 0) {
				return null;
			}
			
			System.out.println(questions.size()+" questions are loaded.");
			
			return questions;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getMax(String column) {
		
		try(
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@"+DATABASE_LINK,"h664800","jelszo");
				Statement pst = conn.createStatement();
			) {
			
			ResultSet rs;
			if(column.equals("difficulty")) {
				rs = pst.executeQuery(SQL_MAX_DIFFICULTY);
			} else if(column.equals("topic_id")) {
				rs = pst.executeQuery(SQL_MAX_TOPIC_ID);
			} else {
				return 0;
			}
			
			while(rs.next()) {
				return rs.getInt(1);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
