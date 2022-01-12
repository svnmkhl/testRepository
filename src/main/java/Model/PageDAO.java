package Model;

import Model.Page;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;


public class PageDAO {

    public Page findById(int id) {
        return HibernateSessionFactoryCreator.getSessionFactory().openSession().get(Page.class, id);
    }

    public void save(Page page) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(page);
        tx1.commit();
        session.close();
    }

    public void update(Page page) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(page);
        tx1.commit();
        session.close();
    }

    public void saveMany(List<Page> pageList) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        for (Page page : pageList) {
            session.save(page);
        }
        tx1.commit();
        session.close();
    }

    public void delete(Page page) {
        Session session = HibernateSessionFactoryCreator.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(page);
        tx1.commit();
        session.close();
    }
}