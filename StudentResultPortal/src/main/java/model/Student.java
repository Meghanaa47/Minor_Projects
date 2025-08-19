package model;

public class Student {
   private String regno;
   private String name;
   private String email;
   private String branch;
   private String batch;
   private String otp; 
   public Student() {}

   public Student(String regno, String name, String email, String branch, String batch) {
       this.regno = regno;
       this.name = name;
       this.email = email;
       this.branch = branch;
       this.batch = batch;
   }

   public String getOtp() {
       return otp;
   }
   public void setOtp(String otp) {
       this.otp = otp;
   }
   public String getRegno() {
	return regno;
   }
   public void setRegno(String regno) {
	this.regno = regno;
   }
   public String getName() {
	return name;
   }
   public void setName(String name) {
	this.name = name;
   }
   public String getEmail() {
	return email;
   }
   public void setEmail(String email) {
	this.email = email;
   }
   public String getBranch() {
	return branch;
   }
   public void setBranch(String branch) {
	this.branch = branch;
   }
   public String getBatch() {
	return batch;
   }
   public void setBatch(String batch) {
	this.batch = batch;
   }
}
