package application;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Database 
{
	private String dbUrl = "jdbc:mysql://localhost:3306/se?useSSL=false&serverTimezone=UTC";
    private String username = "root";
    private String password = "nimra868";
    
    public Database() 
    {
    	/*String query = """
    			DROP TABLE Event;
    			""";
    	int rowsAffected=executeQuery(query);
    	if(rowsAffected!=-1)
    	{
    		System.out.println("Table Event deleted");
    	}
    	else
    	{
    		System.out.println("Error event");
    	}
    	String query1 = """
    			DROP TABLE Organizer;
    			""";
    	rowsAffected=executeQuery(query1);
    	if(rowsAffected!=-1)
    	{
    		System.out.println("Table organizer deleted");
    	}
    	else
    	{
    		System.out.println("Error organizer");
    	}
    	String query2 = """
    			DROP TABLE Attendee;
    			""";
    	rowsAffected=executeQuery(query2);
    	if(rowsAffected!=-1)
    	{
    		System.out.println("Table Attendee deleted");
    	}
    	else
    	{
    		System.out.println("Error attendee");
    	}
    	String query3 = """
    			DROP TABLE Customer;
    			""";
    	rowsAffected=executeQuery(query3);
    	if(rowsAffected!=-1)
    	{
    		System.out.println("Table Customer deleted");
    	}
    	else
    	{
    		System.out.println("Error customer");
    	}*/
    	createCustomerTableIfNotExist();
    	createEventTableIfNotExist();
        createAttendeeTableIfNotExist();
        createOrganizerTableIfNotExist();
    }
    
    
    //Query Execution
    private int executeQuery(String query)
    {
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             Statement stmt = conn.createStatement()) 
        {
            int ra= stmt.executeUpdate(query);
           // System.out.println("Table created/verified successfully.");
            return ra;
        } 
        catch (SQLException e) 
        {
            System.err.println("Error executing query: " + e.getMessage());
            return -1;
        }
    }
    
    
    
    
    
    
    //GET functions
    public ArrayList<Event> getAllEvents(Database db) {
        ArrayList<Event> eventList = new ArrayList<>();
        String query = "SELECT eventID, name, description, location, date, time, capacity, curentRegistartions, " +
                      "registrationDeadlineDate, registrationDeadlineTime, isCancelled, imageUrl FROM Event";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int eventID = rs.getInt("eventID");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String location = rs.getString("location");
                int capacity = rs.getInt("capacity");
                int currentRegistrations = rs.getInt("curentRegistartions");
                String isCancelledStr = rs.getString("isCancelled");
                java.sql.Date sqlDate = rs.getDate("date");
                java.sql.Time sqlTime = rs.getTime("time");
                java.sql.Date sqlDeadlineDate = rs.getDate("registrationDeadlineDate");
                java.sql.Time sqlDeadlineTime = rs.getTime("registrationDeadlineTime");
                String imageUrl = rs.getString("imageUrl");

                LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : null;
                LocalTime time = sqlTime != null ? sqlTime.toLocalTime() : null;
                LocalDate deadlineDate = sqlDeadlineDate != null ? sqlDeadlineDate.toLocalDate() : null;
                LocalTime deadlineTime = sqlDeadlineTime != null ? sqlDeadlineTime.toLocalTime() : null;

                boolean isCancelled = Boolean.parseBoolean(isCancelledStr) || "true".equalsIgnoreCase(isCancelledStr);

                // Create Event object
                Event event = new Event(name, description, date, time, location, capacity, deadlineDate, deadlineTime, imageUrl, db);
                event.setID(eventID);
                event.setCurrentRegistrations(currentRegistrations);
                event.setCancelled(isCancelled);
                event.setImageUrl(imageUrl); // Set the image URL
                eventList.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching events from database: " + e.getMessage());
        }

        return eventList;
    }
    
    public ArrayList<Event> getEventsWithDeadlineTomorrow(int org_ID, Database db) {
        ArrayList<Event> eventList = new ArrayList<>();
        LocalDate tomorrow = LocalDate.now().plusDays(1); // Tomorrow's date
        String query = "SELECT eventID, name, description, location, date, time, capacity, curentRegistartions, " +
                      "registrationDeadlineDate, registrationDeadlineTime, isCancelled, imageUrl " +
                      "FROM Event WHERE registrationDeadlineDate = ? AND isCancelled = 'false'";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, java.sql.Date.valueOf(tomorrow)); // Set tomorrow's date as parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int eventID = rs.getInt("eventID");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String location = rs.getString("location");
                    int capacity = rs.getInt("capacity");
                    int currentRegistrations = rs.getInt("curentRegistartions");
                    String isCancelledStr = rs.getString("isCancelled");
                    java.sql.Date sqlDate = rs.getDate("date");
                    java.sql.Time sqlTime = rs.getTime("time");
                    java.sql.Date sqlDeadlineDate = rs.getDate("registrationDeadlineDate");
                    java.sql.Time sqlDeadlineTime = rs.getTime("registrationDeadlineTime");
                    String imageUrl = rs.getString("imageUrl");
                    
                    LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : null;
                    LocalTime time = sqlTime != null ? sqlTime.toLocalTime() : null;
                    LocalDate deadlineDate = sqlDeadlineDate != null ? sqlDeadlineDate.toLocalDate() : null;
                    LocalTime deadlineTime = sqlDeadlineTime != null ? sqlDeadlineTime.toLocalTime() : null;

                    boolean isCancelled = Boolean.parseBoolean(isCancelledStr) || "true".equalsIgnoreCase(isCancelledStr);

                    // Create Event object
                    Event event = new Event(name, description, date, time, location, capacity, deadlineDate, deadlineTime, imageUrl, db);
                    event.setID(eventID);
                    event.setCurrentRegistrations(currentRegistrations);
                    event.setCancelled(isCancelled);
                    eventList.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching events with deadline tomorrow: " + e.getMessage());
        }

        return eventList;
    }
    
    public ArrayList<Event> getOngoingEvents(int org_ID, Database db) {
        ArrayList<Event> eventList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        String query = "SELECT eventID, name, description, location, date, time, capacity, curentRegistartions, " +
                "registrationDeadlineDate, registrationDeadlineTime, isCancelled, imageUrl " +
                "FROM Event WHERE date = ? AND isCancelled = false;";
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
        	// Convert LocalDate to a string in YYYY-MM-DD format
            String dateStr = today.toString(); // e.g., "2025-03-22"
            pstmt.setString(1, dateStr);
            System.out.println("Querying with date: " + dateStr);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No ongoing events found for " + today);
                }
                while (rs.next()) {
                    int eventID = rs.getInt("eventID");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String location = rs.getString("location");
                    int capacity = rs.getInt("capacity");
                    int currentRegistrations = rs.getInt("curentRegistartions"); // Fixed typo
                    String isCancelledStr = rs.getString("isCancelled");
                    java.sql.Date sqlDate = rs.getDate("date");
                    java.sql.Time sqlTime = rs.getTime("time");
                    java.sql.Date sqlDeadlineDate = rs.getDate("registrationDeadlineDate");
                    java.sql.Time sqlDeadlineTime = rs.getTime("registrationDeadlineTime");
                    String imageUrl = rs.getString("imageUrl");
                    
                    LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : null;
                    LocalTime time = sqlTime != null ? sqlTime.toLocalTime() : null;
                    LocalDate deadlineDate = sqlDeadlineDate != null ? sqlDeadlineDate.toLocalDate() : null;
                    LocalTime deadlineTime = sqlDeadlineTime != null ? sqlDeadlineTime.toLocalTime() : null;

                    boolean isCancelled = Boolean.parseBoolean(isCancelledStr) || "true".equalsIgnoreCase(isCancelledStr);

                    // Create Event object
                    Event event = new Event(name, description, date, time, location, capacity, deadlineDate, deadlineTime, imageUrl, db);
                    event.setID(eventID);
                    event.setCurrentRegistrations(currentRegistrations);
                    event.setCancelled(isCancelled);
                    eventList.add(event);
                }
                System.out.println("Events retrieved: " + eventList.size());
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ongoing events: " + e.getMessage());
        }

        return eventList;
    }
    
    public ArrayList<Event> getEventsThisWeekPast(int org_ID, Database db) {
        ArrayList<Event> eventList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)); // Start of the week (Monday)
        LocalDate yesterday = today.minusDays(1); // Yesterday

        String query = "SELECT eventID, name, description, location, date, time, capacity, curentRegistartions, " +
                      "registrationDeadlineDate, registrationDeadlineTime, isCancelled, imageUrl " +
                      "FROM Event WHERE date BETWEEN ? AND ? AND isCancelled = 'false'";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, java.sql.Date.valueOf(startOfWeek)); // Start of the week
            pstmt.setDate(2, java.sql.Date.valueOf(yesterday)); // Yesterday

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int eventID = rs.getInt("eventID");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String location = rs.getString("location");
                    int capacity = rs.getInt("capacity");
                    int currentRegistrations = rs.getInt("curentRegistartions");
                    String isCancelledStr = rs.getString("isCancelled");
                    java.sql.Date sqlDate = rs.getDate("date");
                    java.sql.Time sqlTime = rs.getTime("time");
                    java.sql.Date sqlDeadlineDate = rs.getDate("registrationDeadlineDate");
                    java.sql.Time sqlDeadlineTime = rs.getTime("registrationDeadlineTime");
                    String imageUrl = rs.getString("imageUrl");

                    LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : null;
                    LocalTime time = sqlTime != null ? sqlTime.toLocalTime() : null;
                    LocalDate deadlineDate = sqlDeadlineDate != null ? sqlDeadlineDate.toLocalDate() : null;
                    LocalTime deadlineTime = sqlDeadlineTime != null ? sqlDeadlineTime.toLocalTime() : null;

                    boolean isCancelled = Boolean.parseBoolean(isCancelledStr) || "true".equalsIgnoreCase(isCancelledStr);

                    // Create Event object
                    Event event = new Event(name, description, date, time, location, capacity, deadlineDate, deadlineTime, imageUrl, db);
                    event.setID(eventID);
                    event.setCurrentRegistrations(currentRegistrations);
                    event.setCancelled(isCancelled);
                    eventList.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching past events this week: " + e.getMessage());
        }

        return eventList;
    }
    
    public ArrayList<Event> getEventsThisWeekUpcoming(int org_ID, Database db) {
        ArrayList<Event> eventList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1); // Tomorrow
        LocalDate endOfWeek = today.with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY)); // End of the week (Sunday)

        String query = "SELECT eventID, name, description, location, date, time, capacity, curentRegistartions, " +
                      "registrationDeadlineDate, registrationDeadlineTime, isCancelled, imageUrl " +
                      "FROM Event WHERE date BETWEEN ? AND ? AND isCancelled = 'false'";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, java.sql.Date.valueOf(tomorrow)); // Tomorrow
            pstmt.setDate(2, java.sql.Date.valueOf(endOfWeek)); // End of the week

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int eventID = rs.getInt("eventID");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String location = rs.getString("location");
                    int capacity = rs.getInt("capacity");
                    int currentRegistrations = rs.getInt("curentRegistartions");
                    String isCancelledStr = rs.getString("isCancelled");
                    java.sql.Date sqlDate = rs.getDate("date");
                    java.sql.Time sqlTime = rs.getTime("time");
                    java.sql.Date sqlDeadlineDate = rs.getDate("registrationDeadlineDate");
                    java.sql.Time sqlDeadlineTime = rs.getTime("registrationDeadlineTime");
                    String imageUrl = rs.getString("imageUrl");
                    
                    LocalDate date = sqlDate != null ? sqlDate.toLocalDate() : null;
                    LocalTime time = sqlTime != null ? sqlTime.toLocalTime() : null;
                    LocalDate deadlineDate = sqlDeadlineDate != null ? sqlDeadlineDate.toLocalDate() : null;
                    LocalTime deadlineTime = sqlDeadlineTime != null ? sqlDeadlineTime.toLocalTime() : null;

                    boolean isCancelled = Boolean.parseBoolean(isCancelledStr) || "true".equalsIgnoreCase(isCancelledStr);

                    // Create Event object
                    Event event = new Event(name, description, date, time, location, capacity, deadlineDate, deadlineTime, imageUrl, db);
                    event.setID(eventID);
                    event.setCurrentRegistrations(currentRegistrations);
                    event.setCancelled(isCancelled);
                    eventList.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching upcoming events this week: " + e.getMessage());
        }

        return eventList;
    }
    
    public ArrayList<Attendee> getRegisteredAttendees(Database db, int eventID)
    {
    	ArrayList<Attendee> attendeeList = new ArrayList<>();
        String query = "SELECT c.userID, c.name, c.email, c.password " +
                      "FROM Attendee a " +
                      "INNER JOIN Customer c ON a.userID = c.userID " +
                      "WHERE a.eventID = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the eventID parameter
            pstmt.setInt(1, eventID);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int userID = rs.getInt("userID");
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String password = rs.getString("password");

                    // Create Attendee object
                    // Assuming Attendee constructor: (name, email, password, role, db)
                    Attendee attendee = new Attendee(name, email, password, "Attendee", db);
                    attendee.setID(userID); // Assuming Attendee has a setUserID method
                    attendeeList.add(attendee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching registered attendees for eventID " + eventID + ": " + e.getMessage());
        }

        return attendeeList;
    }
    
    public ArrayList<Event> getEventsOfaCustomer(int userID, Database db) {
        ArrayList<Event> eventList = new ArrayList<>();
        String query = "SELECT a.eventID, e.name, e.description, e.location, e.date, e.time, " +
                      "e.capacity, e.curentRegistartions, e.registrationDeadlineDate, " +
                      "e.registrationDeadlineTime, e.isCancelled, e.imageUrl " +
                      "FROM Attendee as a " +
                      "INNER JOIN Event as e ON e.eventID = a.eventID " +
                      "WHERE a.userID = ?;";  // Changed to a.userID since we're querying by customer
            
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the userID parameter
            pstmt.setInt(1, userID);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int eventID = rs.getInt("a.eventID");
                    String name = rs.getString("e.name");
                    String description = rs.getString("e.description");
                    String location = rs.getString("e.location");
                    java.sql.Date sqlDate = rs.getDate("e.date");
                    LocalDate date = sqlDate.toLocalDate();
                    java.sql.Time sqlTime = rs.getTime("e.time");
                    LocalTime time = sqlTime.toLocalTime();
                    int capacity = rs.getInt("e.capacity");
                    int currentRegistrations = rs.getInt("e.curentRegistartions");
                    java.sql.Date sqlDeadlineDate = rs.getDate("e.registrationDeadlineDate");
                    LocalDate registrationDeadlineDate = sqlDeadlineDate.toLocalDate();
                    java.sql.Time sqlDeadlineTime = rs.getTime("e.registrationDeadlineTime");
                    LocalTime registrationDeadlineTime = sqlDeadlineTime.toLocalTime();
                    boolean isCancelled = rs.getBoolean("e.isCancelled");
                    String imageUrl = rs.getString("imageUrl");

                    // Create Event object with all parameters
                    Event event = new Event(name,description,date,time,location,capacity,registrationDeadlineDate,registrationDeadlineTime,imageUrl,db);
                    event.setID(eventID);
                    event.setCurrentRegistrations(currentRegistrations);
                    event.setCancelled(isCancelled);
                    eventList.add(event);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching events for userID " + userID + ": " + e.getMessage());
        }

        return eventList;
    }
    
    public Event getNumOfRegistrations(String eventName, Database db)
    {
        String query = "SELECT curentRegistartions, capacity FROM Event WHERE name=?;";
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the eventID parameter
            pstmt.setString(1, eventName);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int currentRegistrations = rs.getInt("curentRegistartions");
                    int cap=rs.getInt("capacity");
                    Event event=new Event(eventName, null, null, null, null, cap, null, null, null, db);
                    return event;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching current registrations for eventName " + eventName + ": " + e.getMessage());
        }

        return null;
    }
    
    public User getOrganizer(String email, String pass, Database db)
    {
    	String query = "SELECT userID, name FROM Organizer WHERE email=? AND password=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, email);
    		pstmt.setString(2, pass);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int userID = rs.getInt("userID");
    			String name=rs.getString("name");
    			User or=new Organizer(name, email, pass, "Organizer", db);
    			or.setID(userID);
    			return or;
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating organizer: " + e.getMessage());
    	}
    	return null;
    }
    
    public User getCustomer(String email, String pass, Database db)
    {
    	String query = "SELECT userID, name FROM Customer WHERE email=? AND password=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, email);
    		pstmt.setString(2, pass);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int userID = rs.getInt("userID");
    			String name=rs.getString("name");
    			User customer=new Customer(name, email, pass, "Customer", db);
    			customer.setID(userID);
    			return customer;
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating organizer: " + e.getMessage());
    	}
    	return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    //Validation queries
    public boolean findEvent(String name)
    {
    	String query = "SELECT COUNT(*) AS eventCount FROM Event WHERE name = ?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, name);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int eventCount = rs.getInt("eventCount");
    			return eventCount > 0;
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating event: " + e.getMessage());
    	}
    	return false;
    }
    
    public boolean findOrganizer(String email, String pass)
    {
    	String query = "SELECT COUNT(*) AS organizerCount FROM Organizer WHERE email=? AND password=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, email);
    		pstmt.setString(2, pass);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int organizertCount = rs.getInt("organizerCount");
    			return organizertCount > 0;
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating organizer: " + e.getMessage());
    	}
    	return false;
    }
    
    public boolean findCustomer(String email)
    {
    	String query = "SELECT COUNT(*) AS customerCount FROM Customer WHERE email=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, email);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int customerCount = rs.getInt("customerCount");
    			return customerCount > 0;
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating customer: " + e.getMessage());
    	}
    	return false;
    }
    
    public boolean findAttendeeInEvent(int userID, String name) {
    	String query = "SELECT COUNT(*) AS coun FROM Attendee AS a " +
                "INNER JOIN Event AS e ON e.eventID = a.eventID " +
                "WHERE a.userID = ? AND e.name = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the query parameters
            pstmt.setInt(1, userID);
            pstmt.setString(2, name);

            // Execute the query
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int coun = rs.getInt("coun");
                return coun > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error validating attendee with userID " + userID + " for event " + name + ": " + e.getMessage());
        }
        return false;
    }
    
    public boolean findCustomerPassword(String pass)
    {
    	String query = "SELECT COUNT(*) as countCustomer FROM Customer WHERE password=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, pass);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int coun = rs.getInt("countCustomer");
    			return coun > 0;
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating Customer: " + e.getMessage());
    	}
    	return false;
    }
    
    public boolean findCustomerUserID(int userID)
    {
    	String query = "SELECT COUNT(*) as countCustomer FROM Customer WHERE userID=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setInt(1, userID);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int coun = rs.getInt("countCustomer");
    			return coun > 0;
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating Customer: " + e.getMessage());
    	}
    	return false;
    }
    
    
    
    
    
    
    //inserting queries
    public void addEvent(String name, String desc, String location, LocalDate date, LocalTime time, int capacity, LocalDate regDeadlineDate, LocalTime regDeadlineTime, String imageUrl) {
        String query = "INSERT INTO Event (name, description, location, date, time, capacity, " +
                      "curentRegistartions, registrationDeadlineDate, registrationDeadlineTime, isCancelled, imageUrl) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
        	conn.setAutoCommit(false);
            pstmt.setString(1, name);
            if (desc == null) {
                pstmt.setNull(2, Types.VARCHAR);
            } else {
                pstmt.setString(2, desc);
            }
            pstmt.setString(3, location);
            pstmt.setDate(4, java.sql.Date.valueOf(date));
            pstmt.setTime(5, java.sql.Time.valueOf(time));
            pstmt.setInt(6, capacity);
            pstmt.setInt(7, 0);
            pstmt.setDate(8, java.sql.Date.valueOf(regDeadlineDate));
            pstmt.setTime(9, java.sql.Time.valueOf(regDeadlineTime));
            pstmt.setBoolean(10, false);
            if (imageUrl == null) {
                pstmt.setNull(11, Types.VARCHAR);
            } else {
                pstmt.setString(11, imageUrl);
            }

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Event added successfully");
            }

        } catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
        }
    }
    
    public void addOrganizer(String name, String email, String pass)
    {
    	String query="""
    			INSERT INTO Organizer(name, email, password) VALUES (?,?,?);
    			""";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    			 PreparedStatement pstmt = conn.prepareStatement(query)) {

    			pstmt.setString(1, name);
    			pstmt.setString(2, email);
    			pstmt.setString(3, pass);
    			
    			int rowsAffected = pstmt.executeUpdate();
    			if (rowsAffected > 0) {
    			    System.out.println("Organizer added successfully");
    			}
    			
    			} catch (SQLException e) {
    			System.err.println("Error adding organizer: " + e.getMessage());
    			}
    }
    
    public void addCustomer(String name, String email, String pass)
    {
    	String query="""
    			INSERT INTO Customer(name, email, password) VALUES (?,?,?);
    			""";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    			 PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		pstmt.setString(1, name);
    		pstmt.setString(2, email);
    		pstmt.setString(3, pass);
    		int rowsAffected = pstmt.executeUpdate();
    		if (rowsAffected > 0)
    		{
    			System.out.println("Customer added successfully");
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error adding customer: " + e.getMessage());
    	}
    }
    
    public void registerForEvent(int userID, String name)
    {
    	String query = "SELECT eventID FROM Event WHERE name=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, name);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int eventID = rs.getInt("eventID");
    			query="""
    	    			INSERT INTO Attendee(userID, eventID) VALUES (?,?);
    	    			""";
    	    	try (Connection conn1 = DriverManager.getConnection(dbUrl, username, password);
    	    			 PreparedStatement pstmt1 = conn1.prepareStatement(query)) 
    	    	{
    	    		pstmt1.setInt(1, userID);
    	    		pstmt1.setInt(2, eventID);
    	    		int rowsAffected = pstmt1.executeUpdate();
    	    		if (rowsAffected > 0)
    	    		{
    	    			System.out.println("Attendee added successfully");
    	    			query="UPDATE Event SET curentRegistartions = curentRegistartions + 1 WHERE eventID = ?;";
    	    			try (Connection conn2 = DriverManager.getConnection(dbUrl, username, password);
    	    			    	PreparedStatement pstmt2 = conn2.prepareStatement(query)) 
    	    			{
    	    				pstmt2.setInt(1, eventID);
    	    				rowsAffected = pstmt2.executeUpdate();
    	    				if(rowsAffected>0)
    	    				{
    	    					
    	    				}
    	    				else
    	    					System.out.println("CurrentRegistrations didn't increase");
    	    			}
    	    			catch (SQLException e) 
    	    	    	{
    	    	    		System.err.println("Error increasing the current registrations " + e.getMessage());
    	    	    	}
    	    		}
    	    	} 
    	    	catch (SQLException e) 
    	    	{
    	    		System.err.println("Error adding attendee: " + e.getMessage());
    	    	}
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating attendee: " + e.getMessage());
    	}
    }
    
    public void cancelRegistration(int eventID, int userID)
    {
    	String query = "DELETE FROM Attendee WHERE eventID=? AND userID=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setInt(1, eventID);
    		pstmt.setInt(2, userID);
    		
    		// Execute the query
    		int rowsAffected = pstmt.executeUpdate();
    	   if (rowsAffected > 0)
    	    {
    	    	System.out.println("Attendee unregistered successfully");
    	    	query="UPDATE Event SET curentRegistartions = curentRegistartions - 1 WHERE eventID = ?;";
    			try (Connection conn2 = DriverManager.getConnection(dbUrl, username, password);
    			    	PreparedStatement pstmt2 = conn2.prepareStatement(query)) 
    			{
    				pstmt2.setInt(1, eventID);
    				rowsAffected = pstmt2.executeUpdate();
    				if(rowsAffected>0)
    				{
    					
    				}
    				else
    					System.out.println("CurrentRegistrations didn't decrease");
    			}
    			catch (SQLException e) 
    	    	{
    	    		System.err.println("Error decreasing the current registrations " + e.getMessage());
    	    	}
    	    }
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error deleteing attendee: " + e.getMessage());
    	}
    }
    
    public void cancelEvent(String name)
    {
    	String query = "SELECT eventID FROM Event WHERE name=?";
    	try (Connection conn = DriverManager.getConnection(dbUrl, username, password);
    	PreparedStatement pstmt = conn.prepareStatement(query)) 
    	{
    		// Set the query parameters
    		pstmt.setString(1, name);

    		// Execute the query
    		ResultSet rs = pstmt.executeQuery();
    		if (rs.next())
    		{
    			int eventID = rs.getInt("eventID");
    			query="""
    	    			DELETE FROM Attendee where eventID=?;
    	    			""";
    	    	try (Connection conn1 = DriverManager.getConnection(dbUrl, username, password);
    	    			 PreparedStatement pstmt1 = conn1.prepareStatement(query)) 
    	    	{
    	    		pstmt1.setInt(1, eventID);
    	    		int rowsAffected = pstmt1.executeUpdate();
    	    		query="DELETE FROM Event WHERE eventID=?;";
    	    		try (Connection conn2 = DriverManager.getConnection(dbUrl, username, password);
    	    		    	PreparedStatement pstmt2 = conn2.prepareStatement(query)) 
    	    		{
    	    			pstmt2.setInt(1, eventID);
    	    			rowsAffected = pstmt2.executeUpdate();
    	    			if(rowsAffected>0)
    	    			{
    	    				System.out.println("Event cancelled");
    	    			}
    	    		}
    	    		catch (SQLException e) 
    	    	    {
    	    	    	System.err.println("Error cancelling event " + e.getMessage());
    	    	    }
    	    	}
    	    	catch (SQLException e) 
    	    	{
    	    		System.err.println("Error deleting attendee before cancelling event: " + e.getMessage());
    	    	}
    		}
    	} 
    	catch (SQLException e) 
    	{
    		System.err.println("Error validating attendee: " + e.getMessage());
    	}
    }
    
    
    
    
    
    
    
    
    //TABLES
    public void createCustomerTableIfNotExist()
    {
    	String query = """
                CREATE TABLE IF NOT EXISTS Customer (
                    userID INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL
                );
                """;
        executeQuery(query);
    }
    
    public void createAttendeeTableIfNotExist()
    {
    	String query = """
                CREATE TABLE IF NOT EXISTS Attendee (
                    userID INT NOT NULL,
                    eventID INT NOT NULL,
                    FOREIGN KEY (userID) REFERENCES Customer(userID),
                    FOREIGN KEY (eventID) REFERENCES Event(eventID)
                );
                """;
        executeQuery(query);
    }
    
    public void createOrganizerTableIfNotExist()
    {
    	String query = """
                CREATE TABLE IF NOT EXISTS Organizer (
                    userID INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL
                );
                """;
        executeQuery(query);
    }
    
    public void createEventTableIfNotExist()
    {
    	String query = """
                CREATE TABLE IF NOT EXISTS Event (
                    eventID INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    description VARCHAR(100),
                    location VARCHAR(100) UNIQUE NOT NULL,
                    date Date NOT NULL,
                    time Time NOT NULL,
                    capacity INT NOT NULL,
                    curentRegistartions INT NOT NULL DEFAULT 0,
                    registrationDeadlineDate DATE NOT NULL,
                    registrationDeadlineTime TIME NOT NULL,
                    isCancelled VARCHAR(10) NOT NULL,
                    imageUrl TEXT
                );
                """;
        executeQuery(query);
    }
}
