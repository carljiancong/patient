package com.harmonycloud.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class JwtUtil {

    private RestTemplate template = new RestTemplate();

    private PublicKey publicKeyObject;

    @Value("${security.publicKey}")
    private String GET_PUBLIC_KEY_URL;


    public String getPublicKey(HttpServletRequest request) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (request.getHeader("user") != null) {
                headers.add("user", request.getHeader("user"));
            }
            if (request.getHeader("clinic") != null) {
                headers.add("clinic", request.getHeader("clinic"));
            }
            TraceUtil.addTraceForHttp(request, headers);
            HttpEntity httpEntity = new HttpEntity(null, headers);
            String key = template.exchange(new URI(GET_PUBLIC_KEY_URL), HttpMethod.GET, httpEntity, String.class).getBody();
            return key;
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean validateToken(String authToken, HttpServletRequest request) throws Exception{
        try {
            String publicKey = getPublicKey(request);
            if (!StringUtils.isEmpty(publicKey)) {
                X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
                KeyFactory x509KeyFactory = KeyFactory.getInstance("RSA");
                publicKeyObject = x509KeyFactory.generatePublic(x509KeySpec);
            }

            if (authToken != null) {
                try {
                    Jwts.parser().setSigningKey(publicKeyObject).parseClaimsJws(authToken).getBody();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Map<String, Object> getUserInfoFromJWT(String token) throws NoSuchAlgorithmException, InvalidKeySpecException {

        Claims claims = Jwts.parser().setSigningKey(publicKeyObject).parseClaimsJws(token).getBody();
        return claims;
    }
}
