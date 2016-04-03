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
	private boolean gameFlag = true; // ���� ���� �Ǵ� �׺��� false
	private boolean moveFlag = true; // move�� false
	private boolean selectFlag = true; // select�� false
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
		// TODO: gcm���� ���� �˷���
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
		JSONObject json = chessBoard.select(player, tile);
		json.put("sessionKey", getSessionKey());
		json.put("userId", player.getId());
		return json.toJSONString();
	}
	
	/*  GameCoreController���� ȣ��. ������ ü������ �����̸� ����� �޼���.
	 * 
	 *  @Param
	 *  Player player : �����̴� ���
	 *  String srcTile : ������ ���� ��ġ
	 *  String destTile : ������
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
	
	/*  GameCoreController���� ȣ��. ������ �ڽ��� �Ͽ� �׺� ����
	 * 
	 *  @Param
	 *  Player player : �׺��� ������ ����
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
