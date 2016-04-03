package chess_server.common.core;


import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public class GameThread implements Runnable {
	private static final long MINUTE = 1000 * 60;
	private static final long SECOND = 1000;
	
	private static final long GAME_COUNT = 60 * MINUTE;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	enum Turn {white, black};
	
	private GCMSender sender;
	private ChessTimer cTimer;
	
	private Thread thread;
	private Player whitePlayer;
	private Player blackPlayer;
	private Algorithm chessBoard;
	private Turn turn;
	private boolean gameFlag = true; // 게임 종료 또는 항복시 false
	private boolean moveFlag = true; // move시 false
	private boolean selectFlag = true; // select시 false
	private boolean turnOverFlag = false;
	
	public GameThread(Player whitePlayer, Player blackPlayer) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		chessBoard = new Algorithm();
		sender = GCMSender.getInstance();
	}
	
	@Override
	public void run() {
		
		while (gameFlag) {
			try {
				cTimer.startCount(GAME_COUNT);
				game();
				cTimer.reCount();
			} catch (InterruptedException e) {
				if (turnOverFlag) {
					((turn == Turn.black) ? blackPlayer : whitePlayer).setPhase(Player.Phase.WAIT);
					turnOverFlag = false;
				}
				else {
					info("Time out!!! " + ((turn == Turn.black) ? blackPlayer.getColor() : whitePlayer.getColor()) + "LOSE");
					info("END SESSION");
					//sender.sendTest(((turn == Turn.black) ? blackPlayer.getGcmToken() : whitePlayer.getGcmToken()), "TIMEOUT");
					//sender.sendTest((!(turn == Turn.black) ? blackPlayer.getGcmToken() : whitePlayer.getGcmToken()), "OPPONENT TIMEOUT");
					break;
				}
			}
		}
		
		GameCoreManager.getInstance().closeSession(getSessionKey());
	}
	
	private void game() throws InterruptedException {
		if (turn == null) {
			turn = Turn.white;
		}
		else {
			turn = (turn == Turn.black) ? Turn.white : Turn.black;
		}
		// TODO: gcm으로 턴을 알려줌
		((turn == Turn.black) ? blackPlayer : whitePlayer).setPhase(Player.Phase.SELECT);
		info(((turn == Turn.black) ? blackPlayer.getColor() : whitePlayer.getColor()) + "'s Turn");
		info((turn == Turn.black) ? blackPlayer.getGcmToken() : whitePlayer.getGcmToken());
		sender.noticeTurn((turn == Turn.black) ? blackPlayer.getGcmToken() : whitePlayer.getGcmToken());
	
		waitNextWithFlag(selectFlag);
		// User selected tile.
		log.debug("USER SELECTED. THREAD AWAKE");
		
		((turn == Turn.black) ? blackPlayer : whitePlayer).setPhase(Player.Phase.MOVE);
		waitNextWithFlag(moveFlag);
		// User moved.
		log.debug("USER MOVED. THREAD AWAKE");
		((turn == Turn.black) ? blackPlayer : whitePlayer).setPhase(Player.Phase.WAIT);
	}
	
	public void startGame() {
		thread = new Thread(this);
		cTimer = new ChessTimer(thread); 
		thread.start();
	}
	
	public long getSessionKey() {
		return thread.getId();
	}
	
	public Thread getThread() {
		return thread;
	}
	
	public Player[] getUsers() {
		return new Player[]{whitePlayer, blackPlayer};
	}
	
	public String getTurn() {
		return turn.toString();
	}
	
	private void waitNext() throws InterruptedException {
//		try {
			synchronized(thread) {
				thread.wait();
			}
//		} catch (InterruptedException ie) {
//			throw new InterruptedException();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
	}
	
	private void waitNextWithFlag(boolean flag) throws InterruptedException {
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
		JSONObject json = chessBoard.select(player, tile);
		json.put("sessionKey", getSessionKey());
		json.put("userId", player.getId());
		return json.toJSONString();
	}
	
	/*  GameCoreController에서 호출. 유저가 체스말을 움직이면 실행될 메서드.
	 * 
	 *  @Param
	 *  Player player : 움직이는 사람
	 *  String srcTile : 움직일 말의 위치
	 *  String destTile : 목적지
	 * 
	 *  @Return
	 *  JSON : 
	 *    {"type" : "MOVE_SUCCESS || MOVE_FAILED",
	 *     "error": : error code }
	 * */
	public String userMoveTile(Player player, String srcTile, String destTile) {
		synchronized(thread) {
			awakeThread(moveFlag);
			thread.notify();
		}
		JSONObject json = chessBoard.move(player, srcTile, destTile);
		json.put("sessionKey", getSessionKey());
		json.put("userId", player.getId());
		return json.toJSONString();
	}
	
	/*  GameCoreController에서 호출. 유저가 자신의 턴에 항복 선언
	 * 
	 *  @Param
	 *  Player player : 항복을 선언한 유저
	 *  
	 *  @Return
	 *  JSON :
	 *    {"type" : "SURRENDER_ACCEPT" || "SURRENDER_FAILED",
	 *     "error" : error_code }
	 */
	public String userSurrender(Player player) {
		//return chessBoard.surrender(player).toJSONString();
		JSONObject json = new JSONObject();
		json.put("type", "SURRENDER_ACCEPT");
		return json.toJSONString();
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
	
	private void info(String message) {
		log.info(String.format("[%d] %s", getSessionKey(), message));
	}
	
	public void endGame() {
		thread.interrupt();
	}
	
	public void turnOver() {
		turnOverFlag = true;
		thread.interrupt();
	}
} 
