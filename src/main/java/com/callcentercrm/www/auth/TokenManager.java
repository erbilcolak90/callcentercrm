package com.callcentercrm.www.auth;

import com.callcentercrm.www.entities.User;
import com.callcentercrm.www.services.CustomUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class TokenManager {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomUserService customUserService;

    private static final int validity = 30*60*1000;
    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(String username){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        User user = customUserService.findByUsername(username);
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",user.getId());
       // claims.put("roles",userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));


        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("jwtTutorial")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .signWith(key)
                .compact();
    }

    public String logoutToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

        int tokenDate = (int) claims.get("exp");

        claims.setExpiration(new Date(tokenDate-validity));
        return "Logout successful";
    }

    public boolean tokenValidate(String token){
        if(parseUserIdFromToken(token) != null && isExpired(token)){
            return true;
        }
        return false;
    }

    public String getUsernameFromToken(String token){
        Claims claims = getClaims(token);

        String userId = (String) claims.get("userId");

        User user = customUserService.findByUserId(userId);

        return user.getUsername();

    }

    public String parseUserIdFromToken(String token){
        try{
            Claims body = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

            String userId = (String) body.get("userId");

            return userId;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean isExpired(String token){
        Claims claims = getClaims(token);
        return claims.getExpiration().after(new Date(System.currentTimeMillis()));
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

}
