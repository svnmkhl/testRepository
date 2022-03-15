package DAO;

import Entity.Lemma;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.HashSet;
import java.util.List;


public class LemmaDAO {

    public synchronized static Lemma findById(int id) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        Lemma lemma = session.get(Lemma.class, id);
        tx1.commit();
        session.close();
        return lemma;
    }

    public synchronized static List<Lemma> findByName(String name) {
        List<Lemma> lemmsList;
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        try {
            Query query = session.createQuery("FROM Lemma WHERE lemma = :name");
            query.setParameter("name", name);
            lemmsList = query.getResultList();
        } catch (Exception e) {
            //tx1.commit();
            session.close();
            return null;
        }
        //tx1.commit();
        session.close();
        return lemmsList;
    }

    public synchronized static void save(Lemma lemma) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(lemma);
        tx1.commit();
        session.close();
    }

    public synchronized static Lemma updateFrequency(Lemma lemma) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        List<Lemma> lemmaList = findByName(lemma.getName());
        Lemma newLemma = session.get(Lemma.class, lemmaList.get(0).getId());
        newLemma.setFrequency(newLemma.getFrequency() + lemma.getFrequency());
        session.update(newLemma);
        tx1.commit();
        session.close();
        return newLemma;
    }

    public synchronized static void saveMany(HashSet<Lemma> lemmaSet){
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        for(Lemma lemma : lemmaSet) {
            session.save(lemma);
        }
        tx1.commit();
        session.close();
    }

    public synchronized static void delete(Lemma lemma) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(lemma);
        tx1.commit();
        session.close();
    }


   /* public Auto findAutoById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Auto.class, id);
    }

    public List<User> findAll() {
        List<User> users = (List<User>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From User").list();
        return users;
    }*/
}
