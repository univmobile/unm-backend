package fr.univmobile.backend.domain;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.parser.stax.FOMEntry;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/application-context.xml")
public class MigrateXMLToDB {
	
	private static Logger logger = Logger.getLogger(MigrateXMLToDB.class.getName());
	
    private static Abdera abdera = null;
    private static MigrateXMLToDB migrate = null;
    
    private static ClassPathXmlApplicationContext applicationContext = null;
    
    private Map<Integer, Category> categories = new HashMap<Integer, Category>();
    private Map<String, Region> regions = new HashMap<String, Region>();
    private Map<String, University> universities = new HashMap<String, University>();
    private Map<Integer, Poi> pois = new HashMap<Integer, Poi>();
    private Map<Poi, Integer> parents = new HashMap<Poi, Integer>();
    private Map<String, User> users = new HashMap<String, User>();
    private User adminUser;
    
    UserRepository userRepository;
    RegionRepository regionRepository;
    UniversityRepository universityRepository;
	CategoryRepository categoryRepository;
	PoiRepository poiRepository;
    
    public static synchronized Abdera getInstance() {
        if (abdera == null) {
            abdera = new Abdera();
        }
        return abdera;
    }

    public static synchronized MigrateXMLToDB getMigrateInstance() {
    	
        if (migrate == null) {
        	migrate = new MigrateXMLToDB();
        }
        return migrate;
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		applicationContext = new ClassPathXmlApplicationContext("/META-INF/application-context.xml");
		getMigrateInstance().createUsers();
		getMigrateInstance().createRegions();
		getMigrateInstance().createCategories();
		getMigrateInstance().processPois();
	}
	
	private void createUsers() {
		userRepository = (UserRepository)applicationContext.getBean(UserRepository.class);
		userRepository.deleteAll();
        Parser parser = getInstance().getParser();
        
        File usersDirectory = new File(getMigrateInstance().getClass().getResource("/unm-backend-0.0.5/users").getFile());
        
        User superAdmin = new User();
        superAdmin.setCreatedOn(new Date());
        superAdmin.setUpdatedOn(new Date());
        superAdmin.setUsername("superadmin");
        superAdmin.setRemoteUser("superadmin");
        superAdmin.setDisplayName("Super Admin");
        superAdmin.setRole(User.SUPERADMIN);
        userRepository.save(superAdmin);
        adminUser = superAdmin;
        
        if (usersDirectory.isDirectory()) {
        	for (File file : usersDirectory.listFiles()) {
		        try {
		            Document<FOMEntry> doc = parser.parse(new FileInputStream(file));
		            FOMEntry feed = doc.getRoot();
		            
		            User user = new User();
		            user.setCreatedOn(feed.getUpdated());
		            user.setCreatedBy(getUser(feed.getAuthor().getName()));
		            user.setUpdatedOn(feed.getUpdated());
		            user.setUpdatedBy(user.getCreatedBy());
		            user.setRole(User.SUPERADMIN);
		            
		            for (Element child : feed.getContentElement().getElements()) {
		            	if (child.getQName().getLocalPart().equals("uid")) {
		            		user.setUsername(child.getText());
		            		users.put(user.getUsername(), user);
		            	}
		            	if (child.getQName().getLocalPart().equals("displayName")) {
		            		user.setDisplayName(child.getText());
		            	}
		            	if (child.getQName().getLocalPart().equals("login_classic")) {
		            		if (child.getAttributeValue("active").equalsIgnoreCase("true")) {
		            			user.setClassicLoginAllowed(true);
		            			for (Element log : child.getElements()) {
		            				if (log.getQName().getLocalPart().equals("password")) {
		            					user.setPassword(log.getAttributeValue("encrypted"));
		            				}
		            			}
		            		}
		            	}
		            	if (child.getQName().getLocalPart().equals("mail")) {
		            		user.setEmail(child.getText());
		            	}
		            	if (child.getQName().getLocalPart().equals("remoteUser")) {
		            		user.setRemoteUser(child.getText());
		            	}
		            	if (child.getQName().getLocalPart().equals("supannCivilite")) {
		            		user.setTitleCivilite(child.getText());
		            	}
		            }
		            
		            userRepository.save(user);
		        } catch (Exception ex) {
		        	logger.log(Level.WARNING, ex.getMessage(), ex);
		        }
        	}
        }
	}
	
