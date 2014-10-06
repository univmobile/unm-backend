## Authentification

### URLs de connexion

Pour se connecter à UnivMobile, on dispose des points d’entrée
suivants :

 * point d’entrée Shibboleth (/testSP/ en intégration)
 * point d’entrée authentifié sans Shibboleth (/login/)
 * point d’entrée non authentifié (/)
 * point d’entrée JSON (/json/)
 
### Modes de connexion

 * applications Mobile :
    * un utilisateur d’UnivMobile peut s’authentifier depuis une application mobile
    * il n’aura accès qu’aux actions du rôle « user » ; il n’est pas possible d’admnistrer UnivMobile depuis les applications mobiles
    * il peut choisir de s’authentifier via Shibboleth :
       * il doit être connu de la fédération d’identité de l’université qu’il a sélectionnée dans l’application (de la première université de sa liste, s’il en a sélectionné plusieurs)
       * l’application mobile affichera une WebView pour la phase d’authentification
    * il peut choisir de s’authentifier hors de Shibboleth :
       * le mode « Login Classique » doit avoir été activé dans son profil
       * il n’y a pas de redirection vers une WebView
       
  * application desktop (backend) :
    * un utilisateur d’UnivMobile peut s’authentifier depuis l’application desktop
    * il aura accès aux actions de son rôle : « user », « admin » ou « superadmin »
    * il peut choisir de s’authentifier via Shibboleth :
       * il doit être connu de la fédération d’identité de l’université qu’il a sélectionnée dans une liste déroulante
    * il peut choisir de s’authentifier hors de Shibboleth :
       * le mode « Login Classique » doit avoir été activé dans son profil
    * s’il passe par Shibboleth, un administrateur a la possibilité de se connecter en tant que délégué d’un autre utilisateur
       
       
### Déconnexion

 * Shibboleth : en cliquant sur « Déconnexion »,
 on conserve sa session Shibboleth, 
 mais l’application UnivMobile n’est plus active.
 
 * Classique : en cliquant sur « Déconnextion »,
 on perd sa session.

### Administrateurs

Les administrateurs ne doivent _pas_ impérativement être
authentifiés à travers la fédération d’identité
Shibboleth pour utiliser l’application.

Vous devez également avoir un compte dans l’application. Si
ce n’est pas le cas, vous devez en faire la demande auprès
d’un des administrateurs.

## Rôles et utilisateurs

Trois rôles sont définis au sein de l’application :

  * user (authentifié)
  * admin
  * superadmin

### Rôle : user (authentifié)

Il peut :

  * modifier son profil
  * activer ou désactiver son mot de passe
  * activer ou désactiver son authentification OAuth
  * ajouter un commentaire sous un POI    
    
### Rôle : admin

En plus des droits user, il peut :

  * accéder à la console d’administration
  * ajouter un nouveau compte utilisateur (user)
  * modifier, activer ou désactiver un compte utilisateur (user)
  * ajouter un POI
  * modifier la configuration d’une université
  * activer ou désactiver un POI
  * activer ou désactiver un commentaire
  * afficher les informations système
  * afficher l’historique des actions
  * afficher les statistiques
  * afficher les éléments de monitoring du serveur

### Rôle : superadmin

En plus des droits admin et user, il peut :

  * accéder à la console d’administration
  * ajouter un nouvel administrateur (admin et superadmin)
  * modifier, activer ou désactiver un compte administrateur (admin et superadmin)
  * afficher les logs techniques du serveur 
  * récupérer une archive des données (sauvegardes / backups)

## Authentification

### Utilisateurs (user)

Un usage d’UnivMobile en lecture seule ne nécessite pas
d’authentification.

L’authentification est demandée pour, notamment,
l’envoi de commentaires.

Deux méthodes d’authentification sont implémentées :

  1. À travers la fédération d’identité Shibboleth.
  2. À l’aide d’un identifiant et un mot de passe.

En tant qu’administrateur, vous pouvez créer un compte utilisateur
dans UnivMobile avec ou sans mot de passe — s’il n’a pas de mot
de passe dans UnivMobile, l’utilisateur sera alors obligé 
d’utiliser Shibboleth.

Le fait d’être dans la fédération d’identité dispense de
créer des comptes utilisateurs dans UnivMobile : un compte
(user) est automatiquement créé à la première authentification
réussie à travers Shibboleth.

En revanche, si vous souhaitez qu’un utilisateur déjà enregistré
devienne administrateur, il faudra modifier son profil et
lui donner les droits admin. Seul un superadmin peut exécuter
cette action.

Certaines universités ne sont pas dans la fédération d’identité.
Pour permettre à leurs étudiants de s’authentifier auprès d’UnivMobile,
il faut donc créer des comptes avec mots de passe.

### Administrateurs (admin et superadmin)

L’authentification à travers la fédération d’identité
Shibboleth est obligatoire.

