package info.kinumi.rakuraku.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import com.google.common.base.Joiner;

/**
 * RakuRaku deleter
 * 
 * <code>
 * tx.delete(A.class).getClass();
 * // => RakuRakuDeleter<A>
 * 
 * tx.delete(A.class)
 *     .where("a = :a")
 *     .param("a", 1)
 *     .execute();
 * // => query by HQL: [delete A where a = 1]
 * </code>
 * 
 * @param <T>
 *            対象エンティティクラス
 * @author kunimi.ikeda
 */
public class RakuRakuDeleter<T> {

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
	 * パラメータマップ
	 */
	Map<String, Object> _params = new HashMap<>();

	/**
	 * コンストラクタ
	 * 
	 * @param klass
	 */
	public RakuRakuDeleter(Class<T> klass, Session session) {
		_klass = klass;
		_session = session;
	}

	/**
	 * where
	 * 
	 * @param where
	 */
	public RakuRakuDeleter<T> where(String... where) {
		StringBuilder sb = new StringBuilder(100);
		sb.append(" where (");
		sb.append(Joiner.on(") and (").join(where));
		sb.append(") ");
		_where = sb.toString();
		return this;
	}

	/**
	 * パラメータのセット
	 * 
	 * @param param
	 * @param value
	 * @return
	 */
	public RakuRakuDeleter<T> param(String param, Object value) {
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
		hql.append("delete ");
		hql.append(_klass.getCanonicalName());
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
