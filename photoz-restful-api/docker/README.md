### Installing a self-signed certificate into JVM CA truststore
Self-Signed certificates must be added to a custom FUSE image to avoid SSL handshake errors by Keycloak adapter.
https://medium.com/@siweheee/keycloak-a-real-scenario-from-development-to-production-ce57800e3ba9

```
cd app-authz-uma-photoz/photoz-restful-api/docker/self-signed

RHSSO_NAMESPACE=raa-sso74
RHSSO_URI=$(oc get route sso74 -n ${RHSSO_NAMESPACE} --template='{{ .spec.host }}')
DOMAIN=apps.mw.consulting-rh-br.com

# step1
echo | openssl s_client -showcerts -servername ${RHSSO_URI} -connect ${RHSSO_URI}:443 2>/dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > self-signed-cert.pem
# Validate the connection first! must return HTTP/1.1 200 OK
curl -v https://${RHSSO_URI}/auth/realms/master --cacert self-signed-cert.pem

# step2 
openssl x509 -in <(openssl s_client -connect sso73.apps.<DOMAIN>:443 -prexit 2>/dev/null) -out temp/sso73.apps.<DOMAIN>.crt

# step3
split the self-signed-cert.pem into Nth .crt files one for each certificate inside de .pem

# ste4 
copy the certificate generated in step2 to cert3.crt 
```

In the end, I had to acquire 2 files:
- The .pem file
- The .cert file

In order to avoid any trouble to import them into the container image, I had splited the .pem into Nth .crt files.

So, in my case, I've ended up with 3 files?
- cert1.crt (first certificate in .pem)
- cert2.crt (second certificate in .pem)
- sso73.crt (the third certificate extracted in step2)

<b>The `Dockerfile` explains better how I had to customize the image in order to allow the keycloak-admin-client to poke with the sso73 self-signed server.</b>

### Creating a custom FUSE docker image with the self-signed certificate

Access your Openshift** instance and execute the following:
```
REDHAT_CONTAINER_USER=xxx
OCP_CLUSTER_ADMIN=yyy
OCP_IMAGE_REGISTRY=docker-registry.apps.mw.consulting-rh-br.com:443

oc expose svc/image-registry -n openshift-image-registry
docker login -u $REDHAT_CONTAINER_USER registry.redhat.io
docker build -t "openshift/eap72-openshift-selfsigned" .
oc login -u admin -p <ADMIN-PASSWORD>
docker login -u $OCP_CLUSTER_ADMIN -p $(oc whoami -t) $OCP_IMAGE_REGISTRY
docker images | grep jboss

# docker rmi $(sudo docker images --filter "dangling=true" -q --no-trunc)
docker tag openshift/eap72-openshift-selfsigned:latest $OCP_IMAGE_REGISTRY/openshift/eap72-openshift-selfsigned:1.0
docker tag openshift/eap72-openshift-selfsigned:latest $OCP_IMAGE_REGISTRY/openshift/eap72-openshift-selfsigned:latest

docker push $OCP_IMAGE_REGISTRY/openshift/eap72-openshift-selfsigned
```

### REFERENCES
https://github.com/docker/docker-credential-helpers/issues/149
https://github.com/docker/for-mac/issues/3785
https://github.com/aelkz/ocp-arekkusu.io/issues/1
