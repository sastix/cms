FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD cms-server-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
RUN mkdir -p /opt/sastixcms
ENV JAVA_OPTS="-Dspring.profiles.active=production,docker"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]