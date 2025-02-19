package com.ecwid.consul.v1.agent;

import com.ecwid.consul.SingleUrlParameters;
import com.ecwid.consul.UrlParameters;
import com.ecwid.consul.json.GsonFactory;
import com.ecwid.consul.transport.HttpResponse;
import com.ecwid.consul.transport.TLSConfig;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.OperationException;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.*;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * @author Vasily Vasilkov (vgv@ecwid.com)
 */
public final class AgentConsulClient implements AgentClient {

	private final ConsulRawClient rawClient;

	public AgentConsulClient(ConsulRawClient rawClient) {
		this.rawClient = rawClient;
	}

	public AgentConsulClient() {
		this(new ConsulRawClient());
	}

	public AgentConsulClient(TLSConfig tlsConfig) {
		this(new ConsulRawClient(tlsConfig));
	}

	public AgentConsulClient(String agentHost) {
		this(new ConsulRawClient(agentHost));
	}

	public AgentConsulClient(String agentHost, TLSConfig tlsConfig) {
		this(new ConsulRawClient(agentHost, tlsConfig));
	}

	public AgentConsulClient(String agentHost, int agentPort) {
		this(new ConsulRawClient(agentHost, agentPort));
	}

	public AgentConsulClient(String agentHost, int agentPort, TLSConfig tlsConfig) {
		this(new ConsulRawClient(agentHost, agentPort, tlsConfig));
	}

