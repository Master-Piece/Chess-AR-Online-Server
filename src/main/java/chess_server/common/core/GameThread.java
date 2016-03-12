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
	boolean gameFlag = true; // 게임 종료 또는 항복시 false
	boolean moveFlag = true; // move시 false
	boolean selectFlag = true; // select시 false
	
	public GameThread(Player whitePlayer, Player blackPlayer) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		chessBoard = new Algorithm();
	}
	
	@Override
	public void run() {
		GCMSender sender = GCMSender.getInstance();
		while (gameFlag) {
			// TODO: gcm으로 턴을 알려줌
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
	 *     "error": : error code }
	 * */
	public String availableTiles(String tile, Player player) {
		synchronized(thread) {
			awakeThread(selectFlag);
			thread.notify();
		}
		return chessBoard.select(tile, player);
	}
	
	/*  GameCoreController에서 호출. 유저가 체스말을 움직이면 실행될 메서드.
	 * 
	 *  @Param
	 *  String srcTile : 움직일 말의 위치
	 *  String destTile : 목적지
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
