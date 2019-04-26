package com.credijusto.utils;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	public FXRateModel getBanxicoRate() {
		FXRateModel fxRateModel = new FXRateModel();
		fxRateModel.setProviderName("Banxico");
		fxRateModel.setFxRate(18.928974);
		fxRateModel.setLastUpdatedDate(new Date());
		
		return fxRateModel;
	}
	
	public FXRateModel getDofRate() {
		FXRateModel fxRateModel = new FXRateModel();
		fxRateModel.setProviderName("Diario Official de la Federacion");
		fxRateModel.setFxRate(18.931174);
		fxRateModel.setLastUpdatedDate(new Date());
		
		return fxRateModel;
	}
	
	public List<FXRateModel> getAllFxRates() throws IOException {
		List<FXRateModel> fxRateModels = new ArrayList<>();
		fxRateModels.add(getDofRate());
		fxRateModels.add(getFixerRate());
		fxRateModels.add(getBanxicoRate());
		
		return fxRateModels;
	}
	
	public JSONObject getFxRatesJson() throws IOException {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		
		JSONObject providerJson = new JSONObject(), responseJson = new JSONObject();
		List<FXRateModel> allFXRates = getAllFxRates();
		
		for(FXRateModel fxRateModel : allFXRates) {
			JSONObject fxRateJson = new JSONObject();
			fxRateJson.put("value", fxRateModel.getFxRate());
			fxRateJson.put("last_updated", df.format(fxRateModel.getLastUpdatedDate()));
			
			providerJson.put(fxRateModel.getProviderName(), fxRateJson);
		}
		
		responseJson.put("rates", providerJson);
		return responseJson;
	}
}
