package com.ecommerce.api.JWTAuthentication.Config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ecommerce.api.Entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String Secret_key="6VFjs7JeJSu//1mXBheB0V6HODTTmjTzOJkowkncXBcD7ZdE50dlS5odVWXS2vtd90DJdAyhmajDETs3kpI5u7DwnT98tojgqMn5nlE4EmfAKWxZlioCo0dIda9XI9sjMaqYJ0BysHmx+AIyKwzzYe37uebUTkGU8UwDom1v4nCIjswvKk3zGr+LB+gs4opnrzEadwrPLz7JzmWqlEf3Qz132mH33i5nkfj0+G4EVkxtdRIonytY4dH8x83AVL17BfIeF1XSY9KNOP9JDNadoWEjf5zDjNcSg1CP6Vsx1bzh5P2Lj1tmyLA2Ps4c4e/kZcv+wyFoXjfA8iQYqlao2pp1xzDKtThF+PZMeoHitEc=";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //generae token just from the user details
    public String generateToken(User user){
        return generateToken(new HashMap<>(), user);
    }


    //validate the token

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username=extractUsername(token);
        return(username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    

    private boolean isTokenExpired(String token) {
        return extractExperation(token).before(new Date());
    }

    private Date extractExperation(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // generateToken
    public String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails
    ){
        return Jwts
        .builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
        
    }
    //extract one claim

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //methode to extract all claims

    private Claims extractAllClaims(String token){
        return Jwts
        .parserBuilder().setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
        
    }

    private Key getSignInKey() {
       byte[] keyBytes= Decoders.BASE64.decode(Secret_key);
       return Keys.hmacShaKeyFor(keyBytes);
    }
    

}
