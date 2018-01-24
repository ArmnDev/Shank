package com.mobiquel.shank.model;

public class UserProductMappingVO {

	private String userId,userType,productId,isInstall,isService,isComplaint,productName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}



	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getIsInstall() {
		return isInstall;
	}

	public void setIsInstall(String isInstall) {
		this.isInstall = isInstall;
	}

	public String getIsService() {
		return isService;
	}

	public void setIsService(String isService) {
		this.isService = isService;
	}

	public String getIsComplaint() {
		return isComplaint;
	}

	public void setIsComplaint(String isComplaint) {
		this.isComplaint = isComplaint;
	}
}
