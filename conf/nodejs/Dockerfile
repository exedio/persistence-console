ARG NODEJS
FROM node:${NODEJS}-alpine3.18

# Without bash jenkins sh step fails: sh: /bin/bash: not found
RUN apk --no-cache add bash

# There is no need to install yarn here, because the docker image already contains
# yarn version 1.22.19:
#
#   https://github.com/nodejs/docker-node/blob/main/20/alpine3.18/Dockerfile
#
# This old version is no problem, as we switch to a later version by yarnPath
# in file .yarnrc.yml:
#
#   https://yarnpkg.com/configuration/yarnrc#yarnPath

# avoids: Internal Error: EACCES: permission denied, mkdir '/.yarn'
ARG JENKINS_OWNER
RUN <<EOF
set -e
mkdir /.yarn
chown ${JENKINS_OWNER} /.yarn
EOF

LABEL purpose=jenkins
LABEL nodejs=yes
LABEL yarnpkg=yes
