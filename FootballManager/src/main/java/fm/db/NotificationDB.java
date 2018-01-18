package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.Notification;
import fm.data.Player;
import fm.data.Notification.NotificationType;
import fm.db.dao.NotificationDAO;
import lombok.Setter;

public class NotificationDB implements NotificationDAO {
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	@Setter private PlayerDB pdb;
	

	private class NotificationMapper implements RowMapper<Notification> {
		public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
			NotificationType type = NotificationType.typeForName(rs.getString("type"));
			
			String startDate = rs.getDate("startDate").toString() + "T" + rs.getTime("startDate").toString();
			String finishDate = rs.getDate("finishDate").toString() + "T" + rs.getTime("finishDate").toString();
			
			Notification notif;
			if(type == NotificationType.SINGLE) {
				notif = new Notification(rs.getLong("idNotification"), pdb.findById(rs.getLong("idPlayer")), rs.getString("text"), LocalDateTime.parse(startDate), LocalDateTime.parse(finishDate));
			}
			else {
				notif = new Notification(rs.getLong("idNotification"), type, rs.getString("text"), LocalDateTime.parse(startDate), LocalDateTime.parse(finishDate));
			}

			return notif;
		}
	}
	
	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Notification findById(long id) {
		String sql = "select * from notification where idNotification = ?";
		Notification notif = jdbcTemplate.queryForObject(sql, new Object[] { id }, new NotificationMapper());
		return notif;
	}

	@Override
	public void delete(long id) {
		String sql = "delete from notification where idNotification = ?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public List<Notification> list() {
		String sql = "select * from notification";
		List<Notification> notifs = jdbcTemplate.query(sql, new NotificationMapper());
		return notifs;
	}

	@Override
	public long getNewId() {
		String sql = "select max(idNotification) from notification";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long)x;
		
		return max + 1;
	}

	@Override
	public List<Notification> listCurrent() {
		String sql = "select * from notification where current_date between startDate and finishDate";
		return jdbcTemplate.query(sql, new NotificationMapper());
	}
	
	@Override
	public List<Notification> listExpired() {
		String sql = "select * from notification where current_date not between startDate and finishDate";
		return jdbcTemplate.query(sql, new NotificationMapper());
	}

	@Override
	public Notification create(String text, LocalDateTime startDate, LocalDateTime finishDate, NotificationType type) {
		if(type == NotificationType.SINGLE)
			return null;
		
		String sql = "insert into Notification (idNotification, text, startDate, finishDate, type) values (?, ?, to_date(?, 'YYYY-MM-DD HH24:MI'), to_date(?, 'YYYY-MM-DD HH24:MI'), ?)";
		long id = getNewId();
		jdbcTemplate.update(sql, new Object[] { id, text, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), finishDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), type.toString() });
		return new Notification(id, type, text, startDate, finishDate);
	}
	
	@Override
	public Notification create(String text, LocalDateTime startDate, LocalDateTime finishDate, Player player) {
		String sql = "insert into Notification (idNotification, text, startDate, finishDate, type, idPlayer) values (?, ?, to_date(?, 'YYYY-MM-DD HH24:MI'), to_date(?, 'YYYY-MM-DD HH24:MI'), ?, ?)";
		long id = getNewId();
		jdbcTemplate.update(sql, new Object[] { id, text, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), finishDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), NotificationType.SINGLE.toString(), player.getIdPlayer() });
		return new Notification(id, player, text, startDate, finishDate);
	}

}
