package chess_server.common.core;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class MatchMaker implements Runnable {
	Logger log = Logger.getLogger(this.getClass());
	
	private ArrayList<Player> queue;
	private static long threadId;
	private static int accumulateUsers;
	private boolean isRunning = true;;
	private static Thread thread;
	
	private static MatchMaker instance;
	
	private MatchMaker() {
		queue = new ArrayList<Player>();
	}
	
	static {
		try {
			accumulateUsers = 0;
			instance = new MatchMaker();
			thread = new Thread(getInstance());
			threadId = Thread.currentThread().getId();
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MatchMaker getInstance() {
		return instance;
	}
	
	@Override
	public void run() {
		isRunning = true;
		while (true) {
			while (!isRunning) {
				synchronized(thread) {
					try {
						thread.wait();
					} catch (InterruptedException e) {}
				}
			}
			if (checkQueue()) {
				matchSuccess();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkQueue() {
		return queue.size() >= 2;
	}
	
	public boolean enqueue(Player player) {
		log.debug(String.format("User enqueued : %s(%s)", player.getNickName(), player.getId()));
		accumulateUsers++;
		return queue.add(player);
	}
	
	// TODO: 디버깅용으로 queue에 있는 id 중 앞의 두 개를 없에고 Log를 남김. 후에는 GameCoreThread를 생성하여 게임을 진행 할 수 있도록.
	private void matchSuccess() {
		Player user1 = queue.get(0);
		Player user2 = queue.get(1);
		queue.remove(0);
		queue.remove(0);
		
		user1.setColor(Player.WHITE);
		user2.setColor(Player.BLACK);
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		gcm.startGame(user1, user2);
		
		log.debug(String.format("Matched : %s VS %s", user1.getNickName(), user2.getNickName()));
	}
	
	public void printQueue() {
		int queueSize = queue.size();
		
		for (int i = 0; i < queueSize; ++i) {
			log.debug(String.format("[%d] %s()", i, queue.get(i).getNickName(), queue.get(i).getId()));
		}
	}
	
	public int getAccumulateUsers() {
		return accumulateUsers;
	}
	
	public Player[] getPlayersInQueue() {
		return queue.toArray(new Player[queue.size()]);
	}
	
	public void stopRunning() {
		log.info("MatchMaker going to sleep...");
		isRunning = false;
	}
	
	public void resumeRunning() {
		isRunning = true;
		synchronized(thread) {
			thread.notify();
		}
		log.info("MatchMaker awaked");
	}
	
	public boolean isMMRunning() {
		return isRunning;
	}
}
