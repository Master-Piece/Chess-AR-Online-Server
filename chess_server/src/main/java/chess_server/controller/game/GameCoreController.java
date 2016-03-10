package chess_server.controller.game;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import chess_server.common.common.CommandMap;
import chess_server.common.core.GameCoreManager;
import chess_server.common.core.GameThread;

@Controller
public class GameCoreController {
	Logger log = Logger.getLogger(this.getClass());
	
	@RequestMapping(value="/game/command.cao", method=RequestMethod.GET)
	public ModelAndView getCommand(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("");
		
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
	        
	        String[] users = gt.getUsers();
	        
	        mv.addObject("users", String.format("%s VS %s", users[0], users[1]));
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
			
			if (!type.equals("COMMAND") || !value.equals("value")) {
				return mv;
			}
			
			long sessionId = Long.parseLong((String)arg.get("sessionId"));
			String userId = (String) arg.get("userId");
			String tile = (String) arg.get("tile");
			
			GameThread gt = gcm.getGame(sessionId);
			String json = gt.availableTiles(userId, tile);
			
			mv.addObject("json", json);
		}
		
		return mv;
	}
}
