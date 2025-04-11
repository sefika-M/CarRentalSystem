package dao;
import dao.*;
import entity.*;
import exception.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import util.DBConnection;
import java.sql.Connection;
import java.sql.Date;
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
    	String uniquePhone = "9" + (int)(Math.random() * 1000000000);
        String uniqueEmail = "test" + uniquePhone + "@mail.com";

        // Add customer and capture the generated ID
        Customer customer = new Customer("JUnit", "Tester", uniqueEmail, uniquePhone);
        int customerId = repo.addCustomer(customer); // ✅ use the returned ID

        // Add vehicle
        Vehicle vehicle = new Vehicle("JUnitMake", "JUnitModel", 2025, 1000.0, "available", 4, 2.0);
        repo.addCar(vehicle);

        // Fetch inserted vehicle
        Vehicle insertedVehicle = repo.listAvailableCars().stream()
            .filter(v -> v.getMake().equals("JUnitMake") && v.getModel().equals("JUnitModel"))
            .findFirst()
            .orElseThrow(() -> new Exception("Inserted vehicle not found"));
        int vehicleId = insertedVehicle.getVehicleID();

        // Create lease
        Date startDate = Date.valueOf("2024-04-10");
        Date endDate = Date.valueOf("2024-05-10");

        Lease lease = repo.createLease(customerId, vehicleId, startDate, endDate);

        assertNotNull("Lease should be created", lease);
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
}

