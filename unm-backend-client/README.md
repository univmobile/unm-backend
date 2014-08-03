# unm-backend-client

Ce projet contient les classes de communication entre le client Java de l’application mobile web UnivMobile,
et le backend J2EE UnivMobile.

La communication HTTP+JSON avec le backend UnivMobile fait intervenir 
plusieurs composants :

  * unm-backend-client-api — L’API applicative : elle manipule des objets applicatifs, comme « Region » et « University ».

  * unm-backend-client-local — L’implémentation de base de l’API applicative : 
elle s’appuie sur des classes d’accès aux
données, qu’on trouve dans un autre projet,
unm-backend-core.

  * unm-backend-client-json — L’implémentation de l’API applicative qui
         utilise des flux JSON en entrée.
   
  * unm-backend-client-jsonapi — L’API JSON : elle manipule la version JSON
         (texte) des objets applicatifs.
                           
  * unm-backend-client-jsonlocal — L’implémentation de base de l’API JSON : 
         elle produit des flux JSON (textes) à partir
         d’objets applicatifs en mémoire.

Les tests d’intégration, avec une web app J2EE déployée
(unm-backend.war — projet unm-backend-app) sont dans le projet
unm-backend-it.
    
Les tests unitaires qui utilisent un couplage local,
sont dans le projet unm-backend-client-json.

Voir le projet unm-backend-client-json pour des diagrammes explicatifs.
