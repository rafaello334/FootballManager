package fm.data;

import lombok.Getter;
import lombok.Setter;

public class NPC {
	@Setter
	@Getter
	protected int idNPC;

	@Setter
	@Getter
	protected String name;

	@Setter
	@Getter
	protected int cost;

	@Setter
	@Getter
	protected int idTeam;

	public enum NPCType {

		TRAINER("t", "trainer"), PLAYER("p", "player");

		private String s, l;

		NPCType(String s, String l) {
			this.s = s;
			this.l = l;
		}

		public static NPCType typeForName(String name) {
			for (NPCType type : NPCType.values()) {
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

	};

	@Getter
	@Setter
	protected NPCType type;

	public NPC(int idNPC, String name, String type, int cost, int idTeam) {
		this.idNPC = idNPC;
		this.name = name;
		this.type = NPCType.typeForName(type);
		this.cost = cost;
		this.idTeam = idTeam;
	}

	@Override
	public String toString() {
		return String.format("NPC[idNPC = %d, name = '%s', type = '%s', cost = %d, idTeam = %d]", idNPC, name,
				type.toString(), cost, idTeam);
	}

}
