package com.pgc.myapp.auth.util;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.pgc.myapp.auth.AuthProfile;

public class JwtUtil {

    public final long TOKEN_TIMEOUT = 1000 * 60 * 60 * 24 * 7;
    public String secret = "your-secret";


    public String createToken(long no, String userId, String userName) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + TOKEN_TIMEOUT);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(String.valueOf(no))
                .withClaim("userId", userId)
                .withClaim("userName", userName)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public AuthProfile validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            Long no = Long.valueOf(decodedJWT.getSubject());
            String userName = decodedJWT
                    .getClaim("userName").asString();
            String userId = decodedJWT
                    .getClaim("userId").asString();

            return AuthProfile.builder()
                    .no(no)
                    .userId(userId)
                    .userName(userName)
                    .build();
        } catch (JWTVerificationException e){
            return null;
        }
    }
}
