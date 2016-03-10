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
	
	public void startGame(String id_1, String id_2) {
		GameThread gt = new GameThread(id_1, id_2);
		gt.startGame();
		gameThreadPool.put(gt.getThreadId(), gt);
		log.debug("Match Success(" + gt.getThreadId() + "): " + id_1 + " VS "+ id_2);
	}
	
	public GameThread getGame(long threadId) {
		return gameThreadPool.get(threadId);
	}
}
