FROM fedora:latest AS prebuild

WORKDIR /build

# Install required packages
RUN dnf update -y && \
    dnf install -y shadow-utils curl tar && \
    dnf clean all && \
    groupadd -g 1000 silverb && \
    useradd -u 1000 -g silverb -s /bin/sh -m silverb && \
    chown silverb:silverb /build

USER silverb

# Install JDK 22
RUN curl -O "https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz" && \
    tar -xvf jdk-22_linux-x64_bin.tar.gz && \
    rm -rf jdk-22_linux-x64_bin.tar.gz

# Set the JAVA_HOME environment variable
ENV JAVA_HOME=/build/jdk-22.0.1
ENV PATH=$JAVA_HOME/bin:$PATH

# Install Maven
RUN curl -O "https://dlcdn.apache.org/maven/maven-3/3.9.8/binaries/apache-maven-3.9.8-bin.tar.gz" && \
    tar -xvf apache-maven-3.9.8-bin.tar.gz && \
    rm -rf apache-maven-3.9.8-bin.tar.gz

# Set the MAVEN_HOME environment variable
ENV MAVEN_HOME=/build/apache-maven-3.9.8
ENV PATH=$MAVEN_HOME/bin:$PATH

# Copy the source code and pom.xml file
COPY --chown=silverb:silverb src /build/src
COPY --chown=silverb:silverb pom.xml /build/

RUN mvn clean install

# ----------------------------------------------------------------------------------------------
FROM fedora:latest

WORKDIR /app

# Install required packages
RUN dnf update -y && \
    dnf install -y shadow-utils curl tar && \
    dnf clean all && \
    groupadd -g 1000 silverb && \
    useradd -u 1000 -g silverb -s /bin/sh -m silverb && \
    chown silverb:silverb /app

USER silverb

# Install JDK 22
RUN curl -O "https://download.oracle.com/java/22/latest/jdk-22_linux-x64_bin.tar.gz" && \
    tar -xvf jdk-22_linux-x64_bin.tar.gz && \
    rm -rf jdk-22_linux-x64_bin.tar.gz

# Set the JAVA_HOME environment variable
ENV JAVA_HOME=/app/jdk-22.0.1
ENV PATH=$JAVA_HOME/bin:$PATH

# Install Tomcat
RUN curl -O "https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.26/bin/apache-tomcat-10.1.26.tar.gz" && \
    tar -xvf apache-tomcat-10.1.26.tar.gz && \
    rm -rf apache-tomcat-10.1.26.tar.gz

# Set the CATALINA_HOME environment variable
ENV CATALINA_HOME=/app/apache-tomcat-10.1.26
ENV PATH=$CATALINA_HOME/bin:$PATH

# Copy the WAR file to the Tomcat webapps directory
RUN rm -rf $CATALINA_HOME/webapps/*
COPY --chown=silverb:silverb --from=prebuild build/target/*.war $CATALINA_HOME/webapps/

# Expose the Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]