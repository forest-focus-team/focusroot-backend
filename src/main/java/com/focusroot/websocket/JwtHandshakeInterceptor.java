package com.focusroot.websocket;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.List;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            return false;
        }

        String token = authHeaders.get(0).replace("Bearer ", "");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            attributes.put("userId", claims.getSubject());
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
