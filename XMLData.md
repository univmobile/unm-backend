Données XML
===========

Documentation parente : [unm-backend](README.md)

Les données du backend UnivMobile sont stockées au format XML.

En itération 3, elles sont directement stockées sur le système de fichiers :

 * sans recours à une base de données pour le stockage,
 * mais en utilisant une base de données pour leur indexation.
 
### Principes généraux

Les données sont accessibles via des structures XML (DOM).

Les données métier ont un suivi de version.

Chaque document XML stocké représente une **version** donnée d’une **entité** métier, stockée dans une **catégories**. En itération 3, les catégories sont :

  * Les Utilisateurs
  * Les [descriptifs de] Régions (ces entités contiennent les descriptifs des Universités)
  * Les Points of Interests, POIs
  * Les Commentaires

Note : en itération 2, on avait également les catégories PoiTrees et CommentThreads, mais il s’agissait moins d’entités métier à proprement parler que de consolidations d’entités préexistantes.

### Noms des fichiers XML

En utilisation normale, les fichiers XML sont nommés de façon technique. Exemple : pois/26401_1409671354012_64022600.xml

Ils sont stockés dans des répertoires qui correspondent aux catégories :

  * users/
  * regions/
  * pois/
  * comments/

En revanche, pour les tests unitaires, les fichiers XML sont en général nommés de façon pratique. Exemple : users/001/dandriana_1.xml, où « dandriana » est l’identifiant métier (en l’occurrence la valeur du champ « uid »), et « _1 » est le numéro de version.

Pour les tests unitaires d’unm-backend-core, les fichiers XML sont stockés dans des répertoires numérotés :

  * users/001/
  * regions/001/
  * pois/001/ — pois/002/ — pois/003/
  * comments/001/

### Structure des documents XML

La structure est un simple flux ATOM. Le namespace est « http://www.w3.org/2005/Atom », dont on prend le plus souvent comme synontme le préfixe `atom:`

Tous les documents XML de données répondent à une structure de métadonnées ATOM commune :

  * Un élément racine qui est en général `atom:entry`
  * Un élément `atom:id` qui sert à identifier techniquement le flux ATOM
     * à ne pas confondre avec l’identifiant de l’entité métier : il y a un identifiant par flux ATOM, donc par version d’une entité métier, alors que quelle que soit sa version, une entité métier aura toujours le même identifiant métier — par exemple le n° du POI.
  * Un élément `atom:link[@rel = 'self']`, qui reprend le champ `atom:id`
  * Un élément `atom:updated`, qui est la date de mise à jour du flux ATOM.
  * Un élément `atom:title`, qui est une indication (par exemple pour un administrateur) de ce que contient l’entité métier.
  * Un élément `atom:category`, qui indique à quelle catégorie appartient l’entité métier.

Exemple, le fichier « dandriana_1.xml » (ces exemples se trouvent dans les tests unitaires du projet Maven unm-backend-core) :

    <?xml version="1.0" encoding="UTF-8"?> 
    <entry xmlns="http://www.w3.org/2005/Atom">
    <id>fr.univmobile:unm-backend:test/users/001:dandriana_1</id>
    <link rel="self" href="fr.univmobile:unm-backend:test/users/001:dandriana_1"/>
    <updated>2014-07-26T19:12:02Z</updated>
    <title>dandriana</title>
    <category term="users"/>
    <author>
            <name>dandriana</name>
            <uri>fr.univmobile:unm-backend:test/users:dandriana</uri>
    </author>
    <content>
        ...
    <content> 
    </entry>

Note : en itération 2, les données métier (à l’intérieur du nœud `atom:content`) sont encore sous le namespace `atom:`

La création d’une donnée métier par l’application UnivMobile (par exemple, la création d’un nouveau POI) donne lieu à un nouveau fichier XML.

Exemple, fragments du fichier « poi20156_1.xml » :

    <entry xmlns="http://www.w3.org/2005/Atom">
    ...
    <link rel="self" href="fr.univmobile:unm-backend:test/users/001:dandriana_1"/>
    ...
    <content uid="20156"
        coordinates="46.141515028834,-1.15040548145"
        active="true"
        markerType="point"
        parentUid="20030"
        name="Génie Civil (GC)">
        <poiType id="27" label="Département IUT"
            categoryId="8" categoryLabel="Administration"/>
    </content>
    </entry>

La mise à jour d’une donnée métier par l’application UnivMobile (par exemple, la modification du numéro de téléphone d’un POI) donne lieu à un nouveau fichier XML, qui référence sa version « parente » à l’aide d’un élément `atom:link[@rel = 'parent']`

