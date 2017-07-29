package cc.ar.messageboard.tag;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class TagDAOHibernate implements TagDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public TagBean select(Integer tid) {
		return getSession().get(TagBean.class, tid);
	}

	public TagBean selectByTagName(String name) {
		if (name != null && name.trim().length() > 0) {
			return getSession().createQuery("from TagBean where tagname = ?", TagBean.class).setParameter(0, name.trim()).uniqueResult();
		}
		return null;
	}

	public TagBean insert(String name) {
		if (name != null && name.trim().length() > 0) {
			TagBean bean = new TagBean();
			bean.setTagname(name.trim());
			getSession().save(bean);
			return bean;
		}
		return null;
	}

}
