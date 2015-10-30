package fr.univmobile.backend.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "user")
public class User extends AuditableEntity {
	
	public final static String STUDENT = "student";
	public final static String ADMIN = "admin";
	public final static String SUPERADMIN = "superadmin";
	public final static String LIBRARIAN = "librarian";
	

	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true, nullable = false)
	private String username;
	@Column(nullable = false)
	private String displayName;
	@Column(nullable = false)
	@JsonIgnore
	private String role;
	@JsonIgnore
	private String password;
	@Column(name = "classicloginallowed", nullable = false)
	@JsonIgnore
	private boolean classicLoginAllowed = false;
	@Column(name = "remoteuser", unique = true, nullable = false)
	@JsonIgnore
	private String remoteUser;
	private String title;
	private String email;
	@Column(name = "profileimageurl")
	private String profileImageUrl;
	private String description;
	@Column(name = "twitterscreenname")
	@JsonIgnore
	private String twitterScreenName;
	@ManyToOne
	@JoinColumn(name = "university_id", nullable = false)
	@JsonIgnore
	private University university;
	@ManyToOne
	@JoinColumn(name = "secondaryuniversity_id")
	@JsonIgnore
	private University secondaryUniversity;
	@Column(name = "notifications_read_date", nullable = false, columnDefinition="TIMESTAMP DEFAULT '1970-01-01'")
	private Date notificationsReadDate = new Date(1000 * 60 * 60 * 24 * 365 * 10);
	@OneToMany(mappedBy="user", fetch = FetchType.EAGER)
	@JsonIgnore
	private Collection<Bookmark> bookmarks = new ArrayList<Bookmark>();

	@Override
	public String toString() {
		return this.getRemoteUser(); // TODO: Done this way tu support hybrid auth. Remove later after trashing legacy authentication.
		/*
		return String.format(
				"User[id='%s', displayName='%s', role='%s', university='%s']",
				id, displayName, role, university.getId());
		*/
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isClassicLoginAllowed() {
		return classicLoginAllowed;
	}

	public void setClassicLoginAllowed(boolean classicLoginAllowed) {
		this.classicLoginAllowed = classicLoginAllowed;
	}

	public String getRemoteUser() {
		return remoteUser;
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public String getTitle() {
		return title;
	}

	public void setTitleCivilite(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTwitterScreenName() {
		return twitterScreenName;
	}

	public void setTwitterScreenName(String twitterScreenName) {
		this.twitterScreenName = twitterScreenName;
	}

	public University getUniversity() {
		return university;
	}

	public void setUniversity(University university) {
		this.university = university;
	}

	public University getSecondaryUniversity() {
		return secondaryUniversity;
	}

	public void setSecondaryUniversity(University secondaryUniversity) {
		this.secondaryUniversity = secondaryUniversity;
	}
	
	public boolean isSuperAdmin() {
		return this.role.equals(SUPERADMIN);
	}

	public boolean isAdmin() {
		return this.role.equals(ADMIN);
	}

	public boolean isStudent() {
		return this.role.equals(STUDENT);
	}

	public boolean isLibrarian() {
		return this.role.equals(LIBRARIAN);
	}
	
	public Date getNotificationsReadDate(){
		return notificationsReadDate;
	}

	public void setNotificationsReadDate(Date date){
		this.notificationsReadDate = date;
	}

	public Collection<Bookmark> getBookmarks(){
		return bookmarks;
	}
}
