/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.tasks.jwt;

import static java.lang.Thread.currentThread;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import pt.cmg.aeminium.datamodel.users.entities.identity.User;

/**
 * @author Carlos Gonçalves
 */
@Singleton
public class JWTokenCreator {

    @Inject
    @ConfigProperty(name = "jwt.privatekey.location", defaultValue = "/META-INF/aeminium_pkey.pem")
    private String privateKeyLocation;

    private String privateKeyBase64;

    private PrivateKey privateKey;

    @PostConstruct
    public void loadPrivateKey() {
        privateKeyBase64 = readKeyFile();
        privateKey = generatePrivateKey(privateKeyBase64);
    }

    public String readKeyFile() {

        String keyString = null;

        try {

            // First we look for the key in the resources. This happens only for the default development key which is packaged in the .war
            URL resourcesKeyURL = currentThread().getContextClassLoader().getResource(privateKeyLocation);

            Path filePath;
            if (resourcesKeyURL == null) {
                // If it's not on resources, then it must be an outer file.
                // Always remember that this file is relative to the working directory
                filePath = Paths.get(privateKeyLocation).toAbsolutePath();
            } else {
                filePath = Paths.get(resourcesKeyURL.toURI());
            }

            keyString = Files.lines(filePath)
                .filter(line -> !line.startsWith("-----") && !line.endsWith("-----"))
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return keyString;
    }

    private PrivateKey generatePrivateKey(String pemKey) {

        byte[] decodedKey = Base64.getDecoder().decode(pemKey);

        PrivateKey privateKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

            privateKey = keyFactory.generatePrivate(keySpec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {

        }

        return privateKey;
    }

    public String generateNewToken(User user) {

        Date now = new Date();
        Date threeDaysFromNow = Date.from(Instant.now().plus(3, ChronoUnit.DAYS));

        String jws = Jwts.builder()
            .header().type("JWT").and()
            .claim("upn", user.getName())
            .subject(user.getId().toString())
            .issuer("aeminium-identity")
            .claim("jti", "x-atm-092")
            .issuedAt(now)
            .expiration(threeDaysFromNow)
            .claim("groups", user.getRolesAsStrings())
            .signWith(privateKey)
            .compact();

        return jws;
    }

}
