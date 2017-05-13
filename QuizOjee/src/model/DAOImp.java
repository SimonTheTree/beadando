package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jcraft.jsch.*;

import model.exceptions.UserAlreadyExistsException;
import model.exceptions.UserNotFoundException;
// igy oldottam meg, hogy ne legyen benne direkt a kodban a jelszavam, nem szeretnem kozzetenni...
// kerlek ne tord fel :D
import ssh.SshCredentials; 

public class DAOImp implements DAO {

	//Connection
	private static final String DATABASE_LINK = "localhost:1521:kabinet";

	//Question
	private static final String SQL_GET_QUESTION_BY_DIFFICULTY = "SELECT question_id, question, right_answer, answer1, answer2, answer3, topic_id, difficulty, author, dbms_random.value AS rand FROM NORMAL_QUESTIONS WHERE difficulty BETWEEN ? and ? ";
	private static final String SQL_GET_RACE_QUESTION = "SELECT question_id, question, right_answer, topic_id, author, dbms_random.value AS rand FROM RACE_QUESTIONS ";
	private static final String SQL_GET_QUESTIONS = "SELECT question, right_answer, answer1, answer2, answer3, name, difficulty, author FROM NORMAL_QUESTIONS, QUESTION_TOPICS WHERE NORMAL_QUESTIONS.topic_id = QUESTION_TOPICS.topic_id ORDER BY DIFFICULTY";
    private static final String SQL_ADD_QUESTION = "INSERT INTO NORMAL_QUESTIONS (question_id, question, right_answer, answer1, answer2, answer3, difficulty, topic_id, author) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	//User
	private static final String SQL_CHECK_USER = "SELECT uname, password FROM USERS WHERE uname = ? and password = ?";
	private static final String SQL_GET_USER = "SELECT uname, password, age, real_name FROM USERS WHERE uname = ?";
    private static final String SQL_ADD_USER = "INSERT INTO USERS (uname, password, real_name, age, wins, defeats, points, right_answers, wrong_answers, right_tips, wrong_tips) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String SQL_MODIFY_USER = "UPDATE USERS SET password = ?, real_name = ?, age = ? WHERE uname = ?";
	private static final String SQL_DEL_USER = "DELETE FROM USERS WHERE uname = ?";

	//Statistics
	private static final String SQL_GET_USER_STATISTICS = "SELECT uname, age, points, wins, defeats, right_answers, wrong_answers, right_tips, wrong_tips FROM USERS WHERE uname = ?";
	private static final String SQL_UPDATE_STATISTICS = "UPDATE USERS SET age = ?, points = ?, wins = ?, defeats = ?, right_answers = ?, wrong_answers = ?, right_tips = ?, wrong_tips = ? WHERE uname = ?";
	private static final String SQL_GET_AGE_STATISTICS = "SELECT uname, age, points, wins, defeats, right_answers, wrong_answers, right_tips, wrong_tips FROM USERS WHERE age BETWEEN ? and ?";

	//tudom hogy ronda,de nem ment preparedStatement-tel:
	private static final String SQL_MAX_DIFFICULTY = "SELECT MAX(difficulty) FROM NORMAL_QUESTIONS";
	private static final String SQL_MAX_TOPIC_ID = "SELECT MAX(topic_id) FROM NORMAL_QUESTIONS";
	private static final String SQL_MAX_QUESTION_ID = "SELECT MAX(question_id) FROM NORMAL_QUESTIONS";
	private static final String SQL_MAX_FORUM_TOPIC_ID = "SELECT MAX(topic_id) FROM FORUM_TOPICS";
	private static final String SQL_MAX_FORUM_ENTRY_ID = "SELECT MAX(comment_id) FROM FORUM_ENTRIES";

	//Topic
	private static final String SQL_GET_TOPICS_WITH_NUMBERS = "select T1.topic_id, normalDB, NVL(raceDBnull,0) AS raceDB, T1.difficulty, name FROM ( select count(*) AS normalDB, difficulty, topic_id FROM NORMAL_QUESTIONS group by topic_id, difficulty order by topic_id, difficulty ) T1 LEFT JOIN ( select count(*) AS raceDBnull, topic_id FROM RACE_QUESTIONS group by topic_id order by topic_id ) T2 ON T1.topic_id = T2.topic_id, QUESTION_TOPICS where T1.topic_id = question_topics.topic_ID ORDER BY name";
	private static final String SQL_GET_TOPICS = "SELECT * FROM QUESTION_TOPICS";

