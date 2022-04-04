package main;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class IndexDAO
{
    public static synchronized Index findById(int id) {
        return HibernateSessionFactoryCreator.getSessionFactory().openSession().get(Index.class, id);
    }

    public static synchronized List<Index> findByPageAndLemmaId(int pageId, int lemmaId) {
        List<Index> indexList;
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        try {
            Query query = session.createQuery("FROM Index WHERE page_Id = :pageId AND lemma_Id = :lemmaId");
            query.setParameter("pageId", pageId);
            query.setParameter("lemmaId", lemmaId);
            indexList = query.getResultList();
        } catch (Exception e) {
            session.close();
            return null;
        }
        session.close();
        return indexList;
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

    public static synchronized List<Index> findAllIndexes() {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        List<Index> indexList = (List<Index>)  session.createQuery("From User").list();
        tx1.commit();
        session.close();
        return indexList;
    }
}
