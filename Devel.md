Développement d’unm-backend
===========================

Documentation parente : [unm-backend](README.md)

### Architecture logicielle

L’application web unm-backend est servie par une servlet J2EE. Les composants sont ensuite organisés en couches :

  * Couche servlet : la partie J2EE du code, qui aiguille vers les différents contrôleurs. Projet Maven : unm-backend-app.
     * le projet Maven unm-backend-app-noshib est une version repackagée de cette application pour le développement, qui fonctionne sans Shibboleth, avec le filtre suivant.
     * le projet Maven unm-backend-filter-noshib est un filtre J2EE qui permet de simuler le comportement d’une authentification Shibboleth réussie.
  * Couche controllers : les composants applicatifs qui analysent les requêtes HTTP et renvoient les réponses. Projet Maven : unm-backend-app.
  * Couche JSON controllers : les composants qui analysent les requêtes HTTP et formatent les réponses. Projet Maven : unm-backend-app.
  * Couche client : les composants chargés de traduire les données métier brutes en données comprises par la couche applicative, et notamment par les clients JSON. Projet Maven : unm-backend-client.
  * Couche managers : organisation et agrégation des traitements des données. Projet Maven : unm-backend-core.
  * Couche locks : gestion technique des verrous sur les données. Projets Maven : unm-backend-core et unm-commons-datasource.
  * Couche datasources : accès aux données brutes. Projets Maven : unm-backend-core et unm-commons-datasource.
  * Fichiers XML : les données.
  * Base de données (non implémenté en itération 2) : pour la gestion des verrous techniques.

![](src/site/images/backend.png?raw=true =600x "Backend")

### Projets Maven

En itération 2, voici pour ce qui concerne unm-backend-app l’arborescence de dépendances entre projets Maven :

* unm-backend-app:war
	* unm-backend-core:jar
		* unm-commons-datasource:jar
	* unm-webapp-commons:jar — _repository GitHub : unm-devel_
		* unm-commons:jar — _repository GitHub : unm-devel_
    * unm-backend-client-jsonlocal:jar
    	* unm-backend-client-jsonapi:jar
    	* unm-backend-client-api:jar
    * unm-backend-client-local:jar

Il y a également les projets Maven qui permettent de simuler une authentification Shibboleth :

* unm-backend-app-noshib:war
	* unm-backend-filter-noshib:jar
	* _unm-backend-app:war_

Et enfin, pour l’intégration continue :

### Packaging : introduction

L’application principale se package avec le projet Maven unm-backend-app :

    $ cd unm-backend-app
    $ mvn install

La webapp doit être configurée à travers son fichier WEB-INF/web.xml, avec les paramètres suivants :

`dataDir` est le répertoire des fichiers de données XML. Voir section : Déploiement.

    <init-param>
        <param-name>dataDir</param-name>
        <param-value>/tmp/unm-backend/dataDir</param-value>
    </init-param>

`baseURL` est l’URL de base sur laquelle sera accessible l’application web. 

    <init-param>
        <param-name>baseURL</param-name>
        <param-value>http://localhost:8080/unm-backend/</param-value>
    </init-param>

La fichier de configuration de Log4J de la webapp est dans WEB-INF/classes/log4j.xml.

À partir de l’itération 3, un paramètre peut être ajouté : `optional-jsonBaseURLs`, une liste d’URLs de base qui seront renvoyées dans les flux JSON, au lieu de l’URL de base `baseURL`, si la servlet en détecte l’utilisation.

    <init-param>
        <param-name>baseURL</param-name>
        <param-value>http://localhost:8080/unm-backend/</param-value>
    </init-param>
    <init-param>
        <param-name>optional-jsonBaseURLs</param-name>
        <param-value>
            http://127.0.0.1:8080/unm-backend/json/
            http://192.168.0.40:8080/unm-backend/json/
        </param-value>
    </init-param>

Exemple sans ce paramètre :

  1. L’application est déployée en local, accessible par http://localhost:8080/unm-backend/
  2. On déclare donc `baseURL` = http://localhost:8080/unm-backend/ dans web.xml
  3. En se connectant à http://localhost:8080/unm-backend/json?html, on voit un champ « url » qui est exactement « http://localhost:8080/unm-backend/json »
  4. L’adresse IP du poste est 192.168.0.40. En se connectant à http://192.168.0.40:8080/unm-backend/json?html, on voit un champ « url » qui est aussi « http://localhost:8080/unm-backend/json », car c’est la  `baseURL` qui a été déclarée dans le packaging.

