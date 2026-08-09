public class CommandParser {

    public static Command parse(String line) {

        String[] parts = line.split(" ");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid command. Use: TYPE ACCOUNT_NUMBER AMOUNT");
        }

        TransactionType type = TransactionType.valueOf(parts[0].toUpperCase());

        String accountNumber = parts[1];

        long amount = Long.parseLong(parts[2]);

        return new Command(type, accountNumber, amount);
    }
}