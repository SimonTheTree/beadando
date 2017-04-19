package controller;

public class GameMessage {

	private String sender;
	private String message;
	private String[] params;
	private boolean automatic;
	
	/**A konstruktor es toString metodusok e menten vagja szet az uzeneteket. */
	private static final String SPLIT = "\t";

	public GameMessage() {}
	public GameMessage(boolean automatic, String sender, String message, String... params) {
		this.automatic = automatic;
		this.sender = sender;
		this.message = message;
		this.params = params;
	}
	public GameMessage(String arg) {
		String[] args = arg.split(SPLIT);
		automatic = args[0].equals("true")?true:false;
		sender = args[1];
		message = args[2];
		if(args.length>3) {
			params = new String[args.length-3];
			for(int i=0;i<params.length;++i) {
				params[i] = args[i+3];
			}
		}
	}
	
	public GameMessage(String message, String... params) {
		this.message = message;
		this.params = params;
	}
	
	public String toString() {
		String re = "";
		re+=automatic+SPLIT;
		re+=sender+SPLIT;
		re+=message;
		if(params != null && params.length>0) {
			re+=SPLIT;
			for(int i=0;i<params.length-1;++i) {
				re+=params[i];
				re+=SPLIT;
			}
			re+=params[params.length-1];
		}
		return re;
	}
	
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public boolean isAutomatic() {
		return automatic;
	}
	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}
	
	
}
