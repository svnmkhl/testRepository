package Model;

import Model.HibernateSessionFactoryCreator;
import Model.Lemma;
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
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        List<Lemma> lemma = session.createSQLQuery("SELECT * FROM lemma WHERE lemma.lemma = \\'" + name + "\\'").getResultList();
        tx1.commit();
        session.close();
        return lemma;
    }

    public synchronized static void save(Lemma lemma) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(lemma);
        tx1.commit();
        session.close();
    }

    public synchronized static void update(Lemma lemma) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        Query query = session.createQuery("update Lemma set frequency = frequency + :newFrequency where lemma = :name");
        query.setParameter("newFrequency", lemma.getFrequency());
        query.setParameter("name", lemma.getName() + "");
        int result = query.executeUpdate();
        tx1.commit();
        session.close();
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
