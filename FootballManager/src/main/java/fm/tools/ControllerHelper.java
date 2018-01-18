package fm.tools;

import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import fm.data.Player;
import fm.data.Player.PlayerType;
import fm.db.PlayerDB;

public class ControllerHelper {
	
	private static PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	
	public static ModelAndView prepareModelAndView(String path, String login) {
		return prepareModelAndView(path, login, null);
	}
	
	public static ModelAndView prepareModelAndView(String path, String login, Map<String, ?> args) {
		ModelAndView model = new ModelAndView(path);
		
		Player player = pdb.findByLogin(login);
		if (player.getType() == PlayerType.ADMIN) {
			model.addObject("adminLogged", true);
			model.addObject("modLogged", true);
		} else if(player.getType() == PlayerType.MOD) {
			model.addObject("modLogged", true);
		}
		
		model.addObject("login", login);
		
		if(args != null) {
			model.addAllObjects(args);
		}
		
		return model;
	}
}
