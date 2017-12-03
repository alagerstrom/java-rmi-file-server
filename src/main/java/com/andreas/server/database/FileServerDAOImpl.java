package com.andreas.server.database;

import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;
import com.andreas.common.exceptions.AccessDeniedException;
import com.andreas.common.exceptions.DatabaseException;
import com.andreas.server.model.FileMetaData;
import com.andreas.server.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileServerDAOImpl implements FileServerDAO {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String SCHEMA = "fileserver_schema";
    private static final String USER = "fileserver";
    private static final String PASSWORD = "fileserver";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA;

    private PreparedStatement createUserTableStatement;
    private PreparedStatement createUserUniqueIndexStatement;
    private PreparedStatement createFileMetaDataTableStatement;

    private PreparedStatement insertUserStatement;
    private PreparedStatement getAllUsersStatement;
    private PreparedStatement getUserByNameStatement;
    private PreparedStatement getUserByIdStatement;
    private PreparedStatement loginStatement;
    private PreparedStatement insertFileStatement;
    private PreparedStatement getFilesForUserStatement;
    private PreparedStatement getFileByNameStatement;
    private PreparedStatement deleteUserStatement;
    private PreparedStatement deleteFileStatement;

    public FileServerDAOImpl() throws DatabaseException {
        Connection connection;
        try {
            connection = createConnection();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

        try {
            prepareStatements(connection);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        try {
            createTables(connection);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<UserDTO> getAllUsers() throws DatabaseException {
        ResultSet resultSet;
        try {
            resultSet = getAllUsersStatement.executeQuery();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        List<UserDTO> users = new ArrayList<>();
        try {
            while (resultSet.next()) {
                users.add(
                        new User(
                                resultSet.getInt(UserTable.COLUMN_ID),
                                resultSet.getString(UserTable.COLUMN_NAME)
                        ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return users;
    }

    @Override
    public User insertUser(String username, String password) throws DatabaseException {
        try {
            insertUserStatement.setString(1, username);
            insertUserStatement.setString(2, password);
            insertUserStatement.execute();
            return getUserByName(username);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private User getUserById(int id) throws DatabaseException {
        try {
            getUserByIdStatement.setInt(1, id);
            ResultSet resultSet = getUserByIdStatement.executeQuery();
            if (resultSet.next())
                return new User(
                        resultSet.getInt(UserTable.COLUMN_ID),
                        resultSet.getString(UserTable.COLUMN_NAME)
                );
            else throw new DatabaseException("Null owner");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    private User getUserByName(String username) throws DatabaseException {
        try {
            getUserByNameStatement.setString(1, username);
            ResultSet resultSet = getUserByNameStatement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getInt(UserTable.COLUMN_ID),
                        resultSet.getString(UserTable.COLUMN_NAME)
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private FileMetaData getFileByName(String filename) throws DatabaseException {
        try {
            getFileByNameStatement.setString(1, filename);
            ResultSet resultSet = getFileByNameStatement.executeQuery();
            if (resultSet.next())
                return new FileMetaData(
                        resultSet.getString(FileMetaDataTable.COLUMN_FILENAME),
                        getUserById(resultSet.getInt(FileMetaDataTable.COLUMN_OWNER)),
                        resultSet.getBoolean(FileMetaDataTable.COLUMN_READ_ONLY),
                        resultSet.getBoolean(FileMetaDataTable.COLUMN_PUBLIC),
                        resultSet.getInt(FileMetaDataTable.COLUMN_SIZE)
                        );
            else return null;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public UserDTO login(String username, String password) throws DatabaseException {
        try {
            loginStatement.setString(1, username);
            loginStatement.setString(2, password);
            ResultSet resultSet = loginStatement.executeQuery();
            if (resultSet.next())
                return new User(
                        resultSet.getInt(UserTable.COLUMN_ID),
                        resultSet.getString(UserTable.COLUMN_NAME)
                );
            else
                return null;
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public List<FileMetaDTO> getFiles(UserDTO user) throws DatabaseException {
        List<FileMetaDTO> files = new ArrayList<>();

        try {
            getFilesForUserStatement.setInt(1, user.getId());
            ResultSet resultSet = getFilesForUserStatement.executeQuery();
            while (resultSet.next()) {
                files.add(new FileMetaData(
                        resultSet.getString(FileMetaDataTable.COLUMN_FILENAME),
                        getUserById(resultSet.getInt(FileMetaDataTable.COLUMN_OWNER)),
                        resultSet.getBoolean(FileMetaDataTable.COLUMN_READ_ONLY),
                        resultSet.getBoolean(FileMetaDataTable.COLUMN_PUBLIC),
                        resultSet.getInt(FileMetaDataTable.COLUMN_SIZE)
                ));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return files;
    }

    @Override
    public void insertFile(FileMetaDTO fileMeta) throws DatabaseException {
        try {
            insertFileStatement.setString(1, fileMeta.getFilename());
            insertFileStatement.setInt(2, fileMeta.getOwner().getId());
            insertFileStatement.setBoolean(3, fileMeta.readOnly());
            insertFileStatement.setBoolean(4, fileMeta.publicAccess());
            insertFileStatement.setInt(5, fileMeta.getSize());
            insertFileStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public void removeUser(UserDTO user) throws DatabaseException {
        try {
            deleteUserStatement.setInt(1, user.getId());
            deleteUserStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public void deleteFile(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException, AccessDeniedException {
        if (!hasAccessRights(currentUser, fileMeta) || !hasWritePermissions(currentUser, fileMeta))
            throw new AccessDeniedException();
        try {
            deleteFileStatement.setString(1, fileMeta.getFilename());
            deleteFileStatement.execute();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    private boolean hasWritePermissions(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException {
        FileMetaData fileMetaData = getFileByName(fileMeta.getFilename());
        assert fileMetaData != null;
        return fileMetaData.getOwner().getId() == currentUser.getId() || !fileMetaData.readOnly();
    }

    private boolean hasAccessRights(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException {
        FileMetaData fileMetaData = getFileByName(fileMeta.getFilename());
        assert fileMetaData != null;
        return fileMetaData.getOwner().getId() == currentUser.getId() || fileMetaData.publicAccess();
    }

    private Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void prepareStatements(Connection connection) throws SQLException {
        this.insertUserStatement = connection.prepareStatement(
                "INSERT INTO " + UserTable.TABLE_NAME + "(" +
                        UserTable.COLUMN_NAME + ", " +
                        UserTable.COLUMN_PASSWORD + ")" + " VALUES (?, ?)");
        this.createUserTableStatement = connection.prepareStatement(
                "CREATE TABLE " + UserTable.TABLE_NAME +
                        "(" +
                        UserTable.COLUMN_ID + " INT PRIMARY KEY AUTO_INCREMENT, " +
                        UserTable.COLUMN_NAME + " VARCHAR(30) NOT NULL, " +
                        UserTable.COLUMN_PASSWORD + " VARCHAR(30) NOT NULL " +
                        ");"
        );
        this.createUserUniqueIndexStatement = connection.prepareStatement(
                "CREATE UNIQUE INDEX User_name_uindex ON "
                        + UserTable.TABLE_NAME + " (" + UserTable.COLUMN_NAME + ");"
        );
        this.getAllUsersStatement = connection.prepareStatement(
                "SELECT * FROM " + UserTable.TABLE_NAME + ";"
        );
        this.getUserByNameStatement = connection.prepareStatement(
                "SELECT * FROM " + UserTable.TABLE_NAME +
                        " WHERE " + UserTable.COLUMN_NAME + " = ?;"
        );
        this.loginStatement = connection.prepareStatement(
                "SELECT * FROM " + UserTable.TABLE_NAME +
                        " WHERE " + UserTable.COLUMN_NAME + " = ? AND " +
                        UserTable.COLUMN_PASSWORD + " = ?;"
        );
        this.createFileMetaDataTableStatement = connection.prepareStatement(
                "CREATE TABLE " + FileMetaDataTable.TABLE_NAME +
                        " (" +
                        FileMetaDataTable.COLUMN_FILENAME + " VARCHAR(30) PRIMARY KEY, " +
                        FileMetaDataTable.COLUMN_OWNER + " INT, " +
                        FileMetaDataTable.COLUMN_READ_ONLY + " BOOLEAN NOT NULL, " +
                        FileMetaDataTable.COLUMN_PUBLIC + " BOOLEAN NOT NULL, " +
                        FileMetaDataTable.COLUMN_SIZE + " INT, " +
                        " CONSTRAINT FileMetaData_User_id_fk FOREIGN KEY (" + FileMetaDataTable.COLUMN_OWNER + ")" +
                        " REFERENCES " + UserTable.TABLE_NAME + " (" + UserTable.COLUMN_ID + ") ON DELETE CASCADE ON UPDATE CASCADE" +
                        ");"
        );
        this.insertFileStatement = connection.prepareStatement(
                "INSERT INTO " + FileMetaDataTable.TABLE_NAME + " (" +
                        FileMetaDataTable.COLUMN_FILENAME + ", " +
                        FileMetaDataTable.COLUMN_OWNER + ", " +
                        FileMetaDataTable.COLUMN_READ_ONLY + ", " +
                        FileMetaDataTable.COLUMN_PUBLIC + ", " +
                        FileMetaDataTable.COLUMN_SIZE +
                        ") VALUES (?,?,?,?,?);"
        );
        this.getFilesForUserStatement = connection.prepareStatement(
                "SELECT * FROM " + FileMetaDataTable.TABLE_NAME + " WHERE " +
                        FileMetaDataTable.COLUMN_OWNER + " = ? OR " +
                        FileMetaDataTable.COLUMN_PUBLIC + " = TRUE;"
        );
        this.getUserByIdStatement = connection.prepareStatement(
                "SELECT * FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_ID + " = ?;"
        );
        this.deleteUserStatement = connection.prepareStatement(
                "DELETE FROM " + UserTable.TABLE_NAME + " WHERE " + UserTable.COLUMN_ID + " = ?;"
        );
        this.deleteFileStatement = connection.prepareStatement(
                "DELETE FROM " + FileMetaDataTable.TABLE_NAME + " WHERE " +
                        FileMetaDataTable.COLUMN_FILENAME + " = ?;"
        );
        this.getFileByNameStatement = connection.prepareStatement(
            "SELECT * FROM " + FileMetaDataTable.TABLE_NAME + " WHERE " + FileMetaDataTable.COLUMN_FILENAME + " = ?;"
        );

    }

    private void createTables(Connection connection) throws SQLException, DatabaseException {
        if (!tableExists(UserTable.TABLE_NAME, connection)) {
            createUserTableStatement.execute();
            createUserUniqueIndexStatement.execute();
        }
        if (!tableExists(FileMetaDataTable.TABLE_NAME, connection)) {
            createFileMetaDataTableStatement.execute();
        }

    }

    private boolean tableExists(String table, Connection connection) throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getTables(null, null, null, null);

        while (resultSet.next()) {
            String tableName = resultSet.getString("TABLE_NAME");
            if (tableName.equals(table)) {
                return true;
            }
        }
        return false;
    }

}
