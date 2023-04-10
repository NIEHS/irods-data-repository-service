FROM adoptopenjdk/openjdk11
LABEL organization="NIEHS"
LABEL maintainer="mike.conway@nih.gov"
LABEL description="iDRS - GA4GH iRODS DOS"
ADD runit.sh /

COPY target/ga4gh-drs-service-0.0.2-SNAPSHOT.jar /ga4gh-drs-service-0.0.2-SNAPSHOT.jar
ENTRYPOINT ["/runit.sh"]
#CMD ["/runit.sh"]

# build: docker build -t michaelconway/ga4gh-drs:0.0.2 .

# run:  docker run -d --rm -p 8080:8080 -v /etc/irods-ext:/etc/irods-ext  --add-host irods420.irodslocal:172.16.250.101 michaelconway/ga4gh-dos:dev
