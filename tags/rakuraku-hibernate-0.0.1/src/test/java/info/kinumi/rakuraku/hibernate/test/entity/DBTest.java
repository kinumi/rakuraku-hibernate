package info.kinumi.rakuraku.hibernate.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test")
public class DBTest {
	@Id
	public int id;
	@Column
	public String a;
	@Column
	public String b;
	@Column
	public String c;
	
	public DBTest() {
		
	}
	public DBTest(int id, String a, String b, String c) {
		this.id = id;
		this.a = a;
		this.b = b;
		this.c = c;
	}
}
