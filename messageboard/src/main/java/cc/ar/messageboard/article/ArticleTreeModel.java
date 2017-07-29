package cc.ar.messageboard.article;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.zul.AbstractTreeModel;


public class ArticleTreeModel extends AbstractTreeModel<ArticleBean> {
	
	private static final long serialVersionUID = 1L;
	
	private ArticleService articleService;	
	
	private ArticleBean root;
	
	private Map<ArticleBean,ArrayList<ArticleBean>> map = new HashMap<ArticleBean,ArrayList<ArticleBean>>();
	
	public ArticleTreeModel(ArticleBean root) {
		super(root);
		this.root = root;
	}
	
	public ArticleTreeModel(ArticleBean root, ArticleService articleService) {
		super(root);
		this.articleService = articleService;
		this.root = root;
	}

	public boolean isLeaf(ArticleBean node) {
		return getChildCount(node) == 0;
	}

	public ArticleBean getChild(ArticleBean parent, int index) {
		ArrayList<ArticleBean> children = map.get(parent);
		if (children == null) {
			children = new ArrayList<ArticleBean>(articleService.selectReplies(parent.getAid()));
			map.put(parent, children);
		}
		return children.get(index);
	}

	public int getChildCount(ArticleBean parent) {
		ArrayList<ArticleBean> children = map.get(parent);
		if (children == null) {
			children = new ArrayList<ArticleBean>(articleService.selectReplies(parent.getAid()));
			map.put(parent, children);
		}
		return children.size();
	}
	
	public ArticleBean getRoot() {
		return root;
	}

	public void remove(ArticleBean node) {
		map.remove(node);
	}
	
}
