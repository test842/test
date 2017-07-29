package cc.ar.messageboard.article;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Repository;

@Repository
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class ArticleDAOHibernate implements ArticleDAO {

	@Autowired
	private SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public boolean insert(ArticleBean bean) {
		if (bean != null) {
			getSession().save(bean);
			return true;
		}
		return false;
	}

	public boolean delete(Integer aid) {
		if (aid != null) {
			getSession().get(ArticleBean.class, aid).setVisible(false);
			return true;
		}
		return false;
	}

	public boolean update(ArticleBean bean) {
		if (bean != null && bean.getAid() != null) {
			getSession().update(bean);
			return true;
		}
		return false;
	}

	public List<ArticleBean> selectByUid(Integer uid) {
		if (uid != null)
			return getSession().createQuery("from ArticleBean where uid = ? and visible = 1 order by date desc", ArticleBean.class).setParameter(0, uid).list();
		return null;
	}

	public List<ArticleBean> selectReplies(Integer ref) {
		if (ref != null)
			return getSession().createQuery("from ArticleBean where ref = ? and visible = 1 order by date desc", ArticleBean.class).setParameter(0, ref).list();
		return null;
	}

	public List<ArticleBean> select(boolean isReply) {
		String str = null;
		if (isReply)
			str = "from ArticleBean where ref is not null and ref != 0 and visible = 1 order by date desc";
		else
			str = "from ArticleBean where ref = 0 and visible = 1 order by date desc";
		return getSession().createQuery(str, ArticleBean.class).list();
	}

	public ArticleBean select(Integer aid) {
		if (aid != null) {
			return getSession().get(ArticleBean.class, aid);
		}
		return null;
	}

	public List<ArticleBean> selectRecent(boolean isReply) {
		String str = null;
		if (isReply)
			str = "from ArticleBean where ref is not null and ref != 0 and visible = 1 order by date desc";
		else
			str = "from ArticleBean where ref = 0 and visible = 1 order by date desc";
		return getSession().createQuery(str, ArticleBean.class).setMaxResults(10).list();
	}

}
