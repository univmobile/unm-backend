Intégration avec Shibboleth
===========================

Documentation parente : [unm-backend](README.md)

### Version de Shibboleth utilisée

Sur univmobile-dev.univ-paris1.fr (intégration) :

    $ more /usr/share/doc/libapache2-mod-shib2/README.txt
    Welcome to Internet2's Shibboleth

### Java et Shibboleth : configuration

La configuration suivante est testée avec succès :

Dans /etc/apache2/sites-enabled/univmobile-ssl :

    LoadModule proxy_module modules/mod_proxy.so
    LoadModule proxy_ajp_module modules/mod_proxy_ajp.so
    ProxyRequests Off
    
    <Location /testSP>
      AuthType shibboleth
      ShibRequireSession On
      require valid-user
      ProxyPass ajp://localhost:8009/unm-backend
      ProxyPassReverse ajp://localhost:8009/unm-backend
    </Location>
    
Modifier les lignes suivantes si l’application J2EE est déployée sous le nom testSP.war :

    ProxyPass ajp://localhost:8009/testSP
    ProxyPassReverse ajp://localhost:8009/testSP

Dans ${TOMCAT_HOME}/conf/server.xml :

    <Connector tomcatAuthentication="false"
        port="8009" protocol="AJP/1.3" redirectPort="8443" />

L’attribut XML « tomcatAuthentication="false" » permettra de propager les attributs de Shibboleth.

Dans /etc/shibboleth/shibboleth2.xml :

    <ApplicationDefaults
        entityID="https://univmobile-dev.univ-paris1.fr"
        attributePrefix="AJP_"
        REMOTE_USER="eppn persistent-id targeted-id">

En effet, AJP ne propage les attributs aux servlets que si leurs noms commencent par « AJP_ ».

### Java et Shibboleth : code applicatif

Dans la servlet, la méthode request.getAttributeNames() ne renverra pas les noms des attributs passés par Shibboleth. Il faudra invoquer directement request.getAttribute("…").

Les underscores « _ » dans les noms d’attributs sont remplacés par des tirets « - ».

Pour REMOTE_USER, on ne le voit pas par request.getAttribute("REMOTE_USER") ni request.getAttribute("REMOTE-USER"), en revanche request.getRemoteUser() fonctionne.

C’est REMOTE_USER qui est l’attribut d’identification préféré dans notre infrastructure Shibboleth : 

  * par défaut, c’est l’eppn (eduPersonPrincipalName), sous la forme \<login\> @ \<domaine logique\>, donc qui ressemble de beaucoup à l’e-mail de la personne, exemple : tformica@univ-paris1.fr
  * en fallback, c’est le persistent_id.
  
Même s’ils sont le plus souvent identiques, pour une identification pérenne, l’attribut REMOTE_USER est toutefois plus sûr que l’attribut mail.

| Attribut Shibboleth | Exemple | Invocation sur HttpServletRequest |
| :-- | :-- |
| cn | Formica Toto | getAttribute("cn") |
| displayName | Toto Formica | getAttribute("displayName") |
| eppn | tformica@univ-paris1.fr | getAttribute("eppn") |
| givenName | Toto | getAttribute("givenName") |
| mail | Toto.Formica@univ-paris1.fr | getAttribute("mail") |
| persistent_id | https://...!CgMqx… | getAttribute("persistent-id") |
| REMOTE_USER | tformica@univ-paris1.fr | getRemoteUser() |
| Shib_Identity_Provider | https://idp-test.univ-paris1.fr | getAttribute("Shib-Identity-Provider") |
| sn | Formica | getAttribute("sn") |
| supannCivilite | M. | getAttribute("supannCivilite") |
| uid | tformica | getAttribute("uid") |

### Authentification Mobile web à travers Shibboleth

L’application Mobile web n’est pas elle-même protégée par Shibboleth, c’est l’application web du backend d’UnivMobile qui l’est. Cela assure la cohérence avec les versions iOS et Android.
  
#### Fonctionnement interne

