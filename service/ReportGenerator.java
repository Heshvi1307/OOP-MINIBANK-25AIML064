
package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class ReportGenerator {
    private ReportGenerator() {
    }

    public static void generate(Path logsFolder, Path reportFile) throws IOException {
        Files.createDirectories(logsFolder);

        long totalDeposits = 0L;
        long totalWithdrawals = 0L;
        StringBuilder fileDetails = new StringBuilder();

        try (DirectoryStream<Path> files = Files.newDirectoryStream(logsFolder)) {
            for (Path file : files) {
                if (!Files.isRegularFile(file)
                        || !file.getFileName().toString().endsWith(".log")
                        || Files.size(file) == 0) {
                    continue;
                }

                BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);

                fileDetails.append("\nFile: ").append(file.getFileName())
                        .append("\nSize: ").append(attributes.size()).append(" bytes")
                        .append("\nLast modified: ")
                        .append(attributes.lastModifiedTime()).append('\n');

                try (BufferedReader reader = Files.newBufferedReader(
                        file, StandardCharsets.UTF_8)) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.trim().split("\\s+");
                        if (parts.length != 3) {
                            continue;
                        }

                        try {
                            long amount = Long.parseLong(parts[2]);
                            if (amount <= 0) {
                                continue;
                            }

                            if (parts[0].equalsIgnoreCase("DEPOSIT")) {
                                totalDeposits = Math.addExact(totalDeposits, amount);
                            } else if (parts[0].equalsIgnoreCase("WITHDRAW")) {
                                totalWithdrawals = Math.addExact(totalWithdrawals, amount);
                            }
                        } catch (NumberFormatException e) {
                            fileDetails.append("Invalid transaction skipped: ")
                                    .append(line).append('\n');
                        }
                    }
                }
            }
        }

        long netChange = totalDeposits - totalWithdrawals;

        String report = "MINIBANK END-OF-DAY REPORT\n"
                + "=========================\n"
                + "Total deposits: Rs." + totalDeposits + "\n"
                + "Total withdrawals: Rs." + totalWithdrawals + "\n"
                + "Net change: Rs." + netChange + "\n"
                + "\nFILE DETAILS\n"
                + "============\n"
                + (fileDetails.length() == 0
                        ? "No non-empty .log files found.\n"
                        : fileDetails);

        Path parent = reportFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(reportFile, report, StandardCharsets.UTF_8);
        System.out.println(report);
    }
}
