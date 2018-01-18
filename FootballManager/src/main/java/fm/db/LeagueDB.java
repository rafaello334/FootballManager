package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.League;
import fm.db.dao.LeagueDAO;

public class LeagueDB implements LeagueDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

	private class LeagueMapper implements RowMapper<League> {
		public League mapRow(ResultSet rs, int rowNum) throws SQLException {
			League league = new League(rs.getLong("idLeague"), rs.getString("name"), rs.getLong("requiredPrestige"), rs.getLong("requiredNumberOfTeams"), rs.getLong("actualNumberOfTeams"));

			return league;
		}
	}

	@Override
	public boolean checkUniqueness(String name) {
		String sql = "select * from League where name = ?";
		return !jdbcTemplate.queryForRowSet(sql, name).next();
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	@Override
	public League findById(long id) {
		String sql = "select * from League where idLeague = ?";
		League league = jdbcTemplate.queryForObject(sql, new Object[] { id }, new LeagueMapper());
		return league;
	}

	@Override
	public void delete(long id) {
		String sql = "delete from League where idLeague = ?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public List<League> list() {
		String sql = "select * from League where idLeague > 0 order by requiredPrestige";
		List<League> leagueList = jdbcTemplate.query(sql, new LeagueMapper());
		return leagueList;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idLeague) from League";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long) x;

		return max + 1;
	}

	@Override
	public League create(String name, long requiredPrestige, long requiredNumberOfTeams) {
		long id = getNewId();
		String sql = "insert into League (idLeague, name, requiredPrestige, requiredNumberOfTeams, actualNumberOfTeams) values (?, ?, ?, ?, 0)";
		jdbcTemplate.update(sql, new Object[] { id, name, requiredPrestige, requiredNumberOfTeams});
		sql = "insert into Chat(idChat, idLeague) values (?, ?)";
		jdbcTemplate.update(sql, new Object[] { id, id });
		return new League(id, name, requiredPrestige, requiredNumberOfTeams, 0);
	}

	@Override
	public List<League> listAvailable(long teamPrestige) {
		String sql = "select * from League where requiredPrestige <= ? and idLeague > 0 order by requiredPrestige";
		List<League> leagueList = jdbcTemplate.query(sql, new Object[] {teamPrestige}, new LeagueMapper());
		return leagueList;
	}
	
	@Override
	public List<League> listLeaguesForTeam(long idTeam) {
		String sql = "select * from League natural join Team_League where idTeam = ? and idLeague > 0";
		List<League> listLeaguesForTeam = jdbcTemplate.query(sql, new Object[] {idTeam}, new LeagueMapper());
		return listLeaguesForTeam;
	}
	
	@Override
	public Integer actualNumberOfTeamsInLeague() {
		String sql = "select * from league natural join team_league";
		List<League> listTeamsInLeague = jdbcTemplate.query(sql, new LeagueMapper());
		return listTeamsInLeague.size();
	}

	@Override
	public void joinTeamToLeague(long idTeam, long idLeague) {
		String sql = "insert into Team_League (idTeam, idLeague) values (?, ?)";
		jdbcTemplate.update(sql, new Object[] { idTeam, idLeague});	
	}

	@Override
	public void updateActualNumberOfTeams(long idLeague) {
		League league = findById(idLeague);
		String sql = "update  League set name = ?, requiredPrestige = ?, requiredNumberOfTeams = ?, actualNumberOfTeams = ? where idLeague = ?";
		jdbcTemplate.update(sql, league.getName(), league.getRequiredPrestige(), league.getRequiredNumberOfTeams(), league.getActualNumberOfTeams() + 1, idLeague);
	}

}
