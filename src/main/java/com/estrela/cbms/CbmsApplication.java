package com.estrela.cbms;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Desktop;

@Log4j2
@SpringBootApplication
public class CbmsApplication {

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(CbmsApplication.class, args);
	}

	@Value("${server.port:8080}") // Inject server.port, default to 8080 if not found
	private String serverPort;

	@EventListener(ApplicationReadyEvent.class)
	public void openBrowserAfterStartup() {
		// Verifica se o Maven está executando testes (via Surefire plugin)
		if (System.getProperty("surefire.test.class.path") != null) {
			return;
		}

		String url = "http://localhost:" + serverPort;

		try {
			if (Desktop.isDesktopSupported()) {
				// For desktop environments
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					desktop.browse(new URI(url));
					log.info("Opened browser to: {}", url);
				}
			} else {
				// For non-desktop or headless environments
				String os = System.getProperty("os.name").toLowerCase();
				Runtime runtime = Runtime.getRuntime();
				if (os.contains("win")) {
					runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
				} else if (os.contains("mac")) {
					runtime.exec("open " + url);
				} else if (os.contains("nix") || os.contains("nux")) {
					runtime.exec("xdg-open " + url);
				}
				log.info("Attempted to open browser via command line to: {}", url);
			}
		} catch (IOException | URISyntaxException e) {
			log.error("Error opening browser: {}", e.getMessage());
		}
	}
}
