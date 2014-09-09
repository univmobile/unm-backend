Indexation des entités
======================

Documentation parente : [unm-backend](README.md)

Pour la documentation quant à l’utilisation de XML pour les données,
voir : [XMLData.md](XMLData.md)

### Généralités

Les données métier sont stockées sous forme de documents XML (en
itération 3, des fichiers
XML sur le système de fichiers local).

Afin d’offrir un accès rapide aux données, aussi bien
par des identifiants et des clés, que par la recherche textuelle,
une indexation est nécessaire.

L’indexation n’ajoute aucune information métier aux documents XML.
En particulier, par construction, 
d’un point de vue métier toutes les solutions d’indexation
peuvent être supprimées, 
puis reconstruites uniquement à partir
des documents XML. Seul l’applicatif subit un impact pendant ce temps ; en 
général, on désactive les accès applicatifs pendant la reconstruction
des index.

Les tables d’indexation ne servant qu’à l’indexation et pouvant
être reconstruites à la demande,
on se permet des redondances. Exemple :
liens inverses entre les tables **unm_revfiles**
et **unm_entities_xxx**.

### Indexation par clés 

Chaque flux ATOM de données
est indexé par son identifiant unique, \<atom:id/\>,
qu’on retrouve également dans
\<link rel="self"/\>.

Les données métier dans l’élément \<atom:content/\>
contiennent, elles, des clés métier d’accès aux données.
Par exemple le POI n°20156, dont la version n°1 est stockée en ce qui concerne les tests unitaires dans le fichier « pois/poi20156_1.xml », aura la clef métier : \<content uid="20156"/\>.

Note : toutes les versions (poi20156_1.xml, poi20156_2.xml, etc.) de l’entité
auront des identifiants \<atom:id/\> différents, mais
auront en commun la clef métier \<content uid="20156"/\>.

À partir d’une DataSource dans le code Java, on peut retrouver une entité par ses attributs déclarés via l’annotation **@SearchAttribute**.

Exemple de récupération d’une entité :

    final RegionDataSource regions = ... 
    
    final Region region = regions.getByUid("ile_de_france");
    
En ayant au préalable déclaré dans l’interface RegionDataSource :

    @SearchAttribute("uid")
    Region getByUid(String uid);
    
Les attributs via lesquels on peut récupérer une entité auprès de la DataSource doivent être déclarés avec une annotation **@SearchAttribute** an niveau de leurs méthodes
d’accès (getters). Une annotation **@PrimaryKey** au niveau de la classe
elle-même permet de spécifier une clef primaire.

Exemple :

    @Category("regions")
    @PrimaryKey("uid")
    @Support(data = Region.class, builder = RegionBuilder.class)
    public interface RegionDataSource extends
            BackendDataSource<Region, RegionBuilder> {

        @SearchAttribute("uid")
        Region getByUid(String uid);

        boolean isNullByUid(String uid);
    }

L’implémentation de cette indexation par clés peut se faire notamment
en Java seul, ou avec une base de données.

### Base de données pour l’indexation

En itérations 1 et 2 on utilisait une HashMap pour cette indexation, avec
tous les documents XML chargés en mémoire (note : 7 440 POIs).

En itération 3, on utilise une base de données.

La table **unm_categories** établit un lien vers les répertoires de fichiers XML, avec
les colonnes suivantes :

   * **id** : ENUM ou VARCHAR(20), NOT NULL, UNIQUE : la catégorie de l’entité métier. Exemples : "users", "pois"…
   * **absolute_path** : VARCHAR(255), NOT NULL, UNIQUE : le chemin local du répertoire de la catégorie sur le système de fichiers. Exemple : "/var/univmobile/data/users/"
   * **locked_since** : TIMESTAMP NULL : renseigné si un verrou logique est posé sur la catégorie, par exemple si elle est en cours d’indexation.
       * Note : deux indexations ne peuvent pas avoir lieu en même temps.

Note : dans cette documentation,
le noms de tables est préfixé par « unm\_ », mais c’est un paramètre
de configuration. Pour l’application J2EE backend, il est dans WEB-INF/web.xml.

