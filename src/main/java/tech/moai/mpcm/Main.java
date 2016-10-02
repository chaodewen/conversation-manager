package tech.moai.mpcm;

import javax.ws.rs.core.Application;
import org.jboss.resteasy.spi.ResteasyDeployment;
import tech.moai.mpcm.server.RestEasyNettyServer;
import tech.moai.mpcm.setting.ManagerSettings;

public class Main {
	public static void main(String[] args) {
		ResteasyDeployment resteasyDeployment = new ResteasyDeployment();
		Application restApplication = new RestApplication();
		resteasyDeployment.setApplication(restApplication);

		RestEasyNettyServer server = new RestEasyNettyServer(
				resteasyDeployment, ManagerSettings.SERVER_PORT
				, ManagerSettings.ROOT_RESOURCE_PATH);

		try {
			System.out.println("Conversation Manager Starting ...");
			server.start();
			System.out.println("Root Path : " + ManagerSettings.ROOT_RESOURCE_PATH);
			System.out.println("Port : " + ManagerSettings.SERVER_PORT);
			System.out.println("Conversation Manager Started ...");
		} catch (Exception e) {
			System.out.println("Conversation Manager Starting Failure ...");
			e.printStackTrace();
			server.stop();
		}
	}
}