package org.team3309.lib.tunable;

import java.lang.reflect.*;
import edu.wpi.first.wpilibj.networktables.*;

public class DashboardHelper {
	public static void updateTunable(IDashboard dashboardObj) {
		Class<?> c = dashboardObj.getClass();

		Field[] fields = c.getDeclaredFields();
		Method[] methods = c.getDeclaredMethods();

		System.out.println("TABLE " + dashboardObj.getTableName());
		NetworkTable table = NetworkTable.getTable(dashboardObj.getTableName());

		// -- Prefix
		String prefix = "";
		String objName = dashboardObj.getObjectName();
		if (objName != null && objName != "")
			prefix = objName + "_";

		try {
			// -- Fields
			for (Field f : fields) {
				Dashboard annon = f.getAnnotation(Dashboard.class);
				if (annon != null) {
					Class<?> ft = f.getType();
					boolean isTunable = annon.tunable();
					String dispName = annon.displayName();
					String name = prefix + (dispName != "" ? dispName : f.getName());

					// -- Boolean
					if (ft.isAssignableFrom(boolean.class)) {
						boolean value = f.getBoolean(dashboardObj);
						if (isTunable) {
							value = table.getBoolean(name, value);
							f.set(dashboardObj, value);
						} else {
							table.putBoolean(name, value);
						}
					}
					// -- Number
					else if (ft.isAssignableFrom(double.class)) {
						double value = f.getDouble(dashboardObj);
						if (isTunable) {
							value = table.getNumber(name, value);
							f.set(dashboardObj, value);
						} else {
							table.putNumber(name, value);
						}
					}
					// -- String
					else if (ft.isAssignableFrom(String.class)) {
						String value = (String) f.get(dashboardObj);
						if (isTunable) {
							value = table.getString(name, value);
							f.set(dashboardObj, value);
						} else {
							table.putString(name, value);
						}
					}
				}
			}

			// -- Methods
			for (Method m : methods) {
				Dashboard annon = m.getAnnotation(Dashboard.class);

				if (annon != null) {
					Class<?> ft = m.getReturnType();
					String dispName = annon.displayName();
					String name = prefix + (dispName != "" ? dispName : m.getName());
					System.out.println(name);
					// -- Boolean
					if (ft.isAssignableFrom(boolean.class)) {
						table.putBoolean(name, (boolean) m.invoke(dashboardObj));
					}
					// -- Number
					else if (ft.isAssignableFrom(double.class)) {
						System.out.println(name + " " + m.invoke(dashboardObj));
						table.putNumber(name, (double) m.invoke(dashboardObj));
					}
					// -- String
					else if (ft.isAssignableFrom(String.class)) {
						table.putString(name, (String) m.invoke(dashboardObj));
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
		}

	}
}
