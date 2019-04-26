package com.credijusto.fxrates;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.credijusto.model.FXRateModel;
import com.credijusto.utils.RateProviderUtil;

@Path("/fixer")
public class ExchangeRateService {

	@GET
	public Response getFXRates() throws IOException {
		RateProviderUtil util = new RateProviderUtil();
		FXRateModel fixerRateModel = util.getFixerRate();
		return Response.status(200).entity(fixerRateModel).build();
	}
}
