package chess_server.common.core;

import java.util.ArrayList;

import org.apache.log4j.Logger;

public class MatchMaker implements Runnable {
	Logger log = Logger.getLogger(this.getClass());
	
	private ArrayList<String> queue;
	private static long threadId;
	
	private static MatchMaker instance;
	
	private MatchMaker() {
		queue = new ArrayList<String>();
	}
	
	static {
		try {
			instance = new MatchMaker();
			Thread thread = new Thread(getInstance());
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
		while (true) {
//			log.debug("Match Maker waiting...");
//			if (queue.isEmpty()) {
//				try {
//					getMatchMakingThread().wait();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
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
	
	public boolean enqueue(String id) {
		log.debug("User enqueued : " + id);
		
//		if (getMatchMakingThread().getState() == Thread.State.BLOCKED) {
//			getMatchMakingThread().notify();
//		}
		
		return queue.add(id);
	}
	
	// TODO: ���������� queue�� �ִ� id �� ���� �� ���� ������ Log�� ����. �Ŀ��� GameCoreThread�� �����Ͽ� ������ ���� �� �� �ֵ���.
	private void matchSuccess() {
		String user1 = queue.get(0);
		String user2 = queue.get(1);
		queue.remove(0);
		queue.remove(0);
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		gcm.startGame(user1, user2);
		
		log.debug(String.format("Matched : %s VS %s", user1, user2));
	}
	
	public void printQueue() {
		int queueSize = queue.size();
		
		for (int i = 0; i < queueSize; ++i) {
			log.debug(String.format("[%d] %s", i, queue.get(i)));
		}
	}
	
	private Thread getMatchMakingThread() {
		Thread mmThread = null;
		
		ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
		ThreadGroup parentGroup;
		while ((parentGroup = rootGroup.getParent()) != null) {
		    rootGroup = parentGroup;
		}
		 
		Thread[] threads = new Thread[rootGroup.activeCount()];
		while (rootGroup.enumerate(threads, true) == threads.length) {
		    threads = new Thread[threads.length * 2];
		}
		 
		for (Thread t : threads) {
		    if (t.getId() == threadId) {
		        /* found it */
		    	mmThread = t;
		    }
		}
		
		return mmThread;
	}
}
