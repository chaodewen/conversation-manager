package tech.moai.mpcm.server;

import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import tech.moai.mpcm.setting.ManagerSettings;

public class RestEasyNettyServer {
	private NettyJaxrsServer netty;
	private ResteasyDeployment deployment;
	private String port = ManagerSettings.SERVER_PORT;
	private String rootResourcePath = ManagerSettings.ROOT_RESOURCE_PATH;

	public RestEasyNettyServer(ResteasyDeployment deployment, String port,
							   String rootResourcePath) {
		this.netty = new NettyJaxrsServer();
		this.deployment = deployment;
		this.port = port;
		this.rootResourcePath = rootResourcePath;
	}

	public void start() throws Exception {
		netty.setDeployment(deployment);
		netty.setPort(Integer.parseInt(port));
		netty.setRootResourcePath(rootResourcePath);
		netty.setSecurityDomain(null);
		netty.start();
	}

	public void stop(){
		netty.stop();
	}
}