## Console d’administration

Vous devez être authentifié à travers la fédération d’identité
Shibboleth pour utiliser l’application, à l’adresse suivante :
[https://univmobile-dev.univ-paris1.fr/testSP/](https://univmobile-dev.univ-paris1.fr/testSP/)

Vous devez également avoir un compte dans l’application. Si
ce n’est pas le cas, vous devez en faire la demande auprès
d’un des administrateurs.

Contacts :

  * Toto Formica — [toto.formica@unpidf.fr](mailto:toto.formica@unpidf.fr)

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

