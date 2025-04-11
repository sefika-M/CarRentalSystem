package util;

public class ValidationUtil {
	public static boolean isValidEmail(String email) {
		return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");
	}

	public static boolean isValidPhone(String phone) {
		return phone != null &&  phone.matches("^\\d{10}$"); 
	}


}
