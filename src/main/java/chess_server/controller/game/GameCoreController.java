package chess_server.controller.game;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import chess_server.common.common.CommandMap;
import chess_server.common.core.GCMSender;
import chess_server.common.core.GameCoreManager;
import chess_server.common.core.GameThread;
import chess_server.common.core.Player;

@Controller
public class GameCoreController {
	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value="/game/command.cao", method=RequestMethod.GET)
	public ModelAndView getCommand(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("game/commandResult");
		
		if (commandMap.isEmpty() == false) {
			Map arg = commandMap.getMap();
			String type = (String) arg.get("type");
			String value = (String) arg.get("value");
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("type", "COMMAND_FAILED");
			mv.addObject("json", jsonObj.toJSONString());
			
			if (!type.equals("COMMAND"))
				return mv;
			
			if (value.equals("select"))
				return getTiles(commandMap);
			else if (value.equals("move"))
				return move(commandMap);
			else
				return mv;
		}
		
		return mv;
	}
	
	@RequestMapping(value="/game/users.cao", method=RequestMethod.GET)
	public ModelAndView getUsers(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("game/getUsers");
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		if(commandMap.isEmpty() == false){
			Map arg = commandMap.getMap();
	        String sessionId = (String) arg.get("sessionId");
	        
	        GameThread gt = gcm.getGame(Long.parseLong(sessionId));
	        
	        Player[] users = gt.getUsers();
	        
	        mv.addObject("users", String.format("%s(%s) VS %s(%s)", users[0].getNickName(), users[0].getId(), users[1].getNickName(), users[1].getId()));
		}
		
		return mv;
	}
	
	@RequestMapping(value="/game/tiles.cao", method=RequestMethod.GET)
	public ModelAndView getTiles(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("game/getTiles");
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		
		if (commandMap.isEmpty() == false) {
			Map arg = commandMap.getMap();
			String type = (String) arg.get("type");
			String value = (String) arg.get("value");
			
			if (!type.equals("COMMAND") || !value.equals("select")) {
				return mv;
			}
			
			long sessionId = Long.parseLong((String)arg.get("sessionId"));
			String userId = (String) arg.get("userId");
			String tile = (String) arg.get("tile");
			
			GameThread gt = gcm.getGame(sessionId);
			String json = gt.availableTiles(tile, gt.getPlayerById(userId));
			
			mv.addObject("json", json);
		}
		
		return mv;
	}
	
	@RequestMapping(value="/game/move.cao", method=RequestMethod.GET)
	public ModelAndView move(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("game/move");
		
		GameCoreManager gcm = GameCoreManager.getInstance();
		
		if (commandMap.isEmpty() == false) {
			Map arg = commandMap.getMap();
			String type = (String) arg.get("type");
			String value = (String) arg.get("value");
			
			if (!type.equals("COMMAND") || !value.equals("move")) {
				return mv;
			}
			
			long sessionId = Long.parseLong((String) arg.get("sessionId"));
			String userId = (String) arg.get("userId");
			String srcTile = (String) arg.get("srcTile");
			String destTile = (String) arg.get("destTile");
			
			GameThread gt = gcm.getGame(sessionId);
			
			String json = gt.userMoveTile(srcTile, destTile);
			mv.addObject("json", json);
		}
		
		return mv;
	}
	
	@RequestMapping(value="/gcmTest.cao", method=RequestMethod.POST)
	public ModelAndView gcmTest(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("gcm");
		
		if (commandMap.isEmpty() == false) {
			String data = (String) commandMap.getMap().get("data");
			String token = (String) commandMap.getMap().get("token");
			log.debug(String.format("GCM request arrive: token: %s, data: %s", token, data));
			String tmp = GCMSender.getInstance().sendTest(token, data);
			mv.addObject("json", tmp);
		}
		
		return mv;
	}
}
