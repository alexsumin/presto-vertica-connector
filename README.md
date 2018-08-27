#Presto Vertica Connector
This is a plugin for Presto that allow you to use Vertica Jdbc Connection

##Connection Configuration
Create new properties file inside etc/catalog dir:

    connector.name=vertica 
    connection-url=jdbc:vertica://ip:port/database
    connection-user=username
    connection-password=userpassword

Create a dir inside plugin dir called vertica. To make it easier you could copy mysql dir to vertica and remove the mysql-connector and prestodb-mysql jars. Finally put the prestodb-vertica and the vertica-jdbc in plugin/vertica folder. Here is the hint:

    cd $PRESTODB_HOME
    cp -r plugin/mysql plugin/vertica
    rm plugin/vertica/mysql-connector*
    rm plugin/vertica/presto-mysql*
    mv /home/Downloads/presto-vertica*.jar plugin/vertica
    mv /home/Downloads/vertica-jdbc-*.jar plugin/vertica

##Building the project
To build Presto Vertica Connector, execute:

    mvn clean install

##Vertica Driver
Vertica Driver is not available in common repositories, so you will need to download it and install manually in your repository. It can be downloaded from https://my.vertica.com/download/vertica/client-drivers/

##Install lib in your local repository
You could install lib in your local repository using command

`mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> 
     -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging>`
     
 In our case for vertica-jdbc
 
 `mvn install:install-file -Dfile=path/vertica-jdbc-9.1.1-0.jar -DgroupId=com.vertica.jdbc -DartifactId=vertica.jdbc -Dversion=9.1.1-0 -Dpackaging=jar`