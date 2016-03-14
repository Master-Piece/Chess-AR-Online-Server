package chess_server.common.core;

import org.apache.log4j.Logger;

public class GameThread implements Runnable {
	Logger log = Logger.getLogger(this.getClass());
	
	enum Turn {white, black};
	
	Thread thread;
	Player whitePlayer;
	Player blackPlayer;
	Algorithm chessBoard;
	Turn turn;
	boolean gameFlag = true; // ���� ���� �Ǵ� �׺��� false
	boolean moveFlag = true; // move�� false
	boolean selectFlag = true; // select�� false
	
	public GameThread(Player whitePlayer, Player blackPlayer) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		chessBoard = new Algorithm();
	}
	
	@Override
	public void run() {
		GCMSender sender = GCMSender.getInstance();
		while (gameFlag) {
			// TODO: gcm���� ���� �˷���
			sender.noticeTurn((turn == Turn.black) ? blackPlayer.getGcmToken() : whitePlayer.getGcmToken());
			
			waitNextWithFlag(selectFlag);
			// User selected tile.
			log.debug("USER SELECTED. THREAD AWAKE");
			
			waitNextWithFlag(moveFlag);
			// User moved.
			log.debug("USER MOVED. THREAD AWAKE");
			
			turn = (turn == Turn.black) ? Turn.white : Turn.black;
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
	
	public Player[] getUsers() {
		return new Player[]{whitePlayer, blackPlayer};
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
		waitThread(flag);
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
	 *     "error": : error code }
	 * */
	public String availableTiles(String tile, Player player) {
		synchronized(thread) {
			awakeThread(selectFlag);
			thread.notify();
		}
		return chessBoard.select(tile, player);
	}
	
	/*  GameCoreController���� ȣ��. ������ ü������ �����̸� ����� �޼���.
	 * 
	 *  @Param
	 *  String srcTile : ������ ���� ��ġ
	 *  String destTile : ������
	 * 
	 *  @Return
	 *  JSON : 
	 *    {"type" : "MOVE_SUCCESS || MOVE_FAILED",
	 *     "error": : error code }
	 * */
	public String userMoveTile(String srcTile, String destTile) {
		synchronized(thread) {
			awakeThread(moveFlag);
			thread.notify();
		}
		return chessBoard.move(srcTile, destTile);
	}
	
	private void awakeThread(boolean flag) {
		flag = false;
	}
	
	private void waitThread(boolean flag) {
		flag = true;
	}
	
	public Player getPlayerById(String id) {
		return id.equals(whitePlayer.getId()) ? whitePlayer : blackPlayer;
	}
} 