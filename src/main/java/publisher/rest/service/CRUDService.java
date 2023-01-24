package publisher.rest.service;

import java.util.List;
import java.util.Optional;

import publisher.rest.exception.InvalidRequestException;
import publisher.rest.exception.RepositoryException;
import publisher.rest.persistence.Repository;

public class CRUDService<T> {

	protected Repository<T> repository;
	protected Class<T> innerClass;

	public CRUDService(Class<T> clazz) {
		this.innerClass = clazz;
		this.repository = new Repository<>(clazz);
	}

	public boolean exist(String id) {
		try {
			return repository.exists(id);
		} catch (RepositoryException e) {
			throw new InvalidRequestException(e.getMessage());
		}
	}

	public T get(String id) {
		Optional<T> endpointOpt = repository.retrieve(id);
		if (endpointOpt.isPresent()) {
			return endpointOpt.get();
		} else {
			throw new InvalidRequestException("No " + innerClass + " object found with provided id");
		}
	}

	public Optional<T> getOptional(String id) {
		try {
			return repository.retrieve(id);
		} catch (RepositoryException e) {
			throw new InvalidRequestException(e.getMessage());
		}
	}

	public List<T> getAll() {
		try {
			return repository.retrieve();
		} catch (RepositoryException e) {
			throw new InvalidRequestException(e.getMessage());
		}
	}

	public void delete(String id) {
		try {
			repository.delete(id);
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new InvalidRequestException(e.getMessage());
		}
	}

	public void create(T object) {
		try {
			repository.persist(object);
		} catch (RepositoryException e) {
			throw new InvalidRequestException(e.getMessage());
		}
	}

	public boolean update(String id, T object) {
		boolean existed = exist(id);
//		if (existed)
//			delete(id);
//		create(object);
		repository.update(object);
		return existed;
	}

	public T update(T object) {
//		if (existed)
//			delete(id);
//		create(object);
		return repository.update(object);
	}


}
