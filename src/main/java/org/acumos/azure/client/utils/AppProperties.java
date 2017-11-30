package org.acumos.azure.client.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "docker")
@Component
public class AppProperties {
	
	private String host;
    private String port;
	private String config;
	private Registry registry = new Registry();
	private Api api = new Api();
	
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public static class Registry {
        private String url;
        private String username;
        private String password;

        

        public String getUrl() {
			return url;
		}



		public void setUrl(String url) {
			this.url = url;
		}



		public String getUsername() {
			return username;
		}



		public void setUsername(String username) {
			this.username = username;
		}



		public String getPassword() {
			return password;
		}



		public void setPassword(String password) {
			this.password = password;
		}



		@Override
        public String toString() {
            return "Registry{" +
                    "url='" + url + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
	
	public static class Api {
        private String version;
       
       

        public String getVersion() {
			return version;
		}



		public void setVersion(String version) {
			this.version = version;
		}



		@Override
        public String toString() {
            return "Api{" +
                    "version='" + version + '\'' +
                    '}';
        }
    }

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Api getApi() {
		return api;
	}

	public void setApi(Api api) {
		this.api = api;
	}

	@Override
	public String toString() {
		return "AppProperties [host=" + host + ", port=" + port + ", config=" + config + ", registry=" + registry
				+ ", api=" + api + "]";
	}
	
	
	
	
	
	

}
