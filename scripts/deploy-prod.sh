#!/usr/bin/env bash

RELEASE_VERSION = git describe --abbrev=0 --tags
echo "Release tag: ${RELEASE_VERSION}"

aws elasticbeanstalk create-application-version --application-name realestate-check \
--version-label ${RELEASE_VERSION} --source-bundle \
S3Bucket=cdeneuve-maven-repo,\
S3Key=release/com/cdeneuve/realestate/realestate-checker/${1}/realestate-checker-${1}.jar

aws elasticbeanstalk update-environment --environment-name realestate-check-env \
 --version-label ${RELEASE_VERSION}