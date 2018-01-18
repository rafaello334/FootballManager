package fm.db.dao;

import java.util.List;

import javax.sql.DataSource;

public interface GenericDAO<T> {
	public void setDataSource(DataSource ds);
	public T findById(long id);
	public void delete(long id);
	public List<T> list();
	public long getNewId();
}
