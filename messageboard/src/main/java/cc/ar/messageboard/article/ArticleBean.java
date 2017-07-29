package cc.ar.messageboard.article;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import cc.ar.messageboard.user.UserBean;

@Entity
@Table(name = "ARTICLE")
public class ArticleBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer aid;
	private String title;
	private String content;
	
	private Integer ref;
	
	private Integer uid;
	
	@Transient
	private ArticleBean parent;
	
	public ArticleBean getParent() {
		return parent;
	}
	public void setParent(ArticleBean parent) {
		this.parent = parent;
	}

	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date date;
	
	@Column(insertable = false)
	private Boolean visible;
	
	@Transient
	private UserBean user;
	
	public UserBean getUser() {
		return user;
	}
	public void setUser(UserBean user) {
		this.user = user;
	}

	@Transient
	private List<ArticleBean> replies; 
	
	
	public List<ArticleBean> getReplies() {
		return replies;
	}
	public void setReplies(List<ArticleBean> replies) {
		this.replies = replies;
	}
	
	
	
	public Integer getRef() {
		return ref;
	}
	public void setRef(Integer ref) {
		this.ref = ref;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Boolean getVisible() {
		return visible;
	}
	public Boolean isVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
	public Integer getAid() {
		return aid;
	}
	public void setAid(Integer aid) {
		this.aid = aid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public java.util.Date getDate() {
		return date;
	}
	public void setDate(java.util.Date date) {
		this.date = date;
	}
	
	@Transient
	private String tags;
	
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
