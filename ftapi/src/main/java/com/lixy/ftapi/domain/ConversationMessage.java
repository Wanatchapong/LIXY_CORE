package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "conversation_message", catalog = "")
public class ConversationMessage implements Serializable {

	private static final long serialVersionUID = -4446929343152142811L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JsonIgnore
	@Column(name = "conversation_id")
	private Long conversationId;

	@JsonIgnore
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private User user;

	@JsonIgnore
	@JoinColumn(name = "vcommenter_id", referencedColumnName = "id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private VirtualCommenter virtualCommenter;

	@Column(name = "reply_text")
	private String replyText;

	@Column(name = "reply_type_id")
	private Long replyTypeId;

	@Column(name = "status")
	private Long status;

	@JsonIgnore
	@Column(name = "ip")
	private String ip;

	@Column(name = "CREATED_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date createdDate;
	
	@OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "conversationMessage", fetch = FetchType.EAGER)
    @Fetch(value = org.hibernate.annotations.FetchMode.SUBSELECT)
    private List<ConversationMessageDetail> conversationMessageDetails;
	
	@Transient
	private String senderFullName;
	
	@Transient
	private Long senderId;
	
	@Transient
	private String vCommenterFullName;
	
	public String getvCommenterFullName() {
		return virtualCommenter.getFullName();
	}

	public String getSenderFullName(){
		return user.getFullName();
	}
	
	public Long getSenderId(){
		return user.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public VirtualCommenter getVirtualCommenter() {
		return virtualCommenter;
	}

	public void setVirtualCommenter(VirtualCommenter virtualCommenter) {
		this.virtualCommenter = virtualCommenter;
	}

	public String getReplyText() {
		return replyText;
	}

	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}

	public Long getReplyTypeId() {
		return replyTypeId;
	}

	public void setReplyTypeId(Long replyTypeId) {
		this.replyTypeId = replyTypeId;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
