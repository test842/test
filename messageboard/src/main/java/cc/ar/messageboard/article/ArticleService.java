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
import cc.ar.messageboard.tag.TagService;
import cc.ar.messageboard.tagdetail.TagDetailBean;
import cc.ar.messageboard.tagdetail.TagDetailService;

@Service
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class ArticleService {
	@Autowired
	private ArticleDAO articleDao;
	
	@Autowired
	private TagService tagService;
	
	@Autowired
	private TagDetailService tagDetailService;
	
	@Transactional
	public boolean insert(ArticleBean bean, String tags) {
		if (bean != null) {
			return articleDao.insert(bean) && editTags(tags, bean);
		}
		return false;
	}
	
	@Transactional
	public boolean update(ArticleBean bean, String tags) {
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
		List<ArticleBean> list = articleDao.select(isReply);
		return list.subList(0, Math.min(list.size(), 10));
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
	
	private boolean editTags(String tags, ArticleBean article) { 
		if (article != null) {
			Set<TagDetailBean> oldTags = new HashSet<TagDetailBean>(tagDetailService.selectByAid(article.getAid()));
			
			if (tags != null && tags.trim().length() > 0)
				for (String tag : tags.trim().split(" ")) {
					TagDetailBean bean = new TagDetailBean();
					bean.setAid(article.getAid());
					TagBean tagBean = tagService.selectByTagName(tag);
					if (tagBean == null) 
						tagBean = tagService.insert(tag);
					bean.setTid(tagBean.getTid());
					if (!oldTags.contains(bean)) 			
						tagDetailService.insert(bean);
					oldTags.remove(bean);
				}
			for (TagDetailBean oldTag : oldTags) {
				tagDetailService.delete(oldTag);
			}
		}
		return true;
	}
}
