package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.LastTraining;
import fm.data.Player;
import fm.data.Team;
import fm.db.dao.TeamDAO;

public class TeamDB implements TeamDAO{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	private class TeamMapper implements RowMapper<Team>{
		public Team mapRow(ResultSet rs, int rowNum) throws SQLException {
			Team team = new Team(rs.getLong("idTeam"), rs.getLong("idPlayer"), rs.getString("name"), 
					rs.getInt("prestige"), rs.getInt("budget"));
			
			return team;
		}
	}
	
	private class LastTrainingMapper implements RowMapper<LastTraining> {
		public LastTraining mapRow(ResultSet rs, int rowNum) throws SQLException {
			String date = rs.getDate("trainingDate").toString() + "T" + rs.getTime("trainingDate").toString();
			LastTraining train = new LastTraining(rs.getLong("idTeam"), LocalDateTime.parse(date));
			return train;
		}
	}

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Team findById(long id) {
		String sql = "select * from Team where idTeam = ?";
		Team team = jdbcTemplate.queryForObject(sql, new Object[] { id }, new TeamMapper());
		return team;
	}

	@Override
	public void delete(long id) {
		String sql = "delete from Team where idTeam = ?";
		jdbcTemplate.update(sql, id);
		
	}

	@Override
	public List<Team> list() {
		String sql = "select * from Team";
		List<Team> teams = jdbcTemplate.query(sql, new TeamMapper());
		return teams;
	}

	@Override
	public Team create(long idPlayer, String name, int prestige, int budget) {
		String sql = "insert into Team (idTeam, idPlayer, name, prestige, budget) values (?, ?, ?, ?, ?)";
		long idTeam = getNewId();
		jdbcTemplate.update(sql, new Object[] { idTeam, idPlayer, name, prestige, budget });
		return new Team(idTeam, idPlayer, name, prestige, budget);
	}

	@Override
	public void update(long idTeam, long idPlayer,String name, int prestige, int budget) {
		String sql = "update Team set idPlayer = ?, name = ?, prestige = ?, budget = ? where idTeam = ?";
		jdbcTemplate.update(sql, idPlayer, name, prestige, budget, idTeam);
	}
	
	@Override
	public void update(long idTeam, int bugdet)
	{
		String sql = "update Team set budget = ? where idTeam = ?";
		jdbcTemplate.update(sql, new Object[] {bugdet, idTeam});
	}

	@Override
	public void update(Team team) {
		update(team.getIdTeam(), team.getIdPlayer(), team.getName(), team.getPrestige(), team.getBudget());
	}

	@Override
	public Team findByName(String name) {
		String sql = "select * from Team where name = ?";
		if(!jdbcTemplate.queryForRowSet(sql, name).next()) {
			return null;
		}
		Team team = jdbcTemplate.queryForObject(sql, new Object[] { name }, new TeamMapper());
		return team;
	}
	
	@Override
	public long getNewId() {
		String sql = "select max(idTeam) from team";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long)x;
		
		return max + 1;
	}

	@Override
	public boolean checkUniqueness(String name) {
		String sql = "select * from Team where name = ?";
		return !jdbcTemplate.queryForRowSet(sql, name).next();
	}

	@Override
	public List<Team> findTeamsByPlayerId(long idPlayer) {
		String sql = "select * from Team where idPlayer = ? order by name";
		List<Team> teams = jdbcTemplate.query(sql, new Object[] {idPlayer}, new TeamMapper());
		return teams;
	}
	
	@Override
	public List<Team> getListOfTeamsFromLeague(long idLeague) {
		String sql = "select * from Team natural join Team_League where idLeague = ?";
		List<Team> listTeamsForLeague = jdbcTemplate.query(sql, new Object[] {idLeague},new TeamMapper());
		return listTeamsForLeague;
	}
	
	@Override
	public LastTraining createTraining(long idTeam) {
		String sql = "insert into LastTraining (idTeam, trainingDate) values (?, to_date(?, 'YYYY-MM-DD HH24:MI'))";
		String dateStr = "1995-01-01 19:02";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
		jdbcTemplate.update(sql, new Object[] { idTeam, dateStr});
		return new LastTraining(idTeam, date);
	}

	@Override
	public void updateTraining(long idTeam, LocalDateTime now) {
		String sql = "update LastTraining set trainingDate = to_date(?, 'YYYY-MM-DD HH24:MI') where idTeam = ?";
		jdbcTemplate.update(sql, now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), idTeam);
	}
	
	@Override
	public LastTraining trainingDate(long idTeam) {
		String sql = "select * from LastTraining where idTeam = ?";
		LastTraining lastTraining = jdbcTemplate.queryForObject(sql, new Object[] { idTeam }, new LastTrainingMapper());
		return lastTraining;
	}
	
	@Override
	public void assignPenalty(Player player, int amount) {
		String sql = "select * from player natural join team where idPlayer = ?";
		List<Team> teams = jdbcTemplate.query(sql, new Object[] { player.getIdPlayer() }, new TeamMapper());
		String s = "update team set budget = ? where idTeam = ?";
		teams.forEach(team -> jdbcTemplate.update(s, new Object[] { team.getBudget() - amount, team.getIdTeam() }) );
	}
}
