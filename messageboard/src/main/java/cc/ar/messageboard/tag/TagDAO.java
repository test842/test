package cc.ar.messageboard.tag;

public interface TagDAO {
	TagBean select(Integer tid);
	TagBean selectByTagName(String name);
	TagBean insert(String name);
	
}
