package main;

import dao.*;
import entity.*;
import exception.*;
import util.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Connection conn = null;
        ICarLeaseRepository repo = null;
        
        try{
            conn = DBConnection.getConnection();
            repo = new ICarLeaseRepositoryImpl(conn);

            while (true) {
                System.out.println("\n---- Car Rental System ----");
                System.out.println("1. Add new Customer");
                System.out.println("2. Remove a Customer");
                System.out.println("3. List All Customer Details");
                System.out.println("4. Get Customer Info by ID");


                System.out.println("5. Add new Car");
                System.out.println("6. List Available Cars");
                System.out.println("7. List Rented Cars");
                System.out.println("8. Remove a Car");
                System.out.println("9. Get Car Info by ID");

                System.out.println("10. Create new Lease");
                System.out.println("11. Return a Car");
                System.out.println("12. List all Active Leases");
                System.out.println("13. Lease History");

                System.out.println("14. Record Payment");
                System.out.println("15. Exit");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();
                sc.nextLine();
                
                switch (choice) {
                case 1:
                	try {
                    System.out.print("Enter your First Name: ");
                    String firstName = sc.nextLine();
                    System.out.print("Enter your Last Name: ");
                    String lastName = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    if (!ValidationUtil.isValidEmail(email)) {
                        System.out.println("Invalid email format.");
                        break;
                    }
                    System.out.print("Enter Phone Number (10 digits): ");
                    String phoneNumber = sc.nextLine();
                    if (!ValidationUtil.isValidPhone(phoneNumber)) {
                        System.out.println("Invalid phone number. Must be 10 digits.");
                        break;
                    }
                    int customerId = repo.addCustomer(new Customer(firstName, lastName, email, phoneNumber));
                    System.out.println("Customer added successfully.");
                	} catch (SQLException e) {
                        if (e.getMessage().contains("Duplicate")) {
                            System.out.println(" Error: Email or phone number already exists.");
                        } else {
                            System.out.println(" SQL Error while adding customer: " + e.getMessage());
                        }
                    } catch (Exception e) {
                        System.out.println(" Unexpected error: " + e.getMessage());
                    }
                    break;
                    
                case 2:
                	try {
                    System.out.print("Enter Customer ID to remove: ");
                    repo.removeCustomer(sc.nextInt());
                    System.out.println("Customer removed successfully.");
                	} catch (CustomerNotFoundException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                            System.out.println("SQL Error while removing customer: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                    }
                    break;
                    
                case 3:
                	try {
                        List<Customer> customers = repo.listCustomers();
                        if (customers == null || customers.isEmpty()) {
                            System.out.println("️ No customers found in the database.");
                        } else {
                            System.out.println("Customer List:");
                            for (Customer c : customers) {
                                System.out.println(c);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println(" SQL Error while fetching customers: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                    }
                    break;
                    
                case 4:
                	try {
                    System.out.print("Enter Customer ID: ");
                    int findCustId = sc.nextInt();
                    Customer cust = repo.findCustomerById(findCustId);
                    System.out.println("Customer found:\n" + cust);
                	} catch (CustomerNotFoundException e) {
                        System.out.println( e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("SQL Error while fetching customer: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println(" Unexpected error: " + e.getMessage());
                    }
                    break;
                    
                case 5:
                	try {
                    System.out.print("Enter Make: ");
                    String make = sc.nextLine();
                    System.out.print("Enter Model: ");
                    String model = sc.nextLine();
                    System.out.print("Enter Year of Release: ");
                    int year = sc.nextInt();
                    System.out.print("Enter Rate: ");
                    double rate = sc.nextDouble();
                    sc.nextLine(); 
                    System.out.print("Enter Vehicle Status (available / notAvailable): ");
                    String status = sc.nextLine().trim().toLowerCase();
                    if (!status.equals("available") && !status.equals("notavailable")) {
                        System.out.println("Invalid status. Please enter either 'available' or 'notAvailable'.");
                        break;
                    }
                    System.out.print("Enter Vehicle Passenger Capacity: ");
                    int passengerCapacity = sc.nextInt();
                    System.out.print("Enter Vehicle Engine Capacity: ");
                    double engineCapacity = sc.nextDouble();
                    repo.addCar(new Vehicle( make, model, year, rate, status, passengerCapacity, engineCapacity));
                    System.out.println("Car added successfully.");
                	 } catch (SQLException e) {
                	        System.out.println(" SQL Error while adding car: " + e.getMessage());
                	    } catch (Exception e) {
                	        System.out.println(" Unexpected error: " + e.getMessage());
                	        sc.nextLine(); 
                	    }
                	    break;
                    
                case 6:
                	try {
                		 List<Vehicle> availableCars = repo.listAvailableCars();
                	        if (availableCars == null || availableCars.isEmpty()) {
                	            System.out.println(" No available cars at the moment.");
                	        } else {
                	            System.out.println("Available Cars:");
                	            for (Vehicle car : availableCars) {
                	                System.out.println(car);
                	            }
                	        }
                	    } catch (SQLException e) {
                	        System.out.println(" SQL Error while fetching available cars: " + e.getMessage());
                	    } catch (Exception e) {
                	        System.out.println("Unexpected error: " + e.getMessage());
                	    }
                	    break;
                	    
                case 7:
                	try {
                        List<Vehicle> rentedCars = repo.listRentedCars();
                        if (rentedCars == null || rentedCars.isEmpty()) {
                            System.out.println("No cars are currently rented.");
                        } else {
                            System.out.println(" Rented Cars:");
                            for (Vehicle car : rentedCars) {
                                System.out.println(car);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println(" SQL Error while fetching rented cars: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println(" Unexpected error: " + e.getMessage());
                    }
                    break;
                   
                case 8:
                	try {
                    System.out.print("Enter Car ID to remove: ");
                    repo.removeCar(sc.nextInt());
                    System.out.println("Car removed successfully.");
                } catch (VehicleNotFoundException e) {
                    System.out.println(e.getMessage());
                } catch (SQLException e) {
                        System.out.println("SQL Error while removing car: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println(" Unexpected error: " + e.getMessage());
                    sc.nextLine();                 }
                break;
                    
                case 9:
                	try {
                    System.out.print("Enter Car ID: ");
                    Vehicle car = repo.findCarById(sc.nextInt());
                    System.out.println("Car found:\n" + car);
                } catch (VehicleNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("SQL Error while fetching car: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                    sc.nextLine(); 
                }
                break;
                    
                case 10:
                	try {
                    System.out.print("Enter Vehicle ID: ");
                    int VehicleId = sc.nextInt();
                    System.out.print("Enter Customer ID: ");
                    int CustId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Start Date (yyyy-mm-dd): ");
                    Date startDate = Date.valueOf(sc.nextLine());
                    System.out.print("Enter End Date (yyyy-mm-dd): ");
                    Date endDate = Date.valueOf(sc.nextLine());
                    if (endDate.before(startDate)) {
                        System.out.println("Error: End date cannot be before start date.");
                        break;
                    }
                    Lease lease = repo.createLease(VehicleId, CustId, startDate, endDate);
                    System.out.println("Lease created: " + lease);
                	} catch (IllegalArgumentException e) {
                        System.out.println("Error: Invalid date format. Please use yyyy-mm-dd.");
                    } catch (SQLException e) {
                        System.out.println("SQL Error while creating lease: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                        sc.nextLine(); 
                    }
                    break;
                    
                case 11:
                	try {
                    System.out.print("Enter Lease ID to return car: ");
                    Lease returned = repo.returnCar(sc.nextInt());
                    System.out.println("Car returned for Lease: " + returned);
                	} catch (LeaseNotFoundException | VehicleNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("SQL Error while returning car: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                        sc.nextLine(); 
                    }
                    break;
                    
                case 12:
                	try {
                        List<Lease> activeLeases = repo.listActiveLeases();
                        if (activeLeases == null || activeLeases.isEmpty()) {
                            System.out.println("No active leases found.");
                        } else {
                            System.out.println("Active Leases:");
                            for (Lease lease : activeLeases) {
                                System.out.println(lease);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("SQL Error while fetching active leases: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                    }
                    break;
                    
                case 13:
                	try {
                	        List<Lease> leaseHistory = repo.listLeaseHistory();
                	        if (leaseHistory == null || leaseHistory.isEmpty()) {
                	            System.out.println("No lease history available.");
                	        } else {
                	            System.out.println("Lease History:");
                	            for (Lease lease : leaseHistory) {
                	                System.out.println(lease);
                	            }
                	        }
                	    } catch (SQLException e) {
                	        System.out.println("SQL Error while fetching lease history: " + e.getMessage());
                	    } catch (Exception e) {
                	        System.out.println("Unexpected error: " + e.getMessage());
                	    }
                	    break;
                	
                case 14:
                	try {
                    System.out.print("Enter Lease ID: ");
                    int LeaseId = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Amount: ");
                    double amount = sc.nextDouble();
                    if (amount <= 0) {
                        System.out.println("Error: Payment amount must be greater than 0.");
                        break;
                    }
                    Lease lease = new Lease();
                    lease.setLeaseID(LeaseId);
                    repo.recordPayment(lease, amount);                    
                    System.out.println("Payment recorded successfully.");
                	} catch (LeaseNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("SQL Error while recording payment: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Unexpected error: " + e.getMessage());
                        sc.nextLine(); 
                    }
                    break;
                    
                case 15:
                    System.out.println("Thank you for using the Car Rental System!");
                    sc.close();
                    System.exit(0);
                    
                default:
                    System.out.println("Invalid choice. Try again.");
                }}
        }catch (Exception e) {
                System.out.println("System Error: " + e.getMessage());
        }
    }
}
            
        
            
             
                

            
