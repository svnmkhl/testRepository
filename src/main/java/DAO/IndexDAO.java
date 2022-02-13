package DAO;


import Entity.Index;
import Entity.Lemma;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class IndexDAO
{
    public static synchronized Index findById(int id) {
        return HibernateSessionFactoryCreator.getSessionFactory().openSession().get(Index.class, id);
    }

    public static synchronized void save(Index index) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(index);
        tx1.commit();
        session.close();
    }

    public static synchronized void saveMany(List<Index> indexList) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        for (Index index : indexList) {
            session.save(index);
        }
        tx1.commit();
        session.close();
    }

    public static synchronized void delete(Index index) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(index);
        tx1.commit();
        session.close();
    }
}
