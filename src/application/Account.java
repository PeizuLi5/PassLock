package application;

public class Account {
	public String getWebsite_name() {
		return website_name;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	private String website_name;
	private String url;
	private String username;
	private String email;
	private String password;
	private String encryptedPassword;
	private String creationDate;
	private String expirationDate;
	
	/**
	 * Constructor that store an account to another account object
	 * 
	 * @param account	- the account that is going to store
	 */
	public Account(Account account) {
		website_name = account.getWebsite_name();
		url = account.getUrl();
		username = account.getUsername();
		email = account.getEmail();
		password = account.getPassword();
		encryptedPassword = account.getEncryptedPassword();
		creationDate = account.getCreationDate();
		expirationDate = account.getExpirationDate();
	}

	/**
	 * Constructor that generate an account
	 * 
	 * @param website_name			- name of the website
	 * @param url					- url of the website
	 * @param username				- user's username on the website
	 * @param email					- user's email on the website
	 * @param password				- user's password on the website
	 * @param encryptedPassword		- user's encrypted password that is going to store in database
	 * @param creationDate			- creation date of the password
	 * @param expirationDate		- expiration date of the password
	 */
	public Account(String website_name, String url, String username, String email, String password, String encryptedPassword, String creationDate,
			String expirationDate) {
		this.website_name = website_name;
		this.url = url;
		this.username = username;
		this.email = email;
		this.password = password;
		this.encryptedPassword = encryptedPassword;
		this.creationDate = creationDate;
		this.expirationDate = expirationDate;
	}
	
	/**
	 * How the account will be displayed
	 */
	@Override
	public String toString() {
		String s = website_name + "\n     Url: " + url + "\n     Username: ";
		String stars = "";
		for(int i = 0; i < password.length(); i++) {
			stars += "*";
		}
		if(username.equals("")) {
			s += email + "\n     Email: " + email + "\n     Password: " + stars + "\n     Creation Date: " + creationDate + "\n     Expiration Date: "
					+ expirationDate;
		}
		else if (email.equals("")) {
			s += username + "\n     Email: " + username + "\n     Password: " + stars + "\n     Creation Date: " + creationDate + "\n     Expiration Date: "
					+ expirationDate;
		}
		else {
			s += username + "\n     Email: " + email + "\n     Password: " + stars + "\n     Creation Date: " + creationDate + "\n     Expiration Date: "
					+ expirationDate;
		}
		return s;
	}
	
}
