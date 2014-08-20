Flux JSON
=========

### Introduction

L’application UnivMobile utilise les flux JSON pour la communication entre ses différents éléments déployés. C’est la partie dite « backend » (projets Maven : unm-backend) qui sert ces flux. Ils sont en particulier consommés par :

  * L’application Mobile web : unm-mobileweb → (JSON) unm-backend
  * L’application iOS : unm-ios → (JSON) unm-backend
  * L’application Android : unm-android → (JSON) unm-backend
  
Sauf mention contraire (informations personnelles de l’utilisateur, messages e-mail, etc.), les flux JSON de cette documentation sont en accès libre.

### Principes

Une URL unique de plus haut niveau permet, au runtime, de retrouver toutes les URLs des flux JSON servis par l’application. Dans notre cas, c’est l’URL « Liste des endpoints JSON », dont le flux contient notamment l’URL du flux JSON de la liste des régions, flux JSON qui contient les URLs des flux JSON des universités, etc.

Chaque URL permet de remonter à une URL parente. Par exemple, les URLs pour les flux JSON des universités ne sont plus sous la forme « /json/listUniversities_bretagne » mais sous la forme « /json/regions/bretagne », étant donné que « /json/regions » est une URL qui fonctionne.

### Notes

Dans ce document, l’expression « ${baseURL} » est à remplacer par l’URL de base par laquelle sont accessibles les web services de l’application web unm-backend déployée sur le serveur.

Exemple en intégration :

  * ${baseURL} = `https://univmobile-dev.univ-paris1.fr`

Exemple en intégration continue (Jenkins) :

  * ${baseURL} = `http://localhost:8380/unm-backend`
  
Exemple en développement :

  * ${baseURL} = `http://localhost:8080/unm-backend`
  
Attention, il ne s’agit pas forcément de l’URL de base de la partie HTML de l’application web unm-backend, qui, contrairement aux flux JSON, doit être protégée à la fois par HTTPS et par la fédération d’identité Shibboleth.

Exemple en intégration :

  * L’IHM HTML est accessible à : https://univmobile-dev.univ-paris1.fr/testSP/ (protégée par Shibboleth), donc on pourrait penser que ${baseURL} = https://univmobile-dev.univ-paris1.fr/testSP
  * mais en fait les flux JSON sont sur : https://univmobile-dev.univ-paris1.fr/json/... ; en réalité, ${baseURL} = https://univmobile-dev.univ-paris1.fr

On essaie de rendre tolérante la syntaxe des URLs. En particulier, les URLs suivantes sont équivalentes :

 * ${baseURL}/json
 * ${baseURL}/json/

Et aussi :

 * ${baseURL}/json/regions
 * ${baseURL}/json/regions.json

### Flux JSON : liste des endpoints JSON

Contexte : on permet d’avoir en un seul coup d’œil la liste des endpoints de plus haut niveau qui renvoient du JSON. 

Pour ceux qui demandent des paramètres, des exemples sont fournis.

URL : `${baseURL}/json`

Exemple de flux :

    {
     'regions': {'url': '${baseURL}/json/regions'},
     'pois':    {'url': '${baseURL}/json/pois'}
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
     'regions': 
      [
        {
         'id':       'bretagne',   
         'label':    'Bretagne',
         'url':      '${baseURL}/json/regions/bretagne',
         'pois': 
            {
               'count':   200,
               'url':     '${baseURL}/json/regions/bretagne/pois'
            }
        },                                                    
        {
         'id':       'ile_de_france',   
         'label':    'Île de France',
         'url':      '${baseURL}/json/regions/ile_de_france',
         'pois':       
            {
               'count':   110,
               'url':     '${baseURL}/json/regions/ile_de_france/pois'
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
     'id':        'bretagne',
     'label':     'Bretagne',
     
     'universities': 
      [
        {
         'id':       'crousVersailles',   
         'title':    'CROUS Versailles',
         'config':
           {
             'url':     '${baseURL}/json/regions/ile_de_france/crousVersailles'
           }
         'pois:         
           {
             'count':   127,
             'url':     '${baseURL}/json/regions/ile_de_france/crousVersailles/pois'
           }
        },                         
        {
         'id':       'ucp',   
         'title':    'Cergy-Pontoise',
         'config':
           {
             'url':     '${baseURL}/json/regions/ile_de_france/ucp'
           }
         'pois':
           {
             'count':   48,
             'url':     '${baseURL}/json/regions/ile_de_france/ucp/pois'
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

TODO : forme des URL, « /pois » semblant être le fragment terminal (permet d’ajouter des paramètres).

### Flux JSON : universités en tant que points of interest (POIs) 

Contexte : on affiche la localisation des universités sur la Métropole.

URL : `${baseURL}/json/pois`

Cette URL est fournie par le flux JSON « Liste des endpoints JSON ».

En tant que POIs, les universités sont regroupées
sous des groupes qui correspondent à leurs régions.


### Flux JSON : points of interest (POIs) au sein d’une université

TODO : URL = ${baseURL}/json/regions/ile_de_france/ucp/pois

TODO : catégories = catégories / tags, POIs = POIs