	//Map
	private static final String SQL_GET_MAP_NAMES = "SELECT NAME FROM MAPS";
	
	//Lekerdezesek:
	private static final String SQL_GET_QUESTION_QUANTITY_BY_CATEGORY = "SELECT T1.NAME AS \"temakor\", (T1.\"count\" + T2.\"count\") AS \"kerdesek_szama\" FROM ( SELECT QT.NAME, COUNT(NQ.TOPIC_ID) AS \"count\" FROM QUESTION_TOPICS QT LEFT JOIN NORMAL_QUESTIONS NQ ON QT.TOPIC_ID=NQ.TOPIC_ID GROUP BY QT.NAME ) T1 LEFT JOIN (SELECT QT.NAME, COUNT(RQ.TOPIC_ID) AS \"count\" FROM QUESTION_TOPICS QT LEFT JOIN RACE_QUESTIONS RQ ON QT.TOPIC_ID=RQ.TOPIC_ID GROUP BY QT.NAME ) T2 ON T1.NAME = T2.NAME ORDER BY \"kerdesek_szama\" DESC";
	private static final String SQL_GET_TOP_TEN_PLAYERS = "SELECT * FROM(SELECT UNAME, POINTS, WINS, DEFEATS, RIGHT_ANSWERS, WRONG_ANSWERS, RIGHT_TIPS, WRONG_TIPS, age FROM USERS ORDER BY POINTS DESC, WINS DESC, DEFEATS ASC, RIGHT_ANSWERS DESC, WRONG_ANSWERS ASC, RIGHT_TIPS DESC , WRONG_TIPS ASC, AGE ASC) WHERE ROWNUM <= 10";
	private static final String SQL_GET_USER_QUESTION_QUANTITY = "SELECT T1.UNAME AS \"username\", 	(T1.\"count\" + T2.\"count\") AS \"kerdesek_szama\" FROM (SELECT UT.UNAME,COUNT(UT.UNAME) AS \"count\" FROM USERS UT LEFT JOIN NORMAL_QUESTIONS NQ ON UT.UNAME=NQ.AUTHOR GROUP BY UT.UNAME) T1 LEFT JOIN (SELECT UT.UNAME,COUNT(UT.UNAME) AS \"count\" FROM USERS UT LEFT JOIN RACE_QUESTIONS NQ ON UT.UNAME=NQ.AUTHOR GROUP BY UT.UNAME) T2 ON T1.UNAME = T2.UNAME ORDER BY \"kerdesek_szama\" DESC";
	private static final String SQL_GET_TOP_FIVE_MAPS = "SELECT * FROM (SELECT M.NAME AS \"Terkep\", T.N_MAP AS \"Nepszeruseg\" FROM (SELECT MAP_ID, COUNT(MAP_ID) AS N_MAP FROM GAMES GROUP BY MAP_ID ) T LEFT JOIN MAPS M ON M.MAP_ID=T.MAP_ID) WHERE ROWNUM <= 5 ORDER BY \"Nepszeruseg\" DESC";
	private static final String SQL_GET_USERQUESTIONS = "SELECT \"kerdes\", \"valasz\", \"temakor\" FROM (SELECT QUESTION AS \"kerdes\", TO_CHAR(RIGHT_ANSWER) AS \"valasz\", QUESTION_TOPICS.NAME AS \"temakor\" FROM RACE_QUESTIONS, QUESTION_TOPICS WHERE RACE_QUESTIONS.TOPIC_ID = QUESTION_TOPICS.TOPIC_ID AND RACE_QUESTIONS.AUTHOR LIKE ? ) UNION ALL ( SELECT QUESTION AS \"kerdes\", RIGHT_ANSWER AS \"valasz\", QUESTION_TOPICS.NAME AS \"temakor\" FROM NORMAL_QUESTIONS, QUESTION_TOPICS WHERE NORMAL_QUESTIONS.TOPIC_ID = QUESTION_TOPICS.TOPIC_ID AND NORMAL_QUESTIONS.AUTHOR LIKE ? )";
	private static final String SQL_GET_GAME_WINNERS = "SELECT U.UNAME AS \"username\", U.REAL_NAME AS \"nev\", T.WINNERSCORE AS \"elert_pontszam\", M.NAME AS \"terkep\" FROM ( SELECT MAP_ID, CASE GREATEST(PLAYER1_SCORE, PLAYER2_SCORE, PLAYER3_SCORE)  WHEN PLAYER1_SCORE THEN PLAYER1 WHEN PLAYER2_SCORE THEN PLAYER2 WHEN PLAYER3_SCORE THEN PLAYER3 END AS winner, GREATEST(PLAYER1_SCORE, PLAYER2_SCORE, PLAYER3_SCORE) AS winnerScore FROM GAMES WHERE STATE = 'finished') T LEFT JOIN USERS U ON winner = U.UNAME LEFT JOIN MAPS M ON T.MAP_ID = M.MAP_ID";
	private static final String SQL_GET_WINNERS = "SELECT CASE GREATEST(PLAYER1_SCORE,PLAYER2_SCORE,PLAYER3_SCORE) WHEN PLAYER1_SCORE THEN PLAYER1 WHEN PLAYER2_SCORE THEN PLAYER2 WHEN PLAYER3_SCORE THEN PLAYER3 END AS winner, GREATEST(PLAYER1_SCORE,PLAYER2_SCORE,PLAYER3_SCORE) AS winnerScore FROM MAPS M LEFT JOIN GAMES G ON M.MAP_ID = G.MAP_ID WHERE M.NAME LIKE ?";
	private static final String SQL_GET_FAV_MAPS = "SELECT \"terkep\", count(\"terkep\") AS \"jatekok_szama\" FROM (SELECT ? AS \"jatekos_neve\", M.NAME AS \"terkep\", G.PLAYER1 AS \"p1\", G.PLAYER2 AS \"p2\", G.PLAYER3 AS \"p3\" FROM GAMES G LEFT JOIN MAPS M ON G.MAP_ID = M.MAP_ID WHERE M.NAME IS NOT NULL ) T WHERE (T.\"p1\" = T.\"jatekos_neve\" OR T.\"p2\" = T.\"jatekos_neve\" OR T.\"p3\" = T.\"jatekos_neve\") GROUP BY \"terkep\" ORDER BY \"jatekok_szama\" DESC";

