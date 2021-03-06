Flux JSON
=========

Documentation parente : [unm-backend](README.md)

### Introduction

L’application UnivMobile utilise les flux JSON pour la communication entre ses différents éléments déployés. C’est la partie dite « backend » (projets Maven : unm-backend) qui sert ces flux. Ils sont en particulier consommés par :

  * L’application Mobile web : unm-mobileweb → (JSON) unm-backend
  * L’application iOS : unm-ios → (JSON) unm-backend
  * L’application Android : unm-android → (JSON) unm-backend
  
Sauf mention contraire (informations personnelles de l’utilisateur, messages e-mail, etc.), les flux JSON de cette documentation sont en accès libre.

### Principes

Une URL unique de plus haut niveau permet, au runtime, de retrouver toutes les URLs des flux JSON servis par l’application. Dans notre cas, c’est l’URL « Liste des endpoints JSON », dont le flux contient notamment l’URL du flux JSON de la liste des régions, flux JSON qui contient les URLs des flux JSON des universités, etc.

Chaque URL permet de remonter à une URL parente. Par exemple, les URLs pour les flux JSON des universités ne sont plus sous la forme « /json/listUniversities_bretagne » mais sous la forme « /json/regions/bretagne », étant donné que « /json/regions » est une URL qui fonctionne.

Dans chaque flux JSON est renseignée l’URL qui permet de retrouver le flux sur le serveur — en général c’est celle qui a initialement permis de récupérer le flux.

Toutes les URLs sont données par des champs JSON de nom `"url"`.
Exemple : « "regions": {"url": "xxx/json/regions"} » au lieu de : « "regions":"xxx/json/regions" ».

### Note : ${baseURL}

Dans ce document, l’expression « ${baseURL} » est à remplacer par l’URL de base par laquelle sont accessibles les web services de l’application web unm-backend déployée sur le serveur.

Exemple en intégration :

  * ${baseURL} = `https://univmobile-dev.univ-paris1.fr`

Exemple en intégration continue (Jenkins) :

  * ${baseURL} = `http://localhost:8380/unm-backend`
  
Exemple en développement (poste de développeur) :

  * ${baseURL} = `http://localhost:8080/unm-backend`

Exemple en développement (déploiement) :

  * ${baseURL} = `http://univmobile.vswip.com/unm-backend`
  
Attention, il ne s’agit pas forcément de l’URL de base de la partie HTML de l’application web unm-backend, qui, contrairement aux flux JSON, doit être protégée à la fois par HTTPS et par la fédération d’identité Shibboleth.

Exemple en intégration :

  * L’IHM HTML est accessible à : https://univmobile-dev.univ-paris1.fr/testSP/ (protégée par Shibboleth), donc on pourrait penser que ${baseURL} = https://univmobile-dev.univ-paris1.fr/testSP
  * mais en fait les flux JSON sont sur : https://univmobile-dev.univ-paris1.fr/json/... En réalité, ${baseURL} = https://univmobile-dev.univ-paris1.fr

On essaie de rendre tolérante la syntaxe des URLs. En particulier, les URLs suivantes sont équivalentes :

 * ${baseURL}/json
 * ${baseURL}/json/

Et aussi :

 * ${baseURL}/json/regions
 * ${baseURL}/json/regions.json
 
### Note : ?html beautifier

Sur les flux JSON servis par l’application web unm-backend, le paramètre HTTP « ?html » permet d’obtenir un affichage HTML correspondant au flux JSON, avec indentation et liens cliquables.

Exemple : http://univmobile.vswip.com/unm-backend/json?html pour avoir les endpoints cliquables en développement.

### Flux JSON : liste des endpoints JSON

Contexte : on permet d’avoir en un seul coup d’œil la liste des endpoints de plus haut niveau qui renvoient du JSON. 

Pour ceux qui demandent des paramètres, des exemples sont fournis.

URL : `${baseURL}/json`

Exemple de flux :

    {
        "url":"${baseURL}/json",   
             
        "regions":{
            "url":"${baseURL}/json/regions"
        },
        
        "pois":{
            "url":"${baseURL}/json/pois"
        }
    }

