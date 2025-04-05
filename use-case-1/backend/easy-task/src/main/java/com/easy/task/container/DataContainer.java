package com.easy.task.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easy.task.beans.Task;

public class DataContainer {
	
	private static final Map<String, List<Task>> tasks = new HashMap<>();
	
	public static Map<String, List<Task>> getDataContainer() {
		return tasks;
	}
}
