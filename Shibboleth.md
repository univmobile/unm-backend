Intégration avec Shibboleth
===========================

Documentation parente : [unm-backend](README.md)

### Java et Shibboleth : configuration

La configuration suivante est testée avec succès :

Dans /etc/apache2/sites-enabled/unm-backend-ssl :

    LoadModule proxy_module modules/mod_proxy.so
    LoadModule proxy_ajp_module modules/mod_proxy_ajp.so
    ProxyRequests Off
    ProxyPass /unm-backend ajp://localhost:8009/unm-backend
    ProxyPassReverse /unm-backend ajp://localhost:8009/unm-backend

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
