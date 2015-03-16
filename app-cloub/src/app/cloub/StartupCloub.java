package app.cloub;

import java.io.IOException;

import app.rpc.Startup;
import app.rpc.remote.deploy.FileSystemDeployContextHandler;

public class StartupCloub {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if(args.length==0){
			args = new String[]{"9000"};
		}
		checkArgsVaild(args);
		
		String configPath = System.getProperty("conf", "config/");
		String bindAddress = "0.0.0.0:" + args[0];
		String beansLocation = configPath + "beans.xml";
		String exportLocation = configPath + "export.properties";
		
		args = new String[] { bindAddress, beansLocation, exportLocation };
		
		if(System.getProperty("log4j")==null){
			System.setProperty("log4j", configPath+"log4j.properties");
		}
		
		Startup.main(args);
		
		// 安装部署插件
		FileSystemDeployContextHandler deployPlugin = new FileSystemDeployContextHandler();
		Startup.getConector().getServerHandler().addPlugin(deployPlugin);
	}

	private static void checkArgsVaild(String[] args) {
		if(args.length>1){
			System.err.println("plz set args: PORT\ne.g: app.cloub.StartupCloub 9000");
		}
		
	}

}
