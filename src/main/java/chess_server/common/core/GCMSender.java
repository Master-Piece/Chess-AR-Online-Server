package chess_server.common.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCMSender {
	Logger log = Logger.getLogger(this.getClass());
	
	/* GCMSender instance. Initialize at once.
	 * */
	private static GCMSender instance;
	private GameCoreManager gameCoreManager;
	private GCMSender() {
		sender = new Sender(API_KEY);
		gameCoreManager = GameCoreManager.getInstance();
	}
	
	private Sender sender;
	private final String API_KEY = "AIzaSyCqNLXChSyptzttEwJsOhsFXdnU2c-QSzE"; 
	
	static {
		instance = new GCMSender();
	}
	
	public static GCMSender getInstance() {
		return instance;
	}
	
	public void matchSuccessNotice(Player player, long sessionKey) {
		JSONObject json = new JSONObject();
		
		json.put("type", "MM_SUCCESS");
		json.put("sessionKey", sessionKey);
		json.put("id", player.getId());
		json.put("color", player.getColor());
		
		String jsonString = json.toJSONString();
		
		sendGCM(player.getGcmToken(), json.toJSONString());
	}
	
	public void noticeTurn(String regId) {
		JSONObject json = new JSONObject();
		JSONObject move = new JSONObject();
		json.put("type", "YOUR_TURN");
		move.put("srcPiece", "BP1");
		move.put("destTile", "A6");
		move.put("targetPiece", "null");
		json.put("move", move);
		json.put("check", false);
		
		log.debug("notice turn to " + regId);
		
		sendGCM(regId, json.toJSONString());
	}
	
	public void surrenderNotice(long sessionKey, String loserId) {
		JSONObject loserJson = new JSONObject();
		JSONObject winnerJson = new JSONObject();
		
		winnerJson.put("type", "YOU_WIN");
		winnerJson.put("state", "SURRENDER");
		
		loserJson.put("type", "YOU_LOSE");
		loserJson.put("state", "SURRENDER");
		
		GameThread gt = GameCoreManager.getInstance().getGame(sessionKey);
		Player[] players = gt.getUsers();
		
		Player winner = players[0].getId().equals(loserId) ? players[1] : players[0];
		Player loser = players[0].getId().equals(loserId) ? players[0] : players[1];
		
		sendGCM(winner.getGcmToken(), winnerJson.toJSONString());
		sendGCM(loser.getGcmToken(), loserJson.toJSONString());
		
		log.debug("surrender notice to " + sessionKey);
	}
	
	public void sendGCM(String gcmToken, String data) {
		Message message = new Message.Builder().addData("data", data)
				.build();
		List<String> list = new ArrayList<String>();
		list.add(gcmToken);
		MulticastResult multiResult;
		try {
			multiResult = sender.send(message, list, 5);
			if (multiResult != null) {
				List<Result> resultList = multiResult.getResults();
				for (Result result : resultList) {
					log.debug("noticeTurn: " + result.toString());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String sendTest(String token, String data) {
		String ret = "";
		Message message = new Message.Builder().addData("data", data)
				.build();
		List<String> list = new ArrayList<String>();
		list.add(token);
		MulticastResult multiResult;
		try {
			multiResult = sender.send(message, list, 5);
			if (multiResult != null) {
				List<Result> resultList = multiResult.getResults();
				for (Result result : resultList) {
					log.debug(result.toString());
					ret += result.toString() + "\n";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
}
