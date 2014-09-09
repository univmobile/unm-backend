Packaging et déploiement J2EE
=============================

Documentation parente : [unm-backend](README.md)

Au déploiement, la webapp unm-backend.war doit pointer vers :

  * un répertoire sur le système de fichiers local, pour les données XML 
    (documentation : [XMLData.md](XMLData.md)),
  * une DataSource JDBC MySQL, pour l’indexation des données
    (documentation : [Indexation.md](Indexation.md)),
  * un fichier de logs.
  
### Serveur d’applications

On a choisi Tomcat.

### Fichiers XML : répertoire dataDir

### DataSource JDBC MySQL

Voici le code dans BackendServlet.java qui récupère la DataSource par un accès JNDI :

    final InitialContext context = new InitialContext();
    
    final DataSource ds = (DataSource) context.lookup("java:/comp/env/jdbc/univmobile");

Dans WEB-INF/web.xml de la webapp (unm-backend.war) :

	<resource-ref>
		<res-ref-name>jdbc/univmobile</res-ref-name>
		<res-type>javax.sql.DataSource</res-type>
		<res-auth>Container</res-auth>
	</resource-ref>

Dans WEB-INF/context.xml de la webapp (unm-backend.war) :

    <Context 
        path="..."
        reloadable="true"
        antiJARLocking="true" 
        antiResourceLocking="true">
    </Context>

path="..." est le contextRoot de la webapp, la partie de l’URL après \<protocol\>://\<host\>:\<port\>. par exemple path="/unm-backend" en développement si l’application doit être accessible par http://localhost:8080/unm-backend

Mettre le JAR du driver JDBC de MySQL (par exemple, mysql-connector-java-5.1.32-bin.jar) dans lib/ de ${CATALINA_HOME} (répertoire d’installation de Tomcat).

À l’intérieur de l’élement \<GlobalNamingResources/\> de conf/server.xml de ${CATALINA_HOME} :

    <Resource  
        name="jdbc/univmobile"
        type="javax.sql.DataSource"
        driverClassName="com.mysql.jdbc.Driver"
        url="jdbc:mysql://localhost:3306/dbxxx?autoReconnect=true"
        username="userxxx"
        password="passxxx"
        maxIdle="2"
        maxWait="5000"
        maxActive="4"/>
        
Où « dbxxx » est le nom de la base de données — ce peut être « univmobile » ou autre chose,
il n’y aucun lien avec name="jdbc/univmobile".

Et enfin à l’inérieur de l’élement \<Context/\> de conf/context.xml de ${CATALINA_HOME} :

    <ResourceLink
        global="jdbc/univmobile"
        name="jdbc/univmobile"
        type="javax.sql.DataSource"/>
        
Redémarrer Tomcat.

Vérification dans Tomcat avec la webapp « manager », exemple :

    $ curl http://tomcatuserxxx:tomcatpassxxx@localhost:8080/manager/text/resources
    OK - Listed global resources of all types
    jdbc/univmobile:org.apache.tomcat.dbcp.dbcp.BasicDataSource
    UserDatabase:org.apache.catalina.users.MemoryUserDatabase

Note : les identifiants Tomcat pour le rôle « manager-script »
sont dans le fichier conf/tomcat-users.xml.

