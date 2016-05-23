package chess_server.common.core;


import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import chess_server.common.core.Player.Phase;

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
	private Player currentPlayer;
	
	private Algorithm chessBoard;
	private JSONObject turnData;
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
		turnData = new JSONObject();
	}
	
	@Override
	public void run() {
		info("game created");
		try {
			waitNext();
		} catch (InterruptedException e) {
			log.debug("start error");
		}
		info("game started.");
		while (gameFlag) {
			try {
				cTimer.startCount(GAME_COUNT);
				game();
				cTimer.reCount();
			} catch (InterruptedException e) {
				if (turnOverFlag) {
					currentPlayer.setPhase(Player.Phase.WAIT);
					turnOverFlag = false;
				}
				else {
					info("Time out!!! " + currentPlayer.getColor() + "LOSE");
					info("END SESSION");
					sender.timeoutNotice(getSessionKey(), currentPlayer.getId());
					break;
				}
			}
		}
		
		GameCoreManager.getInstance().closeSession(getSessionKey());
	}
	
	private void game() throws InterruptedException {
		if (turn == null) {
			turn = Turn.white;
			turnData.put("type", "YOUR_TURN");
		}
		else {
			turn = (turn == Turn.black) ? Turn.white : Turn.black;
		}
		currentPlayer = (turn == Turn.black) ? blackPlayer : whitePlayer;
		info("Turn Data(" + currentPlayer.getColor() + "): " + turnData.toJSONString());
		sender.noticeTurn(currentPlayer.getGcmToken(), turnData);
		
		if (chessBoard.isCheckmate()) {
			gameFlag = false;
			return;
		}
		
		currentPlayer.setPhase(Phase.SELECT);
		info(currentPlayer.getColor() + "'s Turn!");
		
		while (moveFlag) {  /* 플레이어가 움직이기 전에는 무한정 클릭 가능. 플레이어가 움직이면 moveFlag가 false가 되고  루프에서 탈출 한다. */
			info(currentPlayer.getColor() + " Select please");
			waitNext();
			info(currentPlayer.getColor() + " User selected");
		}
		info(currentPlayer.getColor() + " User moved");
		currentPlayer.setPhase(Phase.MOVE);
		
		setTurnData();
		currentPlayer.setPhase(Phase.WAIT);
		info(currentPlayer.getColor() + "Turn End");
		moveFlag = true;
	}
	
	private void setTurnData() {
		boolean checkFlag = chessBoard.isCheck(currentPlayer.getColor().toUpperCase().charAt(0));
//		boolean checkFlag = chessBoard.isCheck();
		boolean checkMateFlag = chessBoard.isCheckmate(currentPlayer.getColor().toUpperCase().charAt(0));
//		boolean checkMateFlag = chessBoard.isCheckmate();
		boolean staleMateFlag = chessBoard.isStalemate(currentPlayer.getColor().toUpperCase().charAt(0));
		boolean drawFlag = chessBoard.isDraw();
		
		turnData.clear();
		
		if (checkMateFlag) {
			turnData.put("type", "YOU_LOSE");
			turnData.put("status", "CHECKMATE");
			JSONObject move = new JSONObject();
			move.put("srcPiece", currentPlayer.getRecentMoveObject());
			move.put("destTile", currentPlayer.getRecentMoveDestnation());
			move.put("targetPiece", currentPlayer.getRecentMoveTarget());
			turnData.put("move", move);
			sender.winnerNotice(getSessionKey(), currentPlayer.getId(), "CHECKMATE");
		}
		else if (staleMateFlag) {
			turnData.put("type", "CHECKMATE");
			JSONObject move = new JSONObject();
			move.put("srcPiece", currentPlayer.getRecentMoveObject());
			move.put("destTile", currentPlayer.getRecentMoveDestnation());
			move.put("targetPiece", currentPlayer.getRecentMoveTarget());
			turnData.put("move", move);
//			sender.winnerNotice(getSessionKey(), currentPlayer.getGcmToken(), "CHECKMATE");
			sender.stalemateNotice(getSessionKey(), currentPlayer.getId());
		}
		else {
			turnData.put("type", "YOUR_TURN");
			JSONObject move = new JSONObject();
			move.put("srcPiece", currentPlayer.getRecentMoveObject());
			move.put("destTile", currentPlayer.getRecentMoveDestnation());
			move.put("targetPiece", currentPlayer.getRecentMoveTarget());
			info("move in setTurnData: " + move.toJSONString());
			turnData.put("move", move);
			turnData.put("check", checkFlag);
		}
	}

	public void createGame() {
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
	
	public Player[] getPlayers() {
		return new Player[]{whitePlayer, blackPlayer};
	}
	
	public String getTurn() {
		return turn.toString();
	}
	
	private void waitNext() throws InterruptedException {
		synchronized(thread) {
			thread.wait();
		}
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
		JSONObject json = chessBoard.select(player, tile);
		json.put("sessionKey", getSessionKey());
		json.put("userId", player.getId());
		
		synchronized(thread) {
			awakeThread(selectFlag);
			thread.notify();
		}
		
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
		JSONObject json = chessBoard.move(player, srcTile, destTile);
		json.put("sessionKey", getSessionKey());
		json.put("userId", player.getId());
		
		if (((String)json.get("type")).equals("MOVE_FAILED")) { // if MOVE_FAILED, don't change state.
			return json.toJSONString();
		}
		
		JSONObject move = (JSONObject) json.get("move");
		info("move: " + move.toJSONString());
		currentPlayer.setRecentMove((String) move.get("srcPiece"), (String) move.get("destTile"), (String) move.get("targetPiece"));
		
		synchronized(thread) {
			moveFlag = false;
			awakeThread(selectFlag);
			thread.notify();
		}
		
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
	
	public void startGame() {
		synchronized(thread) {
			thread.notify();
		}
	}
	
	public boolean isMyTurn(String userId) {
		return currentPlayer.getId().equals(userId);
	}
} 
