package com.backend.resource;

import com.backend.JWTUtil;
import com.backend.model.Article;
import com.backend.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/articles")
public class ArticleResource {
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
    public List<Article> getClanci() {
        List<Article> clanci = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM clanak")) {
            while (resultSet.next()) {
                Article clanak = new Article();
                clanak.setId(resultSet.getInt("id"));
                clanak.setNaslov(resultSet.getString("naslov"));
                clanak.setTekst(resultSet.getString("tekst"));
                clanak.setVremeKreiranja(resultSet.getTimestamp("vreme_kreiranja"));
                clanak.setBrojPoseta(resultSet.getInt("broj_poseta"));
                clanak.setAutorId(resultSet.getInt("autor_id"));
                clanak.setDestinacijaId(resultSet.getInt("destinacija_id"));
                clanci.add(clanak);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clanci;
    }
    @GET
    @Path("/last30days")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Article> getClanci30() {
        List<Article> clanci = new ArrayList<>();
        LocalDateTime pre30Dana = LocalDateTime.now().minusDays(30);
        Timestamp timestampPre30Dana = Timestamp.valueOf(pre30Dana);
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM clanak WHERE vreme_kreiranja > ?")) {
            statement.setTimestamp(1, timestampPre30Dana);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Article clanak = new Article();
                    clanak.setId(resultSet.getInt("id"));
                    clanak.setNaslov(resultSet.getString("naslov"));
                    clanak.setTekst(resultSet.getString("tekst"));
                    clanak.setVremeKreiranja(resultSet.getTimestamp("vreme_kreiranja"));
                    clanak.setBrojPoseta(resultSet.getInt("broj_poseta"));
                    clanak.setAutorId(resultSet.getInt("autor_id"));
                    clanak.setDestinacijaId(resultSet.getInt("destinacija_id"));
                    clanci.add(clanak);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clanci;
    }
    @GET
    @Path("/destination/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Article> getClanciDestinacija(@PathParam("id") int id) {
        List<Article> clanci = new ArrayList<>();
           try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM clanak WHERE destinacija_id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Article clanak = new Article();
                    clanak.setId(resultSet.getInt("id"));
                    clanak.setNaslov(resultSet.getString("naslov"));
                    clanak.setTekst(resultSet.getString("tekst"));
                    clanak.setVremeKreiranja(resultSet.getTimestamp("vreme_kreiranja"));
                    clanak.setBrojPoseta(resultSet.getInt("broj_poseta"));
                    clanak.setAutorId(resultSet.getInt("autor_id"));
                    clanak.setDestinacijaId(resultSet.getInt("destinacija_id"));
                    clanci.add(clanak);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clanci;
    }
    @GET
    @Path("/activity/{aktivnostId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Article> getArticlesByActivityId(@PathParam("aktivnostId") int aktivnostId) {
        List<Article> clanci = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT c.* FROM clanak c JOIN clanak_aktivnost ca ON c.id = ca.clanak_id WHERE ca.aktivnost_id = ?")) {
            statement.setInt(1, aktivnostId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Article clanak = new Article();
                    clanak.setId(resultSet.getInt("id"));
                    clanak.setNaslov(resultSet.getString("naslov"));
                    clanak.setTekst(resultSet.getString("tekst"));
                    clanak.setVremeKreiranja(resultSet.getTimestamp("vreme_kreiranja"));
                    clanak.setBrojPoseta(resultSet.getInt("broj_poseta"));
                    clanak.setAutorId(resultSet.getInt("autor_id"));
                    clanak.setDestinacijaId(resultSet.getInt("destinacija_id"));
                    clanci.add(clanak);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clanci;
    }
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createClanak(Article clanak, @HeaderParam("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = JWTUtil.decodeJWT(token);
        if(user==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(clanak.getAktivnosti().trim().isEmpty() || clanak.getAktivnosti() == null || clanak.getNaslov().trim().isEmpty() || clanak.getNaslov()==null ||
        clanak.getTekst().trim().isEmpty() || clanak.getTekst() == null || ((Integer)clanak.getDestinacijaId()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("All fields are required")
                    .build();
        }
        clanak.setAutorId(user.getId());

        System.out.println(clanak.getId());
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO clanak (naslov, tekst, vreme_kreiranja, broj_poseta, autor_id, destinacija_id) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, clanak.getNaslov());
            statement.setString(2, clanak.getTekst());
            statement.setTimestamp(3, new Timestamp(clanak.getVremeKreiranja().getTime()));
            statement.setInt(4, clanak.getBrojPoseta());
            statement.setInt(5, clanak.getAutorId());
            statement.setInt(6, clanak.getDestinacijaId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating clanak failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    clanak.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating clanak failed, no ID obtained.");
                }
            }
            try (PreparedStatement deleteStmt = connection.prepareStatement(
                    "DELETE FROM aktivnost WHERE id NOT IN (SELECT aktivnost_id FROM clanak_aktivnost)")) {
                deleteStmt.executeUpdate();
            }
            String[] aktivnosti = clanak.getAktivnosti().split(",");
            for (String aktivnost : aktivnosti) {
                aktivnost = aktivnost.trim();

                int aktivnostId = -1;
                try (PreparedStatement checkStmt = connection.prepareStatement("SELECT id FROM aktivnost WHERE LOWER(naziv) = LOWER(?)")) {
                    checkStmt.setString(1, aktivnost);
                    try (ResultSet resultSet = checkStmt.executeQuery()) {
                        if (resultSet.next()) {
                            aktivnostId = resultSet.getInt("id");
                        }
                    }
                }

                if (aktivnostId == -1) {
                    try (PreparedStatement insertAktivnostStmt = connection.prepareStatement("INSERT INTO aktivnost (naziv) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                        insertAktivnostStmt.setString(1, aktivnost);
                        int rows = insertAktivnostStmt.executeUpdate();

                        if (rows == 0) {
                            throw new SQLException("Creating aktivnost failed, no rows affected.");
                        }

                        try (ResultSet generatedKeys = insertAktivnostStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                aktivnostId = generatedKeys.getInt(1);
                            } else {
                                throw new SQLException("Creating aktivnost failed, no ID obtained.");
                            }
                        }
                    }
                }

                try (PreparedStatement insertClanakAktivnostStmt = connection.prepareStatement("INSERT INTO clanak_aktivnost (clanak_id, aktivnost_id) VALUES (?, ?)")) {
                    insertClanakAktivnostStmt.setInt(1, clanak.getId());
                    insertClanakAktivnostStmt.setInt(2, aktivnostId);
                    insertClanakAktivnostStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(clanak).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClanakById(@PathParam("id") int id) {
        Article clanak = null;
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM clanak WHERE id = ?");
             PreparedStatement aktivnostiStatement = connection.prepareStatement("SELECT a.naziv FROM aktivnost a INNER JOIN clanak_aktivnost ca ON a.id = ca.aktivnost_id WHERE ca.clanak_id = ?")) {

            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    clanak = new Article();
                    clanak.setId(resultSet.getInt("id"));
                    clanak.setNaslov(resultSet.getString("naslov"));
                    clanak.setTekst(resultSet.getString("tekst"));
                    clanak.setVremeKreiranja(resultSet.getTimestamp("vreme_kreiranja"));
                    clanak.setBrojPoseta(resultSet.getInt("broj_poseta"));
                    clanak.setAutorId(resultSet.getInt("autor_id"));
                    clanak.setDestinacijaId(resultSet.getInt("destinacija_id"));

                    // Fetch activities associated with the article
                    aktivnostiStatement.setInt(1, id);
                    StringBuilder aktivnostiBuilder = new StringBuilder();
                    try (ResultSet aktivnostiResultSet = aktivnostiStatement.executeQuery()) {
                        while (aktivnostiResultSet.next()) {
                            if (aktivnostiBuilder.length() > 0) {
                                aktivnostiBuilder.append(", ");
                            }
                            aktivnostiBuilder.append(aktivnostiResultSet.getString("naziv"));
                        }
                    }
                    clanak.setAktivnosti(aktivnostiBuilder.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        if (clanak == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Clanak not found").build();
        }
        return Response.status(Response.Status.OK).entity(clanak).build();
    }


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateClanak(@PathParam("id") int id, Article updatedClanak, @HeaderParam("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = JWTUtil.decodeJWT(token);
        if(user==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(updatedClanak.getAktivnosti().trim().isEmpty() || updatedClanak.getAktivnosti() == null || updatedClanak.getNaslov().trim().isEmpty() || updatedClanak.getNaslov()==null ||
                updatedClanak.getTekst().trim().isEmpty() || updatedClanak.getTekst() == null || ((Integer)updatedClanak.getDestinacijaId()) == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("All fields are required")
                    .build();
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE clanak SET naslov = ?, tekst = ?,  destinacija_id = ? WHERE id = ?")) {
            statement.setString(1, updatedClanak.getNaslov());
            statement.setString(2, updatedClanak.getTekst());

            statement.setInt(3, updatedClanak.getDestinacijaId());
            statement.setInt(4, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Clanak not found").build();
            }
            try (PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM clanak_aktivnost WHERE clanak_id = ?")) {
                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();
            }
            try (PreparedStatement deleteStmt = connection.prepareStatement(
                    "DELETE FROM aktivnost WHERE id NOT IN (SELECT aktivnost_id FROM clanak_aktivnost)")) {
                deleteStmt.executeUpdate();
            }
            String[] aktivnosti = updatedClanak.getAktivnosti().split(",");
            for (String aktivnost : aktivnosti) {
                aktivnost = aktivnost.trim();

                int aktivnostId = -1;
                try (PreparedStatement checkStmt = connection.prepareStatement("SELECT id FROM aktivnost WHERE LOWER(naziv) = LOWER(?)")) {
                    checkStmt.setString(1, aktivnost);
                    try (ResultSet resultSet = checkStmt.executeQuery()) {
                        if (resultSet.next()) {
                            aktivnostId = resultSet.getInt("id");
                        }
                    }
                }

                if (aktivnostId == -1) {
                    try (PreparedStatement insertAktivnostStmt = connection.prepareStatement("INSERT INTO aktivnost (naziv) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                        insertAktivnostStmt.setString(1, aktivnost);
                        int rows = insertAktivnostStmt.executeUpdate();

                        if (rows == 0) {
                            throw new SQLException("Creating aktivnost failed, no rows affected.");
                        }

                        try (ResultSet generatedKeys = insertAktivnostStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                aktivnostId = generatedKeys.getInt(1);
                            } else {
                                throw new SQLException("Creating aktivnost failed, no ID obtained.");
                            }
                        }
                    }
                }

                try (PreparedStatement insertClanakAktivnostStmt = connection.prepareStatement("INSERT INTO clanak_aktivnost (clanak_id, aktivnost_id) VALUES (?, ?)")) {
                    insertClanakAktivnostStmt.setInt(1, id);
                    insertClanakAktivnostStmt.setInt(2, aktivnostId);
                    insertClanakAktivnostStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
    @POST
    @Path("/{id}/incrementVisits")
    @Produces(MediaType.APPLICATION_JSON)
    public Response incrementBrojPoseta(@PathParam("id") int id) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE clanak SET broj_poseta = broj_poseta + 1 WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Clanak not found").build();
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
    public Response deleteClanak(@PathParam("id") int id, @HeaderParam("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = JWTUtil.decodeJWT(token);
        if(user==null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM clanak_aktivnost WHERE clanak_id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM komentar WHERE clanak_id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM clanak WHERE id = ?")) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.NOT_FOUND).entity("Clanak not found").build();
            }
            try (PreparedStatement deleteStmt = connection.prepareStatement(
                    "DELETE FROM aktivnost WHERE id NOT IN (SELECT aktivnost_id FROM clanak_aktivnost)")) {
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
        return Response.status(Response.Status.OK).build();
    }
}
