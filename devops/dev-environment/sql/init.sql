-- Create users
CREATE USER 'sastixcms'@'%' IDENTIFIED BY 'sastixcms';
CREATE USER 'keycloak'@'%' IDENTIFIED BY 'keycloak';

-- Sastix CMS
CREATE DATABASE `sastix_cms_docker` /*!40100 DEFAULT CHARACTER SET utf8 */;
GRANT ALL PRIVILEGES ON sastix_cms_docker.* TO 'sastixcms'@'%';

-- Keycloak
CREATE DATABASE `keycloak`;
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak'@'%';