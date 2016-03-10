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
	boolean gameFlag = true; // ���� ���� �Ǵ� �׺��� false
	boolean waitFlag = true; // thread�� ���� ���� ����� ���� �����ϴ� �÷���
	
	public GameThread(String whitePlayer, String blackPlayer) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		chessBoard = new Algorithm();
	}
	
	@Override
	public void run() {
		while (gameFlag) {
			// TODO: gcm���� ���� �˷���
			waitThread();
			
			waitNextWithFlag(waitFlag);
			log.debug("GameThread working");
			// TODO: Command�� ���� thread�� ����
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
	
	/*  GameCoreController���� ȣ��. ������ ü������ �����ϸ� ����� �޼���.
	 * 
	 *  @Param
	 *  String userId : ��û�� ������ ID. ���� �ϰ� ���ؼ� ������ ����
	 *  String tile : ������ ��ġ
	 * 
	 *  @Return
	 *  JSON : 
	 *    {"type" : "SELECT_SUCCESS || SELECT_FAILED",
	 *     "piece" : ������ ��ġ�� �ִ� ü������ ID,
	 *     "tiles" : ������ ��ġ�� �ִ� ü������ �� �� �ִ� Ÿ�ϵ�(Array),
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
