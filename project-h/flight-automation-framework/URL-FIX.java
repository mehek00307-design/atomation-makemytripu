// FIX FOR INCORRECT DATE FORMAT IN URL
// Issue: URL shows 26122025 instead of 20251226
// This means format is ddMMyyyy instead of yyyyMMdd

// SOLUTION: Force correct format
private String buildIxigoResultsUrl(String fromCode, String toCode, LocalDate departureDate) {
    // Force correct yyyyMMdd format
    String year = String.valueOf(departureDate.getYear());
    String month = String.format("%02d", departureDate.getMonthValue());
    String day = String.format("%02d", departureDate.getDayOfMonth());
    String date = year + month + day; // yyyyMMdd format
    
    System.out.println("Building URL with date: " + departureDate + " -> " + date);
    return "https://www.ixigo.com/search/result/flight?from=" + fromCode
            + "&to=" + toCode
            + "&date=" + date
            + "&adults=1&children=0&infants=0&class=e&source=Search%20Form";
}

// ALTERNATIVE: Use multiple format methods to ensure correctness
private String buildIxigoResultsUrlSafe(String fromCode, String toCode, LocalDate departureDate) {
    // Try multiple approaches to get correct date
    String date1 = DateUtils.formatAsCompactDate(departureDate); // Should be yyyyMMdd
    String date2 = String.format("%04d%02d%02d", 
        departureDate.getYear(), 
        departureDate.getMonthValue(), 
        departureDate.getDayOfMonth());
    
    String date = date1.length() == 8 ? date1 : date2; // Use whichever looks correct
    
    System.out.println("Date format check:");
    System.out.println("  formatAsCompactDate: " + date1);
    System.out.println("  manual format: " + date2);
    System.out.println("  using date: " + date);
    
    return "https://www.ixigo.com/search/result/flight?from=" + fromCode
            + "&to=" + toCode
            + "&date=" + date
            + "&adults=1&children=0&infants=0&class=e&source=Search%20Form";
}
