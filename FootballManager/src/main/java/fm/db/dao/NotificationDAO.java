package fm.db.dao;

import java.time.LocalDateTime;
import java.util.List;

import fm.data.Notification;
import fm.data.Notification.NotificationType;
import fm.data.Player;

public interface NotificationDAO extends GenericDAO<Notification> {
	public Notification create(String text, LocalDateTime startDate, LocalDateTime finishDate, NotificationType type);
	public Notification create(String text, LocalDateTime startDate, LocalDateTime finishDate, Player player);
	public List<Notification> listCurrent();
	public List<Notification> listExpired();
}