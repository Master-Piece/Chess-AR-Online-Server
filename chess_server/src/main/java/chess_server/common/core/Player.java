package chess_server.common.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Player {
	private String gcmToken;
	private String nickName;
	private String id;
	
	public Player(String gcmToken, String nickName) {
		this.gcmToken = gcmToken;
		this.nickName = nickName;
		
		this.id = getMD5(gcmToken + ":" + nickName);
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
}