	private void createRegions() {
		regionRepository = (RegionRepository)applicationContext.getBean(RegionRepository.class);
		regionRepository.deleteAll();
		universityRepository = (UniversityRepository)applicationContext.getBean(UniversityRepository.class);
		universityRepository.deleteAll();
        Parser parser = getInstance().getParser();
        
        File regionsDirectory = new File(getMigrateInstance().getClass().getResource("/unm-backend-0.0.5/regions").getFile());
        
        if (regionsDirectory.isDirectory()) {
        	for (File file : regionsDirectory.listFiles()) {
		        try {
		            Document<FOMEntry> doc = parser.parse(new FileInputStream(file));
		            FOMEntry feed = doc.getRoot();

		            Region region = new Region();
		            // TODO Inform that label and name are identical
		            region.setLabel(feed.getTitle());
		            region.setName(feed.getTitle());
		            
		            region.setCreatedBy(getUser(feed.getAuthor().getName()));
		            region.setCreatedOn(feed.getUpdated());
		            
	            	region.setUpdatedBy(region.getCreatedBy());
		            region.setUpdatedOn(feed.getUpdated());
		            
		            for (Element child : feed.getContentElement().getElements()) {
		            	if (child.getQName().getLocalPart().equals("uid")) {
		            		regions.put(child.getText(), region);
		            	}
		            	if (child.getQName().getLocalPart().equals("url")) {
		            		region.setUrl(child.getText());
		            		regionRepository.save(region);
		            	}
		            	if (child.getQName().getLocalPart().equals("universities")) {
		            		for (Element universityE : child.getElements()) {
		            			if (universityE.getQName().getLocalPart().equals("university")) {
		            				University university = new University();
		            				university.setRegion(region);
		            				//region.getUniversities().add(university);
		            				
		            				university.setCreatedBy(region.getCreatedBy());
		            				university.setCreatedOn(region.getCreatedOn());
		            				university.setUpdatedBy(region.getUpdatedBy());
		            				university.setUpdatedOn(region.getUpdatedOn());
		            				for (Element uChild : universityE.getElements()) {
		            					if (uChild.getQName().getLocalPart().equals("id")) {
		            						universities.put(uChild.getText(), university);
		            					}
		            					if (uChild.getQName().getLocalPart().equals("title")) {
		            						university.setTitle(uChild.getText());
		            					}
		            					if (uChild.getQName().getLocalPart().equals("shibboleth")) {
		            						// TODO Seems to miss the Shibboleth Url
		            					}
		            				}
		            				universityRepository.save(university);
		            			}
		            		}
		            	}
		            }
		        } catch (Exception ex) {
		            logger.log(Level.WARNING, ex.getMessage(), ex);
		        }
        	}
        }
	}
	
