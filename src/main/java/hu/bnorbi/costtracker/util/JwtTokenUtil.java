package hu.bnorbi.costtracker.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * JwtTokenUtil osztály, mely a token vissza fejtésére szolgál
 */
public class JwtTokenUtil {

    private static final String SECRET = "secret";
    public static final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET.getBytes());
    public static final String SAUCE = "vfdnb53986sdlvdsg_FHG465hdsvs";

    /**
     * Statikus metódus, a http kérésben a header alapján vissza adja az 'Authorization' kulcs értékét.
     *
     * @return {@code String} token
     */
    public static String getTokenHeader() {
        return getAuthorizationHeader().substring("Bearer ".length());
    }

    /**
     * Vissza fejti a JWT tokent és vissza adja a benne lévő subject-et, ami a username
     *
     * @return {@code String} felhasználó név
     */
    public static String getUsernameByToken() {
        JWTVerifier verifier = JWT.require(JwtTokenUtil.ALGORITHM).build();
        DecodedJWT decodedJWT = verifier.verify(getTokenHeader());
        return decodedJWT.getSubject();
    }

    /**
     * Visszadja a Headerben lévő 'Authorization' kulcs értékét
     *
     * @return {@code} 'Authorization' kulcs értéke
     */
    private static String getAuthorizationHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("Authorization");
    }
}