	@Override
	public Response<Map<String, Check>> getAgentChecks() {
		HttpResponse<Map<String, Check>> httpResponse = rawClient.makeGetRequest("/v1/agent/checks", r -> {
			return GsonFactory.getGson().fromJson(r, new TypeToken<Map<String, Check>>() {}.getType());
		});

		if (httpResponse.getStatusCode() == 200) {
			Map<String, Check> value = httpResponse.getContent();
			return new Response<>(value, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Map<String, Service>> getAgentServices() {
		HttpResponse<Map<String, Service>> httpResponse = rawClient.makeGetRequest("/v1/agent/services", r -> {
			return GsonFactory.getGson().fromJson(r, new TypeToken<Map<String, Service>>() {}.getType());
		});

		if (httpResponse.getStatusCode() == 200) {
			Map<String, Service> agentServices = httpResponse.getContent();
			return new Response<>(agentServices, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<List<Member>> getAgentMembers() {
		HttpResponse<List<Member>> httpResponse = rawClient.makeGetRequest("/v1/agent/members", r -> {
			return GsonFactory.getGson().fromJson(r, new TypeToken<List<Member>>() {}.getType());
		});

		if (httpResponse.getStatusCode() == 200) {
			List<Member> members = httpResponse.getContent();
			return new Response<>(members, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Self> getAgentSelf() {
		return getAgentSelf(null);
	}

	@Override
	public Response<Self> getAgentSelf(String token) {
		UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

		HttpResponse<Self> httpResponse = rawClient.makeGetRequest("/v1/agent/self", r -> {
			return GsonFactory.getGson().fromJson(r, new TypeToken<Self>() {}.getType());
		}, tokenParam);

		if (httpResponse.getStatusCode() == 200) {
			Self self = httpResponse.getContent();
			return new Response<>(self, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentSetMaintenance(boolean maintenanceEnabled) {
		return agentSetMaintenance(maintenanceEnabled, null);
	}

	@Override
	public Response<Void> agentSetMaintenance(boolean maintenanceEnabled, String reason) {
		UrlParameters maintenanceParameter = new SingleUrlParameters("enable", Boolean.toString(maintenanceEnabled));
		UrlParameters reasonParamenter = reason != null ? new SingleUrlParameters("reason", reason) : null;

		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/maintenance", "", r -> null, maintenanceParameter, reasonParamenter);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<Void>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}

	}

	@Override
	public Response<Void> agentJoin(String address, boolean wan) {
		UrlParameters wanParams = wan ? new SingleUrlParameters("wan", "1") : null;
		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/join/" + address, "", r -> null, wanParams);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentForceLeave(String node) {
		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/force-leave/" + node, "", r -> null);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentCheckRegister(NewCheck newCheck) {
		return agentCheckRegister(newCheck, null);
	}

	@Override
	public Response<Void> agentCheckRegister(NewCheck newCheck, String token) {
		UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

		String json = GsonFactory.getGson().toJson(newCheck);
		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/check/register", json, r -> null, tokenParam);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentCheckDeregister(String checkId) {
		return agentCheckDeregister(checkId, null);
	}

	@Override
	public Response<Void> agentCheckDeregister(String checkId, String token) {
		UrlParameters tokenParameter = token != null ? new SingleUrlParameters("token", token) : null;

		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/check/deregister/" + checkId, "", r -> null, tokenParameter);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentCheckPass(String checkId) {
		return agentCheckPass(checkId, null);
	}

	@Override
	public Response<Void> agentCheckPass(String checkId, String note) {
		return agentCheckPass(checkId, note, null);
	}

	@Override
	public Response<Void> agentCheckPass(String checkId, String note, String token) {
		UrlParameters noteParameter = note != null ? new SingleUrlParameters("note", note) : null;
		UrlParameters tokenParameter = token != null ? new SingleUrlParameters("token", token) : null;

		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/check/pass/" + checkId, "", r -> null, noteParameter, tokenParameter);
    
		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentCheckWarn(String checkId) {
		return agentCheckWarn(checkId, null);
	}

	@Override
	public Response<Void> agentCheckWarn(String checkId, String note) {
		return agentCheckWarn(checkId, note, null);
	}

	@Override
	public Response<Void> agentCheckWarn(String checkId, String note, String token) {
		UrlParameters noteParameter = note != null ? new SingleUrlParameters("note", note) : null;
		UrlParameters tokenParameter = token != null ? new SingleUrlParameters("token", token) : null;

		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/check/warn/" + checkId, "", r -> null, noteParameter, tokenParameter);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentCheckFail(String checkId) {
		return agentCheckFail(checkId, null);
	}

	@Override
	public Response<Void> agentCheckFail(String checkId, String note) {
		return agentCheckFail(checkId, note, null);
	}

	@Override
	public Response<Void> agentCheckFail(String checkId, String note, String token) {
		UrlParameters noteParameter = note != null ? new SingleUrlParameters("note", note) : null;
		UrlParameters tokenParameter = token != null ? new SingleUrlParameters("token", token) : null;

		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/check/fail/" + checkId, "", r -> null, noteParameter, tokenParameter);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentServiceRegister(NewService newService) {
		return agentServiceRegister(newService, null);
	}

	@Override
	public Response<Void> agentServiceRegister(NewService newService, String token) {
		UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

		String json = GsonFactory.getGson().toJson(newService);
		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/service/register", json, r -> null, tokenParam);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentServiceDeregister(String serviceId) {
		return agentServiceDeregister(serviceId, null);
	}

	@Override
	public Response<Void> agentServiceDeregister(String serviceId, String token) {
		UrlParameters tokenParam = token != null ? new SingleUrlParameters("token", token) : null;

		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/service/deregister/" + serviceId, "", r -> null, tokenParam);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentServiceSetMaintenance(String serviceId, boolean maintenanceEnabled) {
		return agentServiceSetMaintenance(serviceId, maintenanceEnabled, null);
	}

	@Override
	public Response<Void> agentServiceSetMaintenance(String serviceId, boolean maintenanceEnabled, String reason) {
		UrlParameters maintenanceParameter = new SingleUrlParameters("enable", Boolean.toString(maintenanceEnabled));
		UrlParameters reasonParameter = reason != null ? new SingleUrlParameters("reason", reason) : null;

		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/service/maintenance/" + serviceId, "", r -> null, maintenanceParameter, reasonParameter);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}
	}

	@Override
	public Response<Void> agentReload() {
		HttpResponse<Void> httpResponse = rawClient.makePutRequest("/v1/agent/reload", "", r -> null);

		if (httpResponse.getStatusCode() == 200) {
			return new Response<>(null, httpResponse);
		} else {
			throw new OperationException(httpResponse);
		}

	}
}
