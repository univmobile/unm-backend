unm-backend-app
================

Webapp backend du projet UnivMobile.

Elle regroupe :

  * les web services (JSON) utilisés par les applications mobiles iOS / Android / Mobile web concernant les informations sur les universités
  * les web services Géocampus
  * les autres web services (notamment le mails in-app)
  * la console d’administration UnivMobile
  * la console d’administration Géocampus
  
Pour la console d’adminisration, elle est à déployer dans une configuration sécurisée par HTTPS + Shibboleth.

Pour la partie web services, elle est à déployer dans une configuration sécurisée par HTTPS.
  
## Console d’administration

Sécurisée par HTTPS + Shibboleth.

### Écran d’identification

Cible : on arrive à cet écran par [https://admin.univmobile.fr/](https://admin.univmobile.fr/)

Si on n’est pas authentifié / identifié auprès de la fédération d’identité, on a un écran de connexion SSO.

Si on est authentifié :

  1. Si le compte SSO (exemple : e-mail de la personne connectée) correspond à un compte administrateur d’UnivMobile :
      * Écran d’accueil « Bienvenue M. Xxx »
      * Mention « Pour vous déconnecter, vous devez fermer toutes les fenêtres de votre navigateur web »
      * Possibilité de cliquer sur le bouton « Entrer » (en tant que M. Xxx)
      * Possibilité de remplir le formulaire « Entrer en tant que délégué de… », zone de saisie d’un autre identifiant (exemple : e-mail d’un autre administrateur M<sup>me</sup> Yyy), zone de saisie d’un mot de passe (non pas le mot de passe Shibboleth, mais un mot de passe, optionnel, protégeant la délégation, et positionné par M<sup>me</sup> Yyy)
      * Dans ce cas, toutes les autorisations correspondront au compte principal (M<sup>me</sup> Yyy), et seront tracées comme ayant été faites par le compte délégué (M. Xxx) au nom du compte principal (M<sup>me</sup> Yyy).
      
   2. Si le compte SSO ne correspond à aucun compte administrateur d’UnivMobile :
       * Message « Vous ne disposez pas de compte dans cette application. »
       
### Délégation de compte

Voir plus haut pour la phase d’identification.

Pour la déclaration de délégations :

  * Par défaut, un compte n’a aucun délégué, il faut le configurer pour qu’il permette la délégation.
  * Possibilité de permettre à n’importe quel autre compte administrateur UnivMobile d’être délégué. Saisie d’un mot de passe optionnel (= peut être vide) commun à tous les délégués.
  * Possibilité de sélectionner ses comptes délégués. Saisie d’un mot de passe optionnel (= peut être vide) commun à tous les délégués.

### Console générale

Une fois identifié (en nom propre ou en délégué), on arrive sur l’écran d’administration.

On peut se « déconnecter » :

 1. Si on était délégué, la session se délégation se termine et on revient sur l’écran d’identification.
 2. Si on était en son nom propre, on revient sur l’écran d’identification.
  
### Gestion des utilisateurs

On peut gérer les utilisateurs. Selon ses droits :

  * Voir la liste des utilisateurs
  * Voir leurs profils ; administrateurs / non administrateurs ; actifs / désactivés
  * Consulter leurs fiche-détail
  * Créer un utilisateur
  * Désactiver un utilisateur
  * Supprimer un utilisateur
  * Modifier la fiche-détail d’un utilisateur
