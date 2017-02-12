package org.team3309.lib.tunable;

import java.lang.reflect.Field;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DashboardHelper {
	public static void updateTunable(IDashboard tunable) {
		Class<?> c = tunable.getClass();
		Field[] fields = c.getFields();

		// -- Network Table
		String tableName = tunable.getTableName();
		boolean useSmartDashboard = false;
		NetworkTable table = null;
		if (tableName == "SmartDashboard")
			useSmartDashboard = true;
		else
			table = NetworkTable.getTable(tableName);

		// -- Prefix
		String prefix = "";
		String objName = tunable.getObjectName();
		if (objName != null)
			prefix = objName + "_";

		try {
			for (Field f : fields) {
				Dashboard annon = f.getAnnotation(Dashboard.class);
				if (annon != null) {
					String dispName = annon.displayName();
					boolean readOnly = annon.tunable();
					String name = prefix + (dispName != "" ? dispName : f.getName());

					// ---------------------------------------------------------------
					// -- Set the data

					Class<?> ft = f.getType();
					// -- Boolean
					if (ft.isAssignableFrom(boolean.class)) {
						boolean value = f.getBoolean(tunable);
						if (useSmartDashboard)
							value = SmartDashboard.getBoolean(name, value);
						else
							value = table.getBoolean(name, value);
						if (!readOnly)
							f.set(tunable, value);
					}
					// -- Number
					else if (ft.isAssignableFrom(double.class)) {
						double value = f.getDouble(tunable);
						if (useSmartDashboard)
							value = SmartDashboard.getNumber(name, value);
						else
							value = table.getNumber(name, value);
						if (!readOnly)
							f.set(tunable, value);
					}
					// -- String
					else if (ft.isAssignableFrom(String.class)) {
						String value = (String) f.get(tunable);
						if (useSmartDashboard)
							value = SmartDashboard.getString(name, value);
						else
							value = table.getString(name, value);
						if (!readOnly)
							f.set(tunable, value);
					}
				}
			}
		} catch (IllegalAccessException ex) {
		}

	}
}
