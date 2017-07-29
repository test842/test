package cc.ar.messageboard.user;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class UserDAOHibernate implements UserDAO {
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	
	public UserBean select(Integer aid) {
		if (aid != null) {
			return getSession().get(UserBean.class, aid);
		}
		return null;
	}

}
