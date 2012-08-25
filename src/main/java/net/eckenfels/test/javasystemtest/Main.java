/*
 * JavaSystemTest -- return System properties of the running JVM
 */
package net.eckenfels.test.javasystemtest;


import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.management.JMException;
import javax.management.JMRuntimeException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;


public class Main
{
	private static final String TYPEOF = " : ";


	public static void main(String[] args)
	{
		System.out.printf("--- System Properties ---%n%n");

		Properties systemProperties = System.getProperties();
		for(Entry<Object,Object> entry : systemProperties.entrySet())
		{
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key instanceof String)
				System.out.println((String)key + "=" + asString(value));
			else // unlikely
				System.out.println(asString(key) + "=" + asString(value));
		}

		System.out.printf("%n--- Environment ---%n%n");

		Map<String,String> env = System.getenv();
		for(Entry<String,String> entry : env.entrySet())
		{
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + "=" + asString(value));
		}

		System.out.printf("%n--- JMX Beans ---%n%n");

		final Set<MBeanServer> servers = new HashSet<MBeanServer>();
		servers.add(ManagementFactory.getPlatformMBeanServer());
		servers.addAll(MBeanServerFactory.findMBeanServer(null));
		for (final MBeanServer server : servers)
		{
			System.out.printf("%nServer " + server.toString() +"%n");
			final Set<ObjectName> mbeans = new HashSet<ObjectName>();
			mbeans.addAll(server.queryNames(null, null));
			for (final ObjectName mbean : mbeans)
			{
				String mbeanName = mbean.getCanonicalName();
				System.out.printf("%n " +  mbeanName + "%n");

				MBeanAttributeInfo[] attributes;
				try {
					attributes = server.getMBeanInfo(mbean).getAttributes();
				} catch (JMException e) {
					e.printStackTrace();
					continue;
				} catch (JMRuntimeException e) {
					e.printStackTrace();
					continue;
				}

				for (final MBeanAttributeInfo attribute : attributes)
				{
					String name = attribute.getName();

					String value = getJMXValue(server,mbean,name);

					if ("ObjectName".equals(name))
						continue;

					String desc = attribute.getDescription();
					if (name.equals(desc))
						desc = "";
					else
						desc =" -- " + desc;

					System.out.printf("   " + attribute.getName() + "=" + value + desc + "%n") ;
				}
			}
		}
	}


	/**
	 * Safe function to retrieve JMX Attribute's value as String.
	 *
	 * @param server the MBeanServer
	 * @param mbean the MBean name
	 * @param name the attribute name
	 * @return the {@link #asString(Object)} representation of the value or any exception
	 */
	private static String getJMXValue(MBeanServer server, ObjectName mbean, String name)
	{
		Object value;
		try
		{
			value = server.getAttribute(mbean, name);
		}
		catch (JMException e)
		{
			value = e;
		}
		catch (JMRuntimeException e)
		{
			value = e;
		}

		return asString(value);
	}


	/**
	 * Return String representation.
	 * <P>
	 * If object is not a String, the class will be returned as well.
	 * <P>
	 * TODO: Filter non-printable chars (especially linebreak)
	 *
	 * @param o the object to describe
	 * @return "(null)" or String representation
	 */
	private static String asString(Object o)
	{
		if (o == null)
			return "(null)";

		if (o instanceof String)
		{
			return "\"" + (String)o + "\"";
		}

		if (o instanceof Long)
		{
			return ((Long)o).toString()+"l";
		}

		if (o instanceof String[])
		{
			String[] os = (String[])o;
			StringBuffer b = new StringBuffer("{");
			for(int i=0;i < os.length;i++)
			{
				if (i > 0)
					b.append(',');
				b.append('"');
				b.append(os[i]);
				b.append('"');
			}
			b.append('}');
			return b.toString();
		}

		if (o instanceof long[])
		{
			long[] os = (long[])o;
			StringBuffer b = new StringBuffer("{");
			for(int i=0;i < os.length;i++)
			{
				if (i > 0)
					b.append(',');
				b.append(String.valueOf(os[i]));
				b.append('l');
			}
			b.append('}');
			return b.toString();
		}

		if (o instanceof Integer)
		{
			return ((Integer)o).toString();
		}

		if (o instanceof CompositeDataSupport || o instanceof TabularDataSupport)
		{
			return String.valueOf(o);
		}

		if (o instanceof CompositeData[])
		{
			CompositeData[] os = (CompositeData[])o;
			StringBuffer b = new StringBuffer("CompositeData[]{");
			for(int i=0;i < os.length;i++)
			{
				if (i > 0)
					b.append(',');
				b.append(String.valueOf(os[i]));
			}
			b.append('}');
			return b.toString();
		}

		if (o instanceof Boolean)
		{
			return ((Boolean)o).toString();
		}

		if (o instanceof Double)
		{
			return ((Double)o).toString()+"d";
		}

		if (o instanceof ObjectName)
		{
			return "->" + o.toString();
		}


		if (o instanceof Throwable)
		{
			String t = o.getClass().getCanonicalName();
			String e = o.toString();
			if (e.indexOf(t) < 0)
				return e + TYPEOF + t;
			else
				return e;
		}

		return String.valueOf(o) + TYPEOF + o.getClass().getCanonicalName();
	}

}
