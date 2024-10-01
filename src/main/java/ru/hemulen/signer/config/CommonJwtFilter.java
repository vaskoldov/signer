package ru.hemulen.signer.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * CommonJwtFilter.
 *
 * @author Sergey_Rybakov
 */
public class CommonJwtFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(CommonJwtFilter.class.getName());
    private static final String BEARER = "Bearer";
    private static final String HEADER_AUTHORIZATION = "Authorization";

    private final String jwtSecret;

    public CommonJwtFilter(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HEADER_AUTHORIZATION);

        try {
            UsernamePasswordAuthenticationToken auth = extractAuthInfo(header);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JWTVerificationException ex) {
            LOGGER.log(Level.SEVERE, "Illegal JWT token", ex);
            SecurityContextHolder.clearContext();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Auth token extracting failed", ex);
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken extractAuthInfo(String authHeader) {
        String token = extractAuthToken(authHeader);

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                .build();

        Map<String, Claim> claims = verifier.verify(token)
                .getClaims();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                extractUserName(claims),
                null,
                extractAuthorities(claims)
        );
        authToken.setDetails(new AuthDetails(token));
        return authToken;
    }

    private String extractAuthToken(String header) {
        if (Objects.isNull(header) || header.trim().isEmpty()) {
            throw new JWTVerificationException("Authorization header is missing");
        }

        String[] components = header.split("\\s");

        if (components.length != 2) {
            throw new JWTVerificationException("Malformed Authorization content");
        }

        if (!BEARER.equals(components[0])) {
            throw new JWTVerificationException("Authorization token is required");
        }

        return components[1];
    }

    private String extractUserName(Map<String, Claim> claims) {
        return Optional.ofNullable(claims)
                .map(value -> value.get(Claims.SUB))
                .map(Claim::asString)
                .map(String::trim)
                .orElseThrow(() -> new JWTVerificationException("Missing user_name claim"));
    }

    private List<SimpleGrantedAuthority> extractAuthorities(Map<String, Claim> claims) {
        List<String> authorities = Optional.ofNullable(claims)
                .map(value -> value.get(Claims.ROLES))
                .map(value -> value.asList(String.class))
                .orElse(Collections.emptyList());
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
