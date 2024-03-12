/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ccra3;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author kumpeep
 */
public class MyPasswordEncoder implements PasswordEncoder
{

    @Override
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return rawPassword.toString().equals(encodedPassword.replaceAll(java.util.regex.Pattern.quote("+"), " "));
	}
}