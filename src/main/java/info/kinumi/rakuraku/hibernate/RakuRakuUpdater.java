package info.kinumi.rakuraku.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.common.base.Joiner;

/**
 * RakuRaku updater
 * 
 * <code>
 * tx.updater(A.class).getClass();
 * // => RakuRakuUpdater<A>
 * 
 * tx.update(A.class)
 * 	   .set("b = :b")
 *     .where("a = :a")
 *     .param("a", 1)
 *     .param("b", 1)
 *     .execute();
 * // => query by HQL: [update A set b = 2 where a = 1]
 * </code>
 * 
 * @param <T>
 *            対象エンティティクラス
 * @author kunimi.ikeda
 */
public class RakuRakuUpdater<T> {

	/**
	 * Hibernateセッション
	 */
	Session _session;

	/**
	 * クラス
	 */
	Class<T> _klass;

	/**
	 * set
	 */
	String _set = "";

	/**
	 * where
	 */
	String _where = "";

	/**
	 * パラメータマップ
	 */
	Map<String, Object> _params = new HashMap<>();

	/**
	 * コンストラクタ
	 * 
	 * @param klass
	 */
	public RakuRakuUpdater(Class<T> klass, Session session) {
		_klass = klass;
		_session = session;
	}

	/**
	 * where
	 * 
	 * @param where
	 */
	public RakuRakuUpdater<T> where(String... where) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(" where (");
		sb.append(Joiner.on(") and (").join(where));
		sb.append(") ");
		_where = sb.toString();
		return this;
	}

	/**
	 * set
	 * 
	 * @param set
	 */
	public RakuRakuUpdater<T> set(String... set) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(" set ");
		sb.append(Joiner.on(", ").join(set));
		sb.append(" ");
		_set = sb.toString();
		return this;
	}

	/**
	 * パラメータのセット
	 * 
	 * @param param
	 * @param value
	 * @return
	 */
	public RakuRakuUpdater<T> param(String param, Object value) {
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
		hql.append("update ");
		hql.append(_klass.getCanonicalName());
		hql.append(_set);
		hql.append(_where);
		return hql.toString();
	}

	/**
	 * クエリを実行する
	 * 
	 * @return
	 */
	public int execute() {
		Query query = _session.createQuery(getHQL());
		query.setProperties(_params);
		return query.executeUpdate();
	}
}
