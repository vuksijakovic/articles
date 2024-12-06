package com.backend.resource;

import com.backend.model.ArticleActivity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/article-activities")
public class ArticleActivityResource {
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
    public List<ArticleActivity> getClanakAktivnosti() {
        List<ArticleActivity> clanakAktivnosti = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM clanak_aktivnost")) {
            while (resultSet.next()) {
                ArticleActivity clanakAktivnost = new ArticleActivity();
                clanakAktivnost.setId(resultSet.getInt("id"));
                clanakAktivnost.setClanakId(resultSet.getInt("clanak_id"));
                clanakAktivnost.setAktivnostId(resultSet.getInt("aktivnost_id"));
                clanakAktivnosti.add(clanakAktivnost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clanakAktivnosti;
    }
    @GET
    @Path("/activity/{aktivnostId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArticleActivity> getClanakAktivnostiByAktivnostId(@PathParam("aktivnostId") int aktivnostId) {
        List<ArticleActivity> clanakAktivnosti = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM clanak_aktivnost WHERE aktivnost_id = " + aktivnostId)) {
            while (resultSet.next()) {
                ArticleActivity clanakAktivnost = new ArticleActivity();
                clanakAktivnost.setId(resultSet.getInt("id"));
                clanakAktivnost.setClanakId(resultSet.getInt("clanak_id"));
                clanakAktivnost.setAktivnostId(resultSet.getInt("aktivnost_id"));
                clanakAktivnosti.add(clanakAktivnost);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clanakAktivnosti;
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createClanakAktivnost(ArticleActivity clanakAktivnost) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO clanak_aktivnost (clanak_id, aktivnost_id) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, clanakAktivnost.getClanakId());
            statement.setInt(2, clanakAktivnost.getAktivnostId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating clanak_aktivnost failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    clanakAktivnost.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating clanak_aktivnost failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(clanakAktivnost).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClanakAktivnostById(@PathParam("id") int id) {
        ArticleActivity clanakAktivnost = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM clanak_aktivnost WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    clanakAktivnost = new ArticleActivity();
                    clanakAktivnost.setId(resultSet.getInt("id"));
                    clanakAktivnost.setClanakId(resultSet.getInt("clanak_id"));
                    clanakAktivnost.setAktivnostId(resultSet.getInt("aktivnost_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        if (clanakAktivnost == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("ClanakAktivnost not found").build();
        }
        return Response.status(Response.Status.OK).entity(clanakAktivnost).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClanakAktivnost(@PathParam("id") int id, ArticleActivity updatedClanakAktivnost) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE clanak_aktivnost SET clanak_id = ?, aktivnost_id = ? WHERE id = ?")) {
            statement.setInt(1, updatedClanakAktivnost.getClanakId());
            statement.setInt(2, updatedClanakAktivnost.getAktivnostId());
            statement.setInt(3, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("ClanakAktivnost not found").build();
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
    public Response deleteClanakAktivnost(@PathParam("id") int id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM clanak_aktivnost WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("ClanakAktivnost not found").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
