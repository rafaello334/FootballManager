package fm.db.dao;

import java.util.List;

import fm.data.NPCTrainer;

public interface NPCTrainerDAO extends GenericDAO<NPCTrainer> {
		
	public NPCTrainer create(String name, int cost, int influence, String trainerType);
	public List<NPCTrainer> get(String name, String minCost, String maxCost);
	public List<NPCTrainer> getForTeam(long idTeam);
	public List<NPCTrainer> listTeamless();
	public void updateNpcIdTeam(int idTeam, int idNPC);
	public int highestInfluence(long idTeam, String trainerType);
	
}
