package fm.data;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

public class Notification {
	public enum NotificationType {
		PLAYERS("p", "Gracze"), MODS("m", "Moderatorzy"), ADMINS("a", "Administatorzy"), NONADMINS("n", "Nieadministratorzy"), EVERYONE("e", "Wszyscy"), SINGLE("s", "Pojedynczy");

		private String txt;
		private String ltxt;

		private NotificationType(String txt, String ltxt) {
			this.txt = txt;
			this.ltxt = ltxt;
		}

		public static NotificationType typeForName(String name) {
			for (NotificationType type : NotificationType.values()) {
				if (type.txt.equalsIgnoreCase(name) || type.ltxt.equalsIgnoreCase(name))
					return type;
			}

			return null;
		}

		public String toString() {
			return txt;
		}
		
		public String toLongString() {
			return ltxt;
		}
	}
	
	@Getter @Setter private long id;
	@Getter @Setter private long idPlayer;
	@Getter @Setter private Player player;
	@Getter @Setter String text;
	@Getter @Setter LocalDateTime startDate;
	@Getter @Setter LocalDateTime finishDate;
	@Getter @Setter NotificationType type;
	
	public Notification(long id, NotificationType type, String text, LocalDateTime startDate, LocalDateTime finishDate) {
		this.id = id;
		this.type = type;
		this.text = text;
		this.startDate = startDate;
		this.finishDate = finishDate;
		this.idPlayer = -1;
	}
	
	public Notification(long id, Player player, String text, LocalDateTime startDate, LocalDateTime finishDate) {
		this(id, NotificationType.SINGLE, text, startDate, finishDate);
		this.idPlayer = player.getIdPlayer();
		this.player = player;
	}
}