Exemple avec ce paramètre :

  1. L’application est déployée en local, accessible par http://localhost:8080/unm-backend/
  2. On déclare `baseURL` = http://localhost:8080/unm-backend/ dans web.xml
  3. L’adresse IP du poste est 192.168.0.40. On déclare `optional-jsonBaseURLs` = http://192.168.0.40:8080/unm-backend/json/ dans web.xml
  4. En se connectant à http://localhost:8080/unm-backend/json?html, on voit un champ « url » qui est exactement « http://localhost:8080/unm-backend/json »
  4. En se connectant à http://192.168.0.40:8080/unm-backend/json?html, on voit un champ « url » qui a été réécrit en « http://192.168.0.40:8080/unm-backend/json »

C’est particulièrement utile en test, avec Android Emulator en guest, qui a comme adresse locale 10.0.2.2, et redirige vers le Tomcat local au host (127.0.0.1), qui ne se voit pas comme 10.0.2.2.

C’est également utile en intégration, car on peut donner deux bases d’URLs différentes pour les flux JSON et pour l’application web :

  * `baseURL` = https://univmobile-dev.univ-paris1.fr/testSP (protégée par Shibboleth)
  * `optional-jsonBaseURLs` = https://univmobile-dev.univ-paris1.fr/json (non protégée par Shibboleth)

Les URLs de `optional-jsonBaseURLs` doivent se terminer par « /json/ » ou « /json ».

### Packaging en développement (sans Shibboleth)

Il faut d’abord packager l’application principale, unm-backend-app, puis la repackager en une application qui fonctionnera sans Shibboleth : unm-backend-app-noshib.
 
Utiliser Maven pour le packaging, et Ant pour le repackaging :

    $ cd unm-backend-app
    $ mvn install
    $ cd ../unm-backend-app-noshib
    $ mvn install
    $ ant -f ../unm-backend-app/build.xml repackage_war \
        -DwarFile=target/unm-backend.war \
        -DdataDir=/var/univmobile/ \
        -DbaseURL=http://localhost:8080/unm-backend/

La fichier de configuration de Log4J de la webapp est dans WEB-INF/classes/log4j.xml, mais, en itération 2, la target Ant de repackaging ne le modifie pas.

Voir la partie « Déploiement » pour la vérification du packaging.

### Déploiement en développement (sans Shibboleth)

Il faut d’abord déployer les données.

L’emplacement des fichiers XML de données est déclaré dans la webapp, par le paramètre `dataDir` dans WEB-INF/web.xml. Par défaut, c’est **/tmp/unm-backend/dataDir**

Il faut y copier les données XML, organisées en sous-répertoires :

  * comments
  * comment_threads
  * pois
  * poitrees
  * regions
  * users

Pour avoir des données de test, on peut prendre des données notamment dans les projets Maven suivants :

  * unm-backend-core, sous-répertoires numérotés dans src/test/data/
  * unm-backend-it, sous-répertoires numérotés dans src/main/resources/data/
  
S’assurer que l’emplacement pour le fichier de log est accessible en écriture pour l’utilisateur qui fait s’exécuter Tomcat (en général, « tomcat »). 

Cet emplacement est défini dans la webapp, dans WEB-INF/classes/log4j.xml. Par défaut, c’est **/tmp/unm-backend.log**

S’assurer ensuite que le paramètre `baseURL` est bien positionné dans WEB-INF/web.xml. Par défaut, c’est **http://localhost:8380/unm-backend/**

De plus,
le filtre unm-backend-filter-noshib doit être activé :

    <filter>
        <filter-name>NoShibbolethFilter</filter-name>
        <filter-class>fr.univmobile.backend.noshib.NoShibbolethFilter</filter-class>
        <init-param>
            <param-name>hosts.allow</param-name>
            <param-value>
                Davids-MacBook-Pro.local
            </param-value>
        </init-param>
        <init-param>
            <param-name>hosts.deny</param-name>
            <param-value>
                univmobile-dev
            </param-value>
        </init-param>
    </filter>	

Note : vérifier aussi que le fichier unm-backend-filter-noshib.jar est présent dans WEB-INF/lib/

Vérification de l’application web  :

