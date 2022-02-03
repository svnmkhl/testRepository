package DAO;


import Entity.Index;
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

    public static synchronized Index findByPageAndLemmaId(Index index)   {
        List<Index> indexList;
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        try {
            Query query = session.createQuery("FROM Index WHERE page_id = :pageId AND lemma_id = :lemmaId");
            query.setParameter("pageId", index.getPageId());
            query.setParameter("lemmaId", index.getLemmaId());
            indexList = query.getResultList();
        } catch (Exception e) {
            tx1.commit();
            session.close();
            return null;
        }
        tx1.commit();
        session.close();
        return indexList.get(0);
    }

    public static synchronized void updateRank(Index index) {
        Index oldIndex = findByPageAndLemmaId(index);
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        Index newIndex = session.get(Index.class, oldIndex.getId());
        newIndex.setRank(oldIndex.getRank() + index.getRank());
        session.update(newIndex);
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
