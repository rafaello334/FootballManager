package fm.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fm.FootballManagerApplication;
import fm.data.Notification;
import fm.data.Notification.NotificationType;
import fm.data.Player;
import fm.data.Player.PlayerType;
import fm.db.NotificationDB;
import fm.db.PlayerDB;
import fm.forms.NotificationForm;
import fm.tools.ControllerHelper;
import fm.tools.NotificationManager;

@Controller
public class NotificationController {

	@Autowired private NotificationManager notifManager;
	private PlayerDB pdb = (PlayerDB) (new ClassPathXmlApplicationContext("beans.xml").getBean("PlayerDB"));
	private NotificationDB ndb = (NotificationDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("NotificationDB"));
	private Log logger = FootballManagerApplication.logger;
	
	@GetMapping("/getNotifications")
	public ModelAndView getNotifications(@CookieValue(value = "fm-login") String login) {
		Player player = pdb.findByLogin(login);
		
		List<Notification> notifications = notifManager.getNotificationsForPlayer(player);
		
		ModelAndView model = new ModelAndView("getNotifications");
		model.addObject("notifications", notifications);
		
		return model;
	}
	
	@RequestMapping("/notifications")
	public ModelAndView notifications(@CookieValue(value="fm-login") String login, HttpServletResponse response, NotificationForm notificationForm) {
		Player player = pdb.findByLogin(login);
		if(player.getType() != PlayerType.ADMIN) {
			try {
				response.sendError(403);
			} catch (IOException e) {
				logger.error(e.getStackTrace());
			};
			return null;
		}
		
		ModelAndView model = ControllerHelper.prepareModelAndView("notifications", login);
		
		model.addObject("currentNotifications", ndb.listCurrent());
		model.addObject("expiredNotifications", ndb.listExpired());
		
		return model;
	}
	
	@PostMapping("/addNotification")
	public ModelAndView addNotification(@Valid NotificationForm notificationForm, BindingResult bindingResult, @CookieValue(value="fm-login") String login, HttpServletResponse response) {
		if (bindingResult.hasErrors()) {
			ModelAndView model = notifications(login, response, notificationForm);
			return model;
		}
		
		if(notificationForm.getType() == NotificationType.SINGLE){
			Player p = pdb.findByLogin(notificationForm.getPlayer());
			
			if(p == null) {
				ModelAndView model = notifications(login, response, notificationForm);
				model.addObject("missingPlayer", true);
				return model;
			}
			
			ndb.create(notificationForm.getText(), LocalDateTime.parse(notificationForm.getStartDate(), DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse(notificationForm.getFinishDate(), DateTimeFormatter.ISO_DATE_TIME), p);
		} else {
			ndb.create(notificationForm.getText(), LocalDateTime.parse(notificationForm.getStartDate(), DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse(notificationForm.getFinishDate(), DateTimeFormatter.ISO_DATE_TIME), notificationForm.getType());
		}
		
		ModelAndView model = notifications(login, response, notificationForm);
		model.addObject("success", true);
		return model;
	}
	
	@PostMapping("/removeNotification")
	public String removeNotification(@RequestParam("id") long id) {
		ndb.delete(id);
		return "redirect:/notifications";
	}
	
	@ModelAttribute("notificationTypes")
	public List<NotificationType> getNotificationTypes() {
	    return Arrays.asList(NotificationType.values());
	}
}
