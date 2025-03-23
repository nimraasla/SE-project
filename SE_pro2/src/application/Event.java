package application;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event 
{
	private int ID;
	private String Name;
	private String Desc;
	private LocalDate date;
	private LocalTime time;
	private String Location;
	private int capacity;
	private int currentRegistrations;
	private LocalDate registrationDeadlineDate;
	private LocalTime registrationDeadlineTime;
	private boolean isCancelled;
	
	private String imageUrl;
	
	ArrayList<Attendee> attendees;
	Database db;
	
	public Event(String name, String desc, LocalDate date, LocalTime time, String loc, int cap, LocalDate deadlineDate, LocalTime deadlineTime, String imgUrl, Database db)
	{
		this.Name=name;
		this.Desc=desc;
		this.date=date;
		this.time=time;
		this.Location=loc;
		this.capacity=cap;
		this.currentRegistrations=0;
		this.registrationDeadlineDate=deadlineDate;
		this.registrationDeadlineTime=deadlineTime;
		isCancelled=false;
		this.imageUrl=imgUrl;
		
		attendees=new ArrayList<Attendee>();
		this.db=db;
	}
	
	public void generateCSV() {
        String fileName = "Event_" + ID + "_Attendees.csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("Attendee ID, Name, Email\n");

            attendees = db.getRegisteredAttendees(db, ID);
            for (Attendee attendee : attendees) {
                writer.append(Integer.toString(attendee.getID())).append(", ")
                      .append(attendee.getName()).append(", ")
                      .append(attendee.getEmail()).append("\n");
            }

            System.out.println("CSV report generated successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public int getID() {
		return ID;
	}

	public String getName() {
		return Name;
	}

	public String getDesc() {
		return Desc;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getTime() {
		return time;
	}

	public String getLocation() {
		return Location;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getCurrentRegistrations() {
		return currentRegistrations;
	}

	public LocalDate getRegistrationDeadlineDate() {
		return registrationDeadlineDate;
	}

	public LocalTime getRegistrationDeadlineTime() {
		return registrationDeadlineTime;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setCurrentRegistrations(int currentRegistrations) {
		this.currentRegistrations = currentRegistrations;
	}

	public void setRegistrationDeadlineDate(LocalDate registrationDeadlineDate) {
		this.registrationDeadlineDate = registrationDeadlineDate;
	}

	public void setRegistrationDeadlineTime(LocalTime registrationDeadlineTime) {
		this.registrationDeadlineTime = registrationDeadlineTime;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public String getImageUrl() 
	{
		if(imageUrl!=null)
		{
			return imageUrl;
		}
		return null;
	}

	public void setImageUrl(String imageUrl2) 
	{
		imageUrl=imageUrl2;
	}
}
