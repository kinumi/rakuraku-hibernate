Main features:
  * A transaction using try-with-resources of JDK7.
  * An HQL wrapper which has fluent interface.

# Example #
```
// It is necessary to create SessionFactory by yourself.
SessionFactory sf = sessionFactory;

// Transaction using try-with-resources.
//   Transaction will be rollbacked if never call tx.commit() in try block.
try (Tx tx = new Tx(sf)) {
    // fluent interface query.
    //   [from Cat where (name = :name) and (color = :c1 or color = :c2) order by weight desc;]
    //   [setMaxResults(100), setFirstResult(1000)]
    List<Cat> cats = tx.from(Cat.class)
        .where("name = :name", "color = :c1 or color = :c2")
        .orderBy("weight desc")
        .limit(100)
        .offset(1000)
        .param("name", "Tama")
        .param("c1", "White")
        .param("c2", "Black")
        .list();

    // fluent interface updater.
    //   [update Cat set name = :name where id = :id]
    int updateRowCnt = tx.update(Cat.class)
        .set("name = :name")
        .where("id = :id")
        .param("name", "Tama")
        .param("id", 1)
        .execute();

    // fluent interface deleter.
    //   [delete Cat where id = :id]
    int deleteRowCnt = tx.delete(Cat.class)
        .where("id = :id")
        .param("id", 1)
        .execute();

    // normal save/update/delete

    // save
    Cat c = new Cat("Miko", "White", 1000);
    tx.save(c);

    // merge
    c.setName("Tama");
    tx.merge(c);

    // update
    c.setName("Tama");
    tx.update(c);

    // delete
    tx.delete(c);

    tx.commit();
}
```


# Maven #
```
<dependency>
    <groupId>info.kinumi</groupId>
    <artifactId>rakuraku-hibernate</artifactId>
    <version>0.0.2-SNAPSHOT</version>
</dependency>
```