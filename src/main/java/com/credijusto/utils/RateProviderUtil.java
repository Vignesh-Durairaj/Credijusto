package com.credijusto.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.credijusto.model.FXRateModel;

public class RateProviderUtil {

	@SuppressWarnings("deprecation")
	public FXRateModel getFixerRate() throws IOException {
		String fixerURL = "http://data.fixer.io/api/latest?access_key=1019eae0726ebd47e19ba52e5056852e&base=USD&base=EUR&symbols=USD,MXN";
		String urlString = IOUtils.toString(new URL(fixerURL));
		JSONObject jsonObj = new JSONObject(urlString);
		JSONObject ratesObject = (JSONObject)jsonObj.get("rates");
		Double eurToMxn = (Double)ratesObject.get("MXN");
		Double eurToUSD = (Double)ratesObject.get("USD");
		Integer timeStamp = (Integer)jsonObj.get("timestamp");
		
		FXRateModel fxRateModel = new FXRateModel();
		fxRateModel.setProviderName("FIXER");
		fxRateModel.setFxRate(eurToMxn / eurToUSD);
		fxRateModel.setLastUpdatedDate(new Date(timeStamp * 1000L));
		
		return fxRateModel;
	}
}
