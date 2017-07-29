package cc.ar.messageboard.article;

import java.util.List;

public interface ArticleDAO {

	boolean insert(ArticleBean bean);
	boolean delete(Integer aid);
	boolean update(ArticleBean bean);
	List<ArticleBean> selectByUid(Integer uid);
	List<ArticleBean> selectReplies(Integer ref);
	List<ArticleBean> select(boolean isReply);
	List<ArticleBean> selectRecent(boolean isReply);
	ArticleBean select(Integer aid);

}
