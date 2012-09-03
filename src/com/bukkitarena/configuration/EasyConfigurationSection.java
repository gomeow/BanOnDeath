/**
 * 
 */
package com.bukkitarena.configuration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Nate Mortensen
 * 
 */
public class EasyConfigurationSection{
	ConfigurationSection config;
	String name;
	public EasyConfigurationSection(ConfigurationSection c){
		config = c;
		load();
	}
	public int compareTo(String o){
		return name.compareTo(o);
	}
	public String value(){
		return name;
	}

	public void load() throws InvalidConfigurationException{
		name = config.getName();
		loadObject(this, config);
	}
	public void save(){
		saveObject(this, config);
	}
	//Not needed.
//	public void save(){
//		for (Field field : this.getClass().getDeclaredFields()){
//			if (field.isAnnotationPresent(Load.class)){
//				String path = field.getName();
//				try {
//					config.set(path, field.get(this));
//
//				} catch(IllegalAccessException ex) {
//					
//				}
//			}
//		}
//		FileConfiguration parent = (FileConfiguration)config.getRoot();
//		parent.set(config.getCurrentPath(), config);
//	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void loadObject(Object obj, ConfigurationSection c){
		for (Field field : getValidFields(obj)) {
			String path = field.getName();
			try {
				if (c.isSet(path)) {
					if (field.getType().isInstance(EasyConfigurationSection.class)){
						field.set(obj, newInstance((Class<? extends EasyConfigurationSection>) field.getType(), c));
						continue;
					}
					else if (field.getType().isEnum()){
						try{
							field.set(obj, (Enum<?>)Enum.valueOf((Class<? extends Enum>) field.getType(), (String)c.get(path)));
						} catch(IllegalArgumentException e){
							if (field.getAnnotation(Load.class).value())throw new InvalidConfigurationException(c.getCurrentPath() + "."+path+" in "+c.getName() + " is not a valid enumerator for type "+field.getType().getSimpleName());
						}
					}
					field.set(obj, c.get(path));
				} else {
					if (field.get(obj) != null){
						c.set(path, field.get(obj));
					}
					else if (field.getAnnotation(Load.class).value()){
						throw new InvalidConfigurationException(c.getCurrentPath()+"."+path +" in "+ c.getName() + " may not be null!");
					}
				}
			} catch (IllegalAccessException ex) {

			}

		}
	}
	public void inheritFrom(Object obj){
		inheritValues(this,obj);
	}
	
	public static void inheritValues(Object obj1, Object obj2){
		for (Field field : obj1.getClass().getFields()){
			try {
				Field field2 = obj2.getClass().getField(field.getName());
				field.set(obj1, field2.get(obj2));
			} catch (SecurityException e) {
				continue;
			} catch (NoSuchFieldException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
			
		}
	}
	
	public static void saveObject(Object obj, ConfigurationSection c){
		for (Field field : getValidFields(obj)){
			String path = field.getName();
			if (c.contains(path)){
				try {
					c.set(path, field.get(obj));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	static List<Field> getValidFields(Object obj){
		ArrayList<Field> fields = new ArrayList<Field>();
		for (Field field : obj.getClass().getFields())
			if (field.isAnnotationPresent(Load.class))
				fields.add(field);
		return fields;
		
	}
	@SuppressWarnings("unchecked")
	public<T extends EasyConfigurationSection> void loadSubSections(Class<? extends EasyConfigurationSection> clazz){
		ArrayList<T> objects = new ArrayList<T>();
		for (String s : config.getKeys(false)){
			if (config.get(s) instanceof ConfigurationSection)
				objects.add((T) newInstance(clazz, config.getConfigurationSection(s)));
			
		}
	}
	private static EasyConfigurationSection newInstance(Class<? extends EasyConfigurationSection> clazz, ConfigurationSection c){
		try {
			Constructor<? extends EasyConfigurationSection> constructor = clazz.getConstructor(c.getClass());
			return constructor.newInstance(c);			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getName(){
		return name;
	}
	public ConfigurationSection getConfig(){
		return config;
	}
}
