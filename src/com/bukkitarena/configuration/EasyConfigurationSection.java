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

	public void load() throws InvalidConfigurationException{
		name = config.getName();
		System.out.println("Starting to work on "+ getClass().getSimpleName());
		for (Field field : getValidFields()) {
			String path = field.getName();
			System.out.println("Working on field: "+field.getName());
			try {
				if (config.contains(path)) {
					System.out.println("The config contained "+field.getName());
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
					System.out.println("The config has set "+field.getName()+" to "+config.get(path).toString());
				} else {
					if (field.get(this) != null){
						System.out.println("Setting "+path+" to "+ field.get(this) +" in class "+ this.getClass().getSimpleName());
						config.set(path, field.get(this));
						continue;
					}
					else if (field.getAnnotation(Load.class).value())
						throw new InvalidConfigurationException(config.getCurrentPath()+"."+path +" in "+ config.getName() + " may not be null!");
				}
			} catch (IllegalAccessException ex) {
				System.out.println("IAE - "+field.getName());
			}

		}
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
				System.out.println("Recognized the field: "+field.getName()+" as a valid field in "+this.getClass().getSimpleName());
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
