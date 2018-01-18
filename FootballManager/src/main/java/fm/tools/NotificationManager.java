package fm.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import fm.data.Notification;
import fm.data.Notification.NotificationType;
import fm.data.Player;
import fm.data.Player.PlayerType;
import fm.db.NotificationDB;
import lombok.Getter;

@Component("NotificationManager")
public class NotificationManager {
	
	@Getter private List<Notification> currentNotifications;
	private NotificationDB db = (NotificationDB)(new ClassPathXmlApplicationContext("beans.xml").getBean("NotificationDB"));
	private static final long refreshMillis = TimeUnit.MINUTES.toMillis(2);
	
	@PostConstruct
	private void init() {
		NotificationManager nm = this;
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				nm.updateCurrentNotifications();
			}
		}, 0, refreshMillis);
	}
	
	private void updateCurrentNotifications() {
		currentNotifications = db.listCurrent();
	}
	
	public List<Notification> getNotificationsForPlayer(Player player) {
		List<Notification> ret = new ArrayList<>();
		
		PlayerType pt = player.getType();
		for(Notification notif : currentNotifications) {
			NotificationType nt = notif.getType();
			if((nt == NotificationType.SINGLE && notif.getIdPlayer() == player.getIdPlayer()) ||
					nt == NotificationType.EVERYONE ||
					(nt == NotificationType.ADMINS && pt == PlayerType.ADMIN) ||
					(nt == NotificationType.MODS && pt == PlayerType.MOD) ||
					(nt == NotificationType.PLAYERS && pt == PlayerType.PLAYER) ||
					(nt == NotificationType.NONADMINS && (pt == PlayerType.MOD || pt == PlayerType.PLAYER))) {
				ret.add(notif);
			}
		}
		
		return ret;
	}
}
