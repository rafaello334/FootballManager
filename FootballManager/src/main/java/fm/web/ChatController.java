package fm.web;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.data.ChatMessage;
import fm.data.Player;
import fm.db.ChatMessageDB;
import fm.db.PlayerDB;
import fm.tools.ControllerHelper;

@Controller
public class ChatController {
	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	private ChatMessageDB cdb = (ChatMessageDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("ChatMessageDB"));
	
	@RequestMapping("/getChat")
	public ModelAndView getChat(@CookieValue(value="fm-login") String login, @RequestParam("idChat") long id) {
		ModelAndView model = ControllerHelper.prepareModelAndView("getChat", login);
		
		List<ChatMessage> list = cdb.listMessagesForChat(id);
		model.addObject("chatMessages", list);
		
		return model;
	}
	
	@PostMapping("/postChat")
	public String postChat(@RequestParam("login") String login, @RequestParam("text") String text, @RequestParam("chatId") long chatId) {
		Player p = pdb.findByLogin(login);
		cdb.create(chatId, p, text);
		
		return "redirect:/getChat?idChat=" + chatId;
	}
	
	@PostMapping("/removeChatMessage")
	public String removeMessage(@RequestParam("id") long id, @RequestParam("removed") boolean removed) {
		ChatMessage msg = cdb.findById(id);
		cdb.setRemoved(id, removed);
		
		return "redirect:/getChat?idChat=" + msg.getChatId();
	}
}