Exemple dans le fichier « dandriana_2.xml » (métadonnées spécifiques à une nouvelle version) :

    <entry xmlns="http://www.w3.org/2005/Atom">
    ...
    <link rel="self" href="fr.univmobile:unm-backend:test/users/001:dandriana_2"/>
    <link rel="parent" href="fr.univmobile:unm-backend:test/users/001:dandriana_1"/>
    ...
    </entry>

Dans UnivMobile, il n’y a pas de règle a priori pour l’élément `atom:content` : chaque type d’entité a sa structure propre, en particulier pour les identitifants métier.

Exemples en itération 2 :

  * les identifiants d’Utilisateurs sont stockés dans un élément `atom:content/atom:uid` — Exemple : \<uid\>dandriana\</uid\>,
  * alors que les identifiants de POIs sont stockés dans un attribut `atom:content/@uid` — Exemple : \<content uid="20156" ... > ... \</content\>  

### Données de type « url »

La règle d’uniformisation des champs « url » dans les flux JSON (voir : [JSON.md](JSON.md)) ne concerne que les flux JSON servis dans la couche applicative, et pas les données métier XML stockées.

En particulier, rien n’interdit aux fichiers XML de déclarer et de gérer par exemple un attribut « @commentsUrl », du moment que le flux JSON final contient bien la structure « "comments":{ "url": xxx } » avec un champ explicite « url ».

### Accès Java aux données d’une entité

Les documents XML font l’objet d’un _bind_ vers des classes Java, ce qui permet par exemple d’écrire (voir les tests unitaires dans unm-backend-core) :

    final RegionDataSource regions = ... // Voir plus bas
    
    final Region region = regions.getByUid("ile_de_france");
    
    assertEquals("ile_de_france", region.getUid());
    	
    assertEquals("Île de France", region.getLabel());

Le document XML qui correspond à ce test est le suivant (fichier « ile_de_france_1.xml ») :

    <?xml version="1.0" encoding="UTF-8"?> 
    <entry xmlns="http://www.w3.org/2005/Atom">
    <id>fr.univmobile:unm-backend:test/regions/001:ile_de_france_1</id>
    <link rel="self" href="fr.univmobile:unm-backend:test/regions/001:ile_de_france_1"/>
    <title>Île de France</title>
    <updated>2014-07-27T20:06:02Z</updated>
    <category term="regions"/>
    <author>
            <name>dandriana</name>
            <uri>fr.univmobile:unm-backend:test/users:dandriana</uri>
    </author>
    <content>
            <uid>ile_de_france</uid>
            <label>Île de France</label>
            <url>http://univmobile.vswip.com/unm-backend-mock/listUniversities_ile_de_france</url>
    </content>
    </entry>
    
Cet exemple utilise l’interface Java Region qui va en production. Elle déclare les chemins XPath d’accès aux nœuds du DOM de la façon suivante :

    package fr.univmobile.backend.core;
    
    import net.avcompris.binding.annotation.XPath;
    import fr.univmobile.commons.datasource.Entry;

    public interface Region extends Entry<Region> {
    
        @XPath("atom:content/atom:uid")
        String getUid();

        @XPath("atom:content/atom:label")
        String getLabel();
        
        @XPath("atom:content/atom:url")
        String getUrl();
        
        ...
    }

### Accès Java aux entités

TODO : DataSources, RegionDataSource

TODO : support = Region, RegionBuilder

TODO : @SearchAttribute

### Caching

TODO : on fait encore du caching sur les BackendDataSourceFileSystem, en ité 3 ?

### Indexation des entités

Voir : [Indexation.md](Indexation.md)

### Sauvegarde et restauration

Une sauvegarde des entités métier peut être réalisée. 
En gros, il s’agit d’une copie des fichiers XML.

Un utilitaire est prévu pour une sauvegarde à chaud :

    $ java -jar unm-backend-sysadmin.jar lockedfiles \
        -dburl jdbc:mysql://localhost:3306/univmobile \
        -dbusername xxx -dbpassword

  * à chaud
  
### Liste des fichiers verrouillés

Lorsqu’ils sont accédés en écriture (en itération 3,
cela n’arrive que lors de leur création),
les fichiers XML sont marqués comme verrouillés dans leur table
de suivi en base de données.

Ils ne sont pas éligibles à la lecture par l’application.

Pour avoir la liste des fichiers marqués comme verrouillés :

    $ java -jar unm-backend-sysadmin.jar lockedfiles \
        -dburl jdbc:mysql://localhost:3306/univmobile \
        -dbusername xxx -dbpassword
    xxx