Tailles typiques :

| json/ |
| :--: |
| < 1 Ko | 

Les URLs contenues dans ce flux sont directement exploitables.

### Flux JSON : liste des régions

Contexte : avant d’afficher la liste des universités
dans l’action de sélectionner son université de rattachement,
on affiche d’abord la liste des régions de rattachement.

URL : `${baseURL}/json/regions`

Cette URL est fournie par le flux JSON « Liste des endpoints JSON ».

Exemple de flux :

    {
        "url":"${baseURL}/json/regions",
        
        "regions":[
            {
                "id":    "bretagne",   
                "label": "Bretagne",
                "url":   "${baseURL}/json/regions/bretagne",
                "pois":{
                    "count": 200,
                    "url":   "${baseURL}/json/regions/bretagne/pois"
                }
            },                                                    
            {
                "id":    "ile_de_france",   
                "label": "Île de France",
                "url":   "${baseURL}/json/regions/ile_de_france",
                "pois":{
                    "count": 110,
                    "url":   "${baseURL}/json/regions/ile_de_france/pois"
                }
            },
            ...
        ]
    }

Tailles typiques :

| regions.json |
| :--: |
| < 1 Ko | 

### Flux JSON : liste des universités d’une région

Contexte : on affiche la liste des universités d’une région donnée,
afin d’offrir de sélectionner son université de rattachement.

URL générique : `${baseURL}/json/regions/<regionId>`

Ces URLs sont fournies par le flux JSON « Liste des régions ».

Exemples :

  * `${baseURL}/json/regions/bretagne`
  * `${baseURL}/json/regions/ile_de_france`
  
Exemple de flux :

    {
        "url":   "${baseURL}/json/regions/ile_de_france",
        "id":    "ile_de_france",
        "label": "Île de France",
     
        "universities":[
            {
                "id":    "crousVersailles",   
                "title": "CROUS Versailles",
                "config":{
                    "url":"${baseURL}/json/regions/ile_de_france/crousVersailles"
                },
                "pois":{
                   "count": 127,
                   "url":   "${baseURL}/json/regions/ile_de_france/crousVersailles/pois"
                }
            },                         
            {
                "id":    "ucp",   
                "title": "Cergy-Pontoise",
                "config":{
                    "url":"${baseURL}/json/regions/ile_de_france/ucp"
                },
                "pois":{
                    "count": 48,
                    "url":   "${baseURL}/json/regions/ile_de_france/ucp/pois"
                }
            },
            ...
        ]
    }
     
Tailles typiques :

| bretagne | ile_de_france | unrpcl |
| :--: | :--: | :--: |
| < 1 Ko | < 3 Ko | < 1 Ko |

### FLux JSON : configuration UnivMobile pour une université

TODO : URL = ${baseURL}/json/regions/ile_de_france/ucp

### Flux JSON : points of interest (POIs), introduction

TODO : catégories ad hoc, listes et arborescences de POIs

TODO : contextes de recherche (favoris, université, filtres) (onglets du haut)

TODO : détails et commentaires (onglets du bas)

Dans les URLs qui servent des POIs (Points of interest), le fragment « /pois » se trouve toujours en fin d’URL. Exemples :

  * http://localhost:8380/unm-backend/json/pois
  * http://localhost:8380/unm-backend/json/regions/bretagne/pois
  * http://localhost:8380/unm-backend/json/regions/bretagne/rennes2/pois

En effet, les POIs pouvant être filtrés par critères de recherche passés en paramètres hTTP, il est pertinent de placer le fragment en fin d’URL.

Exemples : 

  * json/regions/bretagne/pois?type=amphitheatre
  * json/pois?id=4431

Les POIS sont toujours servis dans une liste et regroupés.

Exemple :

    {
        "url":"http://univmobile.vswip.com/unm-backend/json/pois",
        "groups":[
            {
                "groupLabel":"Région : Bretagne",
                "pois":[
                    {
                        ... POI 1
                    },
                    {
                        ... POI 2
                    },
                    ...
                ]
            },
            {
                "groupLabel":"Région : Île de France",
                "pois":[
                    ...
                ]
            },
            ...            
        ]
    }
    
