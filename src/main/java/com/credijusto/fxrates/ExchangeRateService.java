package com.credijusto.fxrates;

import java.io.IOException;
import java.text.ParseException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.credijusto.utils.RateProviderUtil;

@Path("/rates")
public class ExchangeRateService {

	@GET
	public Response getFXRates() throws IOException, JSONException, ParseException {
		RateProviderUtil util = new RateProviderUtil();
		JSONObject fxRatesJson = util.getFxRatesJson();
		return Response.status(200).entity(fxRatesJson).build();
	}
}
