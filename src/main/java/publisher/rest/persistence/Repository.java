package publisher.rest.persistence;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import publisher.rest.exception.RepositoryException;


public class Repository<T> {

	private Class<T> innerClass;
	private String queryRetrieve;
	private String queryList;
	private String queryDelete;
	private String queryExists;

	public Repository(Class<T> clazz) {
		innerClass = clazz;
		queryRetrieve = concat("FROM ", innerClass.getSimpleName(), " t WHERE t.id = :id");
		queryList = concat("FROM ", innerClass.getSimpleName());
		queryDelete = concat("DELETE ", innerClass.getSimpleName(), " t WHERE t.id = :id");
		queryExists = concat("SELECT count(*) FROM ", innerClass.getSimpleName(), " t WHERE t.id = :id");
	}

	public void persist(T object) {
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
			transaction = session.beginTransaction();
			session.persist(object);
			session.flush();
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			throw new RepositoryException(e.getMessage());
		}
	}

	public boolean exists(String id) {
		Optional<T> object = retrieve(id);
		return object.isPresent();
	}

	public boolean exists(T object) {
		try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
			session.beginTransaction();
			boolean contains = session.contains(object);
			session.flush();
			return contains;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryException(e.getMessage());
		}
	}

	public Optional<T> retrieve(String id) {
		try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
			session.beginTransaction();
			return session.createQuery(this.queryRetrieve, innerClass).setParameter("id", id).uniqueResultOptional();
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		}
	}

	public List<T> retrieve() {
		try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
			session.beginTransaction();

			return session.createQuery(queryList, innerClass).list();
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage());
		}
	}



	private String concat(String... strings) {
		StringBuilder br = new StringBuilder();
		for (String string : strings)
			br.append(string);
		return br.toString();
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	public void execUpdate(String queryStr, Map<String, String> parameters) {
		try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
			session.beginTransaction();
			Query query = session.createQuery(queryStr);
			Set<Entry<String, String>> entries = parameters.entrySet();
			for (Entry<String, String> entry : entries)
				query = query.setParameter(entry.getKey(), entry.getValue());
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryException(e.getMessage());
		}
	}



	public T update(T object) {
		try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
			Transaction transaction = session.beginTransaction();
			T result = session.merge(object);
			session.flush();
			transaction.commit();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryException(e.getMessage());
		}
	}

	public void delete(String id) {
		Optional<T> object = retrieve(id);
		delete(object.get());
	}

	public void delete(T object) {
		try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
			Transaction transaction = session.beginTransaction();
			session.remove(object);
			session.flush();
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryException(e.getMessage());
		}
	}
}