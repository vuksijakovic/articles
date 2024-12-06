package com.backend.resource;

import com.backend.model.Activity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/activities")
public class ActivityResource {
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
    @Path("/article/{clanakId}")
    @Produces(MediaType.APPLICATION_JSON)
    public  List<Activity> getActivitiesByArticleId(@PathParam("clanakId") int clanakId) {
        List<Activity> activities = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT a.* FROM aktivnost a JOIN clanak_aktivnost ca ON a.id = ca.aktivnost_id WHERE ca.clanak_id = ?")) {
            statement.setInt(1, clanakId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Activity activity = new Activity();
                    activity.setId(resultSet.getInt("id"));
                    activity.setNaziv(resultSet.getString("naziv"));
                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activities;
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Activity> getAktivnosti() {
        List<Activity> aktivnosti = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM aktivnost")) {
            while (resultSet.next()) {
                Activity aktivnost = new Activity();
                aktivnost.setId(resultSet.getInt("id"));
                aktivnost.setNaziv(resultSet.getString("naziv"));
                aktivnosti.add(aktivnost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aktivnosti;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAktivnost(Activity aktivnost) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO aktivnost (naziv) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, aktivnost.getNaziv());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating aktivnost failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    aktivnost.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating aktivnost failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(aktivnost).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAktivnostById(@PathParam("id") int id) {
        Activity aktivnost = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM aktivnost WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    aktivnost = new Activity();
                    aktivnost.setId(resultSet.getInt("id"));
                    aktivnost.setNaziv(resultSet.getString("naziv"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        if (aktivnost == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Aktivnost not found").build();
        }
        return Response.status(Response.Status.OK).entity(aktivnost).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAktivnost(@PathParam("id") int id, Activity updatedAktivnost) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE aktivnost SET naziv = ? WHERE id = ?")) {
            statement.setString(1, updatedAktivnost.getNaziv());
            statement.setInt(2, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Aktivnost not found").build();
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
    public Response deleteAktivnost(@PathParam("id") int id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM aktivnost WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Aktivnost not found").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
