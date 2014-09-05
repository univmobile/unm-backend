Architecture d’unm-backend
==========================

Documentation parente : [unm-backend](README.md)

### Architecture technique

![](src/site/images/backend-arch.png?raw=true =900x "Architecture technique")

Pour la confidentialité, les connexions distantes se font via HTTPS.

Chaînes techniques : Administrateur

  * Un administrateur se connecte à l’application « UnivMobile backend » à travers un navigateur web
  * Il doit être authentifié par Shibboleth (fédération d’identités)
  * Le JavaScript de ses pages HTML donne lieu à des appels JSON vers le backend, qui ne passent pas par Shibboleth.

Chaînes techniques : Étudiant, application Mobile web

  * Un étudiant se connecte à l’application « UnivMobile Mobile web » à travers un navigateur web sur un appareil mobile
  * En itération 2 (pas sur le diagramme), le JavaScript de ses pages HTML donne lieu à des appels JSON directement vers le backend
  * En cible (= diagramme), le JavaScript de ses pages HTML donne lieu à des appels JSON vers l’application Mobile web
  * Pour récupérer des données, l’application Mobile web fait des appels JSON vers le backend (= diagramme)
  
Chaînes techniques : Étudiant, applications iOS et Android

  * Un étudiant se connecte à l’application « UnivMobile » iOS ou Android sur un appareil mobile
  * Pour récupérer des données, l’application mobile fait des appels JSON vers le backend

## Données

### Stockage des données

Les données sont stockées en tant que fichiers XML.

Une démarche d’indexation des données, en particulier pour la recherche, et donc l’utilisation de bases de données relationnelles pour cet aspect, est à mener à partir de l’itération 3.

À l’itération 2, les entités stockées sont :

  * les fiches utilisateurs (users/)
  * les descriptions des régions et des universités dans (regions/)
  * les POIs, Point Of Interest et leurs arbres hiérarchiques (pois/ et poitrees/)
  * les commentaires et leurs fils (comments/ et comment_threads/)

Les répertoires des fichiers XML de données sont dans un répertoire local `dataDir` référencé dans le fichier WEB-INF/web.xml de la servlet unm-backend-app. Exemple : `dataDir` = /var/univmobile/data

### Sauvegarde et restauration

Procédure : sauvegarde et restauration du répertoire `dataDir` qui contient tous les fichiers XML.

À l’itération 2, la sauvegarde et la restauration de données ne sont pas automatisées.

## Authentification

### Shibboleth

L’utilisation de la fédération d’identité Shibboleth offre d’externaliser
la sécurisation de l’accès à l’application d’administration,
tout en garantissant un _Single Sign On_ (SSO) à l’administrateur.

Pour l’utilisation par UnivMobile, voir :
[Intégration avec Shibboleth](unm-backend-app/Shibboleth.md)

### Authentification des administrateurs : Shibboleth

Les administrateurs s’authentifient auprès d’UnivMobile à travers Shibboleth.

C’est à UnivMobile d’avoir dans ses propres bases de données une correspondance entre les
utilisateurs habilités à utiliser l’application, et
les identifiants Shibboleth.

La correspondance se fait par l’attribut « REMOTE_USER ». Exemple dans un fichier XML de données d’un utilisateur habilité, stocké dans `(dataDir)`/users :

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
    
Lorsque l’utilisateur se connecte effectivement au moyen de Shibboleth, celui-ci propage entre autres l’attribut « REMOTE_USER ».

Voir : [Intégration avec Shibboleth](unm-backend-app/Shibboleth.md).

### Authentification des étudiants : Shibboleth, OAuth, mot de passe

Note : en itération 2, les étudiants ne peuvent pas du tout
s’authentifier auprès d’UnivMobile.

En cible : les étudiants qui ont un compte web universitaire protégé par Shibboleth (cela dépend des universités) peuvent s’authentifier (a priori via une Web view) auprès de Shibboleth, et un token sera créé au niveau d’UnivMobile pour leur en permettre l’utilisation.

En cible : les étudiants qui ont un compte UnivMobile protégé par mot de passe (voir plus bas) peuvent s’authentifier avec, sans passer par Shibboleth.

En cible : les étudiants qui le souhaitent, et qui sont déjà authentifiés (Shibboleth ou mot de passe, voir plus bas) peuvent activer ou désactiver OAuth pour leur compte.

En cible : les étudiants qui le souhaitent, et qui sont déjà authentifiés (Shibboleth ou OAuth) peuvent activer ou désactiver une authentification par mot de passe pour leur compte.

En cible : les universités qui ne font pas partie de la fédération d’identité Shibboleth utilisée par UnivMobile, ou tout simplement qui le souhaitent, peuvent créer des comptes étudiants avec mot de passe.

Les chaînes de connexion suivantes sont à valider :

  * navigateur → Mobile web → Shibboleth → UnivMobile backend
  * iOS → UnivMobile iOS → Shibboleth → UnivMobile backend
  * Android → UnivMobile UnivMobile → Shibboleth → UnivMobile backend
  
  