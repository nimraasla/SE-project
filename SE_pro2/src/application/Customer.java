package application;

import java.util.ArrayList;

public class Customer extends User
{
	private int num_events_registered;
	public Customer(String name, String email, String password, String Role, Database db)
	{
		super(name, email, password, Role, db);
		num_events_registered=0;
	}
	public void registerForEvent(int userID, String eventName)
	{
		boolean eventFound=db.findEvent(eventName);
		if(eventFound)
		{
			boolean alreadyReg=db.findAttendeeInEvent(userID, eventName);
			if(!alreadyReg)
			{
				Event event=db.getNumOfRegistrations(eventName, db);
				if(event!=null)
				{
					if(event.getCurrentRegistrations()<event.getCapacity())
					{
						db.registerForEvent(userID, eventName);
					}
					else
					{
						System.out.println("No seats");
					}
				}
			}
			else
			{
				System.out.println("Attendee has already registered for this event");
			}
		}
		else
		{
			System.out.println("Event not in record to register for");
		}
	}
}
