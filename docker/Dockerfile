# VERSION:		  0.1
# DESCRIPTION:	  Run Jenkins and the plugins found in plugins.txt inside a container
# USAGE:
#
# # Build images
# docker build -t jenkins .
#
# # Run container
# docker run --name myjenkins -d -p 8080:8080 jenkins
#

FROM jenkins:latest
MAINTAINER Sion Williams <sion5@hotmail.co.uk>

COPY plugins.txt /usr/share/jenkins/ref/
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/ref/plugins.txt
