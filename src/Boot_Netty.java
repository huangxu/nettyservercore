import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.youxigu.gs.core.Configuration;
import com.youxigu.gs.core.Server;
import com.youxigu.gs.system.EnterShutdown;


public class Boot_Netty {
	private static final Logger logger = LoggerFactory.getLogger(Boot_Netty.class);
	public static void main(String[] args) {
		File file2 = null;
		//通过回车关闭JVM，会调用hook
//		initShutdownEnter();
	    if (args.length < 2) {
	      logger.error("Must supply specific configuration directory and prefix...");
	      return;
	    }
	    File file = new File(args[0]);
	    String prefix = args[1];
	    if (args.length > 2) {
	      file2 = new File(args[2]);
	    }

	    try
	    {
	      Configuration.getInstance().setup(file, file2, prefix);
	    } catch (IOException e) {
	      e.printStackTrace();
	      return;
	    }
	    Server s = new Server();
	    logger.info("Using configuration in '" + file + "' directory.");
	    s.init();
	}
	private static void initShutdownEnter() {
		Thread consoleRead = new Thread(new EnterShutdown());
		consoleRead.setName("enter_to_shutdown");
		consoleRead.start();
	}
}
