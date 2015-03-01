package fr.univmobile.backend.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "authentication_token")
public class Token  extends AuditableEntity{
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private String tokenKey;
    
    @Column(nullable = false)
    private Date startedAt;
    
    @ManyToOne
    private User user;

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

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }

	public String getTokenKey() {
		return tokenKey;
	}

	public void setTokenKey(String tokenKey) {
		this.tokenKey = tokenKey;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}

}
