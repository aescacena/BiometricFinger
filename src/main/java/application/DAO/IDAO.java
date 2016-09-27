package application.DAO;

import java.io.Serializable;
import java.net.BindException;
import java.util.List;

public interface IDAO<T, ID extends Serializable> {
	T create() throws Exception;
	void saveOrUpdate(T entity) throws Exception;
	T get(ID id) throws Exception;
	void delete(ID id) throws Exception;
	List<T> findAll() throws Exception;
	void save(T instance) throws Exception;
}
