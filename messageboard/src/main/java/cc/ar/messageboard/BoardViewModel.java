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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
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

	private static final Logger logger = LoggerFactory.getLogger(BoardViewModel.class);

	@WireVariable
	private TagService tagService;

	@WireVariable
	private TagDetailService tagDetailService;

	@WireVariable
	private ArticleService articleService;

	@WireVariable
	private UserService userService;

	private ArticleTreeModel articleTreeModel;

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
		logger.debug("board view model init");

		currentUser = (UserBean) Sessions.getCurrent().getAttribute("currentUser");
		logger.debug("current user id : {}", currentUser.getUid());
		root = articleService.selectByUid(0).get(0);
		view = 0;
		articlesCache = new HashMap<Integer, ArticleBean>();

		recentReplies = articleService.selectRecent(true);
		recentTopics = articleService.selectRecent(false);
		List<ArticleBean> relateList = articleService.selectByUid(currentUser.getUid());
		relateArticles = relateList.subList(0, Math.min(relateList.size(), 10));

		List<TagBean> tagList = tagService.select();
		tagMap = new HashMap<Integer, TagBean>(); //cache tagbeans for chosenbox
		for (TagBean tag : tagList)
			tagMap.put(tag.getTid(), tag);
		tagsModel = tagList;

		tags = new HashSet<TagBean>();

		allTopics = new ListModelList<ArticleBean>(articleService.select(false));
		articleTreeModel = new ArticleTreeModel(root, articleService);

		eventQueue = EventQueues.lookup("post", EventQueues.APPLICATION, true);
		eventQueue.subscribe(new EventListener<Event>() {
			public void onEvent(Event event) {
				String evtName = event.getName();
				logger.debug("{} event", evtName);
				
				Map<String, Object> map = new HashMap<String, Object>(2);
				map.put("aid", event.getData());
				map.put("event", evtName);
				BindUtils.postGlobalCommand(null, null, "update", map);
			}
		});
		scheduler = Executors.newScheduledThreadPool(1);
	}

	@Command
	@NotifyChange("schedule")
	public void post() {
		logger.debug("post new article");
		
		if (schedule == null) {
			logger.debug("schedule post");
			
			final Desktop desktop = Executions.getCurrent().getDesktop();
			schedule = scheduler.schedule(new Runnable() {
				public void run() {
					logger.debug("do post");
					
					if (newArticle.getAid() != null) {
						articleService.update(newArticle, tags);
						logger.debug("publish edit event");
						eventQueue.publish(new Event("onEdit", null, newArticle.getAid()));
					} else {
						articleService.insert(newArticle, tags);
						logger.debug("publish post event");
						eventQueue.publish(new Event("onPost", null, newArticle.getAid()));
					}
					try {
						logger.debug("try to gain access to current desktop");
						Executions.activate(desktop);
						BindUtils.postGlobalCommand(null, null, "clear", null);
					} catch (DesktopUnavailableException e) {
						logger.error("Desktop Unavailable Exception", e);
						e.printStackTrace();
					} catch (InterruptedException e) {
						logger.error("Interrupted Exception", e);
						e.printStackTrace();
					} finally {
						Executions.deactivate(desktop);
					}
				}
			}, 3, TimeUnit.SECONDS);
		}
	}

	@GlobalCommand
	@NotifyChange({ "articleTreeModel" })
	public void update(@BindingParam("aid") Integer aid, @BindingParam("event") String event) {
		if (event.equals("onEdit")) {
			articlesCache.remove(aid);
			return;
		}
		ArticleBean newPost = articleService.select(aid);
		if (newPost == null) {
			logger.error("new post is null");
			return;
		}
		Integer ref = newPost.getRef();
		logger.debug("update view for new post, aid = {}", aid);

		if (ref.equals(0)) {
			logger.debug("update recent topics and list view");

			allTopics.add(0, newPost);
			updateList(recentTopics, newPost);
			BindUtils.postNotifyChange(null, null, this, "recentTopics");
		} else {
			logger.debug("update recent replies");

			updateList(recentReplies, newPost);
			BindUtils.postNotifyChange(null, null, this, "recentReplies");
		
			articlesCache.remove(ref);
		}
		if (newPost.getUid().equals(currentUser.getUid())) {
			logger.debug("update realte articles");

			updateList(relateArticles, newPost);
			BindUtils.postNotifyChange(null, null, this, "relateArticles");
		}
		logger.debug("update tree view");
		
		Map<Integer, ArrayList<ArticleBean>> map = articleTreeModel.getMap();
		ArrayList<ArticleBean> list = map.remove(ref);
		if (list != null) {
			list.add(0, newPost);
			map.put(ref, list);
		}
		map.put(aid, new ArrayList<ArticleBean>(0));
		articleTreeModel.setMap(map);
	}

	private void updateList(List<ArticleBean> list, ArticleBean bean) {
		if (!list.isEmpty())
			list.remove(list.size() - 1);
		list.add(0, bean);
	}

	@GlobalCommand
	@NotifyChange({ "schedule", "newArticle", "currentArticle" })
	public void clear() {
		logger.debug("clear after post");
		articlesCache.remove(newArticle.getAid());
		articlesCache.remove(newArticle.getRef());
		schedule = null;
		newArticle = null;
		currentArticle = null;
	}

	@Command
	@NotifyChange("schedule")
	public void cancel() {
		if (schedule != null) {
			logger.debug("cancel schedule");
			schedule.cancel(true);
			schedule = null;
		}
	}

	@Command
	@NotifyChange({ "newArticle", "tags" })
	public void newPost() {
		logger.debug("create new article");
		newArticle = new ArticleBean();
		newArticle.setRef(0);
		newArticle.setUid(currentUser.getUid());
		tags.clear();
	}

	@Command
	@NotifyChange("newArticle")
	public void clearNewArticle() {
		logger.debug("clear new article");
		tags.clear();
		newArticle = null;
	}

	@Command
	@NotifyChange({ "currentArticle", "newArticle" })
	public void doListSelect() {
		logger.debug("on list select");
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
	public void open(@BindingParam("article") ArticleBean article) {
		setReplyDetail(article);
	}

	private void setReplyDetail(ArticleBean article) {
		logger.debug("get details of replies of article aid = {}", article.getAid());
		List<ArticleBean> replies = article.getReplies();
		List<ArticleBean> list = new ArrayList<ArticleBean>();
		if (replies != null && !replies.isEmpty())
			for (ArticleBean reply : replies) {
				list.add(setArticleDetail(reply));
			}
		article.setReplies(list);
	}

	private ArticleBean setArticleDetail(ArticleBean article) {
		if (article == null) {
			logger.error("article is null");
			return null;
		}
		Integer aid = article.getAid();
		logger.debug("get details of article, aid = {}", aid);
		ArticleBean current = articlesCache.get(aid); 
		if (current == null) {
			current = article;	
			
			ArticleBean temp = articleService.select(aid);
			current.setTitle(temp.getTitle());
			current.setContent(temp.getContent());
			
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
		logger.debug("clear current article");
		currentArticle = null;
	}

	@Command
	@NotifyChange({ "newArticle", "tags" })
	public void edit(@BindingParam("article") ArticleBean article) {
		if (article == null) {
			logger.error("article is null");
			return;
		}
		logger.debug("editing article,aid = {}", article.getAid());
		newArticle = article;
		tags.clear();
		for (TagDetailBean tag : tagDetailService.selectByAid(article.getAid()))
			tags.add(tagMap.get(tag.getTid()));
	}

	@Command
	@NotifyChange("currentArticle")
	public void delete(@BindingParam("article") ArticleBean article) {
		if (article == null) {
			logger.error("article is null");
			return;
		}
		articleService.delete(article.getAid());
		currentArticle = null;
	}

	@Command
	@NotifyChange({ "newArticle", "tags" })
	public void reply(@BindingParam("article") ArticleBean article) {
		if (article == null) {
			logger.error("article is null");
			return;
		}
		logger.debug("reply to article aid = {}", article.getAid());
		newArticle = new ArticleBean();
		newArticle.setUid(currentUser.getUid());
		newArticle.setRef(article.getAid());
		newArticle.setParent(article);
		tags.clear();
	}
}
