package io.github.sparqlanything.model.resources;

import io.github.sparqlanything.model.IRIArgument;
import io.github.sparqlanything.model.Utils;
import io.github.sparqlanything.model.resources.annotations.TargetOption;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@TargetOption(IRIArgument.COMMAND_NAME)
public class CommandInputService implements ResourceService {

	private static final Logger log = LoggerFactory.getLogger(CommandInputService.class);

	@Override
	public InputStream getInputStream(Properties properties) throws IOException {
		String command = properties.getProperty(IRIArgument.COMMAND.toString());
		Runtime rt = Runtime.getRuntime();
		String[] commands;
		if (Utils.platform != Utils.OS.WINDOWS) {
			// allow shell pipelines and other useful shell functionality
			commands = new String[]{"bash", "-c", command};
		} else { // WINDOWS
			// Credit: https://stackoverflow.com/a/18893443/1035608
			commands = command.split("(?x)   " + "\\s          " + // Split on space
				"(?=        " + // Followed by
				"  (?:      " + // Start a non-capture group
				"    [^\"]* " + // 0 or more non-quote characters
				"    \"     " + // 1 quote
				"    [^\"]* " + // 0 or more non-quote characters
				"    \"     " + // 1 quote
				"  )*       " + // 0 or more repetition of non-capture group (multiple of 2 quotes will be even)
				"  [^\"]*   " + // Finally 0 or more non-quotes
				"  $        " + // Till the end (This is necessary, else every space will satisfy the condition)
				")          " // End look-ahead
			);
		}
		log.info("Running command: {}", String.join(" ", commands));
		Process proc = rt.exec(commands);
		InputStream is = proc.getInputStream();
		InputStream es = proc.getErrorStream();
		log.info("Command stderr: " + IOUtils.toString(es));
		return is;
	}
}
