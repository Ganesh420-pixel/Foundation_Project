package com.thbs.Banking.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;
import com.thbs.Banking.entity.Customer;
import com.thbs.Banking.entity.FundTransfer;
import com.thbs.Banking.entity.Login;
import com.thbs.Banking.entity.Transaction;
import com.thbs.Banking.entity.TransactionsBWDates;
import com.thbs.Banking.repository.CustomerRepository;
import com.thbs.Banking.service.CustomerService;
import com.thbs.Banking.service.TransactionService;
import com.thbs.Banking.service.UserPDFExporter;


@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/api") 
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	 @Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private TransactionService transactionService;

	@PostMapping("/customer")
	public Customer save(@RequestBody Customer customer) {
		return customerService.save(customer);
	}

	@PutMapping("/customer")
	public Customer update(@RequestBody Customer customer) {
		return customerService.update(customer);
	}

	@DeleteMapping("/customer/{id}")
	public void delete(@PathVariable Long id) {
		customerService.delete(id);

	}

	@GetMapping("/customer/{id}")
	public Optional<Customer> getOne(@PathVariable Long id) {
		return customerService.getOne(id);
	}

	@GetMapping("/customer")
	public List<Customer> getAll() {
		return customerService.getAll();
	}
	@PostMapping("/login")
	public Optional<Customer> authenticate(@RequestBody Login login) {
		return customerService.authenticate(login);
	}
	@PostMapping("/deposite")
	public boolean deposite(@RequestBody Transaction transaction) {
		return transactionService.deposite(transaction);
	}
	
	@PostMapping("/withdraw")
	public boolean withdraw(@RequestBody Transaction transaction) {
		return transactionService.withdraw(transaction);
	}
	
	@PostMapping("/fundTransfers")
	public boolean fundTransfer(@RequestBody FundTransfer fundTransfer) {
		return transactionService.fundTransfer(fundTransfer.getTransactions());
	}

	@GetMapping("/transaction/{accountNum}")
	public List<Transaction> getAllTransactions(@PathVariable String accountNum) {
		return transactionService.getAll(accountNum);
	}
	
	@PostMapping("/transaction")
	public List<Transaction> getTransactions(@RequestBody TransactionsBWDates tbwd)
	{
		return transactionService.getTransactions(tbwd);
	}
	
	@GetMapping("/profile/{accountNum}")
	public Customer getProfile(@PathVariable String accountNum) {
		return customerService.getProfile(accountNum);
	}
	
	 @GetMapping("/users/export/pdf/{accountNum}")
	    public void exportToPDF(HttpServletResponse response, @PathVariable String accountNum) throws DocumentException, IOException {
	        response.setContentType("application/pdf");
	        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	        String currentDateTime = dateFormatter.format(new Date());
	         
	        String headerKey = "Content-Disposition";
	        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
	        response.setHeader(headerKey, headerValue);
	        // Transaction tran=new Transaction();
	      // String accNo= "451897";
	        List<Transaction> listTransactions = transactionService.getAll(accountNum);
	         
	        UserPDFExporter exporter = new UserPDFExporter(listTransactions);
	        exporter.export(response);
	         
	    }
	 @PutMapping("/changePass/{id}")
	public Customer ChangePass(@PathVariable Long id, @RequestBody Customer cust)
	{
		 
		return customerRepository.findById(id).map(
				customer ->{
					customer.setPassword(cust.getPassword());
					return customerService.update(customer);
				}).orElseGet(() -> {
					cust.setId(id);
					return customerService.update(cust);
				});
		 
	}
}
