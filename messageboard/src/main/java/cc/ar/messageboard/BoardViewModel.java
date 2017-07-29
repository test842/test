package cc.ar.messageboard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;

import cc.ar.messageboard.article.ArticleBean;
import cc.ar.messageboard.article.ArticleService;
import cc.ar.messageboard.article.ArticleTreeNode;
import cc.ar.messageboard.tag.TagService;
import cc.ar.messageboard.tagdetail.TagDetailBean;
import cc.ar.messageboard.tagdetail.TagDetailService;
import cc.ar.messageboard.user.UserBean;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class BoardViewModel {

	@WireVariable
	private TagService tagService;

	@WireVariable
	private TagDetailService tagDetailService;

	@WireVariable
	private ArticleService articleService;

	private TreeModel<TreeNode<ArticleBean>> articleTreeModel;

	private TreeModel<TreeNode<ArticleBean>> currentArticleTreeModel;

	private TreeNode<ArticleBean> articleTreeNode;

	private TreeNode<ArticleBean> currentNode;

	private TreeNode<ArticleBean> targetNode;

	private ArticleBean root;

	private UserBean userBean;

	private ArticleBean newArticle;

	private String tags;

	private EventQueue<Event> post;

	private ScheduledExecutorService scheduler;

	private ListModelList<ArticleBean> allTopics;

	private ScheduledFuture<?> schedule; //

	public void setCurrentNode(TreeNode<ArticleBean> currentNode) {
		this.currentNode = currentNode;
	}

	public TreeNode<ArticleBean> getCurrentNode() {
		return currentNode;
	}

	public ScheduledFuture<?> getSchedule() {
		return schedule;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public TreeModel<TreeNode<ArticleBean>> getCurrentArticleTreeModel() {
		return currentArticleTreeModel;
	}

	public ArticleBean getNewArticle() {
		return newArticle;
	}

	public void setNewArticle(ArticleBean bean) {
		newArticle = bean;
	}

	private boolean treeview;

	public UserBean getUser() {
		return userBean;
	}

	public boolean isTreeview() {
		return treeview;
	}

	public TreeModel<TreeNode<ArticleBean>> getArticlesModel() {
		return articleTreeModel;
	}

	public List<ArticleBean> getRecentTopics() { //
		return articleService.selectRecent(false);
	}

	public List<ArticleBean> getRecentReplies() {
		return articleService.selectRecent(true);
	}

	public List<ArticleBean> getRelateArticles() {
		List<ArticleBean> list = articleService.selectByUid(userBean.getUid());
		return list.subList(0, Math.min(list.size(), 10));
	}

	public ListModelList<ArticleBean> getAllTopics() {
		return allTopics;
	}

	@Init
	public void init() {
		userBean = (UserBean) Sessions.getCurrent().getAttribute("currentUser");
		root = articleService.selectByUid(0).get(0);
		articleTreeNode = constructTreeNode(root);
		articleTreeModel = new DefaultTreeModel<ArticleBean>(articleTreeNode);
		targetNode = articleTreeNode;
		allTopics = new ListModelList<ArticleBean>(articleService.select(false));
		treeview = true;
		newArticle = new ArticleBean();
		newArticle.setUserBean(userBean);
		newArticle.setParent(root);
		post = EventQueues.lookup("post", EventQueues.APPLICATION, true);
		post.subscribe(new EventListener<Event>() {
			public void onEvent(Event event) {
				String eventName = event.getName();
				Map<String, Object> map = (Map<String, Object>) event.getData();
				TreeNode<ArticleBean> node = (TreeNode<ArticleBean>) map.get("node");
				ArticleBean newPost = (ArticleBean) map.get("article");
				if (newPost != null && newPost.equals(newArticle)) { //global
					newArticle = new ArticleBean();
					newArticle.setUserBean(userBean);
					newArticle.setParent(root);
					schedule = null;
					currentNode = null;
					currentArticleTreeModel = null;
					tags = null; 
					BindUtils.postNotifyChange(null, null, BoardViewModel.this, "tags");
					BindUtils.postNotifyChange(null, null, BoardViewModel.this, "newArticle");
					BindUtils.postNotifyChange(null, null, BoardViewModel.this, "schedule");
					BindUtils.postNotifyChange(null, null, BoardViewModel.this, "currentNode");
				}
				BindUtils.postNotifyChange(null, null, BoardViewModel.this, "relateArticles"); 
				BindUtils.postNotifyChange(null, null, BoardViewModel.this, "recentReplies");
				BindUtils.postNotifyChange(null, null, BoardViewModel.this, "recentTopics");

				if (eventName.equals("onEdit")) {
					node.setData(articleService.select(newPost.getAid()));
				} else if (eventName.equals("onDelete")) {
					TreeNode<ArticleBean> parent = node.getParent();
					parent.remove(node); //
					parent.setData(articleService.select(newPost.getParent().getAid()));
				} else {
					ArticleTreeNode newNode = new ArticleTreeNode(articleService.select(newPost.getAid()));
					node.setData(articleService.select(newPost.getParent().getAid()));
					node.insert(newNode,0);
				}
				
//				articleTreeNode = constructTreeNode(root);
//				articleTreeModel = new DefaultTreeModel<ArticleBean>(articleTreeNode);
				BindUtils.postNotifyChange(null, null, BoardViewModel.this, "articlesModel");

				if (newPost.getParent().getAid().equals(0)) {
					if (eventName.equals("onEdit"))
						allTopics.notifyChange(newPost);
					else if (eventName.equals("onDelete"))
						allTopics.remove(newPost);
					else
						allTopics.add(0, newPost);
				}

			}
		});
		scheduler = Executors.newScheduledThreadPool(1);
	}

	@Command
	@NotifyChange("treeview")
	public void check(@BindingParam("item") String item) {
		if ("tree".equals(item))
			treeview = true;
		else
			treeview = false;
	}

	@Command
	@NotifyChange("currentNode")
	public void close() {
		currentNode = null;
	}

	@Command
	@NotifyChange({ "currentArticleTreeModel", "currentNode" }) //
	public void doSelect(@BindingParam("index") int index) {
		currentNode = articleTreeNode.getChildAt(index);
		currentArticleTreeModel = new DefaultTreeModel<ArticleBean>((ArticleTreeNode) currentNode.clone());
	}

	@Command
	@NotifyChange("newArticle")
	public void edit(@BindingParam("node") TreeNode<ArticleBean> node) {
		ArticleBean article = node.getData();
		if (article.getUserBean().getUid().equals(userBean.getUid())) {
			newArticle = article;
			targetNode = node;
		}
	}

	@Command
	@NotifyChange("newArticle")
	public void reply(@BindingParam("node") TreeNode<ArticleBean> node) {
		reset();
		newArticle.setParent(node.getData());
		targetNode = node;
	}

	@Command
	@NotifyChange({ "currentNode", "currentArticleTreeModel" })
	public void delete(@BindingParam("node") TreeNode<ArticleBean> node) {
		articleService.delete(node.getData().getAid());
		Map<String, Object> map = new HashMap<String, Object>(2);
		map.put("node", node);
		map.put("article", node.getData());
		post.publish(new Event("onDelete", null, map));
		currentNode = null;
		currentArticleTreeModel = null;
	}

	@Command
	@NotifyChange("newArticle")
	public void reset() {
		targetNode = articleTreeNode;
		newArticle = new ArticleBean();
		newArticle.setUserBean(userBean);
		newArticle.setParent(root);
	}

	@Command
	@NotifyChange({ "schedule" })
	public void post() {
		if (schedule == null) {
			schedule = scheduler.schedule(new Runnable() {
				public void run() {
					Map<String, Object> map = new HashMap<String, Object>(2);
					map.put("node", targetNode);
					if (newArticle.getAid() != null) {
						articleService.update(newArticle, tags);	
						map.put("article", newArticle);
						post.publish(new Event("onEdit", null, map));
					} else {
						articleService.insert(newArticle, tags);
						map.put("article", newArticle);
						post.publish(new Event("onPost", null, map));
					}
			//		BindUtils.postGlobalCommand(null, null, cmdName, args);
				}
			}, 1, TimeUnit.SECONDS);
		} else {
			schedule.cancel(true);
			schedule = null;
		}
	}

	private ArticleTreeNode constructTreeNode(ArticleBean root) {
		ArticleTreeNode rootNode = new ArticleTreeNode(root);
		LinkedList<ArticleTreeNode> queue = new LinkedList<ArticleTreeNode>();
		queue.add(rootNode);
		while (!queue.isEmpty()) {
			ArticleTreeNode node = queue.remove();
			for (ArticleBean bean : articleService.selectReplies(node.getData().getAid())) { //
				StringBuilder tags = new StringBuilder();
				for (TagDetailBean tag : tagDetailService.selectByAid(bean.getAid())) {
					tags.append(tagService.select(tag.getTid())).append(" ");
				}
				bean.setTags(tags.toString());
				ArticleTreeNode child = new ArticleTreeNode(bean);
				node.add(child);
				queue.add(child);
			}
		}
		return rootNode;
	}

}
