package fr.univmobile.backend.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
//import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * DO NOT FORGET TO ASK FOR ATTACHMENT FILES
 * @author odifis
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class MigrateXMLToDB {

	private static Logger logger = Logger.getLogger(MigrateXMLToDB.class
			.getName());

	private static Abdera abdera = null;
	private static MigrateXMLToDB migrateIDF = null;
	private static MigrateXMLToDB migratePoitou = null;

	private static ClassPathXmlApplicationContext applicationContext = null;

	private static Map<Long, Region> regions = new HashMap<Long, Region>();
	private Map<Integer, Category> categories = new HashMap<Integer, Category>();

	private Map<String, User> users = new HashMap<String, User>();
	private static User adminUser;

	UserRepository userRepository;
	private static RegionRepository regionRepository;
	static UniversityRepository universityRepository;
	CategoryRepository categoryRepository;
	private Region region;
	private Connection connection;
	static PoiRepository poiRepository;
	
	private MigrateXMLToDB(Region region, Connection connection) {
		this.region = region;
		this.connection = connection;
	}

	public static synchronized Abdera getInstance() {
		if (abdera == null) {
			abdera = new Abdera();
		}
		return abdera;
	}

	public static synchronized MigrateXMLToDB getMigrateInstance(Region region, Connection connection) {
		boolean isIDF = (region.getId() == 1);
		if (isIDF) {
			if (migrateIDF == null) {
				migrateIDF = new MigrateXMLToDB(region, connection);
			}
			return migrateIDF;
		} else {
			if (migratePoitou == null) {
				migratePoitou = new MigrateXMLToDB(region, connection);
			}
			return migratePoitou;
		}
	}

	public static void main(String[] args) {
		try {
			// TODO Auto-generated method stub
			applicationContext = new ClassPathXmlApplicationContext(
					"/META-INF/application-context.xml");

			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			Connection connectIDF = DriverManager
					.getConnection("jdbc:mysql://localhost/univmobile_idf?"
							+ "user=root&password=V1rtualDB");

			Connection connectPoitou = DriverManager
					.getConnection("jdbc:mysql://localhost/univmobile_poitou?"
							+ "user=root&password=V1rtualDB");

			// Create the regions
			//getMigrateInstance().createRegions();
			
			//
			poiRepository = (PoiRepository) applicationContext
					.getBean(PoiRepository.class);
			
			// Delete all the pois of universities ()
			//poiRepository.deleteAll();
			
			universityRepository = (UniversityRepository) applicationContext
					.getBean(UniversityRepository.class);
			// universityRepository.deleteAll();
			
			regionRepository = (RegionRepository) applicationContext
					.getBean(RegionRepository.class);
			
			getMigrateInstance(regionRepository.findOne(1L), connectIDF).migrateData();
			
			getMigrateInstance(regionRepository.findOne(2L), connectPoitou).migrateData();
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "An error occuret...", ex);
		}
	}

	protected void migrateData() throws SQLException {

		users = new HashMap<String, User>();
		categories = new HashMap<Integer, Category>();

		migrateUsers(connection);
		loadCategories(region);
		processPois(connection, region);
	}
	
	private void processPois(Connection connection, Region region) throws SQLException {
		
		PreparedStatement statement = connection.prepareStatement("select p.*, pt.category_id from poi as p JOIN poi_type as pt ON p.poi_type_id = pt.id where p.level = ? and p.deleted_at is null");    
		statement.setInt(1, 0);    
		ResultSet resultSet = statement.executeQuery();
	    
	    // ResultSet is initially before the first data set
	    while (resultSet.next()) {
	    	University university = processUniversity(region, resultSet);
	    	Poi processPoi = processPoi(university, null, resultSet);
	    	recursiveProcessPoi(connection, university, processPoi, resultSet.getInt("root_id"), resultSet.getInt("level") + 1, resultSet.getInt("lft"), resultSet.getInt("rgt"));
	    }
	}
	
	private Poi processPoi(University university, Poi parentPoi, ResultSet resultSet) throws SQLException {
		Poi poi = new Poi();
		
		poi.setCreatedBy(getUser(resultSet.getString("created_by")));
		poi.setCreatedOn(resultSet.getDate("created_at"));
		poi.setUpdatedBy(getUser(resultSet.getString("updated_by")));
		poi.setUpdatedOn(resultSet.getDate("updated_at"));
		poi.setActive(resultSet.getBoolean("status"));
		poi.setAddress(resultSet.getString("address"));
		
		// TODO attachments
		// poi.setAttachmentId(attachmentId);
		// poi.setAttachmentTitle(attachmentTitle);
		// poi.setAttachmentUrl(attachmentUrl);
		
		// Get the category
		Category category = categories.get(resultSet.getInt("category_id"));
		poi.setCategory(category);
		if (category == null) {
			logger.log(Level.WARNING, "POI od id " + resultSet.getLong("id") + "as no category");
			return null;
		}
		//Assert.assertNotNull("The category of POI of ID " + resultSet.getLong("id") + " is null, for region " + university.getRegion().getName(), poi.getCategory());
		
		poi.setCity(resultSet.getString("city"));
		poi.setCountry(resultSet.getString("country"));
		poi.setDescription(resultSet.getString("description"));
		poi.setEmail(resultSet.getString("email"));
		poi.setFloor(resultSet.getString("floor"));
		poi.setItinerary(resultSet.getString("itinerary"));
		
		// Get the lat / lng
		String coordinates = resultSet.getString("coordinates");
		String[] coord = coordinates.split(",");
		if (coord.length == 2) {
			poi.setLat(new Double(coord[0]));
			poi.setLng(new Double(coord[1]));
		} else {
			// We do not process the pois without coordinates or the polygons
			logger.log(Level.WARNING, "POI od id " + resultSet.getLong("id") + "has incorrect coordinates or is type of polygon.");
			// return null;
		}
		
		// TODO Copy the logo locally ! Only 4 logos, better to download manually
		// poi.setLogo(resultSet.getString("logo"));
		poi.setMarkerType(resultSet.getString("type"));
		poi.setName(resultSet.getString("name"));
		poi.setOpeningHours(resultSet.getString("opening_hours"));
		poi.setParent(parentPoi);
		poi.setPhones(resultSet.getString("phone"));
		poi.setUniversity(university);
		poi.setUrl(resultSet.getString("url"));
		poi.setZipcode(resultSet.getString("zip"));
		
		processPoiAttachement(poi, resultSet.getLong("id"));

		// Save the POI
		poiRepository.save(poi);
		
		// Set the POI legacy
		String parentLegacy;
		if (parentPoi != null) {
			parentLegacy = parentPoi.getLegacy();
		} else {
			parentLegacy = "/";
		}
		poi.setLegacy(parentLegacy + poi.getId() + "/");
		poiRepository.save(poi);
		
		return poi;
	}
	
	private University processUniversity(Region region, ResultSet resultSet) throws SQLException {
		// Try to get university by name
		University university = universityRepository.findByTitle(resultSet.getString("name"));
		
		if (university == null) {
			university = new University();
			university.setCreatedBy(getUser(resultSet.getString("created_by")));
			university.setCreatedOn(resultSet.getDate("created_at"));
			university.setUpdatedBy(getUser(resultSet.getString("updated_by")));
			university.setUpdatedOn(resultSet.getDate("updated_at"));
			university.setTitle(resultSet.getString("name"));
			university.setRegion(region);
			
			universityRepository.save(university);
		}
		
		return university;
	}
	
	private void processPoiAttachement(Poi poi, Long poiId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement("select * from attachment where object_class = ? and object_id = ?");    
		statement.setString(1, "Poi");
		statement.setLong(2, poiId);;    
		ResultSet resultSet = statement.executeQuery();
	    
	    // ResultSet is initially before the first data set
	    while (resultSet.next()) {
	    	// we keep only the first one
	    	poi.setAttachmentTitle(resultSet.getString("title"));
	    	poi.setAttachmentType(resultSet.getString("type"));
	    	poi.setAttachmentUrl(resultSet.getString("url"));
	    	poi.setAttachmentId(resultSet.getInt("id"));
	    	break;
	    }
	}
	
	
	private void recursiveProcessPoi(Connection connection, University university, Poi parentPoi, int root_id, int level, int lft, int rgt) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				"select p.*, pt.category_id from poi as p JOIN poi_type as pt ON p.poi_type_id = pt.id where "
				+ " p.root_id = ? and p.level = ? and p.lft >= ? and p.rgt <= ? and p.deleted_at is null");    
		statement.setInt(1, root_id);
		statement.setInt(2, level);
		statement.setInt(3, lft);
		statement.setInt(4, rgt);
		ResultSet resultSet = statement.executeQuery();

	    while (resultSet.next()) {
	    	Poi processPoi = processPoi(university, parentPoi, resultSet);
	    	recursiveProcessPoi(connection, university, processPoi, root_id, level + 1, resultSet.getInt("lft"), resultSet.getInt("rgt"));
	    }
	}
	
	/**
	 * Permits to migrate all the users of a university
	 */
	private void migrateUsers(Connection connection) throws SQLException {
		userRepository = (UserRepository) applicationContext
				.getBean(UserRepository.class);
		PreparedStatement statement = connection.prepareStatement("select * from sf_guard_user");
	    // Result set get the result of the SQL query
	    ResultSet resultSet = statement
	          .executeQuery();
	    
	    
	    // ResultSet is initially before the first data set
	    while (resultSet.next()) {
	    	User user = userRepository.findByRemoteUser(resultSet.getString("username"));
	    	if (user == null) {
		    	user = new User();
				user.setCreatedOn(resultSet.getDate("created_at"));
				user.setCreatedBy(getUser(null));
				user.setUpdatedOn(resultSet.getDate("updated_at"));
				user.setUpdatedBy(getUser(null));
				if (resultSet.getBoolean("is_super_admin")) {
					user.setRole(User.SUPERADMIN);
				} else {
					user.setRole(User.ADMIN);
				}
				user.setClassicLoginAllowed(false);
				String displayName = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
				if (displayName == null || displayName.trim().equals("")) {
					displayName = resultSet.getString("username");
				}
				user.setDisplayName(displayName);
				user.setEmail(resultSet.getString("email"));
				user.setPassword(resultSet.getString("password"));
				user.setRemoteUser(resultSet.getString("username"));
				switch (resultSet.getInt("gender_id")) {
					case 1:
						user.setTitleCivilite("Mlle");
						break;
					case 2:
						user.setTitleCivilite("Mme");
						break;
					case 3:
						user.setTitleCivilite("Mr");
						break;
				}
				user.setUsername(resultSet.getString("username"));
				user.setUniversity(universityRepository.findOne(29L));
				userRepository.save(user);
	    	}
			
			this.users.put(user.getUsername(), user);
	    }
	}

	/**
	 * Permits to load the regions :
	 */
	private void loadRegions() {
		
	}

	/**
	 * We need a boolean isIDF, because the IDs of category are not exactly the
	 * same in IDF and Poitou
	 */
	private void loadCategories(Region region) {
		boolean isIDF = (region.getId() == 1);
		categoryRepository = (CategoryRepository) applicationContext
				.getBean(CategoryRepository.class);
		// categoryRepository.deleteAll();
		// Create all the necessary categories.

		// Date date = new Date();

		/*
		 * Category universityRootCategory = new Category();
		 * universityRootCategory.setActive(true);
		 * universityRootCategory.setDescription(
		 * "Catégorie racine de toutes les catégories de points d'intérêt définis par les universités"
		 * ); universityRootCategory.setName("Catégorie racine université");
		 * universityRootCategory.setCreatedBy(adminUser);
		 * universityRootCategory.setCreatedOn(date);
		 * universityRootCategory.setUpdatedBy(adminUser);
		 * universityRootCategory.setUpdatedOn(date);
		 * categoryRepository.save(universityRootCategory);
		 */
		Category universityRootCategory = categoryRepository.findOne(1L);

		/* '21', 'Plans', '1' */
		/*
		 * Category plan = new Category(); plan.setActive(true);
		 * plan.setCreatedBy(adminUser); plan.setCreatedOn(date);
		 * plan.setDescription("catégorie globale, toujours visible");
		 * plan.setName("Plans"); plan.setParent(universityRootCategory);
		 * plan.setUpdatedBy(adminUser); plan.setUpdatedOn(date);
		 */
		Category plan = categoryRepository.findOne(21L);
		categories.put(1, plan);

		/* categoryRepository.save(plan); */

		/* '22', 'Amphithéâtre', '1' */
		/*
		 * Category amphi = new Category(); amphi.setActive(true);
		 * amphi.setCreatedBy(adminUser); amphi.setCreatedOn(date);
		 * amphi.setDescription(""); amphi.setName("Amphithéâtre");
		 * amphi.setParent(universityRootCategory);
		 * amphi.setUpdatedBy(adminUser); amphi.setUpdatedOn(date);
		 */
		Category amphi = categoryRepository.findOne(22L);
		categories.put(3, amphi);
		if (!isIDF) {
			// For Poitou region, the category is inactive, and the one of ID 16
			// is used in place.
			categories.put(16, amphi);
		}
		/* categoryRepository.save(amphi); */

		/* '25', 'Restauration Universitaire', '1' */
		/*
		 * Category resto = new Category(); resto.setActive(true);
		 * resto.setCreatedBy(adminUser); resto.setCreatedOn(date);
		 * resto.setDescription("cafétéria et restaurant du CROUS");
		 * resto.setName("Restauration Universitaire");
		 * resto.setParent(universityRootCategory);
		 * resto.setUpdatedBy(adminUser); resto.setUpdatedOn(date);
		 */
		Category resto = categoryRepository.findOne(25L);
		categories.put(4, resto);
		/* categoryRepository.save(resto); */

		/* '26', 'Relais handicap', '1' */
		/*
		 * Category relaisH = new Category(); relaisH.setActive(true);
		 * relaisH.setCreatedBy(adminUser); relaisH.setCreatedOn(date);
		 * relaisH.setDescription(
		 * "Lieux aménagés ou disposant de matériels pour les personnes à mobilité réduite"
		 * ); relaisH.setName("Relais handicap");
		 * relaisH.setParent(universityRootCategory);
		 * relaisH.setUpdatedBy(adminUser); relaisH.setUpdatedOn(date);
		 */
		Category relaisH = categoryRepository.findOne(26L);
		categories.put(7, relaisH);
		/* categoryRepository.save(relaisH); */

		/* '6', 'Scolarité & Inscription', '1' */
		/*
		 * Category insc = new Category(); insc.setActive(true);
		 * insc.setCreatedBy(adminUser); insc.setCreatedOn(date);
		 * insc.setDescription
		 * ("Inscription administrative et secrétariat pédagogique");
		 * insc.setName("Scolarité & Inscription");
		 * insc.setParent(universityRootCategory); insc.setUpdatedBy(adminUser);
		 * insc.setUpdatedOn(date);
		 */
		Category insc = categoryRepository.findOne(6L);
		categories.put(8, insc);
		/* categoryRepository.save(insc); */

		/* '7', 'Bibliothèque Universitaire', '1' */
		/*
		 * Category biblio = new Category(); biblio.setActive(true);
		 * biblio.setCreatedBy(adminUser); biblio.setCreatedOn(date);
		 * biblio.setDescription
		 * ("Centre de documentation, bibliothèque et médiathèque universitaire"
		 * ); biblio.setName("Bibliothèque Universitaire");
		 * biblio.setParent(universityRootCategory);
		 * biblio.setUpdatedBy(adminUser); biblio.setUpdatedOn(date);
		 */
		Category biblio = categoryRepository.findOne(7L);
		categories.put(9, biblio);
		/* categoryRepository.save(biblio); */

		/* '8', 'Accès & Adresses', '0' */
		/*
		 * Category acces = new Category(); acces.setActive(false);
		 * acces.setCreatedBy(adminUser); acces.setCreatedOn(date);
		 * acces.setDescription("C'est pour les entrées principales");
		 * acces.setName("Accès & Adresses");
		 * acces.setParent(universityRootCategory);
		 * acces.setUpdatedBy(adminUser); acces.setUpdatedOn(date);
		 */
		Category acces = categoryRepository.findOne(8L);
		categories.put(10, acces);
		/* categoryRepository.save(acces); */

		/* '9', 'Orientation & Insertion professionnelle', '1' */
		/*
		 * Category orientation = new Category(); orientation.setActive(true);
		 * orientation.setCreatedBy(adminUser); orientation.setCreatedOn(date);
		 * orientation.setDescription(
		 * "Bureau d'insertion professionnelle, bureau d'orientation et bureau des échanges internationaux"
		 * ); orientation.setName("Orientation & Insertion professionnelle");
		 * orientation.setParent(universityRootCategory);
		 * orientation.setUpdatedBy(adminUser); orientation.setUpdatedOn(date);
		 */
		Category orientation = categoryRepository.findOne(9L);
		categories.put(11, orientation);
		/* categoryRepository.save(orientation); */

		/* '10', 'Santé & Social', '1' */
		/*
		 * Category sante = new Category(); sante.setActive(true);
		 * sante.setCreatedBy(adminUser); sante.setCreatedOn(date);
		 * sante.setDescription(
		 * "santé : infirmerie, psychologues, centres de prévention et médecins. social : assistant(e) de service social, bourse, logement..."
		 * ); sante.setName("Santé & Social");
		 * sante.setParent(universityRootCategory);
		 * sante.setUpdatedBy(adminUser); sante.setUpdatedOn(date);
		 */
		Category sante = categoryRepository.findOne(10L);
		categories.put(12, sante);
		/* categoryRepository.save(sante); */

		/* '11', 'Equipements sportifs & service des sports', '1' */
		/*
		 * Category sport = new Category(); sport.setActive(true);
		 * sport.setCreatedBy(adminUser); sport.setCreatedOn(date);
		 * sport.setDescription
		 * ("Service des sports, associations sportives et gymnases");
		 * sport.setName("Equipements sportifs & service des sports");
		 * sport.setParent(universityRootCategory);
		 * sport.setUpdatedBy(adminUser); sport.setUpdatedOn(date);
		 */
		Category sport = categoryRepository.findOne(11L);
		categories.put(13, sport);
		/* categoryRepository.save(sport); */

		/* '12', 'Recherche', '1' */
		/*
		 * Category recherche = new Category(); recherche.setActive(true);
		 * recherche.setCreatedBy(adminUser); recherche.setCreatedOn(date);
		 * recherche.setDescription(
		 * "Laboratoires, écoles doctorales, maisons de la recherche et PRES");
		 * recherche.setName("Recherche");
		 * recherche.setParent(universityRootCategory);
		 * recherche.setUpdatedBy(adminUser); recherche.setUpdatedOn(date);
		 */
		Category recherche = categoryRepository.findOne(12L);
		if (isIDF) {
			categories.put(14, recherche);
		} else {
			// For Poitou region, the category ID is 15
			categories.put(15, recherche);
		}
		/* categoryRepository.save(recherche); */

		/* '13', 'Association', '1' */
		/*
		 * Category association = new Category(); association.setActive(true);
		 * association.setCreatedBy(adminUser); association.setCreatedOn(date);
		 * association.setDescription("Association");
		 * association.setName("Association");
		 * association.setParent(universityRootCategory);
		 * association.setUpdatedBy(adminUser); association.setUpdatedOn(date);
		 */
		Category association = categoryRepository.findOne(13L);
		if (isIDF) {
			categories.put(15, association);
		}
		/* categoryRepository.save(association); */

		/* '14', 'Culture', '1' */
		/*
		 * Category culture = new Category(); culture.setActive(true);
		 * culture.setCreatedBy(adminUser); culture.setCreatedOn(date);
		 * culture.setDescription("Culture"); culture.setName("Culture");
		 * culture.setParent(universityRootCategory);
		 * culture.setUpdatedBy(adminUser); culture.setUpdatedOn(date);
		 */
		Category culture = categoryRepository.findOne(14L);
		if (isIDF) {
			categories.put(16, culture);
		}
		/* categoryRepository.save(culture); */

		/* '15', 'Formation', '1' */
		/*
		 * Category formation = new Category(); formation.setActive(true);
		 * formation.setCreatedBy(adminUser); formation.setCreatedOn(date);
		 * formation.setDescription("Formation");
		 * formation.setName("Formation");
		 * formation.setParent(universityRootCategory);
		 * formation.setUpdatedBy(adminUser); formation.setUpdatedOn(date);
		 */
		Category formation = categoryRepository.findOne(15L);
		if (isIDF) {
			categories.put(17, formation);
		}
		/* categoryRepository.save(formation); */

		/* '16', 'Polygone', '0' */
		/*
		 * Category polygone = new Category(); polygone.setActive(false);
		 * polygone.setCreatedBy(adminUser); polygone.setCreatedOn(date);
		 * polygone.setDescription("Polygone"); polygone.setName("Polygone");
		 * polygone.setParent(universityRootCategory);
		 * polygone.setUpdatedBy(adminUser); polygone.setUpdatedOn(date);
		 * categories.put(20, polygone); categoryRepository.save(polygone);
		 */

		/* '17', 'Unité de Formation et de Recherche - Faculté', '1' */
		/*
		 * Category ufac = new Category(); ufac.setActive(true);
		 * ufac.setCreatedBy(adminUser); ufac.setCreatedOn(date);
		 * ufac.setDescription("Unité de Formation et de Recherche - Faculté");
		 * ufac.setName("Unité de Formation et de Recherche - Faculté");
		 * ufac.setParent(universityRootCategory); ufac.setUpdatedBy(adminUser);
		 * ufac.setUpdatedOn(date);
		 */
		Category ufac = categoryRepository.findOne(17L);
		if (isIDF) {
			categories.put(21, ufac);
		}
		/* categoryRepository.save(ufac); */

		/* '18', 'Institut & École', '1' */
		/*
		 * Category institutEcole = new Category();
		 * institutEcole.setActive(true); institutEcole.setCreatedBy(adminUser);
		 * institutEcole.setCreatedOn(date);
		 * institutEcole.setDescription("Institut & École");
		 * institutEcole.setName("Institut & École");
		 * institutEcole.setParent(universityRootCategory);
		 * institutEcole.setUpdatedBy(adminUser);
		 * institutEcole.setUpdatedOn(date);
		 */
		Category institutEcole = categoryRepository.findOne(18L);
		if (isIDF) {
			categories.put(22, institutEcole);
		}
		/* categoryRepository.save(institutEcole); */

		/* '19', 'Résidence Universitaire', '1' */
		/*
		 * Category resUniv = new Category(); resUniv.setActive(true);
		 * resUniv.setCreatedBy(adminUser); resUniv.setCreatedOn(date);
		 * resUniv.setDescription("CROUS");
		 * resUniv.setName("Résidence Universitaire");
		 * resUniv.setParent(universityRootCategory);
		 * resUniv.setUpdatedBy(adminUser); resUniv.setUpdatedOn(date);
		 */
		Category resUniv = categoryRepository.findOne(19L);
		if (isIDF) {
			categories.put(23, resUniv);
		} else {
			// For Poitou region, the category ID is 14
			categories.put(14, resUniv);
		}
		/* categoryRepository.save(resUniv); */

		/* * '20', 'Salle', '1' */
		/*
		 * Category salle = new Category(); salle.setActive(true);
		 * salle.setCreatedBy(adminUser); salle.setCreatedOn(date);
		 * salle.setDescription("Les salles"); salle.setName("Salle");
		 * salle.setParent(universityRootCategory);
		 * salle.setUpdatedBy(adminUser); salle.setUpdatedOn(date);
		 */
		Category salle = categoryRepository.findOne(20L);
		if (isIDF) {
			categories.put(24, salle);
		}
		/* categoryRepository.save(salle); */

	}

	/**
	 * @OTODO To implement
	 */
	private User getUser(String username) {
		if (users.containsKey(username)) {
			return users.get(username);
		} else {
			return adminUser;
		}
	}

}
