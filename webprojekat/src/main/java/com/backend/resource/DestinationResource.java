package com.backend.resource;

import com.backend.JWTUtil;
import com.backend.model.Destination;
import com.backend.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/destinations")
public class DestinationResource {

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
    public List<Destination> getDestinations() {
        List<Destination> destinations = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM destinacija")) {
            while (resultSet.next()) {
                Destination destination = new Destination();
                destination.setId(resultSet.getInt("id"));
                destination.setIme(resultSet.getString("ime"));
                destination.setOpis(resultSet.getString("opis"));
                destinations.add(destination);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return destinations;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createDestination(Destination destination,@HeaderParam("Authorization") String token ) {

        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = JWTUtil.decodeJWT(token);
        if(user==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(destination.getIme().trim().isEmpty() || destination.getIme()==null || destination.getOpis().trim().isEmpty() || destination.getOpis() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("All fields are required")
                    .build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);


             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM destinacija WHERE LOWER(ime) = LOWER(?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, destination.getIme());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return Response.status(Response.Status.CONFLICT).entity("Destination with the same name already exists").build();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);    PreparedStatement statement = connection.prepareStatement("INSERT INTO destinacija (ime, opis) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, destination.getIme());
            statement.setString(2, destination.getOpis());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating destination failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    destination.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating destination failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(destination).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDestinationById(@PathParam("id") int id) {
        Destination destination = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM destinacija WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    destination = new Destination();
                    destination.setId(resultSet.getInt("id"));
                    destination.setIme(resultSet.getString("ime"));
                    destination.setOpis(resultSet.getString("opis"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        if (destination == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Destination not found").build();
        }
        return Response.status(Response.Status.OK).entity(destination).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDestination(@PathParam("id") int id, Destination updatedDestination, @HeaderParam("Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = JWTUtil.decodeJWT(token);
        if(user==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(updatedDestination.getIme().trim().isEmpty() || updatedDestination.getIme()==null || updatedDestination.getOpis().trim().isEmpty() || updatedDestination.getOpis() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("All fields are required")
                    .build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);


             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM destinacija WHERE LOWER(ime) = LOWER(?) AND id != ?", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, updatedDestination.getIme());
            statement.setInt(2, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return Response.status(Response.Status.CONFLICT).entity("Destination with the same name already exists").build();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE destinacija SET ime = ?, opis = ? WHERE id = ?")) {
            statement.setString(1, updatedDestination.getIme());
            statement.setString(2, updatedDestination.getOpis());
            statement.setInt(3, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Destination not found").build();
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
    public Response deleteDestination(@PathParam("id") int id,  @HeaderParam("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = JWTUtil.decodeJWT(token);
        if(user==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM destinacija WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Destination not found").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
