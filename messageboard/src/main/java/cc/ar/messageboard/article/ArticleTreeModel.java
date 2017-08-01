package cc.ar.messageboard.article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.AbstractTreeModel;


public class ArticleTreeModel extends AbstractTreeModel<ArticleBean> {
	
	private static final long serialVersionUID = 1L;
	
	private ArticleService articleService;	
	
	private ArticleBean root;
	
	private Map<Integer, ArrayList<ArticleBean>> map = new HashMap<Integer, ArrayList<ArticleBean>>();
	
	public ArticleTreeModel(ArticleBean root) {
		super(root);
		this.root = root;
	}
	
	public ArticleTreeModel(ArticleBean root, ArticleService articleService, Map<Integer, ArrayList<ArticleBean>> map) {
		super(root);
		this.articleService = articleService;
		this.root = root;
		if (map != null)
			this.map = map;
	}

	public boolean isLeaf(ArticleBean node) {
		return getChildCount(node) == 0;
	}

	public ArticleBean getChild(ArticleBean parent, int index) {
		Integer aid = parent.getAid();
		ArrayList<ArticleBean> children = map.get(aid);
		if (children == null) {
			children = new ArrayList<ArticleBean>(articleService.selectReplies(aid));
			map.put(aid, children);
		}
		return children.get(index);
	}

	public int getChildCount(ArticleBean parent) {
		Integer aid = parent.getAid();
		ArrayList<ArticleBean> children = map.get(aid);
		if (children == null) {
			children = new ArrayList<ArticleBean>(articleService.selectReplies(aid));
			map.put(aid, children);
		}
		return children.size();
	}
	
	public ArticleBean getRoot() {
		return root;
	}
	
	public Map<Integer, ArrayList<ArticleBean>> getMap() {
		return map;
	}
	
	public void setMap(Map<Integer, ArrayList<ArticleBean>> map) {
		this.map = map;
	}
	
}
