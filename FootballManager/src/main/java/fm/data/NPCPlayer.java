package fm.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class NPCPlayer extends NPC implements Comparable<NPCPlayer> {
	public enum Position {
		GOALKEEPER("g", "bramkarz"), DEFENCE("d", "obrońca"), MIDFIELD("m", "pomocnik"), ATTACKING("a", "napastnik");

		private String s, l;

		Position(String s, String l) {
			this.s = s;
			this.l = l;
		}

		public static Position typeForName(String name) {
			for (Position type : Position.values()) {
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

		public static List<String> listShort() {
			List<String> list = new ArrayList<>();
			for (Position type : Position.values()) {
				list.add(type.s);
			}
			return list;
		}
	};



	@Setter @Getter private int stamina;
	@Setter @Getter private int strength;
	@Setter @Getter private int agility;
	@Setter @Getter private int speed;
	@Setter @Getter private int endurance;
	@Setter @Getter private int health;
	@Setter @Getter private int fatigue;
	@Setter @Getter private int inSquad;
	@Setter @Getter private Position position;
	@Setter
	@Getter
	private boolean transfer;

	public NPCPlayer(int idNPC, String name, String type, int cost, int idTeam, int stamina, int strength, int agility,
			int speed, int endurance, int health, int fatigue, String position, int inSquad, boolean transfer) {
		super(idNPC, name, type, cost, idTeam);
		this.stamina = stamina;
		this.strength = strength;
		this.agility = agility;
		this.speed = speed;
		this.endurance = endurance;
		this.health = health;
		this.fatigue = fatigue;
		this.position = Position.typeForName(position);
		this.transfer = transfer;
		this.inSquad = inSquad;

	}

	

	@Override
	public int compareTo(NPCPlayer o) {

		int i = Integer.compare(cost, o.cost);
		if (i != 0) {
			return i;
		}
		return name.compareTo(o.name);

	}



	@Override
	public String toString() {
		return "NPCPlayer [stamina=" + stamina + ", strength=" + strength + ", agility=" + agility + ", speed=" + speed
				+ ", endurance=" + endurance + ", health=" + health + ", fatigue=" + fatigue + ", inSquad=" + inSquad
				+ ", position=" + position + ", transfer=" + transfer + ", idNPC=" + idNPC + ", name=" + name
				+ ", cost=" + cost + ", idTeam=" + idTeam + ", type=" + type + "]";
	}
}