Note : même pour retrouver le POI d’un identifiant donné, on obtiendra une liste — avec un seul élément.

Exemple :

    {
        "url":"http://univmobile.vswip.com/unm-backend/json/pois?id=4431",
        "groups":[
            {
                "groupLabel":null,
                "pois":[
                    {
                        "id":   4431,
                        "name": "ENSIEE",
                        ...
                    }
                ]
            }
        ]
    }

Voici un exemple JSON d’un POI :

    {
       "id":           4431,
       "name":         "ENSIIE",
       "coordinates":  "48.627078,2.431309",
       "lat":          "48.627078",
       "lng":          "2.431309",
       "address":      "18 Allée Jean Rostand",
       "floor":        null,
       "itinerary":    null,       
       "openingHours": null,
       "url":          "http://www.ensiie.fr/",
       "email":        null,
       "fax":          null,
       "phone":        null,
       "image":{
           "url":    null,
           "width":  0,
           "height": 0
       },
       
       "markerIndex":  "A",
       "markerType":   "green",
       
       "comments":{
           "url":"${baseURL}/json/pois/4431/comments"
       }
    }

Le champ « url » du JSON ne correspond pas à l’identifiant du POI, parce que le POI n’est pas servi tel quel mais toujours dans une liste qui, elle, a un champ « url » technique.

Les champs « marker* » du POI sont utilisés pour le dessin sur les cartes Google Maps.

La partie « comments » pointe vers le JSON des commentaires attachés au POI.

Pour le flux JSON des commentaires, l’API est inspirée de l’API Twitter.

En voici un exemple :

    {
        "url":"${baseURL}/json/pois/4431/comments",
        "context":[
            {
                "poi":{
                    "id":   4431,
                    "name": "ENSIEE"
                }
            }   
        ],
        "comments":[
            {
                "id":       "9120122",
                "postedAt": "2014-08-15 09:34:45.894+02:00",                                               
                "source":   "UnivMobile",
                "author":{
                    "username":    "dandriana",
                    "displayName": "David Andriana",
                    "timeZone":    "Central European Time Zone (UTC+01:00)",
                    "lang":        "fr",
                    "profileImage": {
                        "url":"http://unpidf.univ-paris1.fr/wp-content/themes/wp-creativix/images/logos/1338991426.png"
                    }
                },
                "coordinates":{
                    "type": "Point"
                    "lat":  "48.627078",
                    "lng":  "2.431309"
                },
                "lang":"fr",
                "entities":{
                    "hashtags":[
                        {
                            "indices": [28, 34],
                            "text":    "unpidf"
                        }
                    ],
                    "urls":[
                        {
                            "indices":     [36, 65],
                            "url":         "http://unpidf.univ-paris1.fr/",
                            "displayUrl":  "http://unpidf.univ-paris1.fr/",
                            "expandedUrl": "http://unpidf.univ-paris1.fr/",
                        }
                    ]
                },
                "text":"Une application de l’UNPIdF #unpidf http://unpidf.univ-paris1.fr/"
            },
            {   
                "id":       "9120123",
                "postedAt": "2014-08-15 11:02:45.894+02:00",                                               
                "source":   "UnivMobile",
                ...
            },
            ...
        ]
    }

### Flux JSON : universités en tant que points of interest (POIs) 

Contexte : on affiche la localisation des universités sur la Métropole.

URL : `${baseURL}/json/pois`

Cette URL est fournie par le flux JSON « Liste des endpoints JSON ».

En tant que POIs, les universités sont regroupées
sous des groupes qui correspondent à leurs régions.


### Flux JSON : points of interest (POIs) au sein d’une université

TODO : URL = ${baseURL}/json/regions/ile_de_france/ucp/pois

TODO : catégories = catégories / tags, POIs = POIs

### Flux JSON : commentaires (comments), introduction

TODO : exemple de flux JSON.

TODO : url = permalink.

TODO : envoi d’un commentaire :

  * GET unm-backend/comment pour préparer l’envoi (vérifie notamment que Shibboleth est utilisé).
  * POST unm-backend/comment + username + message pour envoyer le message.