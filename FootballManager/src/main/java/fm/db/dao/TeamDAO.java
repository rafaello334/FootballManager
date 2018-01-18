package fm.db.dao;

import java.time.LocalDateTime;
import java.util.List;

import fm.data.LastTraining;
import fm.data.Player;
import fm.data.Team;

public interface TeamDAO extends GenericDAO<Team> {
	public Team create(long idPlayer,String name, int renown, int budget);
	public void update(long idTeam, long idPlayer, String name, int renown, int budget);
	public void update(Team team);
	public Team findByName(String name);
	public boolean checkUniqueness(String name);
	public List<Team> findTeamsByPlayerId(long idPlayer);
	public List<Team> getListOfTeamsFromLeague(long idLeague);
	public LastTraining createTraining(long idTeam);
	public void updateTraining(long idTeam, LocalDateTime now);
	public LastTraining trainingDate(long idTeam);
	public void update(long idTeam, int bugdet);
	public void assignPenalty(Player player, int amount);
}
