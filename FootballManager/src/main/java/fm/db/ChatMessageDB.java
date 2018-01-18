package fm.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import fm.data.ChatMessage;
import fm.data.Player;
import fm.db.dao.ChatMessageDAO;
import lombok.Setter;

public class ChatMessageDB implements ChatMessageDAO {

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	@Setter private PlayerDB pdb;
	
	private class ChatMessageMapper implements RowMapper<ChatMessage> {
		public ChatMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
			String date = rs.getDate("datePosted").toString() + "T" + rs.getTime("datePosted").toString();
			ChatMessage msg = new ChatMessage(rs.getLong("idMessage"), rs.getLong("idChat"), pdb.findById(rs.getLong("idPlayer")), rs.getString("text"), LocalDateTime.parse(date), rs.getBoolean("removed"));
			return msg;
		}
	}
	
	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public ChatMessage findById(long id) {
		String sql = "select * from message where idMessage = ?";
		return jdbcTemplate.queryForObject(sql, new Object[] { id }, new ChatMessageMapper());
	}

	@Override
	public void delete(long id) {
		String sql = "delete from message where idMessage = ?";
		jdbcTemplate.update(sql, id);
	}

	@Override
	public List<ChatMessage> list() {
		String sql = "select * from message";
		return jdbcTemplate.query(sql, new ChatMessageMapper());
	}

	@Override
	public long getNewId() {
		String sql = "select max(idMessage) from message";
		Object x = jdbcTemplate.queryForObject(sql, Long.class);
		long max = (x == null) ? 0 : (Long) x;

		return max + 1;
	}

	@Override
	public ChatMessage create(long chatId, Player sender, String text) {
		String sql = "insert into message(idmessage, idchat, idplayer, text, removed, dateposted) values(?, ?, ?, ?, ?, to_date(?, 'YYYY-MM-DD HH24:MI'))";
		long id = getNewId();
		LocalDateTime now = LocalDateTime.now();
		jdbcTemplate.update(sql, new Object[] { id, chatId, sender.getIdPlayer(), text, "0", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) });
		
		return new ChatMessage(id, chatId, sender, text, now, false);
	}

	@Override
	public void setRemoved(long id, boolean isRemoved) {
		String sql = "update message set removed = ? where idMessage = ?";
		jdbcTemplate.update(sql, new Object[] { isRemoved ? "1" : "0", id });

	}

	@Override
	public List<ChatMessage> listMessagesForChat(long chatId) {
		String sql = "select * from message where idChat = ? order by idMessage desc";
		return jdbcTemplate.query(sql, new Object[] { chatId }, new ChatMessageMapper());
	}

}
