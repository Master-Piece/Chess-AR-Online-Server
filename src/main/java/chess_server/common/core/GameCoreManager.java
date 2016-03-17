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
		gameThreadPool.put(gt.getThreadId(), gt);
		
		GCMSender sender = GCMSender.getInstance();
		sender.matchSuccessNotice(player_1, gt.getThreadId());
		sender.matchSuccessNotice(player_2, gt.getThreadId());
		
		log.debug("Match Success(" + gt.getThreadId() + "): " + player_1.getNickName() + " VS "+ player_2.getNickName());
		
		return gt.getThreadId();
	}
	
	public GameThread getGame(long threadId) {
		return gameThreadPool.get(threadId);
	}
	
	public Long[] getGameList() {
		return gameThreadPool.keySet().toArray(new Long[gameThreadPool.size()]);
	}
	
	public void endGame(long threadId) {
		gameThreadPool.get(threadId).endGame();
		gameThreadPool.remove(threadId);
	}
}
