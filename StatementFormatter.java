public class StatementFormatter {

    public static String buildStatement(Account account) {

        StringBuilder statement = new StringBuilder();

        statement.append("========== ACCOUNT STATEMENT ==========\n");
        statement.append("Account Number : ")
                .append(account.getAccountNumber()).append("\n");
        statement.append("Owner          : ")
                .append(account.getOwnerName()).append("\n");
        statement.append("Balance        : Rs.")
                .append(account.getBalance()).append("\n");
        statement.append("Active         : ")
                .append(account.isActive()).append("\n");

        return statement.toString();
    }
}