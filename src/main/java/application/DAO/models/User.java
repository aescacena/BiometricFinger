package application.DAO.models;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.*;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "USER")
public class User implements Serializable{

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private int userId;

	@Column(name = "USERNAME")
	private String username;
	
	@Column(name="FINGER", length=100000)
	private byte[] image;

	public User() {

	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
}
