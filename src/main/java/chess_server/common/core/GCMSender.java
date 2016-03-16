package chess_server.common.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
	
	public void noticeTurn(String regId) {
		Message message = new Message.Builder().addData("data", "TURN_RESPONSE_HERE")
				.build();
		List<String> list = new ArrayList<String>();
		list.add(regId);
		MulticastResult multiResult;
		try {
			multiResult = sender.send(message, list, 5);
			if (multiResult != null) {
				List<Result> resultList = multiResult.getResults();
				for (Result result : resultList) {
					log.debug(result.getMessageId());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendTest(String token, String data) {
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
					log.debug(result.getMessageId());
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
