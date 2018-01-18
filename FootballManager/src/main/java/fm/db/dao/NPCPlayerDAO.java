package fm.db.dao;

import java.util.List;

import fm.data.NPCPlayer;

public interface NPCPlayerDAO extends GenericDAO<NPCPlayer> 
{
	public NPCPlayer create(String name, int cost, int stamina, int strength, int agility, int speed, int endurance, int health, int fatigue, String position, boolean transfer);
	public List<NPCPlayer> get(String name, String minCost, String maxCost);
	public List<NPCPlayer> getForTeam(long idTeam);
	public List<NPCPlayer> listTeamless();
	public void updateNpcIdTeam(int idTeam, int idNPC);
	public void sellNPC(long idNPC, int cost);
	public NPCPlayer findByName(String name);
	public void updateNpcPlayerStats(int idNPC, int stamina, int strength, int agility, int speed, int endurance, int health);
	public void squadInOut(int idNPC, int squadInOut);
	public List<NPCPlayer> listSquad(long idTeam);
	public List<NPCPlayer> getWholeTeam(long idTeam);
}
