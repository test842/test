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

	
	public boolean insert(Integer aid,Integer tid) {
		if (aid != null && tid != null) {
			TagDetailBean bean = new TagDetailBean();
			bean.setAid(aid);
			bean.setTid(tid);
			getSession().save(bean);
			return true;
		}
		return false;
	}


	public boolean delete(Integer aid,Integer tid) {
		if (aid != null && tid != null) {
			getSession().delete(select(aid, tid));
			return true;
		}
		return false;
	}


	public List<TagDetailBean> selectByAid(Integer aid) {
		if (aid != null)
			return getSession().createQuery("from TagDetailBean where aid = ?", TagDetailBean.class).setParameter(0, aid).list();
		return null;
	}


	public TagDetailBean select(Integer aid,Integer tid) {
		if (aid != null && tid != null)
			return getSession().createQuery("from TagDetailBean where aid = ? and tid = ?", TagDetailBean.class).setParameter(0, aid).setParameter(1, tid).uniqueResult();
		return null;
	}

}
