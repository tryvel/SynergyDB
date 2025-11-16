package synergy.db;

import java.sql.*;

public class JDBCExample {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "010101";

        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement statement = connection.createStatement();
            String createStudentsTable = """
                    CREATE TABLE IF NOT EXISTS Students (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100),
                    age INTEGER
                    );""";
            statement.execute(createStudentsTable);

            String insertStudents = """
                    INSERT INTO Students (name, age) VALUES
                    ('Alice', 22),
                    ('Bob', 52)
                    ;""";
            System.out.println(statement.executeUpdate(insertStudents));

            String selectAllStudents = "SELECT * FROM Students;";
            ResultSet resultSet = statement.executeQuery(selectAllStudents);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idByColumn = resultSet.getInt(1);
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                System.out.printf("Студент номер %d(%d), имя %s, возраст %d\n", id, idByColumn, name, age);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
    }
}
