package fm.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class NPCTrainer extends NPC implements Comparable<NPCTrainer>{
	
	public enum TrainerType{
		COACH_ASSISTANT("c", "asystent trenera"),
		MEDIC("m", "lekarz"),
		PHYSIOTHERAPIST("p", "fizjoterapeuta"),
		MASSEUR("n", "masażysta"),
		PHYSICAL_PREPARATION_COACH("r", "trener przygotowania fizycznego");
		
		private String s,l;
		
		TrainerType(String s, String l)
		{
			this.s=s;
			this.l=l;
		}
		
		public static TrainerType typeForName(String name) {
			for (TrainerType type : TrainerType.values()) {
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
			for (TrainerType type : TrainerType.values()) {
				list.add(type.s);
			}
			return list;
		}
	};
	
	@Setter
	@Getter
	private int influence;
	
	@Setter
	@Getter
	private TrainerType trainerType;
	
	public NPCTrainer(int idNPC, String name, String type, int cost, int idTeam, int influence, String trainerType)
	{
		super(idNPC, name, type, cost, idTeam);
		this.influence = influence;
		this.trainerType = TrainerType.typeForName(trainerType);
	}
	
	@Override
	public String toString()
	{
		return String.format(
							"NPCTrainer[idNPC = %d, name = '%s', type = '%s', cost = %d, idTeam = %d, influence = %d, trainerType = '%s']",
							idNPC, name, type.toString(), cost, idTeam, influence, trainerType.toStringFullName()
							);
	}

	@Override
	public int compareTo(NPCTrainer o) {
		int i = Integer.compare(cost, o.cost);
		if (i != 0) {
			return i;
		}
		return name.compareTo(o.name);
	}

}