	private void createCategories() {
		categoryRepository = (CategoryRepository)applicationContext.getBean(CategoryRepository.class); 
		categoryRepository.deleteAll();
		// Create all the necessary categories.
		
		Date date = new Date();
		
		Category universityRootCategory = new Category();
		universityRootCategory.setActive(true);
		universityRootCategory.setDescription("Catégorie racine de toutes les catégories de points d'intérêt définis par les universités");
		universityRootCategory.setName("Catégorie racine université");
		universityRootCategory.setCreatedBy(adminUser);
		universityRootCategory.setCreatedOn(date);
		universityRootCategory.setUpdatedBy(adminUser);
		universityRootCategory.setUpdatedOn(date);
		categoryRepository.save(universityRootCategory);
		
		Category plan = new Category();
		plan.setActive(true);
		plan.setCreatedBy(adminUser);
		plan.setCreatedOn(date);
		plan.setDescription("catégorie globale, toujours visible");
		plan.setName("Plans");
		plan.setParent(universityRootCategory);
		plan.setUpdatedBy(adminUser);
		plan.setUpdatedOn(date);
		categories.put(1, plan);
		categoryRepository.save(plan);
		
		Category amphi = new Category();
		amphi.setActive(true);
		amphi.setCreatedBy(adminUser);
		amphi.setCreatedOn(date);
		amphi.setDescription("");
		amphi.setName("Amphithéâtre");
		amphi.setParent(universityRootCategory);
		amphi.setUpdatedBy(adminUser);
		amphi.setUpdatedOn(date);
		categories.put(3, amphi);
		categoryRepository.save(amphi);
		
		Category resto = new Category();
		resto.setActive(true);
		resto.setCreatedBy(adminUser);
		resto.setCreatedOn(date);
		resto.setDescription("cafétéria et restaurant du CROUS");
		resto.setName("Restauration Universitaire");
		resto.setParent(universityRootCategory);
		resto.setUpdatedBy(adminUser);
		resto.setUpdatedOn(date);
		categories.put(4, resto);
		categoryRepository.save(resto);
		
		Category relaisH = new Category();
		relaisH.setActive(true);
		relaisH.setCreatedBy(adminUser);
		relaisH.setCreatedOn(date);
		relaisH.setDescription("Lieux aménagés ou disposant de matériels pour les personnes à mobilité réduite");
		relaisH.setName("Relais handicap");
		relaisH.setParent(universityRootCategory);
		relaisH.setUpdatedBy(adminUser);
		relaisH.setUpdatedOn(date);
		categories.put(7, relaisH);
		categoryRepository.save(relaisH);
		
		Category insc = new Category();
		insc.setActive(true);
		insc.setCreatedBy(adminUser);
		insc.setCreatedOn(date);
		insc.setDescription("Inscription administrative et secrétariat pédagogique");
		insc.setName("Scolarité & Inscription");
		insc.setParent(universityRootCategory);
		insc.setUpdatedBy(adminUser);
		insc.setUpdatedOn(date);
		categories.put(8, insc);
		categoryRepository.save(insc);
		
		Category biblio = new Category();
		biblio.setActive(true);
		biblio.setCreatedBy(adminUser);
		biblio.setCreatedOn(date);
		biblio.setDescription("Centre de documentation, bibliothèque et médiathèque universitaire");
		biblio.setName("Bibliothèque Universitaire");
		biblio.setParent(universityRootCategory);
		biblio.setUpdatedBy(adminUser);
		biblio.setUpdatedOn(date);
		categories.put(9, biblio);
		categoryRepository.save(biblio);
		
		Category acces = new Category();
		acces.setActive(false);
		acces.setCreatedBy(adminUser);
		acces.setCreatedOn(date);
		acces.setDescription("C'est pour les entrées principales");
		acces.setName("Accès & Adresses");
		acces.setParent(universityRootCategory);
		acces.setUpdatedBy(adminUser);
		acces.setUpdatedOn(date);
		categories.put(10, acces);
		categoryRepository.save(acces);
		
		Category orientation = new Category();
		orientation.setActive(true);
		orientation.setCreatedBy(adminUser);
		orientation.setCreatedOn(date);
		orientation.setDescription("Bureau d'insertion professionnelle, bureau d'orientation et bureau des échanges internationaux");
		orientation.setName("Orientation & Insertion professionnelle");
		orientation.setParent(universityRootCategory);
		orientation.setUpdatedBy(adminUser);
		orientation.setUpdatedOn(date);
		categories.put(11, orientation);
		categoryRepository.save(orientation);
		
		Category sante = new Category();
		sante.setActive(true);
		sante.setCreatedBy(adminUser);
		sante.setCreatedOn(date);
		sante.setDescription("santé : infirmerie, psychologues, centres de prévention et médecins. social : assistant(e) de service social, bourse, logement...");
		sante.setName("Santé & Social");
		sante.setParent(universityRootCategory);
		sante.setUpdatedBy(adminUser);
		sante.setUpdatedOn(date);
		categories.put(12, sante);
		categoryRepository.save(sante);
		
		Category sport = new Category();
		sport.setActive(true);
		sport.setCreatedBy(adminUser);
		sport.setCreatedOn(date);
		sport.setDescription("Service des sports, associations sportives et gymnases");
		sport.setName("Equipements sportifs & service des sports");
		sport.setParent(universityRootCategory);
		sport.setUpdatedBy(adminUser);
		sport.setUpdatedOn(date);
		categories.put(13, sport);
		categoryRepository.save(sport);
		
		Category recherche = new Category();
		recherche.setActive(true);
		recherche.setCreatedBy(adminUser);
		recherche.setCreatedOn(date);
		recherche.setDescription("Laboratoires, écoles doctorales, maisons de la recherche et PRES");
		recherche.setName("Recherche");
		recherche.setParent(universityRootCategory);
		recherche.setUpdatedBy(adminUser);
		recherche.setUpdatedOn(date);
		categories.put(14, recherche);
		categoryRepository.save(recherche);
		
		Category association = new Category();
		association.setActive(true);
		association.setCreatedBy(adminUser);
		association.setCreatedOn(date);
		association.setDescription("Association");
		association.setName("Association");
		association.setParent(universityRootCategory);
		association.setUpdatedBy(adminUser);
		association.setUpdatedOn(date);
		categories.put(15, association);
		categoryRepository.save(association);
		
		Category culture = new Category();
		culture.setActive(true);
		culture.setCreatedBy(adminUser);
		culture.setCreatedOn(date);
		culture.setDescription("Culture");
		culture.setName("Culture");
		culture.setParent(universityRootCategory);
		culture.setUpdatedBy(adminUser);
		culture.setUpdatedOn(date);
		categories.put(16, culture);
		categoryRepository.save(culture);
		
		Category formation = new Category();
		formation.setActive(true);
		formation.setCreatedBy(adminUser);
		formation.setCreatedOn(date);
		formation.setDescription("Formation");
		formation.setName("Formation");
		formation.setParent(universityRootCategory);
		formation.setUpdatedBy(adminUser);
		formation.setUpdatedOn(date);
		categories.put(17, formation);
		categoryRepository.save(formation);
		
		Category polygone = new Category();
		polygone.setActive(false);
		polygone.setCreatedBy(adminUser);
		polygone.setCreatedOn(date);
		polygone.setDescription("Polygone");
		polygone.setName("Polygone");
		polygone.setParent(universityRootCategory);
		polygone.setUpdatedBy(adminUser);
		polygone.setUpdatedOn(date);
		categories.put(20, polygone);
		categoryRepository.save(polygone);
		
		Category ufac = new Category();
		ufac.setActive(true);
		ufac.setCreatedBy(adminUser);
		ufac.setCreatedOn(date);
		ufac.setDescription("Unité de Formation et de Recherche - Faculté");
		ufac.setName("Unité de Formation et de Recherche - Faculté");
		ufac.setParent(universityRootCategory);
		ufac.setUpdatedBy(adminUser);
		ufac.setUpdatedOn(date);
		categories.put(21, ufac);
		categoryRepository.save(ufac);
		
		Category institutEcole = new Category();
		institutEcole.setActive(true);
		institutEcole.setCreatedBy(adminUser);
		institutEcole.setCreatedOn(date);
		institutEcole.setDescription("Institut & École");
		institutEcole.setName("Institut & École");
		institutEcole.setParent(universityRootCategory);
		institutEcole.setUpdatedBy(adminUser);
		institutEcole.setUpdatedOn(date);
		categories.put(22, institutEcole);
		categoryRepository.save(institutEcole);
		
		Category resUniv = new Category();
		resUniv.setActive(true);
		resUniv.setCreatedBy(adminUser);
		resUniv.setCreatedOn(date);
		resUniv.setDescription("CROUS");
		resUniv.setName("Résidence Universitaire");
		resUniv.setParent(universityRootCategory);
		resUniv.setUpdatedBy(adminUser);
		resUniv.setUpdatedOn(date);
		categories.put(23, resUniv);
		categoryRepository.save(resUniv);
		
		Category salle = new Category();
		salle.setActive(true);
		salle.setCreatedBy(adminUser);
		salle.setCreatedOn(date);
		salle.setDescription("Les salles");
		salle.setName("Salle");
		salle.setParent(universityRootCategory);
		salle.setUpdatedBy(adminUser);
		salle.setUpdatedOn(date);
		categories.put(24, salle);
		categoryRepository.save(salle);
		
	}
	
