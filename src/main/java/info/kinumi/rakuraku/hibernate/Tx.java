package info.kinumi.rakuraku.hibernate;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * try-with-resourcesを使ったトランザクション
 * 
 * try (Tx tx = new Tx(sf)) { ... tx.commit(); }
 * 
 * tryブロック中でcommit()がコールされなかった場合はロールバックします
 * 
 * @author kunimi.ikeda
 */
public class Tx implements AutoCloseable {
	@Override
	public void close() {
		if (_tx.isActive()) {
			_tx.rollback();
		}
	}

	/**
	 * Hibernateセッション
	 */
	private Session _session;
	/**
	 * Hibernateトランザクション
	 */
	private Transaction _tx;

	/**
	 * コンストラクタ
	 * 
	 * @param sf
	 */
	public Tx(SessionFactory sf) {
		_session = sf.openSession();
		_tx = _session.beginTransaction();
	}

	/**
	 * Begins fluent finder.
	 * 
	 * exsample. <code>
	 * Cat o = tx.from(Cat.class)
	 * 	 .where("name = :name", "weight > :weight")
	 *   .orderBy("weight desc", "color asc")
	 *   .param("name", "Tama")
	 *   .param("weight", 1000)
	 *   .limit(100)
	 *   .offset(1000)
	 *   .list(); 
	 * -> [from Cat where name = 'Tama' and weight > 1000 order by weight desc, color asc]
	 *    [setMaxResults(100), setFirstResult(1000)]
	 * </code>
	 * 
	 * @param klass
	 * @return
	 */
	public <T> RakuRakuFinder<T> from(Class<T> klass) {
		return new RakuRakuFinder<T>(klass, _session);
	}

	/**
	 * Begins fluent deleter.
	 * 
	 * exsample. <code>
	 * Cat o = tx.delete(Cat.class)
	 * 	 .where("name = :name", "weight > :weight")
	 *   .param("name", "Tama")
	 *   .param("weight", 1000)
	 *   .execute();
	 * -> [delete Cat where name = 'Tama' and weight > 1000]
	 * </code>
	 * 
	 * @param klass
	 * @return
	 */
	public <T> RakuRakuDeleter<T> delete(Class<T> klass) {
		return new RakuRakuDeleter<>(klass, _session);
	}
	
	/**
	 * Begins fluent updater.
	 * 
	 * exsample. <code>
	 * Cat o = tx.update(Cat.class)
	 * 	 .set("weight = :newWeight")
	 * 	 .where("name = :name", "weight > :weight")
	 *   .param("name", "Tama")
	 *   .param("weight", 1000)
	 *   .param("newWeight", 999)
	 *   .execute();
	 * -> [update Cat set weight = 999 where name = 'Tama' and weight > 1000]
	 * </code>
	 * 
	 * @param klass
	 * @return
	 */
	public <T> RakuRakuUpdater<T> update(Class<T> klass) {
		return new RakuRakuUpdater<>(klass, _session);
	}
	
	/**
	 * save.
	 * 
	 * @param o
	 *            Entity to save
	 * @return
	 */
	public Serializable save(Object o) {
		return _session.save(o);
	}

	/**
	 * merge.
	 * 
	 * @param o
	 *            Entity to merge
	 * @return
	 */
	public Object merge(Object o) {
		return _session.merge(o);
	}

	/**
	 * update.
	 * 
	 * @param o
	 *            Entity to update
	 */
	public void update(Object o) {
		_session.update(o);
	}

	/**
	 * delete.
	 * 
	 * @param o
	 *            Entity to delete
	 */
	public void delete(Object o) {
		_session.delete(o);
	}
	
	/**
	 * save or update.
	 * 
	 * @param o
	 *            Entity to save or update.
	 */
	public void saveOrUpdate(Object o) {
		_session.saveOrUpdate(o);
	}

	
	/**
	 * Commits active transaction.
	 */
	public void commit() {
		try {
			_tx.commit();
		}
		finally {
			_session.close();
		}
	}

	/**
	 * Gets the hibernate's session
	 * 
	 * @return Session
	 */
	public Session getSession() {
		return _session;
	}
}
