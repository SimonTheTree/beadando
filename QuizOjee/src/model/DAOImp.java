package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.jcraft.jsch.*;

import model.exceptions.UserAlreadyExistsException;
import model.exceptions.UserNotFoundException;
// így oldottam meg, hogy ne legyen benne direkt a kódban a jelszavam, nem szeretném közzétenni...
// kérlek ne törd fel :D
import ssh.SshCredentials; 

public class DAOImp implements DAO {

	private static final String DATABASE_LINK = "localhost:1521:kabinet";
	private static final String SQL_GET_QUESTION_BY_DIFFICULTY = "SELECT * FROM NORMAL_QUESTIONS "
			+ "WHERE difficulty BETWEEN ? and ?";
	
	
	private static final String SQL_CHECK_USER = "SELECT uname, password FROM USERS "
			+ "WHERE uname = ? and password = ?";
	private static final String SQL_GET_USER = "SELECT uname, password, age, real_name FROM USERS "
			+ "WHERE uname = ?";
    private static final String SQL_ADD_USER =
            "INSERT INTO USERS " +
            "(uname, password, real_name, age, wins, defeats, points, right_answers, wrong_answers, right_tips, wrong_tips) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_MODIFY_USER = 
			"UPDATE USERS " +
			"SET password = ?, real_name = ?, age = ? " +
			"WHERE uname = ?";
	private static final String SQL_DEL_USER = "DELETE FROM USERS "
			+ "WHERE uname = ?";

	private static final String SQL_GET_AGE_STATISTICS = "SELECT uname, points, wins, defeats, right_answers, wrong_answers, right_tips, wrong_tips FROM USERS "
			+ "WHERE age BETWEEN ? and ?";
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
	        String user="h670486";
	        String password=""; //ezt a jelszó dolgot majd megoldom, de nem ma, már késő van...
	        String tunnelRemoteHost="orania.inf.u-szeged.hu"; // The host of the second target
	        int port=22;
	
	
	        JSch jsch=new JSch();
	        Session session=jsch.getSession(SshCredentials.getUser(), host, port);
	        session.setPassword(SshCredentials.getPassword());
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
	
	public List<Question> getQuestions(int minDiff, int maxDiff, List<Integer> topicList) {

		System.out.print("Load questions from db: ");

		Session session = openSSHTunnel();
		if (session == null)
			return null;
		
		String epicSQL = SQL_GET_QUESTION_BY_DIFFICULTY;
		
		if(topicList != null) { 
			epicSQL+=sqlListAdder("AND","Topic_ID",topicList.size(),"OR");
		}

		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(epicSQL);
		) {

			int index = 1;
			pst.setInt(index++, minDiff);
			pst.setInt(index++, maxDiff);
			if(topicList!=null) {
				for(int i=0;i<topicList.size();++i) {
					pst.setInt(index++, topicList.get(i));
				}
			}
			
			List<Question> questions = new ArrayList<Question>();
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
				q.setUserName(rs.getString("author"));
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
		return null;
	}

