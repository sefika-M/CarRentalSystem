package dao;

import entity.*;
import exception.*;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public interface ICarLeaseRepository {
	
	 void addCar(Vehicle car) throws SQLException;
	    void removeCar(int carID) throws SQLException, VehicleNotFoundException ;
	    List<Vehicle> listAvailableCars() throws SQLException;
	    List<Vehicle> listRentedCars() throws SQLException;
	    Vehicle findCarById(int carID) throws SQLException, VehicleNotFoundException;

	    int addCustomer(Customer customer) throws SQLException;
	    void removeCustomer(int customerID)throws SQLException, CustomerNotFoundException;
	    List<Customer> listCustomers()throws SQLException;
	    Customer findCustomerById(int customerID) throws SQLException, CustomerNotFoundException;

	    Lease createLease(int customerID, int carID, Date startDate, Date endDate) throws SQLException, VehicleNotFoundException, CustomerNotFoundException;
	    Lease returnCar(int leaseID) throws SQLException, LeaseNotFoundException, VehicleNotFoundException;
	    List<Lease> listActiveLeases() throws SQLException;
	    List<Lease> listLeaseHistory() throws SQLException;

	    void recordPayment(Lease lease, double amount) throws SQLException, LeaseNotFoundException;
	
}
