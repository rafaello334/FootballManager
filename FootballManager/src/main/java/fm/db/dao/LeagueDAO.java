package fm.db.dao;

import java.util.List;

import fm.data.League;

public interface LeagueDAO extends GenericDAO<League> {
	public boolean checkUniqueness(String name);
	public League create(String name, long requiredPrestige, long requiredNumberOfTeams);
	public List<League> listAvailable(long teamPrestige);
	public void joinTeamToLeague(long idTeam, long idLeague);
	public List<League> listLeaguesForTeam(long idTeam);
	public Integer actualNumberOfTeamsInLeague();
	public void updateActualNumberOfTeams(long idLeague);
}