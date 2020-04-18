FROM registry.redhat.io/jboss-eap-7/eap72-openshift:1.0

USER 185

COPY self-signed/sso74.crt /tmp/
COPY self-signed/cert1.crt /tmp/
COPY self-signed/cert2.crt /tmp/

USER root

RUN keytool -import -trustcacerts -keystore /etc/pki/ca-trust/extracted/java/cacerts -storepass changeit -noprompt -alias keycloak1 -file /tmp/sso74.crt
RUN keytool -importcert -file /tmp/cert1.crt -alias keycloak2 -keystore /etc/pki/ca-trust/extracted/java/cacerts -storepass changeit -noprompt
RUN keytool -importcert -file /tmp/cert2.crt -alias keycloak3 -keystore /etc/pki/ca-trust/extracted/java/cacerts -storepass changeit -noprompt
RUN rm /tmp/sso74.crt /tmp/cert1.crt /tmp/cert2.crt

USER 185

CMD [ "/usr/local/s2i/run" ]
