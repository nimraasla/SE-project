package application;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Organizer extends User
{
	private ArrayList<Event> events;
	public Organizer(String Name, String Email, String Pass, String Role, Database db)
	{
		super(Name, Email, Pass, Role, db);
		events=new ArrayList<Event>();
		db.addOrganizer(Name, Email, Pass);
	}
	
	public void CreateEvent(String name, String desc, String location, LocalDate date, LocalTime time, int capacity, LocalDate regDeadlineDate, LocalTime regDeadlineTime, String imgUrl)
	{
		boolean eventExists=db.findEvent(name);
		if(!eventExists)
		{
			db.addEvent(name, desc, location, date, time, capacity, regDeadlineDate, regDeadlineTime, imgUrl);
		}
		else
		{
			System.out.println("Event already in database.");
		}
	}

	public boolean addCustomer(String name, String Email, String Password)
	{
		boolean customerExists=db.findCustomer(Email);
		if(!customerExists)
		{
			db.addCustomer(name, Email, Password);
			return true;
		}
		else
		{
			System.out.println("Customer already in database.");
			return false;
		}
	}
	
	public void generateCSV(String name)
	{
		events=db.getAllEvents(db);
		boolean done=false;
		for(Event e:events)
		{
			if(e.getName().equalsIgnoreCase(name))
			{
				e.generateCSV();
				done=true;
			}
		}
		if(done==false)
		{
			System.out.println("Event not in database");
		}
	}
	
	public void cancelEvent(String name)
	{
		events=db.getAllEvents(db);
		boolean done=false;
		for(Event e:events)
		{
			if(e.getName().equalsIgnoreCase(name))
			{
				db.cancelEvent(name);
				done=true;
			}
		}
		if(done==false)
		{
			System.out.println("Event not in database");
		}
	}
}

