package dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public record Event(
        long id,
        String name,
        Long kickoff,
        League league,
        List<Market> markets
) {
    private static final DateTimeFormatter UTC_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
            .withZone(ZoneOffset.UTC);

    @Override
    public String toString() {
        String sport = "";
        String leagueName = "";
        if (league != null) {
            leagueName = Objects.toString(league.name(), "");
            if (league.sport() != null) {
                sport = Objects.toString(league.sport().name(), "");
            }
        }

        final String INDENT_MATCH = "    "; // 4
        final String INDENT_MARKET = "        "; // 8
        final String INDENT_RUNNER = "            "; // 12
        final String SEP_BOT = "------------------------------------------------------------------------------------------------";
        final String SEP_TOP = "--------------------------------------------------";

        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append(SEP_TOP).append("\n");

        // спорт, лига
        sb.append(sport).append(", ").append(leagueName).append("\n");

        // названия, кикоф ютс, айди
        sb.append(INDENT_MATCH);
        sb.append(Objects.toString(name, "")).append(", ")
                .append(formatKickoffUtc(kickoff)).append(", ")
                .append(id)
                .append("\n");

        // маркеты

        if (markets != null) {
            for (Market m : markets) {
                if (m == null) continue;

                sb.append(INDENT_MARKET);
                sb.append(Objects.toString(m.name(), "")).append("\n");

                if (m.runners() != null) {
                    for (Runner r : m.runners()) {
                        if (r == null) continue;
                        sb.append(INDENT_RUNNER);
                        sb.append(Objects.toString(r.name(), ""))
                                .append(", ")
                                .append(formatPrice(r.priceStr(), r.price()))
                                .append(", ")
                                .append(r.id())
                                .append("\n");
                    }
                }
            }
        }

        sb.append(SEP_BOT);

        return sb.toString().trim();
    }

    private static String formatKickoffUtc(Long kickoffMillis) {
        if (kickoffMillis == null) return "";
        return UTC_FMT.format(Instant.ofEpochMilli(kickoffMillis));
    }

    private static String formatPrice(String priceStr, Double price) {
        if (priceStr != null && !priceStr.isBlank()) return priceStr;
        if (price == null) return "";
        try {
            return new BigDecimal(price.toString()).stripTrailingZeros().toPlainString();
        } catch (Exception ignored) {
            return price.toString();
        }
    }
}