Tout d’abord, chaque fichier XML (c’est-à-dire entité + version)
est référencé dans une table **unm_revfiles**, dont voici les champs :

  * **id** BIGINT, PRIMARY KEY : identifiant interne unique du fichier, 
    a priori servi
    par une séquence. Exemples : 1, 2, 30, 10047… Note : il n’y
    aucune obligation à ce qu’un fichier
    d’une version d’une entité métier donnée ait un identifiant numérique
    inférieur à celui du fichier d’une version ultérieure de la même entité.
  * **category_id** ENUM ou VARCHAR(20), NOT NULL, FOREIGN KEY(unm_categories.id) : la catégorie de l’entité métier.
    Exemples : "users", "pois"…
  * **path** VARCHAR(200) NOT NULL, INDEX : le chemin, unique pour la
      catégorie, du fichier XML local
      relatif à sa catégorie.
    Exemple : "poi2604_1.xml"
  * **atom_id** VARCHAR (255) NOT NULL, INDEX : l’identifiant, unique au moins pour la
      catégorie, du flux ATOM contenu dans le fichier XML.
  * **entity_id** BIGINT NULL, INDEX : l’identifiant
    interne de l’entité métier correspondante, dans la table
    « unm_entities_xxx » où « xxx » est la catégorie de l’entité métier.
    Exemple : 1328
  * **rev** BIGINT NOT NULL : le numéro indicatif de version. Exemples, 1, 2, 3… On essaie
    de faire en sorte que ces numéros soient successifs. En revanche, ils peuvent
    être modifiés en cas d’ajout d’une nouvelle version, même en fin de liste.
  * **parent_id** BIGINT NULL, FOREIGN KEY(unm_revfiles.id) : si
    la version de l’entité métier a une version parente, l’identifiant interne de
    cette version.
  * **locked_since** TIMESTAMP NULL : renseigné si un verrou logique est
    posé sur le fichier ;
    par exemple s’il est en train d’être créé sur le disque.
    Ce champ donne le début du verrou.
      * Note : un fichier marqué comme verrouillé (WHERE locked_since IS NOT NULL)
        ne sera pas copié lors d’une sauvegarde
        par l’utilitaire unm-backend-sysadmin.jar.
  * **status** ENUM ou TINYINT, NOT NULL : le statut du fichier :
      * _disabled_ (0)
      * _active_ (1)
      * Note : un fichier marqué _disabled_ ne sera pas copié lors d’une sauvegarde
        par l’utilitaire unm-backend-sysadmin.jar.

Ensuite, chaque entité est référencée dans une table spécifique, dont le nom commence par « unm_entities_ ». Certains champs sont communs à toutes
les entités et, mis à part « id », commencent par « unm\_ », d’autres sont métier
et sont spécifiques à chaque entité ; ils servent à la recherche et sont spécifiés par l’annotation **@SearchAttribute** dans la déclaration Java de la DataSource.
  
  * Champs communs :
    * **id** BIGINT NOT NULL, PRIMARY KEY : identifiant interne unique de l’entité,
      a priori servi par une séquence. Exemples : 5, 6, 7, 1328…
    * **unm_locked_since** TIMESTAMP NULL : renseigné si un verrou métier est posé 
      sur l’entité ; par exemple si une modification bloquante est en cours.
        * Note : à la différence des verrous sur les fichiers de versions,
            les verrous métier sur les entités
            (WHERE unm_entities_xxx.locked_since IS NOT NULL) 
            n’influent pas sur les sauvegardes.
            Toutes les versions de l’entité seront ajoutées à la sauvegarde,
            sauf les versions en cours d’écriture
            (WHERE unm_revfiles.locked_since IS NOT NULL)
    * **unm_active_revfile_id** BIGINT NOT NULL, FOREIGN KEY(unm_revfiles.id) :
      l’identifiant interne du fichier de la version active de cette entité.
      Exemple : 10047
    * **unm_status** ENUM ou TINYINT, NOT NULL : le statut de l’entité :
        * _disabled_ (0)
        * _active_ (1)
  * Champs spécifiques, exemple avec category=users, classe Java UserDataSource :
    * **uid** (exemple) VARCHAR(40) NOT NULL, UNIQUE : un identifiant métier spécifié
      par @SearchAttribute dans le code Java.
    * **remoteUser** (exemple) VARCHAR(200) NOT NULL, UNIQUE : l’identifiant métier
      spécifié à la fois par @SearchAttribute et par @PrimaryKey dans le 
      code Java.
    
Exemple de temps de construction de cet index en ligne de commande :

    $ java -jar unm-backend-sysadmin.jar index \
        -data /var/univmobile/data/ \
        -dburl jdbc:mysql://localhost:3306/univmobile \
        -dbusername xxx -dbpassword
    xxx
    
Si les tables unm_categories, unm_revfiles, unm_entities_xxx n’existent pas, elles
sont créées à l’indexation.



### Structures arborescentes

TODO : poitrees

TODO : comment_threads

### Indexation textuelle

TODO : indexation textuelle

