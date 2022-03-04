package ru.hemulen.docsigner.entity;

import java.sql.*;

public class DBConnection implements AutoCloseable {
    private Connection connection;
    public DBConnection() {
        //String dbURL = "jdbc:postgresql://localhost:5432/adapter";
        //String dbUser = "smev";
        //String dbPass = "smev";
        String dbURL = "jdbc:postgresql://10.82.10.58:5432/smev_adapter_1";
        String dbUser = "smev_adapter";
        String dbPass = "sQ38FyPy}WfZ";
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(dbURL, dbUser, dbPass);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
    public void close()  {
        try {
            connection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    public ResultSet getAnswers(String clientId) {
        try {
            String sql = String.format("select mm.id, mm.message_id , mc.\"mode\" , mc.\"content\" \n" +
                    "from core.message_metadata mm \n" +
                    "inner join core.message_content mc on mc.id = mm.id \n" +
                    "where mm.reference_id = '%s';", clientId);
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public String getMessageId(String clientId) {
        try {
            String sql = String.format("select message_id from \"core\".message_metadata where id = '%s'", clientId);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet != null && resultSet.next()) {
                return resultSet.getString("message_id");
            }
            return null;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public ResultSet getAttachments(String id) {
        try {
            String sql = String.format("select file_name from \"core\".attachment_metadata where message_metadata_id = '%s'", id);
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
