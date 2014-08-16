Flux JSON
=========

### Introduction

L’application UnivMobile utilise les flux JSON pour la communication entre ses différents éléments déployés. C’est la partie dite « backend » (projets Maven : unm-backend) qui sert ces flux. Ils sont en particulier consommés par :

  * L’application Mobile web : unm-mobileweb → unm-backend
  * L’application iOS : unm-ios → unm-backend
  * L’application Android : unm-android → unm-backend
  
Sauf mention contraire (informations personnelles de l’utilisateur, messages e-mail, etc.), les flux JSON de cette documentation sont en accès libre.

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

Contexte : on permet d’avoir en un seul coup d’œil la liste des endpoints qui renvoient du JSON. Pour ceux qui demandent des paramètres, des exemples sont fournis.

URL : `${baseURL}/json`

Exemple de flux :

    {'endpoints': [
        {'url': '${baseURL}/json/regions'},
        {'url': '${baseURL}/json/listUniversities_bretagne'},
        {'url': '${baseURL}/json/listUniversities_ile_de_france'},
        ...
    ]}


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

Exemple de flux :

    {'region': [
        {'id': 'bretagne',   
         'label': 'Bretagne',
         'url': '${baseURL}/json/listUniversities_bretagne'},
        {'id': 'ile_de_france',   
         'label': 'Île de France',
         'url': '${baseURL}/json/listUniversities_ile_de_france'},
        ...
    ]}

Tailles typiques :

| regions.json |
| :--: |
| < 1 Ko | 

### Flux JSON : liste des universités d’une région

Contexte : on affiche la liste des universités d’une région donnée,
afin d’offrir de sélectionner son université de rattachement.

URL générique : `${baseURL}/json/listUniversities_<universityId>`

Exemples :

  * `${baseURL}/json/listUniversities_bretagne`
  * `${baseURL}/json/listUniversities_ile_de_france`
  
Exemple de flux :

    {'universities': [
        {'id': 'crousVersailles',   
         'title': 'CROUS Versailles'},
        {'id': 'ucp',   
         'title': 'Cergy-Pontoise'},
        ...
    ]}

Tailles typiques :

| xxx_bretagne | xxx_ile_de_france | xxx_unrpcl |
| :--: | :--: | :--: |
| < 1 Ko | < 1 Ko | < 1 Ko |

### Flux JSON : points of interest (POIs), introduction

TODO : catégories ad hoc, listes et arborescences de POIs

TODO : contexte de recherche

### Flux JSON : universités en tant que points of interest (POIs) 

TODO : catégories = régions, POIs = universités

### Flux JSON : points of interest (POIs) au sein d’une université

TODO : catégories = catégories / tags, POIs = POIs