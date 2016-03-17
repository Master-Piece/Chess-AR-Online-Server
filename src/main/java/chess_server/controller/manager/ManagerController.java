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
		
		Player[] users = GameCoreManager.getInstance().getGame(sessionKey).getUsers();
		
		ret = String.format("[%d] %s(%s) - %s Vs %s(%s) - %s", sessionKey, users[0].getNickName(), users[0].getId(), users[0].getColor(), users[1].getNickName(), users[1].getId(), users[1].getColor());
		
		try {
			response.getWriter().print(ret);
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
		gcm.endGame(sessionKey);
		
		
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
		String wGcmToken = (String) commandMap.get("wtoken");
		String bNick = (String) commandMap.get("bnick");
		String bGcmToken = (String) commandMap.get("btoken");
		
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
}
