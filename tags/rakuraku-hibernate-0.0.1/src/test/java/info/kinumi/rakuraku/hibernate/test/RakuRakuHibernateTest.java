package info.kinumi.rakuraku.hibernate.test;

import static org.junit.Assert.*;

import java.util.List;

import info.kinumi.rakuraku.hibernate.Tx;
import info.kinumi.rakuraku.hibernate.test.entity.DBTest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RakuRakuHibernateTest {

	SessionFactory _sf;

	@Before
	public void setUp() throws Exception {
		if (_sf == null) {
			_sf = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
		}
		{
			Session s = _sf.openSession();
			s.createSQLQuery(" drop table test if exists; ").executeUpdate();
			s.createSQLQuery(" create table test (id int, a varchar(255), b varchar(255), c varchar(255)); ").executeUpdate();
			s.close();
		}
		try (Tx tx = new Tx(_sf)) {
			Session s = tx.getSession();
			s.createSQLQuery(" insert into test values (1, 'abc', '123', '1'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (2, 'bcd', '234', '0'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (3, 'cde', '345', '1'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (4, 'def', '456', '0'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (5, 'efg', '567', '1'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (6, 'fgh', '678', '0'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (7, 'ghi', '789', '1'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (8, 'hij', '890', '0'); ").executeUpdate();
			s.createSQLQuery(" insert into test values (9, 'ijk', '901', '1'); ").executeUpdate();
			tx.commit();
		}
	}

	@After
	public void tearDown() throws Exception {
		Session s = _sf.openSession();
		s.createSQLQuery(" drop table test if exists; ").executeUpdate();
		s.close();
	}

	@Test
	public void test_トランザクション_commitするパターン() {
		try (Tx tx = new Tx(_sf)) {
			DBTest test = new DBTest(999, "test", "test", "test");
			tx.getSession().save(test);
			tx.commit();
		}
		try (Tx tx = new Tx(_sf)) {
			DBTest o = tx.from(DBTest.class).where("id = 999").uniq();
			assertNotNull(o);
		}
	}

	@Test
	public void test_トランザクション_rollbackするパターン() {
		try (Tx tx = new Tx(_sf)) {
			DBTest test = new DBTest(999, "test", "test", "test");
			tx.getSession().save(test);
		}
		try (Tx tx = new Tx(_sf)) {
			DBTest o = tx.from(DBTest.class).where("id = 999").uniq();
			assertNull(o);
		}
	}

	@Test
	public void test_Finder_list() {
		// from DBTest
		try (Tx tx = new Tx(_sf)) {
			List<DBTest> list = tx.from(DBTest.class).list();
			assertEquals(9, list.size());
		}
		// from DBTest where c = '1' order by id desc
		try (Tx tx = new Tx(_sf)) {
			List<DBTest> list = tx.from(DBTest.class)
				.where("c = :c")
				.orderBy("id desc")
				.param("c", "1")
				.list();
			assertEquals(5, list.size());
			assertEquals(9, list.get(0).id);
			assertEquals(1, list.get(4).id);
		}
		// from DBTest where a like '%bc%' order by id desc
		try (Tx tx = new Tx(_sf)) {
			List<DBTest> list = tx.from(DBTest.class)
				.where("a like :a")
				.orderBy("id desc")
				.param("a", "%bc%")
				.list();
			assertEquals(2, list.size());
			assertEquals(2, list.get(0).id);
			assertEquals(1, list.get(1).id);
		}
		// from DBTest where c = '1' order by id desc
		// setMaxResults(2)
		// setFirstResult(1)
		try (Tx tx = new Tx(_sf)) {
			List<DBTest> list = tx.from(DBTest.class)
				.where("c = :c")
				.orderBy("id desc")
				.param("c", "1")
				.offset(1)
				.limit(2)
				.list();
			assertEquals(2, list.size());
			assertEquals(7, list.get(0).id);
			assertEquals(5, list.get(1).id);
		}
		// from DBTest where a like '%bc%' order by id desc
		// setMaxResults(2)
		// setFirstResult(1)
		try (Tx tx = new Tx(_sf)) {
			List<DBTest> list = tx.from(DBTest.class)
				.where("a like :a")
				.orderBy("id desc")
				.param("a", "%bc%")
				.offset(1)
				.limit(2)
				.list();
			assertEquals(1, list.size());
			assertEquals(1, list.get(0).id);
		}
		
	}
	
	@Test
	public void test_Finder_uniq() {
		// from DBTest
		try (Tx tx = new Tx(_sf)) {
			DBTest o = tx.from(DBTest.class).uniq();
			assertNotNull(o);
		}
		// from DBTest where c = '1' order by id desc 
		try (Tx tx = new Tx(_sf)) {
			DBTest o = tx.from(DBTest.class)
				.where("c = :c")
				.orderBy("id desc")
				.param("c", "1")
				.uniq();
			assertNotNull(o);
			assertEquals(9, o.id);
		}
		// from DBTest where a like '%bc%' order by id desc
		try (Tx tx = new Tx(_sf)) {
			DBTest o = tx.from(DBTest.class)
				.where("a like :a")
				.param("a", "%bc%")
				.orderBy("id desc")
				.uniq();
			assertNotNull(o);
			assertEquals(2, o.id);
		}
	}
}