	//forum
	private static final String SQL_GET_FORUM_ENTRIES = "SELECT * FROM(SELECT * FROM FORUM_ENTRIES WHERE TOPIC_ID = ? ORDER BY COMMENT_ID) WHERE ROWNUM BETWEEN ? AND ?";
	private static final String SQL_ADD_FORUM_ENTRY = "INSERT INTO FORUM_ENTRIES (COMMENT_ID, TEXT, AUTHOR,\"DATE\", TOPIC_ID, REF_COMMENT) VALUES (?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF'), ?, ?)";
	private static final String SQL_ADD_FORUM_ENTRY_WITHOUT_REF = "INSERT INTO FORUM_ENTRIES (COMMENT_ID, TEXT, AUTHOR,\"DATE\", TOPIC_ID) VALUES (?, ?, ?, TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS.FF'), ?)";
	private static final String SQL_GET_FORUM_ENTRIES_COUNT = "SELECT COUNT(*) FROM FORUM_ENTRIES WHERE TOPIC_ID = ?";
	private static final String SQL_GET_FORUM_TOPICS = "SELECT * FROM FORUM_TOPICS ORDER BY TOPIC_ID";
	private static final String SQL_ADD_FORUM_TOPIC = "INSERT INTO FORUM_TOPICS (topic_id, name) VALUES (?, ?)";
	
	
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
	        String tunnelRemoteHost="orania.inf.u-szeged.hu"; // The host of the second target
	        int port=22;
	
	        JSch jsch=new JSch();
	        Session session=jsch.getSession(SshCredentials.getUser(), host, port);
	        session.setPassword(SshCredentials.getPassword());
	        session.setConfig("StrictHostKeyChecking", "no");
	        // create port forward from 1521 on local system to port 1521 on tunnelRemoteHost
	        session.setPortForwardingL(1521, tunnelRemoteHost, 1521);
	        session.setTimeout(3000);
	        session.connect();
	        session.openChannel("direct-tcpip");
	        
