package com.backend.resource;

import com.backend.JWTUtil;
import com.backend.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

@Path("/auth")
public class AuthResource {
    private static final String URL = "jdbc:mysql://localhost:3306/webtabela";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String SECRET_KEY = "L6z2z3s6Fhf7Dkf6Z9lKs9DlpA8+jkf9Fph4MnsJhAs=";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found", e);
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user) {
        User dbUser = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM korisnik WHERE email = ? AND lozinka = ?")) {
            statement.setString(1, user.getEmail());
            statement.setString(2, hashPassword(user.getLozinka()));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    dbUser = new User();
                    dbUser.setId(resultSet.getInt("id"));
                    dbUser.setEmail(resultSet.getString("email"));
                    dbUser.setIme(resultSet.getString("ime"));
                    dbUser.setPrezime(resultSet.getString("prezime"));
                    dbUser.setTipKorisnika(resultSet.getString("tip_korisnika"));
                    dbUser.setStatus(resultSet.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

        if (dbUser == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        if (dbUser.getStatus().equalsIgnoreCase("neaktivan")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        boolean admin = false;
        if(dbUser.getTipKorisnika().equalsIgnoreCase("admin")) {
            admin = true;
        }
        String token = JWTUtil.createJWT(dbUser.getId(), dbUser.getEmail(), admin);

        return Response.status(Response.Status.OK).entity(token).build();
    }

    private String createJWT(int id, String email) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setId(Integer.toString(id))
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(nowMillis + 7200000))
                .signWith(signatureAlgorithm, signingKey)
                .compact();
    }
}
