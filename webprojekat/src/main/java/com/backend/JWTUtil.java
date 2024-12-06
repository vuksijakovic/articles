package com.backend;

import com.backend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.sql.*;
import java.util.Date;

public class JWTUtil {
    private static final String SECRET_KEY = "L6z2z3s6Fhf7Dkf6Z9lKs9DlpA8+jkf9Fph4MnsJhAs=";
    private static final String URL = "jdbc:mysql://localhost:3306/webtabela";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    public static int getIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        return Integer.parseInt(claims.getId());
    }
    public static User decodeJWT(String token) {
        Integer id =JWTUtil.getIdFromToken(token);
        User user2 = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM korisnik WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user2 = new User();
                    user2.setId(resultSet.getInt("id"));
                    user2.setEmail(resultSet.getString("email"));
                    user2.setIme(resultSet.getString("ime"));
                    user2.setPrezime(resultSet.getString("prezime"));
                    user2.setTipKorisnika(resultSet.getString("tip_korisnika"));
                    user2.setStatus(resultSet.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return user2;
        }
        return user2;
    }
    public static String createJWT(int id, String email, boolean isAdmin) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setId(Integer.toString(id))
                .setSubject(email)
                .claim("isAdmin", isAdmin)
                .setIssuedAt(now)
                .setExpiration(new Date(nowMillis + 72000000))
                .signWith(signatureAlgorithm, signingKey)
                .compact();
    }
}
