package com.credijusto.utils;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.credijusto.model.FXRateModel;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

@SuppressWarnings({"deprecation", "unchecked"})
public class RateProviderUtil {

	public FXRateModel getFixerRate() throws IOException {
		String urlString = "http://data.fixer.io/api/latest?access_key=1019eae0726ebd47e19ba52e5056852e&base=USD&base=EUR&symbols=USD,MXN";
		String fixerURL = IOUtils.toString(new URL(urlString));
		JSONObject jsonObj = new JSONObject(fixerURL);
		JSONObject ratesObject = (JSONObject)jsonObj.get("rates");
		Double eurToMxn = (Double)ratesObject.get("MXN");
		Double eurToUSD = (Double)ratesObject.get("USD");
		Integer timeStamp = (Integer)jsonObj.get("timestamp");
		
		FXRateModel fxRateModel = new FXRateModel();
		fxRateModel.setProviderName("Fixer");
		fxRateModel.setFxRate(eurToMxn / eurToUSD);
		fxRateModel.setLastUpdatedDate(new Date(timeStamp * 1000L));
		
		return fxRateModel;
	}
	
	public FXRateModel getBanxicoRate() throws IOException, ParseException {

		String urlString = "https://www.banxico.org.mx/SieAPIRest/service/v1/series/SF43718/datos/oportuno?token=4412e21fd8f0467a7219f8a44be4c3ed8a283894184da634911bac32f082bdb9";
		String banxicoURL = IOUtils.toString(new URL(urlString));
		JSONObject banxicoJson = new JSONObject(banxicoURL);
		JSONObject dato = ((JSONObject)((JSONArray)((JSONObject)((JSONArray)((JSONObject)banxicoJson.get("bmx")).get("series")).get(0)).get("datos")).get(0));
		
		FXRateModel fxRateModel = new FXRateModel();
		fxRateModel.setProviderName("Banxico");
		fxRateModel.setFxRate(Double.valueOf((String)dato.get("dato")));
		fxRateModel.setLastUpdatedDate(new SimpleDateFormat("dd/MM/yyyy").parse((String)dato.get("fecha")));
		
		return fxRateModel;
	}
	
	public FXRateModel getDofRate() throws ParseException {
		FXRateModel fxRateModel = new FXRateModel();
		HtmlPage page = null;
		
		try (WebClient client = new WebClient()){
			client.getOptions().setCssEnabled(false);
			client.getOptions().setJavaScriptEnabled(false);
			String urlString = "http://www.banxico.org.mx/tipcamb/tipCamMIAction.do";
			page = client.getPage(urlString);
		} catch(Exception e) {
			throw new IllegalArgumentException("Unable to scrap out data from web");
		}
		
		List<HtmlTableRow> items = (List<HtmlTableRow>) page.getByXPath("//tr[@class='renglonNon']");
		if (!items.isEmpty()) {
			List<HtmlElement> tableData = items.get(0).getElementsByTagName("td");
			if (!tableData.isEmpty()) {
				fxRateModel.setProviderName("Diario Official de la Federacion");
				fxRateModel.setFxRate(Double.valueOf(tableData.get(1).asText()));
				fxRateModel.setLastUpdatedDate((new SimpleDateFormat("dd/MM/yyyy")).parse(tableData.get(0).asText()));
			}
		}
		
		return fxRateModel;
	}
	
	public List<FXRateModel> getAllFxRates() throws IOException, ParseException {
		List<FXRateModel> fxRateModels = new ArrayList<>();
		fxRateModels.add(getDofRate());
		fxRateModels.add(getFixerRate());
		fxRateModels.add(getBanxicoRate());
		
		return fxRateModels;
	}
	
	public JSONObject getFxRatesJson() throws IOException, ParseException {
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		JSONObject providerJson = new JSONObject(), responseJson = new JSONObject();
		for(FXRateModel fxRateModel : getAllFxRates()) {
			JSONObject fxRateJson = new JSONObject();
			fxRateJson.put("value", fxRateModel.getFxRate());
			fxRateJson.put("last_updated", df.format(fxRateModel.getLastUpdatedDate()));
			
			providerJson.put(fxRateModel.getProviderName(), fxRateJson);
		}
		
		responseJson.put("rates", providerJson);
		return responseJson;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		RateProviderUtil util = new RateProviderUtil();
		System.out.println(util.getFxRatesJson());
	}
}
