FROM adoptopenjdk/openjdk11
LABEL organization="NIEHS"
LABEL maintainer="mike.conway@nih.gov"
LABEL description="GA4GH iRODS DRS Console"
ADD runit.sh /
ADD drscon.sh /

COPY target/ga4gh-console-0.0.2-SNAPSHOT.jar /ga4gh-console-0.0.2-SNAPSHOT.jar
ENTRYPOINT ["/runit.sh"]
#CMD ["/runit.sh"]



# build: docker build -t michaelconway/ga4gh-console:0.0.2 .

# run:  docker run -it  michaelconway/ga4gh-console:0.0.2