Comme Shibboleth n’est pas actif, l’application doit renvoyer une erreur si on se connecte sur son URL (exemple : `baseURL` = http://localhost:8080/unm-backend/) :

    fr.univmobile.backend.noshib.NoShibbolethFilter: The following HTTP parameters must be passed along with the request, in order to mock the Shibboleth presence:
               NO_SHIB_uid: null
              NO_SHIB_eppn: null
       NO_SHIB_displayName: null
        NO_SHIB_remoteUser: null

En effet, pour simuler une authentification Shibboleth, le filtre unm-backend-filter-noshib impose que certains paramètres HTTP soit renseignés. On pourra donc essayer l’URL suivante — à modifier selon `baseURL` — avec un utilisateur fictif : http://localhost:8080/unm-backend/?NO_SHIB_uid=toto&NO_SHIB_eppn=toto@univ-paris1.fr&NO_SHIB_displayName=Toto&NO_SHIB_remoteUser=toto@univ-paris1.fr
    
Pour que l’application unm-backend-app accepte cet utilisateur, il faut que le paramètre « remoteUser » corresponde à un champ « remoteUser » dans un des fichiers XML de `(dataDir)`/users/ :

    <content>
        <uid>crezvani</uid>
        <cn>Rezvani Cyrus</cn>
        <sn>Rezvani</sn>
        <givenName>Cyrus</givenName>
        <remoteUser>crezvani@univ-paris1.fr</remoteUser>
        <mail>Cyrus.Rezvani@univ-paris1.fr</mail>
        <supannCivilite>M.</supannCivilite>
        <displayName>Cyrus Rezvani</displayName>
    </content>

Une fois authentifié par Shibboleth, si la mise en page et les couleurs s’affichent, cela signifie que la valeur de `baseURL` est correcte : les feuilles CSS sont référencées en prenant cette URL comme base.

-> ![](src/site/images/screenshot-home.png?raw=true =300x "Accueil UnivMobile Admin") <-

Vérification des flux JSON :

Se rendre sur `(baseURL)`/json, ou sur `(baseURL)`/json?html pour la version indentée. Pour cette dernière, on reçoit un flux JSON dans ce genre :

    {
        "url":"http://localhost:8080/unm-backend/json",
        "pois":{
            "url":"http://localhost:8080/unm-backend/json/pois"
        },
        "regions":{
            "url":"http://localhost:8080/unm-backend/json/regions"
        }
    }

Les champs « url » dans le flux JSON renvoyés sont cohérents avec `baseURL`, ou
avec les `optional-jsonBaseURLs` selon l’URL invoquée.

### Tests d’intégration continue avec Jenkns

Note : la configuration Jenkins est historisée dans le repository GitHub unm-devel, dans unm-devel-sysadmin, src/main/jenkins/config.

Deux jobs Jenkins lancent des tests d’intégration :

  * unm-backend-it, sur une plate-forme Debian
  * unm-backend-it_macos, sur une plate-forme Mac OS

Projet Maven : unm-backend-it

La partie la plus intéressante est l’écriture de scénarios qui utilisent Selenium.

À partir du moment où les éléments du DOM de la page HTML sont accessibles par leur identifiant, on peut interagir avec eux, prendre des captures d’écrans, etc.

Exemple dans la classe Scenario001.java en itération 2 :

    @Scenario("Voir les commentaires d’un POI")
    @Test
    public void Geocampus_comments000() throws Exception {

        takeScreenshot("home.png");
        elementById("button-myself").click();
        pause(8000);
        takeScreenshot("entered.png");
        elementById("link-geocampus").click();
        pause(8000);
        takeScreenshot("pois.png");
        elementById("link-poi-3792").click();
        pause(PAUSE);
        takeScreenshot("ucp.png");
        final String labelledBy = elementById("li-left-bottom-tabs-comments")
            .attr("aria-labelledby");
        elementById(labelledBy).click();
        pause(8000);
        takeScreenshot("ucp-details.png");
    }

Ces tests de scénarios donnent lieu, à travers Jenkins et le projet Maven unm-devel-it, à des pages HTML récapitulatives.

-> ![](src/site/images/screenshot-scenario.png?raw=true =300x "Scénario unm-backend") <-

### Packaging en intégration (avec Shibboleth)

Il faut d’abord packager l’application principale, puis éventuellement la repackager pour modifier ses paramètres.
 
Utiliser Maven pour le packaging, et Ant pour le repackaging :

    $ cd unm-backend-app
    $ mvn install
    $ ant -f ../unm-backend-app/build.xml repackage_war \
        -DwarFile=target/unm-backend.war \
        -DdataDir=/var/univmobile/ \
        -DbaseURL=https://univmobile-dev.univ-paris1.fr/testSP

La fichier de configuration de Log4J de la webapp est dans WEB-INF/classes/log4j.xml, mais, en itération 2, la target Ant de repackaging ne le modifie pas.

Attention, en itération 1 et 2, c’est l’URL non sécurisée par Shibboleth
https://univmobile-dev.univ-paris1.fr/json qui a été utilisée pour les flux JSON. À partir
de l’itération 3, cela donne lieu à un paramétrage dans WEB-INF/web.xml : 

    <init-param>
        <param-name>optional-jsonBaseURLs</param-name>
        <param-value>
            https://univmobile-dev.univ-paris1.fr/json/
        </param-value>
    </init-param>

Voir la partie « Déploiement » pour la vérification du packaging.

### Déploiement en intégration (avec Shibboleth)

L’emplacement des fichiers XML de données est déclaré dans la webapp, par le paramètre `dataDir` dans WEB-INF/web.xml. En intégration, utiliser un emplacement comme **/var/univmobile/data**

Les données XML y sont organisées en sous-répertoires :

  * comments
  * comment_threads
  * pois
  * poitrees
  * regions
  * users
  
S’assurer que l’emplacement pour le fichier de log est accessible en écriture pour l’utilisateur qui fait s’exécuter Tomcat (en général, « tomcat »). 

Cet emplacement est défini dans la webapp, dans WEB-INF/classes/log4j.xml. En intégration, utiliser un emplacement comme **/var/log/univmobile.log** 

S’assurer que le paramètre `baseURL` est bien positionné dans WEB-INF/web.xml. En intégration, utiliser **https://univmobile-dev.univ-paris1.fr/testSP**

En intégration, comme l’URL de base sécurisée par Shibboleth et l’URL de base des flux JSON sont différentes, il faut que le paramètre `optional-jsonBaseURLs` soit renseigné :

    <init-param>
        <param-name>optional-jsonBaseURLs</param-name>
        <param-value>
            https://univmobile-dev.univ-paris1.fr/json/
        </param-value>
    </init-param>

Le filtre unm-backend-filter-noshib (NoShibbolethFilter) ne doit _pas_ être activé.

Note : en intégration, le fichier unm-backend-filter-noshib.jar n’a pas à être présent dans WEB-INF/lib/

Vérification de l’application web  :

Se connecter à `baseURL` = https://univmobile-dev.univ-paris1.fr/testSP

On doit s’authentifier auprès de Shibboleth.
    
Pour que l’application unm-backend-app accepte l’utilisateur authentifié, il faut que le paramètre « remoteUser » corresponde à un champ « remoteUser » dans un des fichiers XML de `(dataDir)`/users/ :

    <content>
        <uid>crezvani</uid>
        <cn>Rezvani Cyrus</cn>
        <sn>Rezvani</sn>
        <givenName>Cyrus</givenName>
        <remoteUser>crezvani@univ-paris1.fr</remoteUser>
        <mail>Cyrus.Rezvani@univ-paris1.fr</mail>
        <supannCivilite>M.</supannCivilite>
        <displayName>Cyrus Rezvani</displayName>
    </content>

Une fois authentifié par Shibboleth, si la mise en page et les couleurs s’affichent, cela signifie que la valeur de `baseURL` est correcte : les feuilles CSS sont référencées en prenant cette URL comme base.

-> ![](src/site/images/screenshot-home.png?raw=true =300x "Accueil UnivMobile Admin") <-

Vérification des flux JSON :

Se rendre sur `(baseURL)`/json, ou sur `(baseURL)`/json?html pour la version indentée. Pour cette dernière, on reçoit un flux JSON dans ce genre :

    {
        "url":"https://univmobile-dev.univ-paris1.fr/json",
        "pois":{
            "url":"https://univmobile-dev.univ-paris1.fr/json/pois"
        },
        "regions":{
            "url":"https://univmobile-dev.univ-paris1.fr/json/regions"
        }
    }

Les champs « url » dans le flux JSON renvoyés sont cohérents avec les `optional-jsonBaseURLs`.
