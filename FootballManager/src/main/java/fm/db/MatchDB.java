package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.Match;
import fm.db.dao.MatchDAO;

public class MatchDB implements MatchDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	private class MatchMapper implements RowMapper<Match> {
		public Match mapRow(ResultSet rs, int rowNum) throws SQLException {
			String datePlayed = rs.getDate("datePlayed").toString() + "T" + rs.getTime("datePlayed").toString();

			Match match = new Match(rs.getLong("idGame"), rs.getLong("idLeague"), rs.getLong("queueNumber"),
					rs.getLong("idTeam1"), rs.getLong("idTeam2"), rs.getString("score"),
					LocalDateTime.parse(datePlayed), rs.getLong("ticketPrice"));

			return match;
		}
	}

	private class QueueMapper implements RowMapper<Integer> {
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Integer queueNumber = new Integer((int) rs.getLong("queueNumber"));

			return queueNumber;
		}
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public Match findById(long id) {
		String sql = "select * from Game where idGame = ?";
		Match match = jdbcTemplate.queryForObject(sql, new Object[] { id }, new MatchMapper());
		return match;
	}

	@Override
	public void delete(long id) {
		String sql = "delete from Game where idGame = ?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public List<Match> listLeagueMatches(long idLeague) {
		String sql = "select * from Game where idLeague = ?";
		List<Match> matchList = jdbcTemplate.query(sql, new MatchMapper());
		return matchList;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idGame) from Game";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long) x;

		return max + 1;
	}

	@Override
	public void create(long idLeague, long queueNumber, long idTeam1, long idTeam2, long ticketPrice,
			LocalDateTime datePlayed) {
		long idGame = getNewId();
		String sql = "insert into Game (idGame, idLeague, queueNumber, idTeam1, idTeam2, score, datePlayed, ticketPrice) values (?, ?, ?, ?, ?, ' ', to_date(?, 'YYYY-MM-DD HH24:MI'), ?)";
		jdbcTemplate.update(sql, new Object[] { idGame, idLeague, queueNumber, idTeam1, idTeam2,
				datePlayed.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), ticketPrice });
	}

	@Override
	public List<Match> list() {
		String sql = "select * from Game";
		List<Match> matches = jdbcTemplate.query(sql, new MatchMapper());
		return matches;
	}

	@Override
	public List<Match> getListOfMatchesFromLeague(long idLeague) {
		String sql = "select * from Game where idLeague = ?";
		List<Match> listMatchesForLeague = jdbcTemplate.query(sql, new Object[] { idLeague }, new MatchMapper());
		return listMatchesForLeague;
	}

	@Override
	public List<Integer> getListOfQueuesFromLeague(long idLeague) {
		String sql = "select DISTINCT queueNumber from Game where idLeague = ?";
		List<Integer> listMatchesForLeague = jdbcTemplate.query(sql, new Object[] { idLeague }, new QueueMapper());
		return listMatchesForLeague;
	}

	@Override
	public List<Match> getListOfQueueMatchesFromLeague(long queueNumber, long idLeague) {
		String sql = "select * from Game where queueNumber = ? AND idLeague = ?";
		List<Match> listMatchesForLeague = jdbcTemplate.query(sql, new Object[] {queueNumber, idLeague }, new MatchMapper());
		return listMatchesForLeague;
	}

	@Override
	public List<Match> listIncoming(long playerId) {
		String sql = "select * from player natural join team join game on team.idteam = game.idteam1 where idplayer = ? and datePlayed > sysdate and datePlayed < sysdate + 7";
		List<Match> list = jdbcTemplate.query(sql, new Object[] { playerId }, new MatchMapper());
		sql = "select * from player natural join team join game on team.idteam = game.idteam2 where idplayer = ? and datePlayed > sysdate and datePlayed < sysdate + 7";
		list.addAll(jdbcTemplate.query(sql, new Object[] { playerId }, new MatchMapper()));
		list = list.stream().distinct().sorted((m1, m2) -> m1.getDatePlayed().compareTo(m2.getDatePlayed())).collect(Collectors.toList());
		return list;
	}
	
	@Override
	public List<Match> listIncomingForTeam(long teamId) {
		String sql = "select * from team join game on team.idteam = game.idteam1 where idteam = ? and datePlayed > sysdate and datePlayed < sysdate + 7";
		List<Match> list = jdbcTemplate.query(sql, new Object[] { teamId }, new MatchMapper());
		sql = "select * from team join game on team.idteam = game.idteam2 where idTeam = ? and datePlayed > sysdate and datePlayed < sysdate + 7";
		list.addAll(jdbcTemplate.query(sql, new Object[] { teamId }, new MatchMapper()));
		list = list.stream().sorted((m1, m2) -> m1.getDatePlayed().compareTo(m2.getDatePlayed())).collect(Collectors.toList());
		return list;
	}
	
	@Override
	public List<Match> listAllIncoming() {
		String sql = "select * from game where sysdate < datePlayed";
		return jdbcTemplate.query(sql, new Object[] { }, new MatchMapper());
	}

	@Override
	public void updatePlayNow(long idGame, String score) {
		String sql = "update Game set datePlayed = to_date(?, 'YYYY-MM-DD HH24:MI'), score = ? where idGame = ?";
		jdbcTemplate.update(sql, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), score, idGame);
	}

}
