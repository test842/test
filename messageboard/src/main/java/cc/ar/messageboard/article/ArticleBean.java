package cc.ar.messageboard.article;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import cc.ar.messageboard.user.UserBean;

@Entity
@Table(name = "ARTICLE")
public class ArticleBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer aid;
	private String title;
	private String content;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "ref")
	private ArticleBean parent;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "uid")
	private UserBean userBean;
	
	@Column(insertable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date date;
	
	@Column(insertable = false)
	private Boolean visible;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parent")
	@OrderBy("date desc")
	@Where(clause = "visible = 1")
	private List<ArticleBean> replies; 
	
	public ArticleBean getParent() {
		return parent;
	}
	public void setParent(ArticleBean parent) {
		this.parent = parent;
	}
	public List<ArticleBean> getReplies() {
		return replies;
	}
	public void setReplies(List<ArticleBean> replies) {
		this.replies = replies;
	}
	
	public UserBean getUserBean() {
		return userBean;
	}
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
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
