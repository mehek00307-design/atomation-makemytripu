import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestDateFormat {
    public static void main(String[] args) {
        // Test the exact same logic as the test
        String travelMonth = "DECEMBER";
        int travelDay = 26;
        
        // Simulate DateUtils.getNextOccurrence
        LocalDate departureDate = LocalDate.of(2025, 12, 26);
        String compactDate = departureDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String isoDate = departureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        System.out.println("Travel Month: " + travelMonth);
        System.out.println("Travel Day: " + travelDay);
        System.out.println("Departure Date: " + departureDate);
        System.out.println("Compact Date: " + compactDate);
        System.out.println("ISO Date: " + isoDate);
        
        // Build URL like the test does
        String resultsUrl = "https://www.ixigo.com/search/result/flight?from=BOM&to=BLR&date=" + compactDate + "&adults=1&children=0&infants=0&class=e&source=Search%20Form";
        System.out.println("Results URL: " + resultsUrl);
    }
}
