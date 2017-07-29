package cc.ar.messageboard.article;

import java.util.LinkedList;

import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

public class ArticleTreeNode extends DefaultTreeNode<ArticleBean> {
	
	private static final long serialVersionUID = 1L;


	public ArticleTreeNode(ArticleBean data) {
		super(data, new LinkedList<TreeNode<ArticleBean>>());
	}
	
	public boolean isLeaf() {
		return getData() != null && getData().getReplies().isEmpty();
	}

}
