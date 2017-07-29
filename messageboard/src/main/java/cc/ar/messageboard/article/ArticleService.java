package cc.ar.messageboard.article;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cc.ar.messageboard.tag.TagBean;
import cc.ar.messageboard.tagdetail.TagDetailBean;
import cc.ar.messageboard.tagdetail.TagDetailService;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ArticleService {
	@Autowired
	private ArticleDAO articleDao;

	@Autowired
	private TagDetailService tagDetailService;

	@Transactional
	public boolean insert(ArticleBean bean, Set<TagBean> tags) {
		if (bean != null) {
			return articleDao.insert(bean) && editTags(tags, bean);
		}
		return false;
	}

	@Transactional
	public boolean update(ArticleBean bean, Set<TagBean> tags) {
		if (bean != null && articleDao.selectReplies(bean.getAid()).isEmpty()) {
			return articleDao.update(bean) && editTags(tags, bean);
		}
		return false;
	}

	@Transactional
	public boolean delete(Integer aid) {
		if (aid != null) {
			boolean result = true;
			for (ArticleBean bean : articleDao.selectReplies(aid))
				result = delete(bean.getAid()) && result;
			return articleDao.delete(aid) && result;
		}
		return false;
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> selectByUid(Integer uid) {
		if (uid != null) {
			return articleDao.selectByUid(uid);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> selectRecent(boolean isReply) {
		return articleDao.selectRecent(isReply);
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> selectReplies(Integer ref) {
		if (ref != null) {
			return articleDao.selectReplies(ref);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> select(boolean isReply) {
		return articleDao.select(isReply);
	}

	@Transactional(readOnly = true)
	public ArticleBean select(Integer aid) {
		return articleDao.select(aid);
	}

	private boolean editTags(Set<TagBean> tags, ArticleBean article) {
		if (article != null) {
			Integer aid = article.getAid();
			Set<Integer> oldTags = new HashSet<Integer>();
			for (TagDetailBean oldTag : tagDetailService.selectByAid(article.getAid()))
				oldTags.add(oldTag.getTid());
			
			for (TagBean tag : tags) {
				Integer tid = tag.getTid();
				if (!oldTags.contains(tid)) {
					tagDetailService.insert(aid, tid);
				}	
				oldTags.remove(tid);
			}
			for (Integer tid : oldTags) {
				tagDetailService.delete(aid, tid);
			}
			return true;
		}
		return false;
	}
}
