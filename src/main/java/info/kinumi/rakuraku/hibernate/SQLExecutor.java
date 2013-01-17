package info.kinumi.rakuraku.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.common.base.Joiner;

/**
 * RakuRaku finder
 * 
 * <code>
 * tx.from(A.class).getClass();
 * // => RakuRakuFinder<A>
 * 
 * tx.from(A.class)
 *     .where("a = :a")
 *     .orderBy("b")
 *     .limit(10)
 *     .offset(100)
 *     .param("a", 1)
 *     .list();
 * // => query by HQL: [from A where a = 1 order by b]
 * //				and setMaxResult(10), setFirstResult(100)
 * </code>
 * 
 * @param <T>
 *            対象エンティティクラス
 * @author kunimi.ikeda
 */
public class SQLExecutor<T> {

	/**
	 * Hibernateセッション
	 */
	Session _session;

	/**
	 * クラス
	 */
	Class<T> _klass;

	/**
	 * where
	 */
	String _where = "";

	/**
	 * order by
	 */
	String _orderBy = "";
	
	/**
	 * limit
	 */
	Integer _limit = null;
	
	/**
	 * offset
	 */
	Integer _offset = null;

	/**
	 * パラメータマップ
	 */
	Map<String, Object> _params = new HashMap<>();

	/**
	 * コンストラクタ
	 * 
	 * @param klass
	 */
	public SQLExecutor(Class<T> klass, Session session) {
		_klass = klass;
		_session = session;
	}

	/**
	 * where
	 * 
	 * @param where
	 */
	public SQLExecutor<T> where(String... where) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(" where (");
		sb.append(Joiner.on(") and (").join(where));
		sb.append(") ");
		_where = sb.toString();
		return this;
	}

	/**
	 * order by
	 * 
	 * @param orderBy
	 */
	public SQLExecutor<T> orderBy(String... orderBy) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(" order by ");
		sb.append(Joiner.on(", ").join(orderBy));
		sb.append(" ");
		_orderBy = sb.toString();
		return this;
	}
	
	/**
	 * limit
	 * 
	 * @param limit
	 * @return
	 */
	public SQLExecutor<T> limit(int limit) {
		_limit = limit;
		return this;
	}

	/**
	 * offset
	 * 
	 * @param offset
	 * @return
	 */
	public SQLExecutor<T> offset(int offset) {
		_offset = offset;
		return this;
	}

	/**
	 * パラメータのセット
	 * 
	 * @param param
	 * @param value
	 * @return
	 */
	public SQLExecutor<T> param(String param, Object value) {
		_params.put(param, value);
		return this;
	}

	/**
	 * HQLを取得する
	 * 
	 * @return
	 */
	public String getHQL() {
		StringBuilder hql = new StringBuilder(100);
		hql.append("from ");
		hql.append(_klass.getCanonicalName());
		hql.append(_where);
		hql.append(_orderBy);
		return hql.toString();
	}

	/**
	 * データを1件取得する
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T uniq() {
		Query query = _session.createQuery(getHQL());
		query.setMaxResults(1);
		if (_offset != null) {
			query.setFirstResult(_offset);
		}
		query.setProperties(_params);
		return (T) query.uniqueResult();
	}

	/**
	 * データのリストを取得する
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> list() {
		Query query = _session.createQuery(getHQL());
		if (_limit != null) {
			query.setMaxResults(_limit);
		}
		if (_offset != null) {
			query.setFirstResult(_offset);
		}
		query.setProperties(_params);
		return (List<T>) query.list();
	}
}
