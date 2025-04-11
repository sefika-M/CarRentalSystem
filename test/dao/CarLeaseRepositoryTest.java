package dao;
import dao.*;
import entity.*;
import exception.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CarLeaseRepositoryTest {

    private ICarLeaseRepository repo;

    @Before
    public void setup() {
        Connection conn = DBConnection.getConnection();
        repo = new ICarLeaseRepositoryImpl(conn);
    }

    @Test
    public void testAddCar() throws Exception {
        Vehicle vehicle = new Vehicle("TestMake", "TestModel", 2024, 499.99, "available", 4, 1.8);
        repo.addCar(vehicle);

        List<Vehicle> availableCars = repo.listAvailableCars();
        boolean found = availableCars.stream().anyMatch(v -> v.getMake().equals("TestMake") && v.getModel().equals("TestModel"));
        assertTrue("Vehicle should be added and appear in available cars", found);
    }
   
    @Test
    public void testCreateLease() throws Exception {
        Customer customer = new Customer("Test", "User", "user" + System.nanoTime() + "@mail.com", "9" + (int)(Math.random() * 100000000));
        int customerId = repo.addCustomer(customer);
        Vehicle vehicle = new Vehicle("TestMake", "TestModel", 2025, 999.0, "available", 5, 2.0);
        repo.addCar(vehicle);
        Vehicle insertedVehicle = repo.listAvailableCars().stream()
            .filter(v -> v.getMake().equals("TestMake") && v.getModel().equals("TestModel"))
            .findFirst()
            .orElseThrow(() -> new Exception("Inserted vehicle not found"));
        int vehicleId = insertedVehicle.getVehicleID();
        Date startDate = Date.valueOf("2025-04-10");
        Date endDate = Date.valueOf("2025-05-10");
        Lease lease = repo.createLease(vehicleId, customerId, startDate, endDate);

        assertNotNull(lease);
        assertEquals(customerId, lease.getCustomerID());
        assertEquals(vehicleId, lease.getVehicleID());
    }


    @Test
    public void testFindLeaseById() throws Exception {
        int leaseId = 1; 
        Lease lease = repo.returnCar(leaseId); 
        assertNotNull("Lease should be retrieved", lease);
        assertEquals("Lease ID should match", leaseId, lease.getLeaseID());
    }

    @Test(expected = CustomerNotFoundException.class)
    public void testCustomerNotFoundException() throws Exception {
        repo.findCustomerById(-999); 
    }

    @Test(expected = VehicleNotFoundException.class)
    public void testVehicleNotFoundException() throws Exception {
        repo.findCarById(-10); 
    }

    @Test(expected = LeaseNotFoundException.class)
    public void testLeaseNotFoundException() throws Exception {
        repo.returnCar(-45); 
    }
    
    @After
    public void cleanup() throws SQLException {
        Statement stmt = DBConnection.getConnection().createStatement();
        stmt.executeUpdate("DELETE FROM Customer WHERE email LIKE 'test_%@example.com'");
        stmt.executeUpdate("DELETE FROM Vehicle WHERE make = 'TestMake'");
        stmt.executeUpdate("DELETE FROM Lease WHERE customerID NOT IN (SELECT customerID FROM Customer)");
        stmt.executeUpdate("DELETE FROM Payment WHERE leaseID NOT IN (SELECT leaseID FROM Lease)");
        stmt.close();
    }

}