TODO diagramme numéroté

  1. L’utilisateur de l’application Mobile web choisit « Connexion avec Shibboleth ». 
  2. Côté serveur, l’application Mobile web sollicite le backend (par une requête flux HTTPS) en passant un apiToken pour une nouvelle session applicative, et le backend répond en donnant deux identifiants techniques créés aléatoirement, uniques et avec une TTL de 30’ :
    * loginToken — exemple : abc18291
    * key — exemple : fe0a1293
  3. L’application Mobile web dirige l’utilisateur vers une URL UnivMobile (backend) protégée par Shibboleth,  dont l’adresse est fonction de l’université choisie au préalable par l’utilisateur. Par exemple, pour unm-backend.war en intégration, http://univmobile-dev.univ-paris1.fr/testSP/login/paris10?… avec deux paramètres GET :
    * loginToken=xxx (voir plus haut, exemple : abc18291)
    * service=xxx (optionnelle, URL, exemple : http://mobile.univmobile.com/)
  4. Si l’utilisateur n’était par ailleurs pas authentifié auprès de l’IP Shibboleth, il s’authentifie ; sinon, s’il était déjà authentifié, cette étape est sautée.
  5. L’IP Shibboleth redirige le navigateur vers l’URL UnivMobile (http://univmobile-dev.univ-paris1.fr/testSP/login/paris10 dans notre exemple), en ajoutant ses attributs SAML.
  6. L’application backend UnivMobile ouvre une session applicative pour l’utilisateur, identifié par son attribut « remoteUser ». Elle stocke en interne un identifiant de session applicative :
    * appToken — exemple : ab9902e4
  7. La réponse HTTPS de l’application backend UnivMobile contient en cookie l’identifiant de la requête initiale :
    * loginToken (abc18291 dans notre exemple)
  8. S’il y avait un paramètre « service » dans l’URL initiale, l’application backend UnivMobile redirige vers cette URL, en passant l’identifiant de la requête initiale :
    * loginToken (abc18291 dans notre exemple)
    * Note : « appToken » n’est pas renvoyé lors de la redirection, car il peut s’agir d’une URL de redirection en protocole HTTP, non sécurisée.
  9. Si une erreur de redirection a lieu, on arrive quand même d’une façon ou d’une autre (l’utilisateur recharge sa page initiale, ou revient sur une page quelconque de l’application Mobile web) à l’étape suivante.
 10. L’application Mobile web sollicite le backend UnivMobile en HTTPS pour récupérer l’identifiant de session applicative appToken, en passant :
    * loginToken (abc18291 dans notre exemple)
    * keyToken (fe0a1293 dans notre exemple)
 11. Une fois cet identifiant « appToken » connu par l’application Mobile web, chaque requête HTTPS+JSON effectuée auprès du backend par l’application Mobile web sera considérée comme authentifiée.
 
La session applicative ouverte par l’application Mobile web auprès du backend
dépasse la session Shibboleth :

  * Elle est maintenue un mois sans intervention de l’utilisateur.
  * Elle peut être invalidée par un administrateur, ce qui obligera l’utilisateur a retourner sur la page « Login » de l’application Mobile web, puis à cliquer sur « Shibboleth ». S’il est par ailleurs déjà authentifié auprès de Shibboleth, une nouvelle session applicative sera aussitôt créée.
  
#### Exemple vu de l’utilisateur

  1. Dans UnivMobile (Mobile web), il appuie sur « Se connecter… »
  2. Il appuie ensuite sur « Shibboleth… »
     * Navigateur : une redirection a lieu vers une page du backend
     * Navigateur : une redirection a lieu vers la fédération d’identité
  3. Un écran d’authentification Shibboleth apparaît sur son mobile :
  
     ![](src/site/resources/images/shibboleth-mobile.png =100x "Shibboleth")

  4. Il renseigne ses identifiants Shibboleth et appuie sur « Login »
     * Navigateur : une redirection a lieu vers la page du backend initiale
     * Navigateur : une redirection a lieu vers une page d’UnivMobile qui indique que l’authentification a réussi     
  5. L’utilisateur est de retour dans l’application UnivMobile

#### Pourquoi utiliser la sécurisation Shibboleth du backend ?

Le backend accepte la sécurisation Shibboleth, et a pour cela deux jeux d’URLs, toutes en HTTPS : des URLs obligatoirement sécurisées par Shibboleth, des URLs accessibles en anonyme et/ou par identifiants classiques.

Adopter la même mécanique pour une autre application web pouvait être lourd en termes de développement et de tests.

De plus, adopter exactement le même comportement pour les trois types
d’applications mobiles permet de consolider la solution technique.

Enfin, la version Mobile web devrait être la moins utilisée, d’où la recherche d’une solution la plus économique possible.