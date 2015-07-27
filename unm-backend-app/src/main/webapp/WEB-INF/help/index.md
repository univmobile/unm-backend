# Page d'aide
---

Cette page liste et décrit les différentes actions possibles depuis le back-end.

Recherche avancée
---

Cette page permet de rechercher dans différentes catégories du système :

### Les commentaires

Cet onglet permet de lister et de rechercher tous les commentaires postés par les utilisateurs. La recherche se fait sur le champ "Commentaire" (le contenu du commentaire posté par l'utilisateur).

La date indiquée est la date (du serveur) à laquelle le commentaire a été posté.

La seule action possible est la publication / dépublication d'un commentaire via la checkbox de la colonne "actif". L'enregistrement est automatique lorsque la checkbox est cochée / décochée.

### Les POIs

Cet onglet permet de lister et de rechercher tous les POIs enregistrés en base de données. La recherche se fait sur le champs "POI" qui contient le nom du POI enregistré.

La seule action possible est la modification. Cependant, l'écran de modification est l'écran complet de modification d'un POI. Se référer à **GéoCampus** > **Modifier un POI**

### Les utilisateurs

Cet onglet permet de lister et de rechercher tous les utilisateurs. La recherche se fait à la fois sur le champ "Utilisateurs" (le nom des utilisateurs) et sur le champ "email".

Les rôles sont les suivants :

* **A** : Administrateur
* **S** : Super administrateur
* **E** : Étudiant
* **B** : Bibliothécaire

La seule action possible est la modification. Cependant, l'écran de modification est l'écran complet de modification d'un profil utilisateur. Se référer à **Utilisateurs** > **Modifier un utilisateur**

POIs Universités
---
Cette page liste et permet de configurer les POIs dit de "plus haut niveau". Concrétement, c'est ici que sont listés les POI "universités".

Les POIs universités sont différents des "Universités" puisqu'ils sont réellement des POIs alors que les universités sont des entités supérieures aux POIs (cf. **Universités**).

### Ajouter un POI Université

En cliquant sur le lien "Ajouter un POI", l'écran d'ajout d'un POI apparait :

* **État** : Si le POI est actif - c'est à dire si il apparait en front - ou non
* **Nom** : Le nom du POI. Ici, il s'agira du nom de l'université
* **Catégories** : La catégories du POI. Pour un POI université, la catégorie est "Unité de Formation et de Recherche - Faculté"
* **Universités** : L'université (à ne pas confondre avec un POI université) à laquelle le nouveau POI sera rattaché. Donc, ici, l'université pour laquelle on créé ce POI
* **Emplacement** : Le champ "emplacement" d'un POI
* **Ville** : La ville du POI
* **Pays** : Le pays du POI
* **Code Postal** : Le code postal du POI
* **Horaires** : Le champ "horaires" du POI
* **Téléphone** : Le champ téléphone du POI
* **Adresse** : L'adresse du POI
* **E-mail** : L'email du POI
* **Accès** : Le champ "accès" du POI
* **Site Web** : L'adresse du site Web du POI
* **Lat** : La latitude du POI
* **Lng** : La longitude du POI

**Attention** : Seul le champ "Nom" est absolument obligatoire.

**Attention** : Il n'est pas nécessaire de remplir l'adresse et la latitude / longitude, l'un ou l'autre suffit. Dans le cas où les deux sont saisis, c'est les coordonnées lat / lon qui seront utilisées.

Commentaires
---

Cette page permet de lister et de rechercher tous les commentaires postés par les utilisateurs. La recherche se fait sur le champ "Commentaire" (le contenu du commentaire posté par l'utilisateur).

La date indiquée est la date (du serveur) à laquelle le commentaire a été posté.

La seule action possible est la publication / dépublication d'un commentaire via la checkbox de la colonne "actif". L'enregistrement est automatique lorsque la checkbox est cochée / décochée.

Catégories de POI
---

Cette page liste les catégories parentes des POI, et permet de configurer les sous-catégories.

### Catégories parentes :
Une description pour chaque catégories est disponible dans la page.

Les catégories parentes permettent de trier les sous-catégories par types de POI.

##### Catégories par défaut : 

* Catégorie racine université 
* Bon plans 
* Intra-bâtiment
* Bibliothèques
* API Paris
* Que Faire à Paris

Il est possible d'ajouter une catégorie parente en sélectionnant "(aucune)" comme "catégorie parente".

**Attention** : Dans des conditions réelles, il sera rare qu'une nouvelle catégorie soit créée puisqu'il faudra un cas d'utilisation réel dans les applications. Cette fonctionnalité n'est disponible que pour assurer dès maintenant l'évolutivité du service.

**Attention** : Toujours dans un souci d'évolutivité les catégories parentes admettent une icône alors que pour l'instant elles ne sont pas utilisées.

#### Actions possibles sur les catégories parentes :

* **Modifier** : permet de modifier le nom, la description et l'état de la catégorie (les champs "catégorie parente" et "catégorie API Paris" ne sont pas utilisés pour le catégories parentes)
* **Sous-catégories** : permet de gérer les sous-catégories attachées à cette catégorie parente
* **Icônes** : permet d'attacher une icône à la catégorie parente, bien qu'à l'heure actuelle celle-ci ne soit pas utilisée

### Sous-catégories :
Les sous-catégories sont, par définition, dépendantes de la catégorie parente. Une description des différentes sous-catégories est présente sur la page.

Pour accéder à la liste de sous-catégories d'une catérogie parente il suffit de cliquer sur le lien "Sous-catégories" de la catégorie parente voulue.

##### Ajouter une sous-catégorie :

Sur la page des sous-catégories, en bas de page, il suffit de cliquer sur le lien "Ajouter une catégorie". La catégorie parente sera pré-remplie en fonction de la catégorie parente sélectionnée préalablement.

* **Nom** : Le nom de la catégorie
* **Description** : La description de la catégorie. Il est fortement recommandé de remplir ce champ afin de faciliter la recherche des catégories
* **Catégorie active** : L'état de la catégorie, si elle est active (cochée) ou non (décochée)
* **Catégorie parente** : La catégorie parente de cette nouvelle sous-catégorie. La catégorie parente est pré-selectionnée mais il est possible de la changer via ce champ
* **Catégorie API Paris** : Dans le cas où il s'agit d'une sous-catégorie pour "Que faire à Paris", ce champ sert à saisir l'ID numérique de catégorie "Que Faire à Paris" de l'API de Paris. À ce jour, la documentation de l'API ne liste pas les différentes catégories qui peuvent changer à tout moment... Une liste à date est reproduite plus bas

**Attention** : seul le champ "Nom" est obligatoire.

Une fois les différents champs remplis, validez en cliquant sur le bouton "Enregistrer".

#### Attacher une icône à une sous-catérogie 

Pour sélectionner une icône a attacher à la sous-catégorie, il suffit de se rendre sur la page listant les sous-catégories, cliquer sur le lien "Icônes".

L'écran permet d'uploader 3 icônes différentes : 

* **Icône active** : 
* **Icône inactive** :
* **Icône marqueur map** :

Lorsqu'une icône est déjà présente, une nouvelle ligne apparait par icône sous la forme "[nom du type d'icône] courante". Pour la remplacer, il suffit d'en uploader une nouvelle.

#### Liste des catégories API de Paris :

Ci-dessous une liste au 23/03/2015, telle que retournée par le service. 

Pour obtenir cette liste, il faut avoir un token valide pour l'[API de Paris](https://api.paris.fr/ "API de Paris") et effectuer la requête suivante : https://api.paris.fr/api/data/1.0/Equipements/get_categories/?token=[LE TOKEN]

* **3** : Activités
* **9** : Architecture / Patrimoine
* **26** : Autre
* **12** : Autre
* **19** : Autre
* **31** : Brocante / Vide grenier
* **18** : Cirque / Arts de la rue
* **43** : Concert
* **13** : Concert
* **41** : Conférence
* **22** : Conférence / Débat
* **20** : Cours / Atelier
* **16** : Danse
* **10** : Design / Mode
* **4** : Événements
* **27** : Exhibition sportive
* **37** : Exposition
* **1** : Expositions
* **29** : Festival / Cycle
* **30** : Fête de quartier
* **45** : Foodtruck
* **15** : Humour
* **35** : Installation
* **11** : Installation / Performance
* **42** : Lecture
* **25** : Lecture / Rencontre
* **24** : Loisirs / Jeux
* **5** : Nuit blanche
* **33** : Paris événement
* **8** : Peinture / Illustration
* **39** : Performance
* **6** : Photographie
* **40** : Photographie
* **17** : Projection
* **38** : Projection
* **28** : Salon
* **34** : Sculpture
* **32** : Soirée
* **2** : Spectacles
* **21** : Stage / Formation
* **44** : Street-art
* **14** : Théâtre
* **36** : Vidéo
* **23** : Visite / Promenade

Régions
---

Cette page permet de lister les différentes régions disponibles et permet surtout d'éditer le label (le nom utilisé par les applications) des régions.

Il n'est actuellement pas possible d'ajouter de nouvelles régions.

GéoCampus
---

L'interface d'administration de GéoCampus permet de voir, ajouter et modifier des POIs, des Bons Plans, des points sur des images (typiquement des plans) et les Bibliothèques.

#### Administrer des POIs

La première chose à faire et de sélectionner l'université ciblée via la liste en haut à droite. Sélectionner l'université va charger tous les POIs de l'université en question dans la liste de gauche.

**Attention** : si il y a beaucoup de POIs à charger, cette opération peut prendre quelques secondes.

##### Ajouter un POI

Pour ajouter un POI, il suffit de cliquer sur le bouton "+" (entouré d'un rond pour ne pas le placer dans un groupe, ou le bouton "+" standard pour l'ajouter à l'arborescence d'un groupe existant) dans la liste de POIs dans la partie gauche de l'écran.

La pop-up d'ajout de POI apparaitra avec le formulaire permettant la création d'un POI :

* **Nom** : Le nom du POI 
* **Description** : Une description du POI
* **Catégorie** : La catégorie dans laquelle classer le POI
* **Emplacement** : Le champ "emplacement" du POI
* **Horaires et jours d'ouvertures** : Le champ "Horaires et jours d'ouvertures" du POI
* **Téléphone** : Le numéro de téléphone du POI
* **Adresse** : L'adresse du POI
* **Latitude** : La latitude du POI
* **Longitude** : La longitude du POI
* **E-mail** : L'e-mail du POI
* **Itinéraire** : Le champ "itinéraire" du POI
* **Sites web** : L'adresse du site web du POI
* **Crous URL** : L'adresse du site du CROUS du POI
* **Resto Id** : Permet d'attacher un restaurant CROUS via son ID (celui qui se trouve dans les flux)
* **Actif** : Si le POI est actif - c'est à dire si il apparait en front - ou non

**Attention** : Seulement le champ "Nom" est obligatoire et certains champs, selon le POI ajouté, ne seront pas utilisés du tout.

**Attention** : Il n'est pas nécessaire de remplir l'adresse et la latitude / longitude, l'un ou l'autre suffit. Dans le cas où les deux sont saisis, c'est les coordonnées lat / lon qui seront utilisées.

Une fois le formulaire rempli, il suffit de cliquer sur le bouton "Enregistrer" pour valider la création du POI.

##### Modifier un POI

Il existe deux façons de modifier un POI : modifier toutes les informations du POI et ne modifier que la position d'un POI :

##### Modifier toutes les informations d'un POI

Il faut d'abord sélectionner le POI dans la liste de gauche, puis cliquer sur le bouton d'édition (représenté par un stylo). L'écran d'édition, identique à l'écran d'ajout, apparaitra alors. Il suffira de modifier les champs voulus puis de sauvegarder.

##### Modifier uniquement la position d'un POI

Sur la carte, les POI sont matérialisés par des marqueurs. Un clic sur le marqueur le fera passer en vert et il deviendra alors possible de le déplacer avec la souris. L'enregistrement de la position se fait automatiquement une fois le marqueur déplacé.

##### Supprimer un POI

Pour éviter les erreurs, il n'est pas possible de supprimer réellement un POI.

Ici, l'utilisation du statut (actif / inactif) est préférée pour gérer l'affichage d'un POI.

#### Administrer des Bons Plans

Les Bons Plans sont gérés exactement de la même manière que les POIs standards.

#### Administrer les points sur images (plans)

Les Bons Plans sont gérés exactement de la même manière que les POIs standards, sauf qu'au lieu d'avoir des coordonées sur un fond de carte, ils utilisent des coordonées sur une image (le plan dans la plupart des cas).

##### Ajouter une nouvelle image

Pour ajouter une nouvelle image, il suffit de cliquer sur le bouton "+" en haut à droite de l'écran pour faire apparaitre le formulaire d'upload d'image.

L'administrateur saisi un nom pour l'image, utilise le bouton vert "+ image" pour sélectionner une image sur son disque dur et l'uploader.

Il suffit alors de cliquer sur "enregistrer" pour sauvegarder l'image.

##### Ajouter un nouveau point sur une image

Pour ajouter un nouveau point sur une image, nous suivons la même procédure que pour les autres POIs, via le formulaire puis en validant.

Une fois le POI sauvegardé, il suffit de double-cliquer sur l'image pour y placer le point. Comme le point sera vert, il sera alors possible de le déplacer à sa guise.

#### Administrer des bibliothèques

Les bibliothèques sont gérées exactement de la même manière que les POIs standards, sauf qu'un nouvel onglet fait son apparition lorsqu'on veut en créer ou en modifier une.

C'est dans cet onglet qu'on saisi les données propres aux bibliothèques.

Flux d'acutalités des universités
---

Cette page permet de lister, modifier, supprimer et d'ajouter des flux d'actualités par universités. Le super-administrateur peut lui ajouter un flux pour l'ensemble des universités.

C'est ici aussi que seront gérés les flux de type "restaurants universitaires".

#### Ajouter un flux

Pour ajouter un nouveau flux, il suffit de cliquer sur le bouton vert en haut à droite de l'écran "Ajouter un flux" pour faire apparaitre le formulaire d'ajout.

* **Nom** : Le nom du flux
* **Université** : L'université concernée par le flux. Le super-administrateur peut sélectionner "---" pour que le flux soit affiché pour toutes les universités
* **Type** : RSS pour une flux d'actualités de type RSS - RESTO pour un flux de restaurant universitaire
* **URL** : L'adresse à laquelle le flux est disponible

**Attention** : Tous les champs sont requis pour ajouter un flux

**Attention** : Les flux RSS doivent respecter la [norme RSS 2](http://www.scriptol.fr/rss/RSS-2.0.html) et ne peuvent admettre d'avoir un contenu HTML (les applications natives ne sont pas capables de *parser* le HTML)

#### Modifier un flux

Pour modifier un flux existant, il suffit de cliquer sur le bouton bleu avec un stylo pour faire apparaitre le formulaire pré-rempli éditable.

#### Supprimer un flux

Pour supprimer un flux existant, il suffit de cliquer sur le bouton rouge avec une croix. Un pop-up de confirmation apparaitra demandant à l'utilisateur de confirmer la suppression du flux.

Utilisateurs
---

Cette page permet de lister, modifier, rechercher et d'ajouter un utilisateur.

#### Ajouter un utilisateur

Pour ajouter un utilisateur, il suffit de cliquer sur le lien en bas de page "ajouter un utilisateur", ce qui fera apparaitre le formulaire d'ajout d'utilisateur :

* **Role** : Le rôle de l'utilisateur (Super Administrateur, Administrateur, Étudiant ou Bibliothécaire)
* **REMOTE_USER** : Il s'agit de l'ID de l'utilisateur qu'utilise Shibboleth (adresse mail ici)
* **Civilité** : La civilité de l'utilisateur
* **Nom complet** : Le nom complet de l'utilisateur (nom + prénom)
* **Email** : L'adresse mail de l'utilisateur
* **Nom d'utilisateur** : Le nom de l'utilisateur pour le système (le login)
* **Mot de passe** : Le mot de passe de l'utilisateur (utile si l'université n'est pas derrière Shibboleth)
* **Activé** : Si l'utilisateur est activé ou non
* **Université de rattachement** : L'université principale de rattachement de l'utilisateur
* **Autre université d'intérêt** : Il est possible d'attacher à l'utilisateur une seconde université (sélectionnez la même si l'utilisateur n'a qu'une seule université)
* **Twitter screen_name** : Le nom d'utilisateur Twitter (non utilisé actuellement)
* **Description** : Il est possible d'attacher une description à l'utilisateur. Visible uniquement pour les administrateurs

Il suffit alors de cliquer sur "sauvegarder" pour valider la création de l'utilisateur.

**Attention** : Les champs "REMOTE_USER", "Nom complet" et "Nom d'utilisateur" sont obligatoires.

#### Modifier un utilisateur

Pour modifier un utilisateur, il suffit de cliquer sur le lien "Modifier..." ce qui fera apparaitre le même écran que lors de l'ajout d'un nouvel utilisateur mais avec les champs pré-remplis. 

**Attention** : Il n'est pas possible de supprimer un utilisateur.

Notifications
---

Cette page permet de lister, rédiger et envoyer des notifications aux applications.

#### Ajouter une notification

Pour rédiger une nouvelle notification, il suffit de cliquer sur le bouton vert en haut à droite "Ajouter notification". Le formulaire apparaitra :

**Université** : L'administrateur d'une université ne pourra sélectionner que la sienne alors qu'une super-administrateur pourra sélectionner n'importe laquelle. Le super-administrateur peut également sélectionner "--" pour que la notification apparaisse pour toutes les universités
**Content** : Le contenu de la notification

Une fois les deux champs remplis, il suffit de cliquer sur "enregistrer" pour envoyer la notification.

#### Modifier une notification

Pour modifier une notification, il suffit de cliquer sur le bouton bleu avec un pictogramme en forme de stylo dans la liste. Le même formulaire que celui d'ajout apparaitra alors, pré-rempli, qu'il suffira de modifier puis d'enregistrer.

#### Supprimer une notification

Pour supprimer une notification, il suffit de cliquer sur le bouton rouge avec un pictogramme en forme croix pour supprimer la notification. Un pop-up demandant confirmation apparaitra alors.

Menus
---

Cette page permet de lister, modifier et ajouter des menus aux applications.

#### Ajouter un menu

Pour ajouter un menu aux applications, il suffit de cliquer sur le bouton vert en haut à droite "Ajouter un menu".

Le formulaire d'ajout de menu apparaitra alors :

* **Nom** : Le nom du menu, c'est ce nom qui apparaitra dans l'arborescence
* **Université** : L'université dans pour laquelle ce nouveau menu apparaitra. Le super administrateur peut sélectionner "--" pour faire apparaitre le menu pour toutes les universités
* **Groupe** : Il existe 4 groupes de menus :
  * Mes Services (MS)
  * Act'Universitaire (AU)
  * Tou'trouver (TT)
  * Ma vie U (MU)
* **Actif** : Indique si le menu est actif (visible) ou non
* **Ordre** : Permet d'ordonner le menu (le plus petit nombre, le plus haut dans la liste). Ex: 0 mettra le menu tout en haut de la liste (en 1er) 
* **URL** : Si il s'agit d'un menu "WebView" (qui redirige vers une page Web), c'est ici que l'administrateur doit saisir l'adresse
* **Content** : Si il s'agit d'un menu qui renvoie vers une page customisée, c'est ici que l'administrateur en saisi le contenu

**Attention** : Seuls les champs Nom et Ordre (pré-rempli, par défaut 0) sont obligatoires.

Il suffit, une fois le formulaire rempli, de cliquer sur "Enregistrer" pour sauvegarder le nouveau menu.

#### Modifier un menu

Pour modifier un menu, il suffit de cliquer sur le bouton bleu avec un pictogramme en forme de stylo dans la liste. Le même formulaire que celui d'ajout apparaitra alors, pré-rempli, qu'il suffira de modifier puis d'enregistrer.

#### Supprimer un menu

Pour supprimer un menu, il suffit de cliquer sur le bouton rouge avec un pictogramme en forme croix pour supprimer la notification. Un pop-up demandant confirmation apparaitra alors.

#### Menu spéciaux

Certains menus sont listés pour permettre aux administrateurs de changer le nom, la position ou l'état du menu mais ne peuvent pas changer le contenu. Il s'agit des menus qui mènent vers des rubriques de l'application (Mon Profil par exemple).

Les menus concernés sont les suivants :

* Mon Profil
* Mes Bibliothèques
* Ma Médiathèque
* GéoCampus
* Que Faire à Paris
* Les Bons Plans

Universités
---

Cette page permet de lister et modifier les universités présentes dans UnivMobile.

#### Modifier une université

Pour modifier une université, il suffit de cliquer sur le lien "Modifier" dans la liste des universités. La page avec le formulaire d'édition apparaitra alors.

* **Titre** : Il s'agit du nom de l'université
* **Mobile Shibboleth URL** : L'URL de l'écran de login Shibboleth. Si aucune adresse n'est saisie, l'université utilisera un système de login / mot de passe à la place de Shibboleth
* **Modérer les commentaires par défaut ?** : Si c'est activé, les commentaires postés par des utilisateurs de cette université seront désactivés par défaut et nécessiteront l'intervention d'un administrateur pour être activés (visibles)
* **Nouveau Logo** : Permet d'uploader un logo pour l'université, qui est utilisé dans le menu des applications. La taille recommandée est de 275x73 pixels
* **Type CROUS ?** : Cochez si cette "université" est en fait un CROUS

**Attention** : Il n'est pas possible d'ajouter ou de supprimer des universités depuis le back-end

Médiathèques
---

Cette page permet de lister, modifier et ajouter des liens de type médiathèques.

Les liens de ce type viennent s'ajouter dans la rubrique "Mes médiathèques" des applications.

#### Ajouter une médiathèque

Pour ajouter une médiathèque il suffit de cliquer sur le bouton vert en haut à droite "Ajouter une médiathèque", ce qui fera apparaitre le formulaire d'ajout :

* **Université** : L'université pour laquelle on ajoute le lien. Le super-administrateur peut sélectionner "--" pour ajouter le lien à toutes les universités.
* **Étiquette** : Il s'agit du titre du lien, visible par les utilisateurs
* **URL** : Le lien vers lequel renvoyer

**Attention** : Tous les champs sont requis.

#### Modifier une médiathèque

Pour modifier une médiathèque, il suffit de cliquer sur le bouton bleu avec un pictogramme en forme de stylo dans la liste. Le même formulaire que celui d'ajout apparaitra alors, pré-rempli, qu'il suffira de modifier puis d'enregistrer.

#### Supprimer une médiathèque

Pour supprimer une médiathèque, il suffit de cliquer sur le bouton rouge avec un pictogramme en forme croix pour supprimer la notification. Un pop-up demandant confirmation apparaitra alors.

Bibliothèques
---

Cette page permet de rattacher des bibliothèques à des universités pour la rubrique "Mes Bibliothèques". Un administrateur a la possibilité d'ajouter des bibliothèques par défaut dans cette rubrique.

#### Attacher une bibliothèque

La démarche pour attacher une bibliothèque est très simple : on sélectionne une université dans la première liste et une bibliothèque dans la seconde puis on clique sur le bouton vert "+" pour créer le lien.

#### Supprimer un rattachement de bibliothèque

Pour supprimer une lien entre une université et une bibliothèque, il suffit de cliquer sur le bouton rouge "X" dans la liste des bibliothèques attachées. Un pop-up de confirmation demandera à l'utilisateur de confirmer son action.

Liens CROUS
---

Cette page permet de lier des CROUS à des Universités.

#### Attacher un CROUS

La démarche pour attacher un CROUS est très simple : on sélectionne une université dans la première liste et un CROUS dans la seconde puis on clique sur le bouton vert "+" pour créer le lien.

#### Supprimer un rattachement de CROUS

Pour supprimer une lien entre une université et un CROUS, il suffit de cliquer sur le bouton rouge "X" dans la liste des CROUS attachés. Un pop-up de confirmation demandera à l'utilisateur de confirmer son action.

Statistiques
---

Cette page permet de consulter les statistiques de connexions par universités et sur une période données.

#### Consulter des statistiques

Pour consulter les statistiques, il suffit de sélectionner une université dans la liste et de définir une période (par défaut : le dernier mois) puis de valider.

Les statistiques sont visibles sous la forme d'un tableau avec le nombre de connexions par applications (iOS, Android & Web). Si une des applications ne comptabilise pas de connexions, elle ne sera pas listée. Si aucunes applications ne comptabilisent de connexions, le message "Aucune statistique" apparaitra.

Logs Techniques
---

Cette page permet de consulter les derniers logs du serveur Tomcat directement depuis le back-end. Il est possible de limiter le nombre de lignes à afficher (par défaut : 200 lignes).