package com.entitylinking.entitylinking;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.entitylinking.bean.HibernateBean;
import com.entitylinking.config.HibernateSession;

public class HibernateTest {
	 public static void main(String[] args) {  
          
        //创建Session  
        Session session = HibernateSession.getSession();
          
        //开始事务  
        Transaction transaction = session.beginTransaction();  
          
        //创建消息对象，并设置属性  
        HibernateBean person = new HibernateBean();  
        person.setName("kobe");  
        person.setId(2);  
          
        //1.保存消息  
//        session.save(person);  
        //2.更新消息
//        session.update(person);
        //3.删除消息
        session.delete(person);
        //提交事物  
        transaction.commit();  
       HibernateSession.closeSession();
    }  
	 
	 
	 /*
	    1、增加数据
		Users users = new Users();
		users.setPwd="admin";
		users.setName="admin";
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		session.save(users);
		session.getTransaction().commit();
		
		2、修改数据
		session.update(users);
		
		3、删除数据
		session.delete(users);
		
		注意：2 和 3 中使用的users中要包含主键的值
		
		4、查询数据
		String HQLString = "*****";
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createQuery(HQLString);
		java.util.List list = query.list();
		for(Object o : list){
		   Users u = (Users)o;
		   System.out.println(u.getPwd+" "+u.getName);
		}
	  
	  */

}
