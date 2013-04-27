package ros.zeroconf.jmdns;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DiscoveredService {

//	discovered_service.name = service_info.getName();
//	String[] type_domain_str = service_info.getType().split("\\.");
//	discovered_service.type = type_domain_str[0] + "." + type_domain_str[1];
//	discovered_service.domain = service_info.getDomain();
//	discovered_service.hostname = service_info.getServer();
//	discovered_service.port = service_info.getPort();
//	for ( InetAddress inet_address : service_info.getInetAddresses() ) {
//		if ( inet_address instanceof Inet4Address) {
//			discovered_service.ipv4_addresses.add(inet_address.getHostAddress());
//		} else { // Inet6Address
//			discovered_service.ipv6_addresses.add(inet_address.getHostAddress());
//		}
//	}	
	public String name="";
	public String type="";
	public String domain="";
	public String hostname="";
	public int port=8888;
	public List<String> ipv4_addresses=new ArrayList<String>();
//	public List<String> ipv6_addresses=new ArrayList<String>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

}
