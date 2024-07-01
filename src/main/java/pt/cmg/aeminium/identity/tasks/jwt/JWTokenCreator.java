/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.tasks.jwt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;

/**
 * @author Carlos Gonçalves
 */
@Singleton
public class JWTokenCreator {

    private static final Logger LOGGER = Logger.getLogger(JWTokenCreator.class.getName());

    @Inject
    @ConfigProperty(name = "jwt.privatekey.location", defaultValue = "src/main/resources/aeminium_pkey.pem")
    private String privateKeyLocation;

    private String privateKeyBase64;

    private PrivateKey privateKey;

    @PostConstruct
    public void loadPrivateKey() {

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(privateKeyLocation))) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("-----") && !line.endsWith("-----")) {
                    builder.append(line);
                }
            }

        } catch (IOException e) {

        }

        privateKeyBase64 = builder.toString();
        privateKey = generatePrivateKey(privateKeyBase64);

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
