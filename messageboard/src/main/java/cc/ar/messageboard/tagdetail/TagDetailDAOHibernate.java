package cc.ar.messageboard.tagdetail;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class TagDetailDAOHibernate implements TagDetailDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	
	public boolean insert(TagDetailBean bean) {
		if (bean != null) {
			getSession().save(bean);
			return true;
		}
		return false;
	}


	public boolean delete(TagDetailBean bean) {
		if (bean != null) {
			getSession().delete(bean);
			return true;
		}
		return false;
	}


	public List<TagDetailBean> selectByAid(Integer aid) {
		if (aid != null)
			return getSession().createQuery("from TagDetailBean where aid = ?", TagDetailBean.class).setParameter(0, aid).list();
		return null;
	}


	public TagDetailBean select(TagDetailBean bean) {
		if (bean != null)
			return getSession().createQuery("from TagDetailBean where aid = ? and tid = ?", TagDetailBean.class).setParameter(0, bean.getAid()).setParameter(1, bean.getTid()).uniqueResult();
		return null;
	}

}
