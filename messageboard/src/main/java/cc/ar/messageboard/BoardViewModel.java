package cc.ar.messageboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.TreeModel;

import cc.ar.messageboard.article.ArticleBean;
import cc.ar.messageboard.article.ArticleService;
import cc.ar.messageboard.article.ArticleTreeModel;
import cc.ar.messageboard.tag.TagBean;
import cc.ar.messageboard.tag.TagService;
import cc.ar.messageboard.tagdetail.TagDetailBean;
import cc.ar.messageboard.tagdetail.TagDetailService;
import cc.ar.messageboard.user.UserBean;
import cc.ar.messageboard.user.UserService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class BoardViewModel {

	@WireVariable
	private TagService tagService;

	@WireVariable
	private TagDetailService tagDetailService;

	@WireVariable
	private ArticleService articleService;

	@WireVariable
	private UserService userService;

	private TreeModel<ArticleBean> articleTreeModel;

	private ArticleBean currentArticle;

	private ArticleBean root;

	private UserBean currentUser;

	private ArticleBean newArticle;

	private EventQueue<Event> eventQueue;

	private ScheduledExecutorService scheduler;

	private ListModelList<ArticleBean> allTopics;

	private ScheduledFuture<?> schedule;

	private List<ArticleBean> recentTopics;

	private List<ArticleBean> recentReplies;

	private List<ArticleBean> relateArticles;

	private int view;

	private List<TagBean> tagsModel;

	private HashMap<Integer, TagBean> tagMap;

	private Set<TagBean> tags;
	
	private HashMap<Integer, ArticleBean> articlesCache;

	public void setView(int view) {
		this.view = view;
	}

	public Set<TagBean> getTags() {
		return tags;
	}

	public void setTags(Set<TagBean> tags) {
		this.tags = tags;
	}

	public UserBean getCurrentUser() {
		return currentUser;
	}

	public List<TagBean> getTagsModel() {
		return tagsModel;
	}

	public int getView() {
		return view;
	}

	public ArticleBean getCurrentArticle() {
		return currentArticle;
	}

	public void setCurrentArticle(ArticleBean currentArticle) {
		this.currentArticle = currentArticle;
	}

	public ArticleBean getNewArticle() {
		return newArticle;
	}

	public void setNewArticle(ArticleBean newArticle) {
		this.newArticle = newArticle;
	}

	public TreeModel<ArticleBean> getArticleTreeModel() {
		return articleTreeModel;
	}

	public ListModelList<ArticleBean> getAllTopics() {
		return allTopics;
	}

	public ScheduledFuture<?> getSchedule() {
		return schedule;
	}

	public List<ArticleBean> getRecentTopics() {
		return recentTopics;
	}

	public List<ArticleBean> getRecentReplies() {
		return recentReplies;
	}

	public List<ArticleBean> getRelateArticles() {
		return relateArticles;
	}

	@Init
	public void init() {
		currentUser = (UserBean) Sessions.getCurrent().getAttribute("currentUser");
		root = articleService.selectByUid(0).get(0);
		view = 0;
		articlesCache = new HashMap<Integer, ArticleBean>();

		recentReplies = articleService.selectRecent(true);
		recentTopics = articleService.selectRecent(false);
		List<ArticleBean> relateList = articleService.selectByUid(currentUser.getUid());
		relateArticles = relateList.subList(0, Math.min(relateList.size(), 10));

		List<TagBean> tagList = tagService.select();
		tagMap = new HashMap<Integer, TagBean>();
		for (TagBean tag : tagList)
			tagMap.put(tag.getTid(), tag);
		tagsModel = tagList;

		tags = new HashSet<TagBean>();

		allTopics = new ListModelList<ArticleBean>(articleService.select(false));
		articleTreeModel = new ArticleTreeModel(root, articleService);

		eventQueue = EventQueues.lookup("post", EventQueues.APPLICATION, true);
		eventQueue.subscribe(new EventListener<Event>() {
			public void onEvent(Event event) {
				Map<String, Object> map = new HashMap<String, Object>(1);
				map.put("aid", event.getData());
				BindUtils.postGlobalCommand(null, null, "update", map);
			}
		});
		scheduler = Executors.newScheduledThreadPool(1);
	}

	@Command
	@NotifyChange({ "schedule" })
	public void post() {
		if (schedule == null) {
			schedule = scheduler.schedule(new Runnable() {
				public void run() {
					if (newArticle.getAid() != null) {
						articleService.update(newArticle, tags);
					} else {
						articleService.insert(newArticle, tags);
						eventQueue.publish(new Event("onPost", null, newArticle.getAid()));
					}
//					BindUtils.postGlobalCommand(null, null, "clear", null);
				}
			}, 3, TimeUnit.SECONDS);
		}
	}
	
	@GlobalCommand
	@NotifyChange({ "articleTreeModel" })
	public void update(@BindingParam("aid") Integer aid ) {
		ArticleBean newPost = articleService.select(aid);
		if (newPost.getRef().equals(0)) {
			allTopics.add(0, newPost);
			updateList(recentTopics, newPost);
			BindUtils.postNotifyChange(null, null, this, "recentTopics");
		} else {
			updateList(recentReplies, newPost);
			BindUtils.postNotifyChange(null, null, this, "recentReplies");
		}
		if (newPost.getUid().equals(currentUser.getUid())) {
			updateList(relateArticles, newPost);
			BindUtils.postNotifyChange(null, null, this, "relateArticles");
		}
		articleTreeModel = new ArticleTreeModel(root, articleService);
	}
	
	private void updateList(List<ArticleBean> list, ArticleBean bean) {
		if (!list.isEmpty())
			list.remove(list.size() - 1);
		list.add(0, bean);
	}
	
	@GlobalCommand
	@NotifyChange({ "schedule" , "newArticle" })
	public void clear() {
		System.out.println("clear");
		schedule = null;
		newArticle = null;
	}

	@Command
	@NotifyChange("schedule")
	public void cancel() {
		if (schedule != null) {
			schedule.cancel(true);
			schedule = null;
		}
	}

	@Command
	@NotifyChange("newArticle")
	public void newPost() {
		newArticle = new ArticleBean();
		newArticle.setRef(0);
		newArticle.setUid(currentUser.getUid());
		tags.clear();
	}

	@Command
	@NotifyChange({ "currentArticle", "newArticle" })
	public void doListSelect() {
		doTreeSelect();
		setReplyDetail(currentArticle);
	}

	@Command
	@NotifyChange({ "currentArticle", "newArticle" })
	public void doTreeSelect() {
		currentArticle = setArticleDetail(currentArticle);
		newArticle = null;
	}
	
	@Command
	@NotifyChange("currentArticle")
	public void clearCurrentArticle() {
		currentArticle = null;
	}
	
	@Command
	public void open(@BindingParam("article") ArticleBean article) {
		setReplyDetail(article);
	}
	
	private void setReplyDetail(ArticleBean article) {
		List<ArticleBean> replies = article.getReplies();
		List<ArticleBean> list = new ArrayList<ArticleBean>();
		if (replies != null && !replies.isEmpty())
		for (ArticleBean reply : replies) {
			list.add(setArticleDetail(reply));
		}
		article.setReplies(list);
	}
	
	private ArticleBean setArticleDetail(ArticleBean article) {
		if (article == null)
			return article;
		Integer aid = article.getAid();	
		ArticleBean current = articlesCache.get(aid);
		if (current == null) {
			current = article;
			current.setUser(userService.selectByUid(current.getUid()));

			StringBuilder sb = new StringBuilder();
			for (TagDetailBean bean : tagDetailService.selectByAid(aid))
				sb.append(tagMap.get(bean.getTid())).append(" ");
			current.setTags(sb.toString());

			current.setReplies(articleService.selectReplies(aid));

			current.setParent(articleService.select(current.getRef()));
			articlesCache.put(aid, current);
		}
		return current;
	}

	@Command
	@NotifyChange("currentArticle")
	public void close() {
		currentArticle = null;
	}

	@Command
	@NotifyChange({ "newArticle", "tags" })
	public void edit(@BindingParam("article") ArticleBean article) {
		newArticle = article;
		tags.clear();
		for (TagDetailBean tag : tagDetailService.selectByAid(article.getAid()))
			tags.add(tagMap.get(tag.getTid()));

	}

	@Command
	@NotifyChange("currentArticle")
	public void delete(@BindingParam("article") ArticleBean article) {
		articleService.delete(article.getAid());
		currentArticle = null;
	}

	@Command
	@NotifyChange({ "newArticle", "tags" })
	public void reply(@BindingParam("article") ArticleBean article) {
		newArticle = new ArticleBean();
		newArticle.setUid(currentUser.getUid());
		newArticle.setRef(article.getAid());
		newArticle.setParent(article);
		tags.clear();
	}

}
