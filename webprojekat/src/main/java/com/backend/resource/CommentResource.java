package com.backend.resource;

import com.backend.model.Comment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/comments")
public class CommentResource {
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
    public List<Comment> getKomentari() {
        List<Comment> komentari = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM komentar")) {
            while (resultSet.next()) {
                Comment komentar = new Comment();
                komentar.setId(resultSet.getInt("id"));
                komentar.setAutorIme(resultSet.getString("autor_ime"));
                komentar.setTekst(resultSet.getString("tekst"));
                komentar.setDatumKreiranja(resultSet.getTimestamp("datum_kreiranja"));
                komentar.setClanakId(resultSet.getInt("clanak_id"));
                komentari.add(komentar);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return komentari;
    }
    @GET
    @Path("/article/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Comment> getKomentariArtikal(@PathParam("id") int id) {
        List<Comment> komentari = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM komentar")) {
            while (resultSet.next()) {
                Comment komentar = new Comment();
                komentar.setId(resultSet.getInt("id"));
                komentar.setAutorIme(resultSet.getString("autor_ime"));
                komentar.setTekst(resultSet.getString("tekst"));
                komentar.setDatumKreiranja(resultSet.getTimestamp("datum_kreiranja"));
                komentar.setClanakId(resultSet.getInt("clanak_id"));
                if(komentar.getClanakId()==id) {
                    komentari.add(komentar);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return komentari;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createKomentar(Comment komentar) {
        komentar.setDatumKreiranja(Timestamp.valueOf(LocalDateTime.now()));
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO komentar (autor_ime, tekst, datum_kreiranja, clanak_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, komentar.getAutorIme());
            statement.setString(2, komentar.getTekst());
            statement.setTimestamp(3, new Timestamp(komentar.getDatumKreiranja().getTime()));
            statement.setInt(4, komentar.getClanakId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating komentar failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    komentar.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating komentar failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(komentar).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKomentarById(@PathParam("id") int id) {
        Comment komentar = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM komentar WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    komentar = new Comment();
                    komentar.setId(resultSet.getInt("id"));
                    komentar.setAutorIme(resultSet.getString("autor_ime"));
                    komentar.setTekst(resultSet.getString("tekst"));
                    komentar.setDatumKreiranja(resultSet.getTimestamp("datum_kreiranja"));
                    komentar.setClanakId(resultSet.getInt("clanak_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        if (komentar == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Komentar not found").build();
        }
        return Response.status(Response.Status.OK).entity(komentar).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateKomentar(@PathParam("id") int id, Comment updatedKomentar) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE komentar SET autor_ime = ?, tekst = ?, datum_kreiranja = ?, clanak_id = ? WHERE id = ?")) {
            statement.setString(1, updatedKomentar.getAutorIme());
            statement.setString(2, updatedKomentar.getTekst());
            statement.setTimestamp(3, new Timestamp(updatedKomentar.getDatumKreiranja().getTime()));
            statement.setInt(4, updatedKomentar.getClanakId());
            statement.setInt(5, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Komentar not found").build();
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
    public Response deleteKomentar(@PathParam("id") int id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM komentar WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Komentar not found").build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
