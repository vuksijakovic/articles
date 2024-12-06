package com.backend.resource;

import com.backend.JWTUtil;
import com.backend.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Path("/users")
public class UserResource {
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

    private static final String URL = "jdbc:mysql://localhost:3306/webtabela";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found", e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM korisnik")) {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setEmail(resultSet.getString("email"));
                user.setIme(resultSet.getString("ime"));
                user.setPrezime(resultSet.getString("prezime"));
                user.setTipKorisnika(resultSet.getString("tip_korisnika"));
                user.setStatus(resultSet.getString("status"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(User user, @HeaderParam("Authorization") String token) {
        User user2 = null;

        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        user2 = JWTUtil.decodeJWT(token);
        if(user2==null || !user2.getTipKorisnika().equalsIgnoreCase("admin")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        if(user.getTipKorisnika().trim().isEmpty() || user.getEmail().trim().isEmpty() || user.getIme().trim().isEmpty() || user.getStatus().trim().isEmpty() || user.getPrezime().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("All fields are required")
                    .build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);


             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM korisnik WHERE LOWER(email) = LOWER(?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getEmail());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return Response.status(Response.Status.CONFLICT).entity("User with the same mail already exists").build();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO korisnik (email, ime, prezime, tip_korisnika, status, lozinka) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getIme());
            statement.setString(3, user.getPrezime());
            statement.setString(4, user.getTipKorisnika());
            statement.setString(5, user.getStatus());
            String hashedPassword = hashPassword(user.getLozinka());
            statement.setString(6, hashedPassword);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(user).build();
    }
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") int id) {
        User user = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM korisnik WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setEmail(resultSet.getString("email"));
                    user.setIme(resultSet.getString("ime"));
                    user.setPrezime(resultSet.getString("prezime"));
                    user.setTipKorisnika(resultSet.getString("tip_korisnika"));
                    user.setStatus(resultSet.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        return Response.status(Response.Status.OK).entity(user).build();
    }
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") int id, User updatedUser, @HeaderParam("Authorization") String token) {
        User user2 = null;

        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        user2 = JWTUtil.decodeJWT(token);
        if(user2==null || !user2.getTipKorisnika().equalsIgnoreCase("admin")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        if(updatedUser.getTipKorisnika().trim().isEmpty() || updatedUser.getEmail().trim().isEmpty() || updatedUser.getIme().trim().isEmpty() || updatedUser.getPrezime().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("All fields are required")
                    .build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);


             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM korisnik WHERE LOWER(email) = LOWER(?) AND id != ?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, updatedUser.getEmail());
            statement.setInt(2, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return Response.status(Response.Status.CONFLICT).entity("User with the same mail already exists").build();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE korisnik SET email = ?, ime = ?, prezime = ?, tip_korisnika = ? WHERE id = ?")) {
            statement.setString(1, updatedUser.getEmail());
            statement.setString(2, updatedUser.getIme());
            statement.setString(3, updatedUser.getPrezime());
            statement.setString(4, updatedUser.getTipKorisnika());
            statement.setInt(5, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
    @POST
    @Path("/activate/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("id") int id, @HeaderParam("Authorization") String token) {
        User user2 = null;

        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        user2 = JWTUtil.decodeJWT(token);
        if(user2==null || !user2.getTipKorisnika().equalsIgnoreCase("admin")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE korisnik SET status = CASE WHEN status = 'aktivan' THEN 'neaktivan' ELSE 'aktivan' END WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") int id, @HeaderParam("Authorization") String token) {
        User user2 = null;

        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        user2 = JWTUtil.decodeJWT(token);
        if(user2==null || !user2.getTipKorisnika().equalsIgnoreCase("admin")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM korisnik WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
    }
