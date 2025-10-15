package com.kien.project.clinicmanagement.utils;

public class PageHelper {
	
	public int currentPage;
	public int rowsPerPage;
	
	public PageHelper ( int currentPage, int rowsPerPage) {
		this.currentPage = currentPage;
		this.rowsPerPage = rowsPerPage;
	}
	
	public static int getOffSet(int currentPage, int rowsPerPage) {
		return (currentPage -1) * rowsPerPage;
	}
}
