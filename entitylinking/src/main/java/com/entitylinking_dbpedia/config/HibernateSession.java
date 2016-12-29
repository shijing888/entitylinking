package com.entitylinking_dbpedia.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * session的创建与关闭
 * @author shijing
 *
 */
public class HibernateSession {

	private static Session session = null;
	private static SessionFactory sessionFactory = null;
	
	public static Session getSession(){
		//不带参数的configure方法将默认加载hibernate.cfg.xml文件，如果传入abc.xml作为参数  
        //则不加载hibernate.cfg.xml，而改成abc.xml  
        Configuration configuration = new Configuration().configure();  
  
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()  
            .applySettings(configuration.getProperties()).buildServiceRegistry();  
          
        //以Configuration实例创建SessionFactory实例  
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);  
          
        //创建Session  
        if(session==null)
        	session = sessionFactory.openSession();  
        
        return session;
	}
	
	public static void closeSession(){
		session.close();  
        sessionFactory.close();  
	}
}
