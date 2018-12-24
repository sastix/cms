FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD target/cms-server-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
RUN mkdir -p /opt/sastixcms
