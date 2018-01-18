package fm.data;

import lombok.Getter;
import lombok.Setter;

public class Player implements Comparable<Player> {
	public enum PlayerType  {
		PLAYER("p", "player"), MOD("m", "mod"), ADMIN("a", "admin");

		private String s, l;

		private PlayerType(String s, String l) {
			this.s = s;
			this.l = l;
		}

		public static PlayerType typeForName(String name) {
			for (PlayerType type : PlayerType.values()) {
				if (type.s.equalsIgnoreCase(name) || type.l.equalsIgnoreCase(name))
					return type;
			}

			return null;
		}

		public String toString() {
			return s;
		}

		public String toStringFullName() {
			return l;
		}
	}

	@Getter
	@Setter
	private long idPlayer;
	@Getter
	@Setter
	private String login;
	@Getter
	@Setter
	private String email;
	@Getter
	@Setter
	private String password;
	@Getter
	@Setter
	private PlayerType type;
	@Getter
	@Setter
	private boolean active;
	@Getter
	@Setter
	private boolean banned;

	public Player(long idPlayer, String login, String email, String password, String type, boolean active,
			boolean banned) {
		this.idPlayer = idPlayer;
		this.login = login;
		this.email = email;
		this.password = password;
		this.type = PlayerType.typeForName(type);
		this.active = active;
		this.banned = banned;
	}

	@Override
	public int compareTo(Player o) {

		return login.compareTo(o.login);

	}

	@Override
	public String toString() {
		return String.format(
				"Player[idPlayer=%d, login='%s', password='%s', email='%s', type='%s', active='%b', banned='%b']",
				idPlayer, login, password, email, type.toString(), active, banned);
	}
}
