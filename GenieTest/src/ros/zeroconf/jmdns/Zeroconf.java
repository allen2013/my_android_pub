package ros.zeroconf.jmdns;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jmdns.JmmDNS;
import javax.jmdns.NetworkTopologyEvent;
import javax.jmdns.NetworkTopologyListener;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

import com.filebrowse.ScanDeviceService;
import com.turbo.Turbo_DevicelistActivity;

/**
 * This is a wrapper around the jmmdns (multi-homed) part of the jmdns library.
 * 
 * On the surface it does not look as if we need this wrapper since jmdns has
 * quite a nice api, but it turned out to be quite awkward to use. There are
 * some broken api, others that need some black magic, and also the ouput (in
 * ServiceInfo types) is fixed - you can't modify, or merge them to simplify the
 * output list of discovered services.
 * 
 * Currently working with the jmdns guy and merging convenient additions here
 * upstream but its a gradual process that needs alot of testing.
 * 
 * In summary, this is a nice, simple api for publishing services and doing
 * service discovery (either via polling or via callback).
 */
public class Zeroconf implements ServiceListener, ServiceTypeListener,
		NetworkTopologyListener {

	private class DefaultLogger implements ZeroconfLogger {
		public void println(String msg) {
		}
	}

	JmmDNS jmmdns;
	Set<String> listeners;
	Set<ServiceInfo> services;
	ZeroconfLogger logger;
	Map<String, ZeroconfDiscoveryHandler> listener_callbacks;
	ZeroconfDiscoveryHandler default_listener_callback;

	public Zeroconf() {
		/********************
		 * Variables
		 *******************/
		this.jmmdns = JmmDNS.Factory.getInstance();
		this.listeners = new HashSet<String>();
		this.services = new HashSet<ServiceInfo>();
		this.logger = new DefaultLogger();
		this.listener_callbacks = new HashMap<String, ZeroconfDiscoveryHandler>();
		this.default_listener_callback = null;

		/********************
		 * Methods
		 *******************/
		// be nice to get rid of this completely - and have it in the jmdns
		// library itself.
		this.jmmdns.addNetworkTopologyListener(this);
	}

	public Zeroconf(ZeroconfLogger logger) {
		/********************
		 * Variables
		 *******************/
		this.jmmdns = JmmDNS.Factory.getInstance();
		this.listeners = new HashSet<String>();
		this.services = new HashSet<ServiceInfo>();
		this.logger = logger;
		this.listener_callbacks = new HashMap<String, ZeroconfDiscoveryHandler>();
		this.default_listener_callback = null;

		/********************
		 * Methods
		 *******************/
		// be nice to get rid of this completely - and have it in the jmdns
		// library itself.
		this.jmmdns.addNetworkTopologyListener(this);
	}

	/*************************************************************************
	 * User Interface
	 ************************************************************************/
	public void setDefaultDiscoveryCallback(
			ZeroconfDiscoveryHandler listener_callback) {
		this.default_listener_callback = listener_callback;
	}

	public void addListener(String service_type, String domain) {
		addListener(service_type, domain, this.default_listener_callback);
	}

	/**
	 * If you call this early in your program, the jmmdns.addServiceListener
	 * will often do nothing as it hasn't discovered the interfaces yet. In this
	 * case, we save the listener data (listeners.add(service) and add them when
	 * the network interfaces come up.
	 */
	public void addListener(String service_type, String domain,
			ZeroconfDiscoveryHandler listener_callback) {
		String service = service_type + "." + domain + ".";
		logger.println("Activating listener: " + service);
		listeners.add(service);
		if (listener_callback != null) {
			listener_callbacks.put(service, listener_callback);
		}
		// add to currently established interfaces
		jmmdns.addServiceListener(service, this);
	}

	/**
	 * Removes a single listener - though we're not likely to use this much.
	 */
	public void removeListener(String service_type, String domain) {
		String listener_to_remove = service_type + "." + domain + ".";
		for (Iterator<String> listener = listeners.iterator(); listener
				.hasNext();) {
			String this_listener = listener.next().toString();
			if (this_listener.equals(listener_to_remove)) {
				logger.println("Deactivating listener: " + this_listener);
				listener.remove();
				// remove from currently established interfaces
				jmmdns.removeServiceListener(listener_to_remove, this);
				break;
			}
		}
		listener_callbacks.remove(listener_to_remove);
	}

	/**
	 * Publish a zeroconf service.
	 * 
	 * Should actually provide a return value here, so the user can see the
	 * actually published name.
	 * 
	 * @param name
	 *            : english readable name for the service
	 * @param type
	 *            : zeroconf service type, e.g. _ros-master._tcp
	 * @param domain
	 *            : domain to advertise on (usually 'local')
	 * @param port
	 *            : port number
	 * @param description
	 *            :
	 */
	public void addService(String name, String type, String domain, int port,
			String description) {
		String full_service_type = type + "." + domain + ".";
		logger.println("Registering service: " + full_service_type);
		String service_key = "description"; // Max 9 chars
		HashMap<String, byte[]> properties = new HashMap<String, byte[]>();
		properties.put(service_key, description.getBytes());
		ServiceInfo service_info = ServiceInfo.create(full_service_type, name,
				port, 0, 0, true, properties);
		// we need much better logic here to handle duplications.
		if (services.add(service_info)) {
			try {
				jmmdns.registerService(service_info);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// this is broken - it adds it, but fails to resolve it on other systems
		// https://sourceforge.net/tracker/?func=detail&aid=3435220&group_id=93852&atid=605791
		// services.add(ServiceInfo.create(service_type, service_name,
		// service_port, 0, 0, true, text));
	}

	/**
	 * If you try calling this immediately after a service added callback
	 * occurred, you probably wont see anything - it needs some time to resolve.
	 * 
	 * It will block if it needs to resolve services (and aren't in its cache
	 * yet).
	 */
	public List<DiscoveredService> listDiscoveredServices() {
		List<ServiceInfo> service_infos = new ArrayList<ServiceInfo>();
		for (String service : listeners) {
			service_infos.addAll(Arrays.asList(this.jmmdns.list(service)));
			// service_infos.addAll(Arrays.asList(this.jmmdns.list(" ")));
		}
		// At this point, we have a real problem - quite often they are
		// duplicated
		// but have different addresses resolved to each, in other words, we
		// need
		// to uniquely resolve them since jmdns doesn't do us that favour!
		// Todo: Maybe get jmdns to patch this?
		List<DiscoveredService> discovered_services = new ArrayList<DiscoveredService>();
		for (ServiceInfo service_info : service_infos) {
			Boolean service_found = false;
			for (DiscoveredService discovered_service : discovered_services) {
				if (service_info.getQualifiedName().equals(
						discovered_service.name + "." + discovered_service.type
								+ "." + discovered_service.domain + ".")) {
					for (InetAddress inet_address : service_info
							.getInetAddresses()) {
						if (inet_address instanceof Inet4Address) {
							Boolean address_found = false;
							for (String unique_address : discovered_service.ipv4_addresses) {
								if (inet_address.getHostAddress().equals(
										unique_address)) {
									address_found = true;
									break;
								}
							}
							if (!address_found) {
								discovered_service.ipv4_addresses
										.add(inet_address.getHostAddress());
							}
						}
					}
					service_found = true;
					break;
				}
			}
			if (!service_found) {
				discovered_services.add(toDiscoveredService(service_info));
			}

		}
		return discovered_services;
	}

	/**
	 * This should be called when your application shuts down to remove all
	 * services so you don't pollute the zeroconf namespace with hanging,
	 * unresolvable services.
	 */
	public void removeAllServices() {
		logger.println("Removing all services");
		jmmdns.unregisterAllServices();
		services.clear();
	}

	public void shutdown() throws IOException {
		removeAllServices();
		logger.println("Shutdown");
		jmmdns.close();
	}

	/*************************************************************************
	 * Listener Callbacks - from ServiceListener and ServiceTypeListener
	 ************************************************************************/
	@Override
	public void serviceAdded(ServiceEvent event) {
		final ServiceInfo service_info = event.getInfo();
		System.out.println("增加的服务" + service_info + "   名称:"
				+ service_info.getName());
		jmmdns.getServiceInfos(service_info.getType(), service_info.getName(),
				true);
		ZeroconfDiscoveryHandler callback = listener_callbacks.get(service_info
				.getType());
		if (callback != null) {
			callback.serviceAdded(toDiscoveredService(service_info));
		} else {
			logger.println("[+] Service         : "
					+ service_info.getQualifiedName());
		}
		String ip = "";
		for (InetAddress inet_address : service_info.getInetAddresses()) {
			if (inet_address instanceof Inet4Address) {
				ip = inet_address.getHostAddress();
				break;
			}
		}
		if ("".equals(ip)) {
			if (ScanDeviceService.scanDevice != null) {
				if (ScanDeviceService.discovered_services != null) {

					List<DiscoveredService> discovered_services = listDiscoveredServices();
					if (discovered_services.size() > 0) {
						for (DiscoveredService discovered_service : discovered_services) {

							if (discovered_service.ipv4_addresses != null
									&& discovered_service.ipv4_addresses.size() > 0) {
								if (!ScanDeviceService.discovered_services
										.contains(discovered_service.ipv4_addresses
												.get(0))) {
									ScanDeviceService.discovered_services
											.add(discovered_service.ipv4_addresses
													.get(0));
								}
								//新增加的文件传输
								if(!ScanDeviceService.turbo_map.containsKey(discovered_service.ipv4_addresses.get(0))){
									ScanDeviceService.turbo_map.put(discovered_service.ipv4_addresses.get(0),discovered_service.name);
								}
							}
						}

					}
				}
			}
		}else{
			if (ScanDeviceService.scanDevice != null) {
				if (ScanDeviceService.discovered_services != null) {
					if (!ScanDeviceService.discovered_services
							.contains(ip)) {
						ScanDeviceService.discovered_services
								.add(ip);
					}
				}
				
				//新增加的文件传输
				if(!ScanDeviceService.turbo_map.containsKey(ip)){
					ScanDeviceService.turbo_map.put(ip,service_info.getName());
				}
			}
		}
		if (ScanDeviceService.scanDevice != null) {
			ScanDeviceService.scanDevice.sendMessage();
		}
			
	}

	@Override
	public void serviceRemoved(ServiceEvent event) {
		final String name = event.getName();
		final ServiceInfo service_info = event.getInfo();
		ZeroconfDiscoveryHandler callback = listener_callbacks.get(service_info
				.getType());
		if (callback != null) {
			callback.serviceRemoved(toDiscoveredService(service_info));
		} else {
			logger.println("[-] Service         : " + name);
		}
		String ip = "";
		for (InetAddress inet_address : service_info.getInetAddresses()) {
			if (inet_address instanceof Inet4Address) {
				ip = inet_address.getHostAddress();
				break;
			}
		}
		if ("".equals(ip)) {
			if (ScanDeviceService.scanDevice != null) {
				if (ScanDeviceService.discovered_services != null) {

					List<DiscoveredService> discovered_services = listDiscoveredServices();
					if (discovered_services.size() > 0) {
						Set<String> allip=new HashSet<String>();
						ScanDeviceService.turbo_map.clear();
						for (DiscoveredService discovered_service : discovered_services) {

							if (discovered_service.ipv4_addresses != null
									&& discovered_service.ipv4_addresses.size() > 0) {
									allip.add(discovered_service.ipv4_addresses
													.get(0));
									//新增加的文件传输
									ScanDeviceService.turbo_map.put(discovered_service.ipv4_addresses.get(0),discovered_service.name);
							}
							
						}
						ScanDeviceService.discovered_services=allip;

					}
				}
			}
		}else{
			if (ScanDeviceService.scanDevice != null) {
				if (ScanDeviceService.discovered_services != null) {
					if (ScanDeviceService.discovered_services
							.contains(ip)) {
						ScanDeviceService.discovered_services
								.remove(ip);
					}
				}
				//新增加的文件传输
				if(ScanDeviceService.turbo_map.containsKey(ip)){
					ScanDeviceService.turbo_map.remove(ip);
				}
			}
		}
		if (ScanDeviceService.scanDevice != null) {
			ScanDeviceService.scanDevice.sendMessage();
		}
	}

	@Override
	/**
	 * This implements service resolved in a very simple way. If the user has callbacks, then
	 * it just directly resolves the service info to a ros service info. Note that if you have
	 * multiple interfaces (e.g. eth0, wlan0) then this won't provide the resolved artifact
	 * for all interfaces. It might be worth adding a check for that service across all
	 * interfaces here and providing a fully updated (with regards to addresses) ros
	 * service info artifact to the user's callback here.
	 */
	public void serviceResolved(ServiceEvent event) {
		final ServiceInfo service_info = event.getInfo();
		ZeroconfDiscoveryHandler callback = listener_callbacks.get(service_info
				.getType());
		if (callback != null) {
			callback.serviceResolved(toDiscoveredService(service_info));
		} else {
			logger.println("[=] Resolved        : "
					+ service_info.getQualifiedName());
			logger.println("      Port          : " + service_info.getPort());
			for (int i = 0; i < service_info.getInetAddresses().length; ++i) {
				logger.println("      Address       : "
						+ service_info.getInetAddresses()[i].getHostAddress());
			}
		}
	}

	@Override
	public void serviceTypeAdded(ServiceEvent event) {
		// final String aType = event.getType();
		// logger.println("TYPE: " + aType);
	}

	@Override
	public void subTypeForServiceTypeAdded(ServiceEvent event) {
		// logger.println("SUBTYPE: " + event.getType());
	}

	/******************************
	 * Network Topology Callbacks
	 *****************************/
	@Override
	public void inetAddressAdded(NetworkTopologyEvent event) {
		try {
			logger.println("[+] NetworkInterface: "
					+ event.getInetAddress().getHostAddress()
					+ " ["
					+ NetworkInterface.getByInetAddress(event.getInetAddress())
							.getDisplayName() + "]");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			event.getDNS().addServiceTypeListener(this);
			for (String listener : listeners) {
				logger.println("      Adding service listener '" + listener
						+ "'");
				event.getDNS().addServiceListener(listener, this);
			}
			for (ServiceInfo service : services) {
				logger.println("Publishing Service on "
						+ event.getInetAddress().getHostAddress());
				logger.println("  Name   : " + service.getName());
				logger.println("  Type   : " + service.getType());
				logger.println("  Port   : " + service.getPort());
				event.getDNS().registerService(service.clone()); // if you don't
																	// clone it,
																	// it falls
																	// over
																	// badly!
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void inetAddressRemoved(NetworkTopologyEvent event) {
		String event_address_str = event.getInetAddress().getHostAddress();
		// can't get the display name like above, as the interface is no longer
		// available.
		// if we really want the display name, need to store it somewhere when
		// the network interface
		// is added.
		logger.println("[-] NetworkInterface: " + event_address_str);
		// Trigger service info callbacks - this is fairly brutal. It might be
		// better to
		// check here if that service is no longer supplied on all interfaces,
		// then call
		// serviceRemoved. If it is still supplied, provide a serviceResolved
		// callback with
		// the updated addresses.

		event.getDNS().removeServiceTypeListener(this);
		for (String listener : listeners) {
			logger.println("      Removing service listener '" + listener + "'");
			event.getDNS().removeServiceListener(listener, this);
		}
		for (ServiceInfo service : services) {
			logger.println("Unpublishing Service:");
			logger.println("  Name   : " + service.getName());
			logger.println("  Type   : " + service.getType());
			logger.println("  Port   : " + service.getPort());
			event.getDNS().unregisterService(service); // this may not work
														// because we're cloning
														// it.
		}
	}

	/******************************
	 * Utility Functions
	 *****************************/
	// public void display(DiscoveredService discovered_service) {
	// logger.println("Discovered Service:");
	// logger.println("  Name   : " + discovered_service.name );
	// logger.println("  Type   : " + discovered_service.type );
	// logger.println("  Port   : " + discovered_service.port );
	// for ( String address : discovered_service.ipv4_addresses ) {
	// logger.println("  Address: " + address );
	// }
	// for ( String address : discovered_service.ipv6_addresses ) {
	// logger.println("  Address: " + address );
	// }
	// }

	// public String toString(DiscoveredService discovered_service) {
	// String result = "Service Info:\n";
	// result += "  Name   : " + discovered_service.name + "\n";
	// result += "  Type   : " + discovered_service.type + "\n";
	// result += "  Port   : " + discovered_service.port + "\n";
	// for ( String address : discovered_service.ipv4_addresses ) {
	// result += "  Address: " + address + "\n";
	// }
	// for ( String address : discovered_service.ipv6_addresses ) {
	// result += "  Address: " + address + "\n";
	// }
	// return result;
	// }

	/*************************************************************************
	 * Private
	 ************************************************************************/
	// /******************************
	// * Discovery
	// *****************************/
	// /**
	// * If you try calling this immediately after a service added callback
	// * occurred, you probably wont see anything - it needs some time to
	// resolve.
	// *
	// * It will block if it needs to resolve services (and aren't in its cache
	// yet).
	// *
	// * @sa listDiscoveredServices
	// *
	// * @return service_infos : an array of discovered ServiceInfo objects.
	// */
	// private List<ServiceInfo> listJmdnsDiscoveredServices() {
	// List<ServiceInfo> service_infos = new ArrayList<ServiceInfo>();
	// for(String service : listeners ) {
	// service_infos.addAll(Arrays.asList(this.jmmdns.list(service)));
	// }
	// return service_infos;
	// }

	/******************************
	 * Utility Functions
	 *****************************/
	private DiscoveredService toDiscoveredService(ServiceInfo service_info) {
		DiscoveredService discovered_service = new DiscoveredService();
		discovered_service.name = service_info.getName();
		String[] type_domain_str = service_info.getType().split("\\.");
		discovered_service.type = type_domain_str[0] + "." + type_domain_str[1];
		discovered_service.domain = service_info.getDomain();
		discovered_service.hostname = service_info.getServer();
		discovered_service.port = service_info.getPort();
		for (InetAddress inet_address : service_info.getInetAddresses()) {
			if (inet_address instanceof Inet4Address) {
				discovered_service.ipv4_addresses.add(inet_address
						.getHostAddress());
			}
		}
		return discovered_service;
	}

	public void findallservice(){
		if (ScanDeviceService.discovered_services != null) {
			ScanDeviceService.discovered_services.clear();
			List<DiscoveredService> discovered_services = listDiscoveredServices();
			if (discovered_services.size() > 0) {
				for (DiscoveredService discovered_service : discovered_services) {

					if (discovered_service.ipv4_addresses != null
							&& discovered_service.ipv4_addresses.size() > 0) {
						if (!ScanDeviceService.discovered_services
								.contains(discovered_service.ipv4_addresses
										.get(0))) {
							ScanDeviceService.discovered_services
									.add(discovered_service.ipv4_addresses
											.get(0));
						}

						//新增加的文件传输
						if(!ScanDeviceService.turbo_map.containsKey(discovered_service.ipv4_addresses.get(0))){
							ScanDeviceService.turbo_map.put(discovered_service.ipv4_addresses.get(0),discovered_service.name);
						}
					}
				}
//				if (ScanDeviceService.scanDevice != null) {
//					ScanDeviceService.scanDevice.sendMessage();
//				}
			}
		}
	}
	
	
}