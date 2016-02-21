package com.lixy.ftapi.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lixy.ftapi.type.AuthorityType;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user", catalog = "")
public class User extends BaseEntity implements UserDetails, Serializable{
	private static final long serialVersionUID = -1814215056161163500L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Basic(optional = false)
	@Column(name = "username")
	private String username;
	
	@JsonIgnore
	@Column(name = "password")
	private String password;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "surname")
	private String surname;
	
	@Column(name = "register_channel")
	private String registerChannel;
	
	@Column(name = "fb_profile_id")
	private Long fbProfileId;
	
	@Column(name = "fb_profile_picture_url")
	private String fbProfilePictureUrl;
	
	@JsonIgnore
	@Column(name = "fb_user_access_token")
	private String fbUserAccessToken;
	
	@JsonIgnore
	@Column(name = "fb_total_friend_count")
	private Long fbTotalFriendCount;
	
	@Column(name = "locale")
	private String locale;
	
	@Column(name = "mobile_phone")
	private String mobilePhone;
	
	@JsonIgnore
	@Column(name = "register_ip")
	private String registerIp;
	
	@Column(name = "status")
	private Long status;
	
	@JsonInclude
	public List<String> getAuthoritiesStr(){
		List<String> auths = new ArrayList<>();
		Set<Authority> authoritySet = getAuthoritySet();
		
		if(authoritySet == null)
			return null;
		
		for (Authority authority : authoritySet) {
			auths.add(authority.getAuthorityName());
		}
		return auths;
	}
	
	public boolean isRoot(){
		return hasAuthority(AuthorityType.ROLE_ROOT);
	}
	
	public boolean isCommenter(){
		return hasAuthority(AuthorityType.ROLE_COMMENTER);
	}
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<UserAuthority> userAuthorityList;
	
	@JsonIgnore
	public Set<Authority> getAuthoritySet(){
		Set<Authority> authoritySet = new HashSet<>();
		if(userAuthorityList != null) {
			for (UserAuthority uauth : userAuthorityList) {
				if(uauth.getStatus().longValue() == 0)
					authoritySet.add(uauth.getAuthority());
			}
		}
		return authoritySet;
	}

	public void setUserAuthorityList(List<UserAuthority> userAuthorityList) {
		this.userAuthorityList = userAuthorityList;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public String getRegisterIp() {
		return registerIp;
	}

	public void setRegisterIp(String registerIp) {
		this.registerIp = registerIp;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Long getFbProfileId() {
		return fbProfileId;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public void setFbProfileId(Long fbProfileId) {
		this.fbProfileId = fbProfileId;
	}

	public String getFbProfilePictureUrl() {
		return fbProfilePictureUrl;
	}

	public String getRegisterChannel() {
		return registerChannel;
	}

	public void setRegisterChannel(String registerChannel) {
		this.registerChannel = registerChannel;
	}

	public void setFbProfilePictureUrl(String fbProfilePictureUrl) {
		this.fbProfilePictureUrl = fbProfilePictureUrl;
	}

	public String getFbUserAccessToken() {
		return fbUserAccessToken;
	}

	public void setFbUserAccessToken(String fbUserAccessToken) {
		this.fbUserAccessToken = fbUserAccessToken;
	}

	public Long getFbTotalFriendCount() {
		return fbTotalFriendCount;
	}

	public void setFbTotalFriendCount(Long fbTotalFriendCount) {
		this.fbTotalFriendCount = fbTotalFriendCount;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return (Collection<? extends GrantedAuthority>) getAuthoritySet();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return false;
	}
	
	public boolean hasAuthority(AuthorityType authority){
		for (UserAuthority userAuthority : userAuthorityList) {
			if(userAuthority.getStatus().longValue() == 0L && userAuthority.getAuthority().getAuthorityName().equals(authority.toString()))
				return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "User [" + username + "(id:"+id+")]";
	}
	
	

}
