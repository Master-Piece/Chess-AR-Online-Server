package chess_server.common.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Player {
	enum Phase {SELECT, MOVE, WAIT};
	
	private String gcmToken;
	private String nickName;
	private String id;
	private String color;
	private Phase phase;
	
	private String[] recentMove; // 0 : 움직인 말, 1 : 움직인 위치
	
	public static final String WHITE = "white";
	public static final String BLACK = "black";
	
	public Player(String gcmToken, String nickName) {
		this.gcmToken = gcmToken;
		this.nickName = nickName;
		this.phase = Phase.WAIT;
		this.id = getMD5(gcmToken + ":" + nickName);
		recentMove = new String[2];
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public void setPhase(Phase phase) {
		this.phase = phase;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getGcmToken() {
		return gcmToken;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public String getId() {
		return id;
	}
	
	public String getPhase() {
		return phase.toString();
	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	private String getMD5(String str){
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	
	public String getRecentMoveObject() {
		return recentMove[0];
	}
	
	public String getRecentMoveDestnation() {
		return recentMove[1];
	}
	
	public String getRecentMoveTarget() {
		return recentMove[2];
	}
	
	public void setRecentMove(String object, String destination) {
		recentMove[0] = object;
		recentMove[1] = destination;
		recentMove[2] = "null";
	}
	
	public void setRecentMove(String object, String destination, String target) {
		recentMove[0] = object;
		recentMove[1] = destination;
		recentMove[2] = target;
	}
}
