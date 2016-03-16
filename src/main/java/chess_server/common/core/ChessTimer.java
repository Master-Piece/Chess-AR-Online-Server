package chess_server.common.core;

import org.apache.log4j.Logger;

public class ChessTimer implements Runnable {
	Logger log = Logger.getLogger(this.getClass());
	
	Thread thread;
	
	Thread chessThread;
	
	long waitTime;
	long startTime;
	
	@Override
	public void run() {
		try {
			while ((System.currentTimeMillis() - startTime) < waitTime) {
				Thread.sleep(1);
			}
			chessThread.interrupt();
			log.debug("Timer out");
		} catch (InterruptedException e) {
			log.debug("Timer Failed");
		}
	}
	
	public ChessTimer(Thread chessThread) {
		this.chessThread = chessThread;
	}
	
	public long startCount(long millis) throws InterruptedException {
		waitTime = millis;
		startTime = System.currentTimeMillis();
		
		thread = null;
		System.gc();
		
		thread = new Thread(this);
		thread.start();
		return thread.getId();
	}
	
	public long getTimerId() {
		return thread.getId();
	}
}
