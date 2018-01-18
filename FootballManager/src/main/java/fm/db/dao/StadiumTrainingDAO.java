package fm.db.dao;

import fm.data.StadiumTraining;

public interface StadiumTrainingDAO extends GenericDAO<StadiumTraining> {
	public StadiumTraining create(long idTeam, int xpMultiplier, int cost);
	public void update(long idStadium, int xpMultiplier, int cost);
	public void update(StadiumTraining stadium);
}
