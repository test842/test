package cc.ar.messageboard.tag;

import java.util.List;

public interface TagDAO {
	TagBean select(Integer tid);
	TagBean selectByTagName(String name);
	TagBean insert(String name);
	List<TagBean> select();
	
}
