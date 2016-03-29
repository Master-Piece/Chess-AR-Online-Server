package chess_server.common.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class GameCoreManager {
	Logger log = Logger.getLogger(this.getClass());
	
	private Map<Long, GameThread> gameThreadPool;
	private static GameCoreManager instance;
	
	private GameCoreManager(){
		gameThreadPool = new HashMap<Long, GameThread>();
	}
	
	static {
		instance = new GameCoreManager();
	}
	
	public static GameCoreManager getInstance() {
		return instance;
	}
	
	public long startGame(Player player_1, Player player_2) {
		GameThread gt = new GameThread(player_1, player_2);
		gt.startGame();
		gameThreadPool.put(gt.getSessionKey(), gt);
		
		GCMSender sender = GCMSender.getInstance();
		sender.matchSuccessNotice(player_1, gt.getSessionKey());
		sender.matchSuccessNotice(player_2, gt.getSessionKey());
		
		log.debug("Match Success(" + gt.getSessionKey() + "): " + player_1.getNickName() + " VS "+ player_2.getNickName());
		
		return gt.getSessionKey();
	}
	
	public GameThread getGame(long sessionKey) {
		return gameThreadPool.get(sessionKey);
	}
	
	public Long[] getGameList() {
		return gameThreadPool.keySet().toArray(new Long[gameThreadPool.size()]);
	}
	
	public void forceEndGame(long sessionKey) {
		gameThreadPool.get(sessionKey).endGame();
		gameThreadPool.remove(sessionKey);
	}
	
	public void closeSession(long sessionKey) {
		gameThreadPool.remove(sessionKey);
	}
	
	public boolean checkSessionExist(long sessionKey) {
		return gameThreadPool.containsKey(sessionKey);
	}
}
