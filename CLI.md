Outil en ligne de commande
==========================

Documentation parente : [unm-backend](README.md)

### Introduction

L’outil ligne de commande « unm-backend-sysadmin »
permet de manipuler les données métier de l’application
UnivMobile.

Commandes :

| Commande | Description |
| :-- | :-- |
| **lock** | Affiche les ressources verrouillées / Verrouille des ressources |
| **unlock** | Déverrouille des ressources |
| **drop** | (DANGEREUX) Supprime les tables métier de la base de données |
| **index** | (DANGEREUX) Réindexe toutes les données XML |

Par exemple, la commande suivante affiche les ressources verrouillées :

    $ java -jar unm-backend-sysadmin.jar lock

L’option « -h » permet d’avoir une liste des commandes et des options :

    $ java -jar unm-backend-sysadmin.jar -h
    Usage: java -jar unm-backend-sysadmin.jar [options] [command] [command options]

    
Les options communes sont :

| Option | Description |
| :-- | :-- |
| -dbtype xxx | Le type de la base de données : mysql (par défaut) ou h2 |
| -dburl xxx | L’URL JDBC de la base de données |
| -dbusername xxx | L’identifiant avec lequel se connecter à la base de données | 
| -dbpassword | Déclenche une invite de commande pour le mot de passe |

On peut s’affranchir des options en renseignant aux variables d’environnement suivantes :

  * UNM_DBTYPE
  * UNM_DBURL
  * UNM_DBUSERNAME
  * UNM_DBPASSWORD
  
### Commande : lock

Affiche les ressources verrouillées.

Exemple :

    $ java -jar unm-backend-sysadmin.jar lock \
        -dburl jdbc:mysql://localhost:3306/univmobile \
        -dbusername xxx -dbpassword
    The DB Connection password: 
    Locked categories: 1
      pois, since: 2014-09-03T07:34:05.000+02:00 -- .../data/pois

