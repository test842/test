package cc.ar.messageboard.article;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

	@Autowired
	private TagDetailService tagDetailService;

	@Transactional
	public boolean insert(ArticleBean bean, Set<TagBean> tags) {
		if (bean != null) {
			logger.info("insert article, article's title = {}, author id = {}", bean.getTitle(), bean.getUid());
			return articleDao.insert(bean) && editTags(tags, bean);
		}
		logger.error("insert article but bean is null");
		return false;
	}

	@Transactional
	public boolean update(ArticleBean bean, Set<TagBean> tags) {
		if (bean != null && articleDao.selectReplies(bean.getAid()).isEmpty()) {
			logger.info("edit article, aid = {}", bean.getAid()); 
			return articleDao.update(bean) && editTags(tags, bean);
		}
		logger.error("edit article but bean is null or cannot edit"); //
		return false;
	}

	@Transactional
	public boolean delete(Integer aid) {
		logger.info("hide article and its replies, aid = {}", aid);
		if (aid != null) {
			boolean result = true;
			logger.debug("hide replies of article recusively");
			for (ArticleBean bean : articleDao.selectReplies(aid)) {
				logger.debug("hide reply (aid = {}) of article (aid = {})", bean.getAid(), aid);
				result = delete(bean.getAid()) && result;
			}
			logger.debug("all replies of article (aid = {}) have been hidden", aid);
			return articleDao.delete(aid) && result;
		}
		logger.error("aid is null");
		return false;
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> selectByUid(Integer uid) {
		logger.debug("select articles by user, uid = {}", uid);
		if (uid != null) {
			return articleDao.selectByUid(uid);
		}
		logger.error("uid is null");
		return null;
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> selectRecent(boolean isReply) {
		logger.debug("select recent {}", isReply ? "replies" : "topics");
		return articleDao.selectRecent(isReply);
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> selectReplies(Integer ref) {
		logger.debug("select replies of article, aid = {}", ref);
		if (ref != null) {
			return articleDao.selectReplies(ref);
		}
		logger.error("ref is null");
		return null;
	}

	@Transactional(readOnly = true)
	public List<ArticleBean> select(boolean isReply) {
		logger.debug("select all {}", isReply ? "replies" : "topics");
		return articleDao.select(isReply);
	}

	@Transactional(readOnly = true)
	public ArticleBean select(Integer aid) {
		logger.debug("select article by aid = {}", aid);
		return articleDao.select(aid);
	}

	private boolean editTags(Set<TagBean> tags, ArticleBean article) {
		logger.debug("update tags for article");
		if (article != null) {
			Integer aid = article.getAid();
			Set<Integer> oldTags = new HashSet<Integer>();
			logger.debug("collect old tags of the article (aid = {})", aid);
			for (TagDetailBean oldTag : tagDetailService.selectByAid(article.getAid()))
				oldTags.add(oldTag.getTid());
			
			if (tags != null) {
				logger.debug("insert new tags");
				for (TagBean tag : tags) {
					logger.debug("new tag, tid = {}", tag.getTid());
					Integer tid = tag.getTid();
					if (!oldTags.contains(tid)) 
						tagDetailService.insert(aid, tid);
					else	
						oldTags.remove(tid);
				}
			}
			logger.debug("delete old tags");
			for (Integer tid : oldTags) {
				logger.debug("delete old tag, tid = {}", tid);
				tagDetailService.delete(aid, tid);
			}
			return true;
		}
		logger.error("article is null");
		return false;
	}
}
