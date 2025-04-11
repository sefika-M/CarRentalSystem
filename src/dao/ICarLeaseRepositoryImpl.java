package dao;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import entity.*;
import util.*;
import exception.*;

public class ICarLeaseRepositoryImpl implements ICarLeaseRepository {
	
	private Connection conn; 
	
	public ICarLeaseRepositoryImpl(Connection conn) {
        this.conn = conn;
	}
    public void addCar(Vehicle car) throws SQLException {
        String sql = "insert into Vehicle (make, model, year, dailyRate, status, passengerCapacity, engineCapacity) values(?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, car.getMake());
        ps.setString(2, car.getModel());
        ps.setInt(3, car.getYear());
        ps.setDouble(4, car.getDailyRate());
        ps.setString(5, car.getStatus());
        ps.setInt(6, car.getPassengerCapacity());
        ps.setDouble(7, car.getEngineCapacity());
        ps.executeUpdate();
    }


    public void removeCar(int carID) throws SQLException, VehicleNotFoundException {
        String sql = "delete from Vehicle where vehicleID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, carID);
        int rows = ps.executeUpdate();
        if (rows == 0) throw new VehicleNotFoundException("Car ID " + carID + " is not found.");
    }

    public List<Vehicle> listAvailableCars() throws SQLException {
        List<Vehicle> cars = new ArrayList<>();
        String sql = "select * from Vehicle where status = 'available'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            cars.add(new Vehicle(rs.getInt("vehicleID"), rs.getString("make"), rs.getString("model"), rs.getInt("year"),
                    rs.getDouble("dailyRate"), rs.getString("status"), rs.getInt("passengerCapacity"), rs.getDouble("engineCapacity")));
        }
        return cars;
    }

    public List<Vehicle> listRentedCars() throws SQLException {
        List<Vehicle> cars = new ArrayList<>();
        String sql = "select * from Vehicle where status = 'notAvailable'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            cars.add(new Vehicle(rs.getInt("vehicleID"), rs.getString("make"), rs.getString("model"), rs.getInt("year"),
                    rs.getDouble("dailyRate"), rs.getString("status"), rs.getInt("passengerCapacity"), rs.getDouble("engineCapacity")));
        }
        return cars;
    }

    public Vehicle findCarById(int carID) throws SQLException, VehicleNotFoundException {
        String sql = "select * from Vehicle where vehicleID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, carID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Vehicle(rs.getInt("vehicleID"), rs.getString("make"), rs.getString("model"), rs.getInt("year"),
                    rs.getDouble("dailyRate"), rs.getString("status"), rs.getInt("passengerCapacity"), rs.getDouble("engineCapacity"));
        } else {
            throw new VehicleNotFoundException("Vehicle ID " + carID + " is not found.");
        }
    }

    
    
    public int addCustomer(Customer customer) throws SQLException {
        String sql = "insert into Customer (firstName, lastName, email, phoneNumber) values(?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, customer.getFirstName());
        ps.setString(2, customer.getLastName());
        ps.setString(3, customer.getEmail());
        ps.setString(4, customer.getPhoneNumber());
        ps.executeUpdate();
        
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1); 
        } else {
            throw new SQLException("Customer insertion failed, no ID obtained.");
        }
    }

    public void removeCustomer(int customerID) throws SQLException, CustomerNotFoundException {
        String sql = "delete from Customer where customerID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, customerID);
        int rows = ps.executeUpdate();
        if (rows == 0) throw new CustomerNotFoundException("Customer ID " + customerID + " not found.");
    }

    public List<Customer> listCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from Customer");
        while (rs.next()) {
            customers.add(new Customer(rs.getInt("customerID"), rs.getString("firstName"),
                    rs.getString("lastName"), rs.getString("email"), rs.getString("phoneNumber")));
        }
        return customers;
    }

    public Customer findCustomerById(int customerID) throws SQLException, CustomerNotFoundException {
        String sql = "select * from Customer where customerID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, customerID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Customer(rs.getInt("customerID"), rs.getString("firstName"), rs.getString("lastName"),
                    rs.getString("email"), rs.getString("phoneNumber"));
        } else {
            throw new CustomerNotFoundException("Customer ID " + customerID + " not found.");
        }
    }

    
    public Lease createLease(int carID, int customerID, Date startDate, Date endDate) throws SQLException {
        long timeDiff = endDate.getTime() - startDate.getTime();
    	long dayDiff = timeDiff / (1000 * 60 * 60 * 24);
    	String type = (dayDiff >= 30) ? "Monthly" : "Daily";       
    	String sql = "insert into Lease (vehicleID, customerID, startDate, endDate, type) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, carID);
        ps.setInt(2, customerID);
        ps.setDate(3, new java.sql.Date(startDate.getTime()));
        ps.setDate(4, new java.sql.Date(endDate.getTime()));
        ps.setString(5, type);
        ps.executeUpdate();
        PreparedStatement updateCar = conn.prepareStatement("update Vehicle set status = 'notAvailable' where vehicleID = ?");
        updateCar.setInt(1, carID);
        updateCar.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()) {
        return new Lease(rs.getInt(1), carID, customerID, startDate, endDate, type);
    } else {
        throw new SQLException("Lease ID could not be retrieved.");
    }
    }

    public Lease returnCar(int leaseID) throws SQLException, LeaseNotFoundException , VehicleNotFoundException{
        Lease lease = findLease(leaseID);
        String sql = "update Vehicle set status='available' where vehicleID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, lease.getVehicleID());
        ps.executeUpdate();
        return lease;
    }

    public Lease findLease(int leaseID) throws SQLException, LeaseNotFoundException {
        String sql = "select * from Lease where leaseID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, leaseID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Lease(rs.getInt("leaseID"), rs.getInt("vehicleID"), rs.getInt("customerID"),
                    rs.getDate("startDate"), rs.getDate("endDate"), rs.getString("type"));
        } else throw new LeaseNotFoundException("Lease ID " + leaseID + " not found.");
    }

    public List<Lease> listActiveLeases() throws SQLException {
        List<Lease> leases = new ArrayList<>();
        String sql = "select * from Lease where endDate >= CURDATE()";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            leases.add(new Lease(rs.getInt("leaseID"), rs.getInt("vehicleID"), rs.getInt("customerID"),
                    rs.getDate("startDate"), rs.getDate("endDate"), rs.getString("type")));
        }
        return leases;
    }

    public List<Lease> listLeaseHistory() throws SQLException {
        List<Lease> leases = new ArrayList<>();
        String sql = "select * from Lease";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            leases.add(new Lease(rs.getInt("leaseID"), rs.getInt("vehicleID"), rs.getInt("customerID"),
                    rs.getDate("startDate"), rs.getDate("endDate"), rs.getString("type")));
        }
        return leases;
    }

    
    public void recordPayment(Lease lease, double amount) throws SQLException, LeaseNotFoundException {
    	Lease existingLease = findLease(lease.getLeaseID());
    	 if (amount <= 0) {
    	        throw new SQLException("Amount must be greater than 0.");
    	    }
        String sql = "insert into Payment (leaseID, paymentDate, amount) values (?, CURDATE(), ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, lease.getLeaseID());
        ps.setDouble(2, amount);
        ps.executeUpdate();
    }
}




	
	   