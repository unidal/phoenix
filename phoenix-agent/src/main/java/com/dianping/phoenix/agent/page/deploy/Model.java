package com.dianping.phoenix.agent.page.deploy;

import com.dianping.phoenix.agent.AgentPage;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.transform.DefaultJsonBuilder;

import org.unidal.web.mvc.ViewModel;

public class Model extends ViewModel<AgentPage, Action, Context> {

	private Response response;

	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.DEFAULT;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	public String getResponseInJson() {
		return new DefaultJsonBuilder().buildJson(response);
	}

}
