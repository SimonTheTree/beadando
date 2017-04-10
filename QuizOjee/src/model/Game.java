package model;

public class Game {

	public static final String SPLIT = "@";
	private int gameId;
	private int mapId;
	private int created;
	private int state;
	private int player1Score;
	private int player2Score;
	private int player3Score;
	private String player1;
	private String player2;
	private String player3;
	//private String ip;
	
	public Game() {
	}
	
	public Game(String gameString) {
		setFromString(gameString);
	}
	
	public void setFromString(String gameString) {
		String[] pieces = gameString.split(SPLIT);
		int index = 0;
		gameId = Integer.parseInt(pieces[index++]);
		mapId = Integer.parseInt(pieces[index++]);
		created = Integer.parseInt(pieces[index++]);
		state = Integer.parseInt(pieces[index++]);
		player1Score = Integer.parseInt(pieces[index++]);
		player2Score = Integer.parseInt(pieces[index++]);
		player3Score = Integer.parseInt(pieces[index++]);
		player1 = pieces[index].equals("null")?null:pieces[index++];
		player2 = pieces[index].equals("null")?null:pieces[index++];
		player3 = pieces[index].equals("null")?null:pieces[index++];		
	}
	
	public String toString() {
		return gameId+SPLIT+
				mapId+SPLIT+
				created+SPLIT+
				state+SPLIT+
				player1Score+SPLIT+
				player2Score+SPLIT+
				player3Score+SPLIT+
				player1+SPLIT+
				player2+SPLIT+
				player3;
	}
	
	/*public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}*/
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public int getMapId() {
		return mapId;
	}
	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
		this.created = created;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getPlayer1Score() {
		return player1Score;
	}
	public void setPlayer1Score(int player1Score) {
		this.player1Score = player1Score;
	}
	public int getPlayer2Score() {
		return player2Score;
	}
	public void setPlayer2Score(int player2Score) {
		this.player2Score = player2Score;
	}
	public int getPlayer3Score() {
		return player3Score;
	}
	public void setPlayer3Score(int player3Score) {
		this.player3Score = player3Score;
	}
	public String getPlayer1() {
		return player1;
	}
	public void setPlayer1(String player1) {
		this.player1 = player1;
	}
	public String getPlayer2() {
		return player2;
	}
	public void setPlayer2(String player2) {
		this.player2 = player2;
	}
	public String getPlayer3() {
		return player3;
	}
	public void setPlayer3(String player3) {
		this.player3 = player3;
	}
	
}
