mvn clean package -DskipTests
mv target/helio-publisher-0.4.0.jar .
java -jar helio-publisher-0.4.0.jar