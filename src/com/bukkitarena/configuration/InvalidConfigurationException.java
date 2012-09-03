/**
 * 
 */
package com.bukkitarena.configuration;

/**
 * @author Nate Mortensen
 * 
 */
@SuppressWarnings("serial")
public class InvalidConfigurationException extends RuntimeException{

	public InvalidConfigurationException(){}
	
	public InvalidConfigurationException(String msg){
		super(msg);
	}
}
