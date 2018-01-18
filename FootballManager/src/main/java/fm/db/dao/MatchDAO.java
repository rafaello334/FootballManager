package fm.db.dao;

import java.time.LocalDateTime;
import java.util.List;

import fm.data.Match;

public interface MatchDAO extends GenericDAO<Match> {

	public List<Match> listLeagueMatches(long idLeague);
	public void create(long idLeague, long queueNumber, long idTeam1, long idTeam2, long ticketPrice, LocalDateTime datePlayed);
	public List<Match> getListOfMatchesFromLeague(long idLeague);
	public List<Integer> getListOfQueuesFromLeague(long idLeague);
	public List<Match> getListOfQueueMatchesFromLeague(long queueNumber, long idLeague);
	public List<Match> listIncoming(long playerId);
	public List<Match> listIncomingForTeam(long teamId);
	public List<Match> listAllIncoming();
	public void updatePlayNow(long idGame, String score);
}