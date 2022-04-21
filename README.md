# RSS to DB

## Purpose

To read a RSS feed from Podbean and get one new entry into a SQLite DB.

 
## Maven Command to Build the Binary

To build a binary, run the maven command `mvn clean test compile assembly:single`

An executable will be created on the target directory.


## Configuration

Examples of SystemD service and timer files along with a properties file can be found on the `src/main/resources/config` folder.


## SonarQube Analysis 

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=pizzacodr_rsstodb&metric=bugs)](https://sonarcloud.io/summary/new_code?id=pizzacodr_rsstodb) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=pizzacodr_rsstodb&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=pizzacodr_rsstodb) [![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=pizzacodr_rsstodb&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=pizzacodr_rsstodb) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=pizzacodr_rsstodb&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=pizzacodr_rsstodb) [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=pizzacodr_rsstodb&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=pizzacodr_rsstodb)