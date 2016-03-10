package chess_server.common.core;

import org.apache.log4j.Logger;

public class GameThread implements Runnable {
	Logger log = Logger.getLogger(this.getClass());
	
	enum Turn {white, black};
	
	Thread thread;
	String whitePlayer;
	String blackPlayer;
	Algorithm chessBoard;
	Turn turn;
	boolean gameFlag = true; // 게임 종료 또는 항복시 false
	boolean waitFlag = true; // thread가 블럭이 될지 논블럭이 될지 결정하는 플래그
	
	public GameThread(String whitePlayer, String blackPlayer) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		chessBoard = new Algorithm();
	}
	
	@Override
	public void run() {
		while (gameFlag) {
			// TODO: gcm으로 턴을 알려줌
			waitThread();
			
			waitNextWithFlag(waitFlag);
			log.debug("GameThread working");
			// TODO: Command가 오면 thread를 깨움
			// TODO: Processing
			waitThread();
		}
	}
	
	public void startGame() {
		thread = new Thread(this);
		thread.start();
	}
	
	public long getThreadId() {
		return thread.getId();
	}
	
	public Thread getThread() {
		return thread;
	}
	
	public String[] getUsers() {
		return new String[]{whitePlayer, blackPlayer};
	}
	
	private void waitNext() {
		try {
			synchronized(thread) {
				thread.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void waitNextWithFlag(boolean flag) {
		while (flag) {
			waitNext();
		}
	}
	
	/*  GameCoreController에서 호출. 유저가 체스말을 선택하면 실행될 메서드.
	 * 
	 *  @Param
	 *  String userId : 요청한 유저의 ID. 현재 턴과 비교해서 같으면 응답
	 *  String tile : 선택한 위치
	 * 
	 *  @Return
	 *  JSON : 
	 *    {"type" : "SELECT_SUCCESS || SELECT_FAILED",
	 *     "piece" : 선택한 위치에 있는 체스말의 ID,
	 *     "tiles" : 선택한 위치에 있는 체스말이 갈 수 있는 타일들(Array),
	 *     "error: : error code }d
	 * */
	public String availableTiles(String userId, String tile) {
		synchronized(thread) {
			awakeThread();
			thread.notify();
		}
		return "[\"hello\"]";
	}
	
	private void awakeThread() {
		this.waitFlag = false;
	}
	
	private void waitThread() {
		this.waitFlag = true;
	}
} 
