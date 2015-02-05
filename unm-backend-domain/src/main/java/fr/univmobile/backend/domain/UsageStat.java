package fr.univmobile.backend.domain;

import javax.persistence.*;

@Entity
@Table(name = "usagestat")
public class UsageStat extends AuditableEntity {

    public enum UsageType {
        A, I, W
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private University university;

    private UsageType source;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public University getUniversity(){
        return university;
    }

    public void setUniversity(University university){
        this.university = university;
    }

    public UsageType getSource(){
        return source;
    }

    public void setSource(UsageType source){
        this.source = source;
    }

}