	/** a WHERE fetletelbe teheted bele.<p>
	 * <b>Pelda:</b> sqlListAdder("AND","TOPIC_ID",[egy 2 elemu lista],"OR");<br>
	 * <b>ezt adja vissza:</b> " AND ( TOPIC_ID = ? OR TOPIC_ID = ? )"
	 * 
	 * @param startOperator : ha a lista nem null vagy ures ezt irja az elejere. (pl AND, OR)
	 * @param dataName : pl UNAME, vagy TOPIC_ID stb
	 * @param listSize : a lista elemeinek szama, ennyi ?-et tesz bele.
	 * @param betweenOperator : A listaelemek osszehasonlitasai koze irja be (pl AND, OR)
	 */
	private static String sqlListAdder(String startOperator, String dataName, int listSize, String betweenOperator) {
		if(listSize == 0) return "";
		String re = " "+startOperator+" (";
		for(int i=0;i<listSize;++i) {
			re+= " "+dataName+" = ?";
			if(i != listSize-1) re+=" "+betweenOperator;
		}
		re+= " )";
		return re;
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

	public boolean checkUser(String uname, String pw) {
		
		System.out.println("check if the User is legit");
		
		Session session = openSSHTunnel();
		if (session == null) return false;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL_CHECK_USER);
		) {

			int index = 1;
			pst.setString(index++, uname);
			pst.setString(index++, pw);

			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				return true;
			}
			return false;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return false;
	}

	public boolean addUser(User user) throws UserAlreadyExistsException {
		System.out.println("add User " + user.getUsername());
		
		Session session = openSSHTunnel();
		if (session == null) return false;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL_ADD_USER);
		) {
			if(getUser(user.getUsername(),conn) != null) throw new UserAlreadyExistsException();
//"(uname, password, real_name, age, wins, defeats, points, right_answers, wrong_answers, right_tips, wrong_tips) " +
			int index = 1;
			pst.setString(index++,user.getUsername());
			pst.setString(index++,user.getPassword());
			pst.setString(index++,user.getRealName());
			pst.setInt(index++,user.getAge());
			pst.setInt(index++, 0);
			pst.setInt(index++, 0);
			pst.setInt(index++, 0);
			pst.setInt(index++, 0);
			pst.setInt(index++, 0);
			pst.setInt(index++, 0);
			pst.setInt(index++, 0);
			int rowsAffected = pst.executeUpdate();
			if(rowsAffected > 0) {
				return true;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return false;
	}
	
	public boolean modifyUser(User user) throws UserNotFoundException {
		
		System.out.println("modify User " + user.getUsername());
		
		Session session = openSSHTunnel();
		if (session == null) return false;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL_MODIFY_USER);
		) {
			User old = getUser(user.getUsername(),conn); 
			if(old == null) throw new UserNotFoundException();
			if(old.equals(user)) {
				return true;
			}
			//"SET password = ?, real_name = ?, age = ? " +
			//"WHERE uname = ?";
			int index = 1;
			pst.setString(index++,user.getPassword()==null?old.getPassword():user.getPassword());
			pst.setString(index++,user.getRealName()==null?old.getRealName():user.getRealName());
			pst.setInt(index++,user.getAge()==0?old.getAge():user.getAge());
			pst.setString(index++,user.getUsername());
			int rowsAffected = pst.executeUpdate();
			if(rowsAffected > 0) {
				return true;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return false;
	}
	
	/** visszaadja az adott user-t. vagy null-t ha nincs */
	private User getUser(String uname, Connection conn) throws SQLException {
		try (
			PreparedStatement pst = conn.prepareStatement(SQL_GET_USER);
		) {
			pst.setString(1, uname);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				User re = new User();
				re.setUsername(uname);
				re.setCodedPassword(rs.getString("password"));
				re.setAge(rs.getInt("age"));
				re.setRealName(rs.getString("real_Name"));
				return re;
			}
			return null;
		}
	}
	
	public User getUser(String uname) {
		System.out.println("get User " + uname);
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
		) {
			return getUser(uname,conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}

	public boolean deleteUser(String uname) {
	System.out.println("get User " + uname);
		
		Session session = openSSHTunnel();
		if (session == null) return false;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			PreparedStatement pst = conn.prepareStatement(SQL_DEL_USER);
		) {
			pst.setString(1, uname);
			return pst.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return false;
	}

	public List<Statistics> getAgeStatistics(int ageMin, int ageMax) {
		System.out.println("getAgeStataistics " + ageMin+"-"+ageMax);
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			PreparedStatement pst = conn.prepareStatement(SQL_GET_AGE_STATISTICS);
		) {
			List<Statistics> stats = new ArrayList<Statistics>();
			int index = 1;
			pst.setInt(index++, ageMin);
			pst.setInt(index++, ageMax);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				Statistics stat = new Statistics();
				stat.setUname(rs.getString("uname"));
				stat.setPoints(rs.getInt("points"));
				stat.setWins(rs.getInt("wins"));
				stat.setDefeats(rs.getInt("defeats"));
				stat.setRightAnswers(rs.getInt("right_answers"));
				stat.setWrongAnswers(rs.getInt("wrong_answers"));
				stat.setRightTips(rs.getInt("right_tips"));
				stat.setWrongTips(rs.getInt("wrong_tips"));
				stats.add(stat);
			}
			return stats;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
}
