# This image is meant to be run AFTER the project has been locally built with maven. This will suffice for now.
FROM alpine:latest AS runtime

# Create directories
RUN mkdir -p /usr/local/java /usr/local/app/

# Download OpenJDK
WORKDIR /usr/local
RUN wget -qO temurin_jdk21_linux_x64_bin.tar.gz https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.4+7/OpenJDK21U-jdk_x64_alpine-linux_hotspot_21.0.4_7.tar.gz \
    && tar -xvf temurin_jdk21_linux_x64_bin.tar.gz -C /usr/local/java --strip-components=1 \
    && rm -rf temurin_jdk21_linux_x64_bin.tar.gz

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/local/java

# Add Maven to the PATH
ENV PATH=${JAVA_HOME}/bin:${PATH} 

# Copy project files
ADD target/aem-identity-microbundle.jar /usr/local/app

ADD ../infra/hazelcast/hazelcast-config-aws.xml /usr/local/app

# Note that WORKDIR is passed to Java programs as the value for the user.dir system property, which is important for relative path resolution
WORKDIR /usr/local/app

# JVM_OPTIONS can be used to add specific behaviour to the JVM
ENV JVM_OPTIONS="-Xms128m -Xmx256m"

# Hazelcast: 6900 , HTTP Port: 8080
EXPOSE 6900 8080   

ENTRYPOINT java ${JVM_OPTIONS} -jar aem-identity-microbundle.jar --hzconfigfile  hazelcast-config-aws.xml