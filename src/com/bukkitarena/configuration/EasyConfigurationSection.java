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
	}
	public int compareTo(String o){
		return name.compareTo(o);
	}
	public String value(){
		return name;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void load() throws InvalidConfigurationException{
		name = config.getName();
		for (Field field : getValidFields()) {
			String path = field.getName();
			try {
				if (config.contains(path)) {
					if (field.getType().isInstance(EasyConfigurationSection.class)){
						field.set(this, newInstance((Class<? extends EasyConfigurationSection>) field.getType(), config));
					}
					else if (field.getType().isEnum()){
						try{
							field.set(this, (Enum<?>)Enum.valueOf((Class<? extends Enum>) field.getType(), ((String)config.get(path)).toUpperCase()));
						} catch(IllegalArgumentException e){
							if (field.getAnnotation(Load.class).value())
								throw new InvalidConfigurationException(config.getCurrentPath() + "."+path+" in "+config.getName() + " is not a valid enumerator for type "+field.getType().getSimpleName());
						}
					}
					field.set(this, config.get(path));
				} else {
					if (field.get(this) != null){
						config.set(path, field.get(this));
						continue;
					}
					else if (field.getAnnotation(Load.class).value())
						throw new InvalidConfigurationException(config.getCurrentPath()+"."+path +" in "+ config.getName() + " may not be null!");
				}
			} catch (IllegalAccessException ex) {
			}

		}
	}
	public void save(){
		saveObject(this, config);
	}
	public void inheritFrom(Object obj){
		for (Field field : this.getClass().getDeclaredFields())
			try {
				if (field.get(this) == null){
					Field field2 = obj.getClass().getField(field.getName());
					if (field2.get(obj) != null)
						field.set(this, field2.get(obj));
				}
			} catch (SecurityException e) {
			} catch (NoSuchFieldException e) {
			} catch (IllegalAccessException e) {
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
	public List<Field> getValidFields(){
		ArrayList<Field> fields = new ArrayList<Field>();
		for (Field field : this.getClass().getDeclaredFields())
			if (field.isAnnotationPresent(Load.class)){
				fields.add(field);
			}
		return fields;

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