	/**
	 * @OTODO To implement
	 */
	private User getUser(String userKey) {
		if (users.containsKey(userKey)) {
			return users.get(userKey);
		} else {
			return adminUser;
		}
	}
	
	private void processPois() {
		poiRepository = (PoiRepository)applicationContext.getBean(PoiRepository.class);
		poiRepository.deleteAll();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		
        Parser parser = getInstance().getParser();
        
        File poiDirectory = new File(getMigrateInstance().getClass().getResource("/unm-backend-0.0.5/pois").getFile());
        
        if (poiDirectory.isDirectory()) {
        	for (File file : poiDirectory.listFiles()) {
		        try {
		            Document<FOMEntry> doc = parser.parse(new FileInputStream(file));
		            FOMEntry feed = doc.getRoot();
		            // Get the feed title
		            
		            Poi poi = new Poi();
		            
		            pois.put(Integer.parseInt(feed.getContentElement().getAttributeValue("uid")), poi);
		            
		            poi.setName(feed.getTitle());
		            if (feed.getContentElement().getAttributeValue("active") != null) {
		            	poi.setActive(feed.getContentElement().getAttributeValue("active").equalsIgnoreCase("true"));
		            }
		            
		            poi.setCreatedBy(getUser(feed.getAuthor().getName()));
		            poi.setCreatedOn(dateFormat.parse(feed.getContentElement().getAttributeValue("createdAt")));
		            
		            if (feed.getContentElement().getAttributeValue("updatedBy") != null) {
		            	poi.setUpdatedBy(getUser(feed.getContentElement().getAttributeValue("updatedBy")));
		            }
		            poi.setUpdatedOn(feed.getUpdated());
		            
		            String coordinates = feed.getContentElement().getAttributeValue("coordinates");
		            String[] coord = coordinates.split(",");
		            if (coord.length == 2) {
		            	// TODO Take in account the polygones
			            poi.setLat(new Double(coord[0]));
			            poi.setLng(new Double(coord[1]));
		            }
		            
		            poi.setLogo(feed.getContentElement().getAttributeValue("logo"));
		            poi.setMarkerType(feed.getContentElement().getAttributeValue("markerType"));
		            
		            /*
		             * Get the parent, and the university of the parent
		             */
		            if (feed.getContentElement().getAttributeValue("parentUid") != null) {
		            	//Poi parent = pois.get(Integer.parseInt(feed.getContentElement().getAttributeValue("parentUid")));
		            	Integer parentUid = Integer.parseInt(feed.getContentElement().getAttributeValue("parentUid"));
		            	if (parentUid == null) {
		            		logger.log(Level.WARNING, "Impossible to find the parent of ID " + feed.getContentElement().getAttributeValue("parentUid"));
		            	} else {
		            		parents.put(poi, parentUid);
		            		//poi.setParent(parent);
		            		//parent.getChildren().add(poi);
		            	}
		            }
		            
		            for (Element child : feed.getContentElement().getElements()) {
		            	if (child.getQName().getLocalPart().equals("poiType")) {
		            		// TODO Some POIS without type
		            		if (child.getAttributeValue("categoryId") != null) {
			            		Category poiCategory = categories.get(Integer.valueOf(child.getAttributeValue("categoryId")));
			            		poi.setCategory(poiCategory);
		            		} else {
		            			break;
		            		}
		            	}
		            	if (child.getQName().getLocalPart().equals("university")) {
		            		University university = universities.get(child.getAttributeValue("id"));
		            		poi.setUniversity(university);
		            	}
		            	if (child.getQName().getLocalPart().equals("description")) {
		            		poi.setDescription(child.getText());
		            	}
		            	// Process the address element
		            	if (child.getQName().getLocalPart().equals("address")) {
		            		poi.setZipcode(child.getAttributeValue("zipCode"));
		            		poi.setCity(child.getAttributeValue("city"));
		            		poi.setCountry(child.getAttributeValue("countryCode"));
		            		/*try {
		            			poi.setLat(new Double(child.getAttributeValue("latitude")));
		            			poi.setLng(new Double(child.getAttributeValue("longitude")));
		            		} catch (Exception ex) {
		            			logger.log(Level.WARNING, "Impossible de trouver la latitude ou la longitude de " + feed.getTitle());
		            		}*/
		            		for (Element addressChild: child.getElements()) {
		            			if (addressChild.getQName().getLocalPart().equals("url")) {
		            				poi.setUrl(addressChild.getText());
		            			}
		            			if (addressChild.getQName().getLocalPart().equals("phone")) {
		            				String prefixPhone = "";
		            				if (poi.getPhones() != null) {
		            					prefixPhone = poi.getPhones() + "\n";
		            				}
		            				poi.setPhones(prefixPhone + addressChild.getText());
		            			}
		            			if (addressChild.getQName().getLocalPart().equals("fullAddress")) {
		            				poi.setAddress(addressChild.getText());
		            			}
		            			if (addressChild.getQName().getLocalPart().equals("itinerary")) {
		            				poi.setItinerary(addressChild.getText());
		            			}
		            			if (addressChild.getQName().getLocalPart().equals("floor")) {
		            				poi.setFloor(addressChild.getText());
		            			}
		            			if (addressChild.getQName().getLocalPart().equals("email")) {
		            				poi.setEmail(addressChild.getText());
		            			}
		            			if (addressChild.getQName().getLocalPart().equals("openingHours")) {
		            				poi.setOpeningHours(addressChild.getText());
		            			}
		            		}
		            	}
		            	// Process attachment information
		            	if (child.getQName().getLocalPart().equals("attachment")) {
		            		poi.setAttachmentId(Integer.parseInt(child.getAttributeValue("id")));
		            		poi.setAttachmentType(child.getAttributeValue("type"));
		            		poi.setAttachmentTitle(child.getAttributeValue("title"));
		            		poi.setAttachmentUrl(child.getAttributeValue("url"));
		            	}
		            }
		            if (poi.getCategory() != null) {
		            	poiRepository.save(poi);
		            }
		        } catch (Exception ex) {
		        	logger.log(Level.WARNING, "File : " + file.getName() + " ; " +  ex.getMessage(), ex);
		        }
        	}
        	
        	// Set all the parents :
        	for (Poi poi : parents.keySet()) {
        		Poi parent = pois.get(parents.get(poi));
        		if (parent == null) {
        			logger.log(Level.WARNING, "Impossible de trouver le parent d'ID" + parents.get(poi));
        		} else {
        			if (poi.getCategory() != null){
	        			poi.setUniversity(parent.getUniversity());
		        		poi.setParent(parent);
		        		poiRepository.save(poi);
        			}
	        		//parent.getChildren().add(poi);
	        		// TODO Persist child
	        		// TODO Persist parent
        		}
        	}
        }
	}
	

}
