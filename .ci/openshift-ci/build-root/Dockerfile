# Use the centos image as the base image
FROM quay.io/centos/centos:stream8

ENV VERSION=1.19 \
    GOCACHE=/go/.cache \
    GOARM=5 \
    GOPATH=/go \
    GOROOT=/usr/local/go \
    LOGNAME=deadbeef
ENV PATH=$PATH:$GOROOT/bin:$GOPATH/bin

# Do all the stuff related with Golang, see https://github.com/openshift/release/blob/master/clusters/app.ci/supplemental-ci-images/ci-tools-build-root.yaml#L338
RUN yum install -y epel-release && \
    rpm -V epel-release && \
    INSTALL_PKGS="bc bind-utils bsdtar device-mapper device-mapper-persistent-data e2fsprogs ethtool file findutils gcc git glib2-devel hostname iptables jq krb5-devel libseccomp-devel libvirt-devel lsof make mercurial nmap-ncat openssl rsync socat systemd-devel tar tito tree util-linux wget which xfsprogs zip" && \
    yum install -y $INSTALL_PKGS && \
    rpm -V $INSTALL_PKGS && \
    yum clean all && \
    touch /os-build-image && \
    git config --system user.name fabiobrz && \
    git config --system user.email fburzigo@redhat.com

RUN \
    set -x &&  \
    curl --fail --retry 3 -L https://golang.org/dl/go$VERSION.linux-amd64.tar.gz | tar -C /usr/local -xzf - && \
    go install golang.org/x/tools/cmd/cover@latest && \
    go install golang.org/x/tools/cmd/goimports@latest && \
    go install golang.org/x/lint/golint@latest && \
    go install gotest.tools/gotestsum@v1.9.0 && \
    mv $GOPATH/bin/* /usr/bin/ && \
    rm -rf $GOPATH/* $GOPATH/.cache && \
    mkdir $GOPATH/bin && \
    ln -s /usr/bin/goimports $GOPATH/bin/goimports

RUN chmod g+xw -R $GOPATH && \
    chmod g+xw -R $(go env GOROOT)

# Install Maven
RUN yum install -y maven
