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
ADD src/main/resources/aeminium_pkey.pem /usr/local/app

WORKDIR /usr/local/app

# These ENV variables are meant to affect the application itself
ENV payaramicro_minHttpThreads=10 \
    payaramicro_maxHttpThreads=10 \
    JVM_OPTIONS=\
    JWT_PRIVATEKEY_LOCATION=\
    DATABASE_USER=\
    DATABASE_PASSWORD=\
    DATABASE_HOST=\
    DATABASE_NAME=\
    DATABASE_PORT=\
    JPA_CACHE_LOADATSTARTUP=
    

EXPOSE 6900 8080   

ENTRYPOINT java ${JVM_OPTIONS} -jar aem-identity-microbundle.jar