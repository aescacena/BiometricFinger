package application.DAO;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class DAOImpl<T, ID extends Serializable> implements IDAO<T, ID> {

	SessionFactory sessionFactory;

	private final static Logger LOGGER = Logger.getLogger(DAOImpl.class.getName());

	public DAOImpl() {
		// TODO Auto-generated constructor stub
		sessionFactory=HibernateUtil.getSessionFactory();
	}

	@Override
	public T create() throws Exception {
		try {
			return getEntityClass().newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void saveOrUpdate(T entity) throws Exception {
		Session session = sessionFactory.getCurrentSession();

		try {
			session.beginTransaction();
			session.saveOrUpdate(entity);
			session.getTransaction().commit();
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void save(T instance) throws Exception {
		Session session = sessionFactory.getCurrentSession();
        try {
        	session.beginTransaction();
        	session.save(instance);
        	session.getTransaction().commit();
        } catch (final Exception e) {
            throw e;
        }
    }

	@Override
	public T get(ID id) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			T entity = (T) session.get(getEntityClass(), id);
			session.getTransaction().commit();

			return entity;
		} catch (Exception e) {
			throw e;
			//			return null;
		}
	}

	@Override
	public void delete(ID id) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			T entity = (T) session.get(getEntityClass(), id);
			if (entity == null) {
				throw new Exception(new String(null, "Los datos a borrar ya no existen"));
			}
			session.delete(entity);
			session.getTransaction().commit();
		}catch (Exception e) {
			throw e;
		}
	}

	@Override
	public List<T> findAll() throws Exception {
		Session session = sessionFactory.getCurrentSession();
		try {
			Query query = session.createQuery("SELECT e FROM " + getEntityClass().getName() + " e");
			List<T> entities = query.list();

			return entities;
		} catch (Exception e) {
			throw e;
		}
	}

	private Class<T> getEntityClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

}
