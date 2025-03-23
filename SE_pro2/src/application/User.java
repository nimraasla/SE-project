package application;

public class User
{
	private int ID;
	private String Name;
	private String Email;
	private String password;
	private String Role;
	Database db;
	
	public User(String Name, String Email, String password, String Role, Database db)
	{
		this.Name=Name;
		this.Email=Email;
		this.password=password;
		this.Role=Role;
		this.db=db;
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return Name;
	}

	public String getEmail() {
		return Email;
	}

	public String getRole() {
		return Role;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public void setRole(String role) {
		Role = role;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		password = password;
	}
}

