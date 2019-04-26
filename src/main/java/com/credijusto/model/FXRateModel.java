package com.credijusto.model;

import java.util.Date;

public class FXRateModel {
	
	private String providerName;
	
	private Double fxRate;
	
	private Date lastUpdatedDate;

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public Double getFxRate() {
		return fxRate;
	}

	public void setFxRate(Double fxRate) {
		this.fxRate = fxRate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}
}
