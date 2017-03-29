package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.jcraft.jsch.*;

public class DAOImp implements DAO {

	private static final String DATABASE_LINK = "localhost:1521:kabinet";
	private static final String SQL_GET_QUESTION = "SELECT * FROM NORMAL_QUESTIONS "
			+ "WHERE difficulty = ? and topic_id = ?";
	// tudom hogy ronda,de nem ment preparedStatement-tel:
	private static final String SQL_MAX_DIFFICULTY = "SELECT MAX(difficulty) FROM NORMAL_QUESTIONS";
	private static final String SQL_MAX_TOPIC_ID = "SELECT MAX(topic_id) FROM NORMAL_QUESTIONS";

	public DAOImp() {
		/*
		 * SSHSocketFactory fact = new SSHSocketFactory(sshHost, sshPort, new
		 * SSHPasswordAuthenticator(sshUser, sshPassword));
		 * 
		 * sock = fact.createSocket(host, port);
		 */

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}		

	}

	/**
	 * 
	 * @return te {@link Session} object with the succesfull connection or null if connection
	 * could not have been initialised.
	 * @throws JSchException
	 */
	Session openSSHTunnel(){
		try{
			String host="linux.inf.u-szeged.hu"; // First level target
	        String user="h664800";
	        String password=""; //ezt a jelszó dolgot majd megoldom, de nem ma, már késő van...
	        String tunnelRemoteHost="orania.inf.u-szeged.hu"; // The host of the second target
	        int port=22;
	
	
	        JSch jsch=new JSch();
	        Session session=jsch.getSession(user, host, port);
	        session.setPassword(password);
	        session.setConfig("StrictHostKeyChecking", "no");
	        // create port from 1521 on local system to port 1521 on tunnelRemoteHost
	        session.setPortForwardingL(1521, tunnelRemoteHost, 1521);
	        session.connect();
	        session.openChannel("direct-tcpip");
	        
	        if (session.isConnected()){
	        	return session;
	        } else {
	        	return null;
	        }
		} catch (JSchException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Question> getQuestions(int diff, int topic) {

		List<Question> questions = new ArrayList<Question>();

		System.out.print("Load questions from db: ");

		Session session = openSSHTunnel();
		if (session == null)
			return null;
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL_GET_QUESTION);
		) {

			int index = 1;
			pst.setInt(index++, diff);
			pst.setInt(index++, topic);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				Question q = new Question();
				q.setQuestionId(rs.getInt("question_id"));
				q.setQuestion(rs.getString("question"));
				q.setRightAnswer(rs.getString("right_answer"));
				q.setAnswer1(rs.getString("answer1"));
				q.setAnswer2(rs.getString("answer2"));
				q.setAnswer3(rs.getString("answer3"));
				q.setTopicId(rs.getInt("topic_id"));
				q.setDifficulty(rs.getInt("difficulty"));
				q.setUserName(rs.getString("user_name"));
				questions.add(q);
			}

			// Ha nincs kerdes ebben a kategoriaban, az hibas parameternek
			// szamit
			if (questions.size() == 0) {
				return null;
			}

			System.out.println(questions.size() + " questions are loaded.");

			return questions;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		
		session.disconnect();
		return null;
	}

	public int getMax(String column) {
		Session session = openSSHTunnel();
		try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
				Statement pst = conn.createStatement();) {

			ResultSet rs;
			if (column.equals("difficulty")) {
				rs = pst.executeQuery(SQL_MAX_DIFFICULTY);
			} else if (column.equals("topic_id")) {
				rs = pst.executeQuery(SQL_MAX_TOPIC_ID);
			} else {
				return 0;
			}

			while (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return 0;
	}

}
