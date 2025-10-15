package com.kien.project.clinicmanagement.utils;

import com.kien.project.clinicmanagement.model.User;

public class Session {
	
	// Tạo biến lưu người dùng hiện tại sau khi đăng nhập
	private static User currentUser;
	
	// Hàm lưu User sau khi đăng nhập vào biến currentUser
	public static void setCurrentUser(User user) {
		currentUser = user;
	}
	
	// Lấy thông tin User hiện tại
	public static User getCurrentUser() {
		return currentUser;
	}
	
	// Xóa User khi logout
	public static void clearCurrentUser() {
		currentUser = null;
	}
	
	// Kiểm tra người dùng đăng nhập chưa
	public static boolean isLoggedIn() {
		return currentUser != null;
	}
}
