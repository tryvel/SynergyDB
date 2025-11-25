package topic05.task01;

import java.sql.*;

public class OrganizationsDB {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "010101";

        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            String organizationCountSQL = "SELECT COUNT (*) FROM organizations";
            // при работе с ENUM используется не просто знак '?', а строка формы '?::<имя списка значений ENUM>'
            String totalAmountSQL = """
                    SELECT
                        CASE
                            WHEN o.org_type = 'legal_entity' THEN 'Юридическое лицо'
                            WHEN o.org_type = 'individual_entrepreneur' THEN 'Индивидуальный предприниматель'
                            ELSE 'Неизвестный тип'
                        END AS organization_type,
                        COUNT(DISTINCT o.id) AS organization_count,
                        SUM(cw.total_price) as total_completed_amount
                    FROM organizations o
                    JOIN contracts c ON o.id = c.organization_id
                    JOIN contract_works cw ON c.id = cw.contract_id
                    WHERE o.org_type = ?::organization_type AND cw.is_completed = true
                    GROUP BY o.org_type;
                    """;
//            String totalAmountSQL = "SELECT COUNT (*) FROM organizations WHERE org_type = ?::organization_type";
            String organizationInfoSQL = "SELECT full_name, legal_address, phone, email FROM organizations;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(organizationCountSQL)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    System.out.println("Количество контрагентов: " + resultSet.getInt("count"));
                }
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(totalAmountSQL)) {
                System.out.println("Выплаты по типам организаций:");
                String format = "| %-" + 30 + "s | %" + 25 + "d | %" + 20 + ".2f |\n";
                System.out.printf("| %-" + 30 + "s | %" + 25 + "s | %" + 20 + "s |\n",
                        "Тип организации", "Количество организаций", "Выплаты за работы");

                preparedStatement.setString(1, "legal_entity");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    System.out.printf(format, resultSet.getString("organization_type"),
                            resultSet.getInt("organization_count"),
                            resultSet.getDouble("total_completed_amount"));
                }

                preparedStatement.setString(1, "individual_entrepreneur");
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    System.out.printf(format, resultSet.getString("organization_type"),
                            resultSet.getInt("organization_count"),
                            resultSet.getDouble("total_completed_amount"));
                }

            }

            try(PreparedStatement preparedStatement = connection.prepareStatement(organizationInfoSQL)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                System.out.println("Информация обо всех контрагентах:");
                while (resultSet.next()) {
                    System.out.println("/ " + resultSet.getString("full_name") +
                            " / " + resultSet.getString("legal_address") +
                            " / " + resultSet.getString("phone") +
                            " / " + resultSet.getString("email") + " /");
                }


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
