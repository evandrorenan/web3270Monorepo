FROM openjdk:8-jdk-alpine
EXPOSE 8080

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080

docker run -d -p 8080:8080 -rm -backend -v "/mnt/c/web3270/target:/web3270"


version: "3.8"
services: 
  backend: 
    build: 
	  context: ./web3270/backend
	  dockerfile: Dockerfile
	ports: 
	  - '8080:8080'
	volumes:
	  - log:web3270/logs
	  - ./backend:/web3270
	env_file:
	  - ./env/backend.env
	depends_on:
	  - database-container
    image: 'web3270'
	volumes: 
	  - "/mnt/c/web3270/target:/web3270"
	
backend.env <== it's a property file.
xxx=xxx 