	        if (session.isConnected()){
	        	return session;
	        } else {
	        	return null;
	        }
		} catch (Exception  e){
			e.printStackTrace();
			return null;
		}
//		System.out.println("vege");
	}
	
	public synchronized List<Question> getQuestions(int minDiff, int maxDiff, List<Integer> topicList, int n) {

		System.out.print("Load questions from db: ");

		Session session = openSSHTunnel();
		if (session == null)
			return null;
		
		String epicSQL = SQL_GET_QUESTION_BY_DIFFICULTY;
		
		if(topicList != null) { 
			epicSQL+=sqlListAdder("AND","Topic_ID",topicList.size(),"OR");
		}

		epicSQL+="ORDER BY rand";
		
		epicSQL = sqlRowAdder(epicSQL);
		
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
			pst.setInt(index++, n);
			
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
				q.setAuthor(rs.getString("author"));
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

	public synchronized List<RaceQuestion> getRaceQuestions(List<Integer> topicList, int n) {

		System.out.print("Load questions from db: ");

		Session session = openSSHTunnel();
		if (session == null)
			return null;
		
		String epicSQL = SQL_GET_RACE_QUESTION;
		
		if(topicList != null) { 
			epicSQL+=sqlListAdder("WHERE","Topic_ID",topicList.size(),"OR");
		}
		
		epicSQL+="ORDER BY rand";

		epicSQL = sqlRowAdder(epicSQL);

		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(epicSQL);
		) {

			int index = 1;
			if(topicList!=null) {
				for(int i=0;i<topicList.size();++i) {
					pst.setInt(index++, topicList.get(i));
				}
			}
			pst.setInt(index++, n);
			
			List<RaceQuestion> questions = new ArrayList<RaceQuestion>();
			ResultSet rs = pst.executeQuery();
			
			while (rs.next()) {
				RaceQuestion q = new RaceQuestion();
				q.setQuestionId(rs.getInt("question_id"));
				q.setQuestion(rs.getString("question"));
				q.setRightAnswer(Double.parseDouble(rs.getString("right_answer")));
				q.setTopicId(rs.getInt("topic_id"));
				q.setAuthor(rs.getString("author"));
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
	
	private static String sqlRowAdder(String SQL) {
		return "SELECT * FROM (" + SQL + ") WHERE ROWNUM <= ?";
	}
	
	public synchronized int getMax(String column) {
		Session session = openSSHTunnel();
		try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
				Statement pst = conn.createStatement();) {

			ResultSet rs;
			if (column.equals("difficulty")) {
				rs = pst.executeQuery(SQL_MAX_DIFFICULTY);
			} else if(column.equals("topic_id")) {
				rs = pst.executeQuery(SQL_MAX_TOPIC_ID);
			} else if(column.equals("question_id")) {
				rs = pst.executeQuery(SQL_MAX_QUESTION_ID);
			} else if(column.equals("forum_topic_id")) {
				rs = pst.executeQuery(SQL_MAX_FORUM_TOPIC_ID);
			}  else if(column.equals("comment_id")) {
				rs = pst.executeQuery(SQL_MAX_FORUM_ENTRY_ID);
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

	public synchronized boolean checkUser(String uname, String pw) {
		
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
	
	public synchronized boolean addQuestion(Question question) {
	System.out.println("add Question " + question.getQuestion());
		int questionId = getMax("question_id") + 1;

		Session session = openSSHTunnel();
		if (session == null) return false;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL_ADD_QUESTION);
		) {
//question_id, question, right_answer, answer1, answer2, answer3, difficulty, topic_id, author
			int index = 1;
			pst.setInt(index++, questionId);
			pst.setString(index++,question.getQuestion());
			pst.setString(index++,question.getRightAnswer());
			pst.setString(index++,question.getAnswer1());
			pst.setString(index++,question.getAnswer2());
			pst.setString(index++,question.getAnswer3());
			pst.setInt(index++,question.getDifficulty());
			pst.setInt(index++,question.getTopicId());
			pst.setString(index++,question.getAuthor());
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

	public synchronized boolean addUser(User user) throws UserAlreadyExistsException {
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
	
	public synchronized boolean modifyUser(User user) throws UserNotFoundException {
		
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
	
	public synchronized User getUser(String uname) {
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

	public synchronized boolean deleteUser(String uname) {
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
	
	public synchronized boolean updateStatistics(Statistics stat) throws UserNotFoundException {
		System.out.println("modify User " + stat.getUname());
		
		Session session = openSSHTunnel();
		if (session == null) return false;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL_UPDATE_STATISTICS);
		) {
			Statistics old = getUserStatistics(stat.getUname(),conn);
			if(old == null) throw new UserNotFoundException();
			if(old.equals(stat)) {
				return true;
			}
			//"SET age = ?, points = ?, wins = ?, defeats = ?, right_answers = ?, wrong_answer = ?, right_tips = ?, wrong_tips = ?" +
			//"WHERE uname = ?";
			int index = 1;
			pst.setInt(index++,stat.getAge()==0?old.getAge():stat.getAge());
			pst.setInt(index++,stat.getPoints()==-1?old.getPoints():stat.getPoints());
			pst.setInt(index++,stat.getWins()==-1?old.getWins():stat.getWins());
			pst.setInt(index++,stat.getDefeats()==-1?old.getDefeats():stat.getDefeats());
			pst.setInt(index++,stat.getRightAnswers()==-1?old.getRightAnswers():stat.getRightAnswers());
			pst.setInt(index++,stat.getWrongAnswers()==-1?old.getWrongAnswers():stat.getWrongAnswers());
			pst.setInt(index++,stat.getRightTips()==-1?old.getRightTips():stat.getRightTips());
			pst.setInt(index++,stat.getWrongTips()==-1?old.getWrongTips():stat.getWrongTips());
			pst.setString(index++,stat.getUname());
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

	public synchronized Statistics getUserStatistics(String uname) {
	System.out.println("get Statistics " + uname);
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
		) {
			return getUserStatistics(uname,conn);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	private Statistics getUserStatistics(String username, Connection conn) {
		try(
			PreparedStatement pst = conn.prepareStatement(SQL_GET_USER_STATISTICS);
		) {
			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				Statistics stat = new Statistics();
				stat.setUname(rs.getString("uname"));
				stat.setAge(rs.getInt("age"));
				stat.setPoints(rs.getInt("points"));
				stat.setWins(rs.getInt("wins"));
				stat.setDefeats(rs.getInt("defeats"));
				stat.setRightAnswers(rs.getInt("right_answers"));
				stat.setWrongAnswers(rs.getInt("wrong_answers"));
				stat.setRightTips(rs.getInt("right_tips"));
				stat.setWrongTips(rs.getInt("wrong_tips"));
				return stat;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public synchronized List<Statistics> getAgeStatistics(int ageMin, int ageMax) {
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
				stat.setAge(rs.getInt("age"));
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
	
	public synchronized List<Topic> getTopics() {
		System.out.println("get Topics");
			
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			ResultSet rs = st.executeQuery(SQL_GET_TOPICS);
			List<Topic> topics = new ArrayList<Topic>();
			while(rs.next()) {
				Topic topic = new Topic();
				topic.setName(rs.getString("name"));
				topic.setTopicId(rs.getInt("topic_id"));
				topics.add(topic);
			}
			return topics;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	public synchronized List<Topic> getTopicsWithQuestionNumbers() {
		System.out.println("get Topics");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			ResultSet rs = st.executeQuery(SQL_GET_TOPICS_WITH_NUMBERS);
			List<Topic> topics = new ArrayList<Topic>();
			if(rs.next()) {
				while(!rs.isAfterLast()) {
					Topic topic = new Topic();
					topic.setName(rs.getString("name"));
					topic.setNumberOfRaceQuestions(rs.getInt("raceDB"));
					int id = rs.getInt("topic_ID");
					topic.setTopicId(id);
					
					Map<Integer,Integer> normals = new HashMap<Integer,Integer>();
					normals.put(rs.getInt("difficulty"), rs.getInt("normalDB"));

					while(rs.next()) {
						if(rs.getInt("topic_ID") != id) {
							break;
						}
						normals.put(rs.getInt("difficulty"), rs.getInt("normalDB"));
					}
					topic.setNumberOfQuestionsByDifficulty(normals);
					topics.add(topic);
				}
			}
			return topics;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}

	
	public synchronized List<String[]> getQuestions() {
		System.out.println("getQuestions");
		List<String[]> re = new ArrayList<String[]>();
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			ResultSet rs = st.executeQuery(SQL_GET_QUESTIONS);
			while(rs.next()) {
				String[] str = new String[8];
				for(int i=0;i<8;++i) {
					str[i] = rs.getString(i+1);
				}
				re.add(str);
			}
			return re;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	public synchronized List<String> getMapNames() {
		System.out.println("get Map Names");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			
			ResultSet rs = st.executeQuery(SQL_GET_MAP_NAMES);
			List<String> re = new ArrayList<>();
			while(rs.next()) {
				re.add(rs.getString(1));
			}
			return re;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	public List<ForumEntry> getForumEntries(ForumTopic forumTopic, int minNum, int maxNum) {
		System.out.println("get Forum Entries");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			PreparedStatement pst = conn.prepareStatement(SQL_GET_FORUM_ENTRIES);
		) {
			int index = 1;
			pst.setInt(index++, forumTopic.getTopicId());
			pst.setInt(index++, minNum);
			pst.setInt(index++, maxNum);
			
			ResultSet rs = pst.executeQuery();
			List<ForumEntry> re = new ArrayList<>();
			while(rs.next()) {
				ForumEntry entry = new ForumEntry();
				entry.setCommentId(rs.getInt("comment_id"));
				entry.setText(rs.getString("text"));
				entry.setAuthor(rs.getString("author"));
				entry.setTopicId(rs.getInt("topic_id"));
				String ref = rs.getString("ref_comment");
				if(ref == null) {
					entry.setRefComment(-1);	
				} else {
					entry.setRefComment(Integer.parseInt(ref));
				}
				entry.setDate(rs.getTimestamp("date"));
				re.add(entry);
			}
			return re;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}

	public int getForumEntriesCount(ForumTopic forumTopic) {
		System.out.println("get Forum Entries Count");
		
		Session session = openSSHTunnel();
		if (session == null) return 0;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			PreparedStatement pst = conn.prepareStatement(SQL_GET_FORUM_ENTRIES_COUNT);
		) {
			
			pst.setInt(1, forumTopic.getTopicId());
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return 0;
	}

	public List<ForumTopic> getForumTopics() {
		System.out.println("get Forum Topics");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			
			ResultSet rs = st.executeQuery(SQL_GET_FORUM_TOPICS);
			List<ForumTopic> re = new ArrayList<>();
			while(rs.next()) {
				ForumTopic topic = new ForumTopic();
				topic.setName(rs.getString("name"));
				topic.setTopicId(rs.getInt("topic_id"));
				re.add(topic);
			}
			return re;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}

	private String dateZeros(String str) {
		String re = str;
		String[] kocka = str.split("\\.");
		String last = kocka[kocka.length-1];
		for(int i=last.length();i<9;++i) {
			re+="0";
		}
		return re;
	}
	
	public boolean addForumEntry(ForumEntry forumEntry) {
		System.out.println("add ForumTopic " + forumEntry.getText());
		int commentId = getMax("comment_id") + 1;
		System.out.println("Date " + forumEntry.getDate());
		String date = dateZeros(forumEntry.getDate().toString());

		Session session = openSSHTunnel();
		if (session == null) return false;
		
		String SQL = SQL_ADD_FORUM_ENTRY;
		if(forumEntry.getRefComment() == -1) {
			SQL = SQL_ADD_FORUM_ENTRY_WITHOUT_REF;
		}
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL);
		) {
			int index = 1;
			//(COMMENT_ID, TEXT, AUTHOR,\"DATE\", TOPIC_ID, REF_COMMENT)
			pst.setInt(index++, commentId);
			pst.setString(index++,forumEntry.getText());
			pst.setString(index++,forumEntry.getAuthor());
			pst.setString(index++,date);
			pst.setInt(index++,forumEntry.getTopicId());
			if(forumEntry.getRefComment() != -1) {
				pst.setInt(index++,forumEntry.getRefComment());
			}
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

	public boolean addForumTopic(ForumTopic forumTopic) {
		System.out.println("add ForumTopic " + forumTopic.getName());
		int forumTopicId = getMax("forum_topic_id") + 1;

		Session session = openSSHTunnel();
		if (session == null) return false;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo"); 
			PreparedStatement pst = conn.prepareStatement(SQL_ADD_FORUM_TOPIC);
		) {
			int index = 1;
			pst.setInt(index++, forumTopicId);
			pst.setString(index++,forumTopic.getName());
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
	
	//TODO lekerdezes
	
	public synchronized Map<String,Integer> getQuestionQuantityByCategory() {
		System.out.println("1.lekerdezes");
		Map<String,Integer> re = new HashMap<String,Integer>();
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			ResultSet rs = st.executeQuery(SQL_GET_QUESTION_QUANTITY_BY_CATEGORY);
			while(rs.next()) {
				re.put(rs.getString("temakor"),Integer.parseInt(rs.getString("kerdesek_szama")));
			}
			return re;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}

	public synchronized List<Statistics> getTopTenPlayersStatistics() {
		System.out.println("2.lekerdezes");
		
		List<Statistics> stats = new ArrayList<Statistics>();
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			
			ResultSet rs = st.executeQuery(SQL_GET_TOP_TEN_PLAYERS);
			while(rs.next()) {
				Statistics stat = new Statistics();
				stat.setUname(rs.getString("uname"));
				stat.setAge(rs.getInt("age"));
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

	public synchronized Map<String, Integer> getUserQuestionQuantity() {
		System.out.println("3.lekerdezes");
		Map<String,Integer> re = new HashMap<String,Integer>();
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			ResultSet rs = st.executeQuery(SQL_GET_USER_QUESTION_QUANTITY);
			while(rs.next()) {
				re.put(rs.getString("username"),Integer.parseInt(rs.getString("kerdesek_szama")));
			}
			return re;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	public synchronized Map<String, Integer> getTopFiveMaps() {
		System.out.println("4.lekerdezes");
		
		Map<String, Integer> maps = new HashMap<String, Integer>();
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			
			ResultSet rs = st.executeQuery(SQL_GET_TOP_FIVE_MAPS);
			while(rs.next()) {
				maps.put(rs.getString("Terkep"),rs.getInt("nepszeruseg"));
			}
			return maps;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;

	}
	
	public synchronized List<String[]> getUserQuestions(String uname) {
	System.out.println("5.lekerdezes");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			PreparedStatement pst = conn.prepareStatement(SQL_GET_USERQUESTIONS);
		) {
			pst.setString(1, uname);
			pst.setString(2, uname);
			ResultSet rs = pst.executeQuery();
			
			List<String[]> re = new ArrayList<>();
			
			while(rs.next()) {
				String[] str = new String[3];
				for(int i=0;i<3;++i) {
					str[i] = rs.getString(i+1);
				}
				re.add(str);
			}
			return re;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	public synchronized List<String[]> getGameWinners() {
	System.out.println("6.lekerdezes");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			Statement st = conn.createStatement();
		) {
			ResultSet rs = st.executeQuery(SQL_GET_GAME_WINNERS);
			
			List<String[]> re = new ArrayList<>();
			
			while(rs.next()) {
				String[] str = new String[4];
				for(int i=0;i<4;++i) {
					str[i] = rs.getString(i+1);
				}
				re.add(str);
			}
			return re;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	public synchronized List<String> getWinners(String map) {
	System.out.println("7.lekerdezes");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			PreparedStatement pst = conn.prepareStatement(SQL_GET_WINNERS);
		) {
			pst.setString(1, map);
			
			ResultSet rs = pst.executeQuery();
			
			List<String> re = new ArrayList<>();
			
			while(rs.next()) {
				re.add(rs.getString(1));
			}
			return re;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}
	
	public synchronized Map<String,Integer> getFavMaps(String uname) {
	System.out.println("8.lekerdezes");
		
		Session session = openSSHTunnel();
		if (session == null) return null;
		
		try (
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@" + DATABASE_LINK, "h664800", "jelszo");
			PreparedStatement pst = conn.prepareStatement(SQL_GET_FAV_MAPS);
		) {
			pst.setString(1, uname);
			ResultSet rs = pst.executeQuery();
			
			Map<String,Integer> re = new HashMap<String,Integer>();
			while(rs.next()) {
				re.put(rs.getString("terkep"),rs.getInt("jatekok_szama"));
			}
			return re;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return null;
	}

}
