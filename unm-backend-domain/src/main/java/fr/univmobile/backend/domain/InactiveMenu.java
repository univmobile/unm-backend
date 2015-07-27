package fr.univmobile.backend.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "inactivemenu")
public class InactiveMenu {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "university_id", nullable = false)
	private University university;
	
	@ManyToOne
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;
	
	protected InactiveMenu() {
		
	}
	
	public InactiveMenu(Menu menu, University university) {
		this.menu = menu;
		this.university = university;
	}

	public Long getId() {
		return id;
	}

	public University getUniversity() {
		return university;
	}

	public Menu getMenu() {
		return menu;
	}
	
	public Long getMenuId(){
		Long menuId = null;
		if (menu != null){
			menuId = menu.getId();
		}
		return menuId;
	}
	
	public Long getUniversityId(){
		Long universityId = null;
		if (university != null){
			universityId = university.getId();
		}
		return universityId;
	}
	
	public String getUniversityTitle() {
		String universityTitle = null;
		if (university != null) {
			universityTitle = university.getTitle();
		}
		return universityTitle;
	}
	
}
