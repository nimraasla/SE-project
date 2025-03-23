package application;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Attendee extends Customer
{
	ArrayList<Event> events; 
	public Attendee(String name, String email, String password, String Role, Database db)
	{
		super(name, email, password, Role, db);
		events=new ArrayList<Event>();
	}
	public void cancelRegistration(int ID, String name)
	{
		events=db.getEventsOfaCustomer(ID, db);
		boolean done=false;
		for(Event event:events)
		{
			if(event.getName().equalsIgnoreCase(name))
			{
				db.cancelRegistration(event.getID(), ID);
				done=true;
				break;
			}
		}
		if(done==false)
		{
			System.out.println("Attendee was not registered for this event");
		}
	}
	
	public void giveFeedback(int ID, String comment, int rating)
	{
		
	}
}

