package Model;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class LemmaDAO {

    public Lemma findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Lemma.class, id);
    }

    public void save(Lemma lemma) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(lemma);
        tx1.commit();
        session.close();
    }

    public void update(Lemma lemma) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(lemma);
        tx1.commit();
        session.close();
    }

    public void saveMany(List<Lemma> lemmaList){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        for(Lemma lemma : lemmaList) {
            session.save(lemma);
        }
        tx1.commit();
        session.close();
    }

    public void delete(Lemma lemma) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
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
