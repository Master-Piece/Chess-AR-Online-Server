package chess_server.controller.manager;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import chess_server.common.common.CommandMap;
import chess_server.common.core.GameCoreManager;
import chess_server.common.core.GameThread;
import chess_server.common.core.MatchMaker;
import chess_server.common.core.Player;

@Controller
public class ManagerController {
	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value="/manager/managing.cao")
	public ModelAndView getManager(CommandMap commandMap) {
		ModelAndView mv = new ModelAndView("manage/manager");
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		MatchMaker mm = MatchMaker.getInstance();
		
		Long[] gameList = gcm.getGameList();
		Player[] queue = mm.getPlayersInQueue();
		mv.addObject("gameList", gameList);
		mv.addObject("queue", queue);
		mv.addObject("mmIsRunning", mm.isMMRunning());
		
		return mv;
	}
	
	@RequestMapping(value="manager/getGameInfo.cao", method=RequestMethod.POST)
	public void getGameInfo(CommandMap commandMap, HttpServletResponse response) {
		String ret;
		
		long sessionKey = Long.parseLong((String) commandMap.getMap().get("sessionKey"));
		
		GameThread gt = GameCoreManager.getInstance().getGame(sessionKey);
		Player[] users = gt.getPlayers();
		
		JSONObject json = new JSONObject();
		json.put("sessionKey", sessionKey);
		json.put("turn", gt.getTurn());
		JSONObject white = new JSONObject();
		JSONObject black = new JSONObject();
		int whiteIndex = users[0].getColor().equals("white") ? 0 : 1;
		int blackIndex = users[0].getColor().equals("white") ? 1 : 0;
		white.put("nick", users[whiteIndex].getNickName());
		white.put("id", users[whiteIndex].getId());
		white.put("phase", users[whiteIndex].getPhase());
		black.put("nick", users[blackIndex].getNickName());
		black.put("id", users[blackIndex].getId());
		black.put("phase", users[blackIndex].getPhase());
		
		json.put("white", white);
		json.put("black", black);
		
		try {
			response.getWriter().print(json.toJSONString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="manager/closeSession.cao", method=RequestMethod.POST)
	public void closeGame(CommandMap commandMap, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		long sessionKey = Long.parseLong((String) commandMap.get("sessionKey"));
		gcm.forceEndGame(sessionKey);
		
		
		json.put("status", "Exit");
		json.put("sessionKey", sessionKey);
		
		try {
			response.getWriter().print(json.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="manager/createGame.cao", method=RequestMethod.POST)
	public void createNewGame(CommandMap commandMap, HttpServletResponse response) {
		String wNick = (String) commandMap.get("wnick");
		String wGcmToken = (String) commandMap.get("wgcmToken");
		String bNick = (String) commandMap.get("bnick");
		String bGcmToken = (String) commandMap.get("bgcmToken");
		
		Player whitePlayer = new Player(wGcmToken, wNick);
		whitePlayer.setColor(Player.WHITE);
		Player blackPlayer = new Player(bGcmToken, bNick);
		blackPlayer.setColor(Player.BLACK);
		
		long threadId = GameCoreManager.getInstance().startGame(whitePlayer, blackPlayer);
		
		try {
			response.getWriter().print(threadId);
		} catch (Exception e) {
			
		}
	}
	
	@RequestMapping(value="manager/refreshAll.cao")
	public void refresh(CommandMap commandMap, HttpServletResponse response) {
		JSONObject json = new JSONObject();
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		MatchMaker mm = MatchMaker.getInstance();
		
		Long[] gameList = gcm.getGameList();
		JSONArray gameListJson = new JSONArray();
		for (Long game : gameList) {
			gameListJson.add(game.longValue());
		}
		
		Player[] queue = mm.getPlayersInQueue();
		JSONArray queueJson = new JSONArray();
		for (Player player : queue) {
			queueJson.add(String.format("%s(%s)", player.getNickName(), player.getId()));
		}
		json.put("gameList", gameListJson);
		json.put("queue", queueJson);
		
		try {
			response.getWriter().print(json.toJSONString());
		} catch (Exception e) {
			
		}
	}
	
	@RequestMapping(value="manager/enqueueUser.cao", method=RequestMethod.POST)
	public void enqueueUser(CommandMap commandMap, HttpServletResponse response) {
		String gcmToken = (String) commandMap.get("token");
		String nick = (String) commandMap.get("nick");
		
		Player player = new Player(gcmToken, nick);
		MatchMaker mm = MatchMaker.getInstance();
		
		mm.enqueue(player);
	}
	
	@RequestMapping(value="manager/offMM.cao")
	public void offMM(CommandMap commandMap) {
		MatchMaker mm = MatchMaker.getInstance();
		if (mm.isMMRunning())
			mm.stopRunning();
	}
	
	@RequestMapping(value="manager/onMM.cao")
	public void onMM(CommandMap commandMap) {
		MatchMaker mm = MatchMaker.getInstance();
		if (!mm.isMMRunning())
			mm.resumeRunning();
	}
	
	@RequestMapping(value="manager/turnOver.cao", method=RequestMethod.POST)
	public void turnOver(CommandMap commandMap, HttpServletResponse response) {
		long sessionKey = Long.parseLong((String) commandMap.get("sessionKey"));
		String playerColor = (String) commandMap.get("player");
		
		JSONObject json = new JSONObject();
		
		GameThread gt = GameCoreManager.getInstance().getGame(sessionKey);
		if (gt.getTurn().equals(playerColor)) {
			gt.turnOver();
			json.put("status", "SUCCESS");
		}
		else {
			json.put("status", "FAILED");
		}
		
		try {
			response.getWriter().print(json.toJSONString());
		} catch (Exception e) {
			
		}
	}
}
