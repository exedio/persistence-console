FROM debian:bullseye-20240904

ARG JDK
RUN <<EOF
set -e
apt-get update
apt-get install --no-install-recommends --assume-yes \
	${JDK}-jdk-headless
rm -rf /var/lib/apt/lists/*
EOF

ARG IDEA
ARG IDEA_SHA256
ADD --checksum=sha256:${IDEA_SHA256} https://ivyartifacts.exedio.com/idea/ideaIC-${IDEA}.tar.gz /tmp/ideaIC.tar.gz
RUN <<EOF
set -e
mkdir /opt/idea
tar \
	-xf /tmp/ideaIC.tar.gz \
	--directory /opt/idea \
	--strip-components=1
rm /tmp/ideaIC.tar.gz
EOF

# keeping idea files  out of workspace, where they are scanned by idea inspections
COPY idea64.vmoptions /tmp
RUN cat /tmp/idea64.vmoptions >> /opt/idea/bin/idea64.vmoptions
